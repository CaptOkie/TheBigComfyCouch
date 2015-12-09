package couch.cushion.actor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.Decode;
import couch.cushion.actor.message.FrameRate;
import couch.cushion.actor.message.Pause;
import couch.cushion.actor.message.Play;
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
import scala.concurrent.duration.FiniteDuration;

public class MediaDecoder extends AbstractActor {
    
    private static final long TICK_DELAY = 1000;
    private static final Process PROCESS_MSG = new Process();
    
    private Demuxer demuxer;
    private MediaPacket packet;
    
    private Map<Integer, Decoder> audioDecoders;
    private Map<Integer, MediaAudioConverter> audioConverters;
    private Map<Integer, MediaAudio> audios;
    
    private int videoStreamId;
    private Decoder videoDecoder;
    private MediaPictureConverter pictureConverter;
    private MediaPicture picture;
    
    private Cancellable processTick;
    private boolean playing;
    
    public static Props props() {
        return Props.create(MediaDecoder.class, () -> new MediaDecoder());
    }
    
    private MediaDecoder() {
        audioDecoders = new HashMap<>();
        audioConverters = new HashMap<>();
        audios = new HashMap<>();
        reset();
        
        receive(ReceiveBuilder.match(Decode.class, msg -> handleDecode(msg))
                .match(Process.class, msg -> doProcessWork(msg))
                .match(Play.class, msg -> play())
                .match(Pause.class, msg -> pause())
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
                        if (videoDecoder == null) {
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
        
        if (playing) {
            play();
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
                pause();
            }
        }
    }
    
    private void play() {
        playing = true;
        if (demuxer != null && processTick == null) {
            FiniteDuration tick = FiniteDuration.create(TICK_DELAY, TimeUnit.MICROSECONDS);
            processTick = getContext().system().scheduler().schedule(FiniteDuration.Zero(), tick, self(), PROCESS_MSG, getContext().dispatcher(), self());
        }
    }
    
    private void pause() {
        playing = false;
        if (processTick != null) {
            processTick.cancel();
            processTick = null;
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
        
        processTick = null;
        playing = false;
    }
    
    @Override
    public void postStop() throws Exception {        
        if (demuxer != null) {
            demuxer.close();
        }
        if (processTick != null) {
            processTick.cancel();
        }
        reset();        
    }
}
