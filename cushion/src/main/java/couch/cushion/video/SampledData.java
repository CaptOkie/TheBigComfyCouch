package couch.cushion.video;

import java.util.Optional;

import io.humble.video.AudioFormat;
import io.humble.video.Codec;
import io.humble.video.Decoder;
import io.humble.video.Encoder;
import io.humble.video.MediaAudio;
import io.humble.video.MediaAudioResampler;
import io.humble.video.MediaPicture;
import io.humble.video.MediaPictureResampler;
import io.humble.video.MediaResampler;
import io.humble.video.MediaSampled;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;

public class SampledData {

    public static Optional<SampledData> make(final Decoder decoder, final MuxerFormat format) {
        
        if (decoder == null || format == null) {
            return Optional.empty();
        }
        
        final Optional<SampledData> data;
        final Codec codec;
        switch (decoder.getCodecType()) {
            case MEDIA_VIDEO:
                codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
                data = Optional.of(Encoder.make(codec)).map(encoder -> {
                    
                    encoder.setWidth(decoder.getWidth());
                    encoder.setHeight(decoder.getHeight());
                    encoder.setTimeBase(decoder.getTimeBase());
                    encoder.setPixelFormat(PixelFormat.Type.PIX_FMT_YUV420P); // TODO
                    if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
                        encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
                    }
                    
                    final MediaPicture readPicture = MediaPicture.make(decoder.getWidth(), decoder.getHeight(), decoder.getPixelFormat());
                    final MediaPicture writePicture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), encoder.getPixelFormat());
                    final MediaPictureResampler pictureResampler = MediaPictureResampler.make(writePicture.getWidth(), writePicture.getHeight(), writePicture.getFormat(), 
                            readPicture.getWidth(), readPicture.getHeight(), readPicture.getFormat(), 0);
                    pictureResampler.open();
                    
                    return new SampledData(encoder, readPicture, writePicture, pictureResampler);
                });
                break;
                
            case MEDIA_AUDIO:
                codec = Codec.findEncodingCodec(format.getDefaultAudioCodecId());
                data = Optional.of(Encoder.make(codec)).map(encoder -> {
                    
                    encoder.setChannelLayout(decoder.getChannelLayout());
                    encoder.setChannels(decoder.getChannels());
                    encoder.setSampleRate(decoder.getSampleRate());
                    encoder.setTimeBase(decoder.getTimeBase());
                    encoder.setSampleFormat(AudioFormat.Type.SAMPLE_FMT_S16); // TODO
                    if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
                        encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
                    }

                    final MediaAudio readAudio = MediaAudio.make(decoder.getFrameSize(), decoder.getSampleRate(), decoder.getChannels(), decoder.getChannelLayout(), decoder.getSampleFormat());
                    final MediaAudio writeAudio = MediaAudio.make(encoder.getFrameSize(), encoder.getSampleRate(), encoder.getChannels(), encoder.getChannelLayout(), encoder.getSampleFormat());
                    final MediaAudioResampler audioResampler = MediaAudioResampler.make(writeAudio.getChannelLayout(), writeAudio.getSampleRate(), writeAudio.getFormat(),
                            readAudio.getChannelLayout(), readAudio.getSampleRate(), readAudio.getFormat());
                    audioResampler.open();
                    
                    return new SampledData(encoder, readAudio, writeAudio, audioResampler);
                });
                break;
                
            default:
                data = Optional.empty();
        }
        
        data.ifPresent(d -> d.getEncoder().open(null, null));
        return data;
    }
    
    private final Encoder encoder;
    private final MediaSampled readMedia;
    private final MediaSampled writeMedia;
    private final MediaResampler resampler;

    private SampledData(final Encoder encoder, final MediaSampled readMedia, final MediaSampled writeMedia, final MediaResampler resampler) {
        this.encoder = encoder;
        this.readMedia = readMedia;
        this.writeMedia = writeMedia;
        this.resampler = resampler;
    }

    public Encoder getEncoder() {
        return encoder;
    }

    public MediaSampled getReadMedia() {
        return readMedia;
    }
    
    public MediaSampled getWriteMedia() {
        return writeMedia;
    }
    
    public int resample() {
        return resampler.resample(writeMedia, readMedia);
    }
}
