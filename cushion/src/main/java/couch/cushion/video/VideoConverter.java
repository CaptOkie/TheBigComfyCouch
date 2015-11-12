package couch.cushion.video;

import java.io.IOException;
import java.net.URL;

import io.humble.video.Codec;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.Encoder;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;

public class VideoConverter {

    private final String convertTo;

    /**
     * Creates a new {@link VideoConverter} that converts to the given format.
     * @param convertTo The format to convert to.
     */
    public VideoConverter(final String convertTo) {
        this.convertTo = convertTo;
    }

    /**
     * Reads the source video and saves the result to the destination video as the previously specified format.
     * @param src A {@link URL} to the source video.
     * @param dest A {@link URL} to the destination video.
     * @throws InterruptedException 
     * @throws IOException
     */
    public void convert(final URL src, final URL dest) throws InterruptedException, IOException { // TODO CLEAN THE FUCK UP!

        final Demuxer demuxer = Demuxer.make();
        try {
            demuxer.open(src.toString(), null, false, true, null, null);

            for (int i = 0; i < demuxer.getNumStreams(); ++i) {

                final Decoder decoder = demuxer.getStream(i).getDecoder();
                if (decoder != null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_VIDEO) {

                    final Muxer muxer = Muxer.make(dest.toString(), null, convertTo);

                    try {
                        final MuxerFormat format = muxer.getFormat();
                        final Encoder encoder = Encoder.make(Codec.findEncodingCodec(format.getDefaultVideoCodecId()));
                        encoder.setWidth(decoder.getWidth());
                        encoder.setHeight(decoder.getHeight());
                        encoder.setPixelFormat(decoder.getPixelFormat());
                        encoder.setTimeBase(decoder.getTimeBase());
                        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
                            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
                        }
                        encoder.open(null, null);
                        muxer.addNewStream(encoder);
                        muxer.open(null, null);

                        decoder.open(null, null);

                        final MediaPacket read = MediaPacket.make();
                        final MediaPacket write = MediaPacket.make();
                        final MediaPicture picture = MediaPicture.make(decoder.getWidth(), decoder.getHeight(), decoder.getPixelFormat());
                        while (demuxer.read(read) >= 0) {

                            if (read.getStreamIndex() == i) {

                                int offset = 0;
                                do {
                                    offset += decode(decoder, picture, read, offset, encoder, write, muxer);
                                } while (offset < read.getSize());
                            }
                        }

                        // Clearing any cached picture data
                        do {
                            decode(decoder, picture, null, 0, encoder, write, muxer);
                        } while (picture.isComplete());

                        // Clearing any cached packet data
                        encode(encoder, write, null, muxer);
                    }
                    finally {
                        muxer.close();
                    }

                    break;
                }
            }
        }
        finally {
            demuxer.close();
        }
    }
    
    /**
     * Decodes the read {@link MediaPacket} into the {@link MediaPicture} and then uses the write {@link MediaPacket} to write the {@link MediaPicture}
     * to the {@link Muxer} when appropriate.
     * @param decoder The {@link Decoder} used to decode the picture.
     * @param picture The {@link MediaPicture} to decode into and encode from.
     * @param read The {@link MediaPacket} used for reading.
     * @param offset The offset to read from.
     * @param encoder The {@link Encoder} used to encode the picture.
     * @param write The {@link MediaPacket} encode into.
     * @param muxer The {@link Muxer} to write to.
     * @return The number of bytes decoded.
     * @throws IOException
     */
    private int decode(final Decoder decoder, final MediaPicture picture, final MediaPacket read, final int offset, final Encoder encoder, final MediaPacket write, final Muxer muxer) throws IOException {
        final int ret = decoder.decode(picture, read, offset);
        if (picture.isComplete()) {
            encode(encoder, write, picture, muxer);
        }
        if (ret < 0) {
            throw new IOException("Error " + ret + " returned while decoding");
        }
        return ret;
    }
    
    /**
     * Encodes the {@link MediaPacket} from the {@link MediaPicture} and writes the {@link MediaPacket} to the {@link Muxer}.
     * @param encoder The {@link Encoder} used to encode the picture.
     * @param write The {@link MediaPacket} to encode into.
     * @param picture The {@link MediaPicture} to decode from.
     * @param muxer The {@link Muxer} to write to.
     */
    private void encode(final Encoder encoder, final MediaPacket write, final MediaPicture picture, final Muxer muxer) {
        do {
            encoder.encode(write, picture);
            if (write.isComplete()) {
                muxer.write(write, false);
            }
        } while (write.isComplete());
    }
}
