package couch.cushion.actor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.Decode;
import couch.cushion.actor.message.FrameRate;
import couch.cushion.actor.message.Process;
import couch.cushion.media.AudioData;
import couch.cushion.media.ImageData;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaAudio;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import io.humble.video.javaxsound.MediaAudioConverter;
import io.humble.video.javaxsound.MediaAudioConverterFactory;

public class MediaDecoder extends AbstractActor {
    
    private static final Process PROCESS_MSG = new Process();
    
//    private final Rational systemTimeBase;
    private Demuxer demuxer;
    private MediaPacket packet;
    
    private Map<Integer, Decoder> audioDecoders;
    private Map<Integer, MediaAudioConverter> audioConverters;
    private Map<Integer, MediaAudio> audios;
    
    private int videoStreamId;
    private Decoder videoDecoder;
    private MediaPictureConverter pictureConverter;
    private MediaPicture picture;
    
    public static Props props() {
        return Props.create(MediaDecoder.class, () -> new MediaDecoder());
    }
    
    private MediaDecoder() {
//        systemTimeBase = Rational.make(1, 1000000000);
        audioDecoders = new HashMap<>();
        audioConverters = new HashMap<>();
        audios = new HashMap<>();
        reset();
        
        receive(ReceiveBuilder.match(Decode.class, msg -> handleDecode(msg))
                .match(Process.class, msg -> doProcessWork(msg))
                .build());
    }
    
    private void handleDecode(final Decode decode) throws InterruptedException, IOException {
        
        if (demuxer != null) {
            demuxer.close();
        }
        reset();

        demuxer = Demuxer.make();
        packet = MediaPacket.make();

        demuxer.open(decode.getUrl(), null, false, true, null, null);
        
        final int numStreams = demuxer.getNumStreams();        
        for (int i = 0; i < numStreams; ++i) {
            
            final DemuxerStream stream = demuxer.getStream(i);
            final Decoder decoder = stream.getDecoder();
            
            if (decode != null) {
                switch (decoder.getCodecType()) {
                    case MEDIA_VIDEO:
                        if (videoDecoder != null) {
                            videoStreamId = i;
                            videoDecoder = decoder;
                            final Rational frameRate = stream.getFrameRate();
                            getContext().parent().tell(new FrameRate(frameRate.getNumerator(), frameRate.getDenominator()), self());
                        }
                        break;
                        
                    case MEDIA_AUDIO:
                        audioDecoders.put(i, decoder);
                        break;
                        
                    default:
                        break;
                }
            }
        }
        
        if (videoDecoder != null) {
            videoDecoder.open(null, null);
            picture = MediaPicture.make(videoDecoder.getWidth(), videoDecoder.getHeight(), videoDecoder.getPixelFormat());
            pictureConverter = MediaPictureConverterFactory.createConverter(MediaPictureConverterFactory.HUMBLE_BGR_24, picture);
        }
        else {
            throw new RuntimeException("No video stream"); // TODO: Make more specific
        }
        
        for (Map.Entry<Integer, Decoder> entry : audioDecoders.entrySet()) {
            final Decoder decoder = entry.getValue();
            decoder.open(null, null);
            audios.put(entry.getKey(), MediaAudio.make(decoder.getFrameSize(), decoder.getSampleRate(), decoder.getChannels(), decoder.getChannelLayout(), decoder.getSampleFormat()));
            audioConverters.put(entry.getKey(), MediaAudioConverterFactory.createConverter(MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO, audios.get(entry.getKey())));
        }
    }
    
    private void doProcessWork(final Process obj) throws InterruptedException, IOException {
        if (obj == PROCESS_MSG) {
            if (demuxer.read(packet) >= 0) {
                
                final int index = packet.getStreamIndex();
                final Decoder decoder = audioDecoders.get(index);
                
                if (decoder != null) {
                    
                    final MediaAudio audio = audios.get(index);
                    final MediaAudioConverter converter = audioConverters.get(index);
                    
                    int offset = 0;
                    int bytesRead = 0;
                    do {
                        bytesRead += decoder.decode(audio, packet, offset);
                        if (audio.isComplete()) {
                            ByteBuffer rawAudio = converter.toJavaAudio(null, audio);
                            getContext().parent().tell(new AudioData(rawAudio, audio.getTimeStamp()), self());
                        }
                        offset += bytesRead;
                    } while (offset < packet.getSize());
                }
                else if (index == videoStreamId) {
                    
                    int offset = 0;
                    int bytesRead = 0;
                    do {
                        bytesRead += videoDecoder.decode(picture, packet, offset);
                        if (picture.isComplete()) {
                            BufferedImage image = pictureConverter.toImage(null, picture);
                            getContext().parent().tell(new ImageData(image, picture.getTimeStamp()), self());
                        }
                        offset += bytesRead;
                    } while (offset < packet.getSize());
                }
            }
            else {
                
                do {
                    videoDecoder.decode(picture, null, 0);
                    if (picture.isComplete()) {
                        BufferedImage image = pictureConverter.toImage(null, picture);
                        getContext().parent().tell(new ImageData(image, picture.getTimeStamp()), self());
                    }
                } while (picture.isComplete());
                
                for (Map.Entry<Integer, Decoder> entry : audioDecoders.entrySet()) {
                    
                    final Decoder decoder = entry.getValue();
                    final MediaAudio audio = audios.get(entry.getKey());
                    final MediaAudioConverter converter = audioConverters.get(entry.getKey());
                    
                    do {
                        decoder.decode(audio, null, 0);
                        if (audio.isComplete()) {
                            ByteBuffer rawAudio = converter.toJavaAudio(null, audio);
                            getContext().parent().tell(new AudioData(rawAudio, audio.getTimeStamp()), self());
                        }
                    } while (audio.isComplete());
                }
            }
        }
    }
    
    private void reset() {
        demuxer = null;
        packet = null;
        
        audioDecoders.clear();
        audioConverters.clear();
        audios.clear();
        
        videoStreamId = -1;
        videoDecoder = null;
        pictureConverter = null;
        picture = null;
    }
    
    @Override
    public void postStop() throws Exception {        
        if (demuxer != null) {
            demuxer.close();
        }
        reset();        
    }
}
