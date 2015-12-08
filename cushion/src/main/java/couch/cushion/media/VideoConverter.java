package couch.cushion.media;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.humble.video.*;
import io.humble.video.awt.ImageFrame;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

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
            
            final Muxer muxer = Muxer.make(dest.toString(), null, convertTo);
            try {
                final MuxerFormat format = muxer.getFormat();
                
                final Map<Integer, SampledData> sampledData = new HashMap<>();
                for (int i = 0; i < demuxer.getNumStreams(); ++i) {
                    
                    final Optional<SampledData> data = SampledData.make(demuxer.getStream(i).getDecoder(), format);
                    if (data.isPresent()) {
                        sampledData.put(i, data.get());
                        muxer.addNewStream(data.get().getEncoder());
                    }
                }
                muxer.open(null, null);
                
                for (final Map.Entry<Integer, SampledData> data : sampledData.entrySet()) {
                    final MediaPacket read = MediaPacket.make();
                    final MediaPacket write = MediaPacket.make();
                    final Decoder decoder = demuxer.getStream(data.getKey()).getDecoder();

                    if (decoder != null) {
                        decoder.open(null, null);

                        while (demuxer.read(read) >= 0) {
                        
                            if (read.getStreamIndex() == data.getKey()) {
                                int offset = 0;
                                do {
                                    offset += decode(decoder, read, offset, data.getValue(), write, muxer);
                                } while (offset < read.getSize());
                            }
                        }
                        
                        do {
                            decode(decoder, null, 0, data.getValue(), write, muxer);
                        } while (data.getValue().getReadMedia().isComplete());
                        
                        encode(data.getValue().getEncoder(), write, null, muxer);
                    }
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            finally {
                muxer.close();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
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
    private int decode(final Decoder decoder, final MediaPacket read, final int offset, final SampledData sampledData, final MediaPacket write, final Muxer muxer) throws IOException {
        final int ret = decoder.decode(sampledData.getReadMedia(), read, offset);
        if (sampledData.getReadMedia().isComplete()) {
            sampledData.resample();
            encode(sampledData, write, muxer);
        }
        if (ret < 0) {
            throw new IOException("Error " + ret + " returned while decoding");
        }
        return ret;
    }
    
    /**
     * Encodes the {@link MediaPacket} from the {@link MediaSampled} and writes the {@link MediaPacket} to the {@link Muxer}.
     * @param encoder The {@link Encoder} used to encode the media.
     * @param write The {@link MediaPacket} to encode into.
     * @param media The {@link MediaSampled} to decode from.
     * @param muxer The {@link Muxer} to write to.
     */
    private void encode(final Encoder encoder, final MediaPacket write, final MediaSampled media, final Muxer muxer) {
        do {
            encoder.encode(write, media);
            if (write.isComplete()) {
                muxer.write(write, false);
            }
        } while (write.isComplete());
    }

    private void encode(final SampledData sampledData, final MediaPacket write, final Muxer muxer) {
        encode(sampledData.getEncoder(), write, sampledData.getWriteMedia(), muxer);
    }

    public static void playVideo(String filename, ImageView view) throws InterruptedException, IOException {
    /*
     * Start by creating a container object, in this case a demuxer since
     * we are reading, to get video data from.
     */
        Demuxer demuxer = Demuxer.make();

    /*
     * Open the demuxer with the filename passed on.
     */
        demuxer.open(filename, null, false, true, null, null);

    /*
     * Query how many streams the call to open found
     */
        int numStreams = demuxer.getNumStreams();

    /*
     * Iterate through the streams to find the first video stream
     */
        int videoStreamId = -1;
        long streamStartTime = Global.NO_PTS;
        Decoder videoDecoder = null;
        for(int i = 0; i < numStreams; i++)
        {
            final DemuxerStream stream = demuxer.getStream(i);
            streamStartTime = stream.getStartTime();
            final Decoder decoder = stream.getDecoder();
            if (decoder != null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_VIDEO) {
                videoStreamId = i;
                videoDecoder = decoder;
                // stop at the first one.
                break;
            }
        }
        if (videoStreamId == -1)
            throw new RuntimeException("could not find video stream in container: "+filename);

    /*
     * Now we have found the audio stream in this file.  Let's open up our decoder so it can
     * do work.
     */
        videoDecoder.open(null, null);

        final MediaPicture picture = MediaPicture.make(
                videoDecoder.getWidth(),
                videoDecoder.getHeight(),
                videoDecoder.getPixelFormat());
        
        /** A converter object we'll use to convert the picture in the video to a BGR_24 format that Java Swing
         * can work with. You can still access the data directly in the MediaPicture if you prefer, but this
         * abstracts away from this demo most of that byte-conversion work. Go read the source code for the
         * converters if you're a glutton for punishment.
         */
        final MediaPictureConverter converter =
                MediaPictureConverterFactory.createConverter(
                        MediaPictureConverterFactory.HUMBLE_BGR_24,
                        picture);
        BufferedImage image = null;

        /**
         * This is the Window we will display in. See the code for this if you're curious, but to keep this demo clean
         * we're 'simplifying' Java AWT UI updating code. This method just creates a single window on the UI thread, and blocks
         * until it is displayed.
         */
        final ImageFrame window = null;
//        final ImageFrame window = ImageFrame.make();
//        if (window == null) {
//            throw new RuntimeException("Attempting this demo on a headless machine, and that will not work. Sad day for you.");
//        }

        /**
         * Media playback, like comedy, is all about timing. Here we're going to introduce <b>very very basic</b>
         * timing. This code is deliberately kept simple (i.e. doesn't worry about A/V drift, garbage collection pause time, etc.)
         * because that will quickly make things more complicated.
         *
         * But the basic idea is there are two clocks:
         * <ul>
         * <li>Player Clock: The time that the player sees (relative to the system clock).</li>
         * <li>Stream Clock: Each stream has its own clock, and the ticks are measured in units of time-bases</li>
         * </ul>
         *
         * And we need to convert between the two units of time. Each MediaPicture and MediaAudio object have associated
         * time stamps, and much of the complexity in video players goes into making sure the right picture (or sound) is
         * seen (or heard) at the right time. This is actually very tricky and many folks get it wrong -- watch enough
         * Netflix and you'll see what I mean -- audio and video slightly out of sync. But for this demo, we're erring for
         * 'simplicity' of code, not correctness. It is beyond the scope of this demo to make a full fledged video player.
         */

        // Calculate the time BEFORE we start playing.
        long systemStartTime = System.nanoTime();
        // Set units for the system time, which because we used System.nanoTime will be in nanoseconds.
        final Rational systemTimeBase = Rational.make(1, 1000000000);
        // All the MediaPicture objects decoded from the videoDecoder will share this timebase.
        final Rational streamTimebase = videoDecoder.getTimeBase();

        /**
         * Now, we start walking through the container looking at each packet. This
         * is a decoding loop, and as you work with Humble you'll write a lot
         * of these.
         *
         * Notice how in this loop we reuse all of our objects to avoid
         * reallocating them. Each call to Humble resets objects to avoid
         * unnecessary reallocation.
         */
        final MediaPacket packet = MediaPacket.make();
        while(demuxer.read(packet) >= 0) {
            /**
             * Now we have a packet, let's see if it belongs to our video stream
             */
            if (packet.getStreamIndex() == videoStreamId)
            {
                /**
                 * A packet can actually contain multiple sets of samples (or frames of samples
                 * in decoding speak).  So, we may need to call decode  multiple
                 * times at different offsets in the packet's data.  We capture that here.
                 */
                int offset = 0;
                int bytesRead = 0;
                do {
                    bytesRead += videoDecoder.decode(picture, packet, offset);
                    if (picture.isComplete()) {
                        image = displayVideoAtCorrectTime(streamStartTime, picture,
                                converter, image, window, systemStartTime, systemTimeBase,
                                streamTimebase, view);
                    }
                    offset += bytesRead;
                } while (offset < packet.getSize());
            }
        }

        // Some video decoders (especially advanced ones) will cache
        // video data before they begin decoding, so when you are done you need
        // to flush them. The convention to flush Encoders or Decoders in Humble Video
        // is to keep passing in null until incomplete samples or packets are returned.
        do {
            videoDecoder.decode(picture, null, 0);
            if (picture.isComplete()) {
                image = displayVideoAtCorrectTime(streamStartTime, picture, converter,
                        image, window, systemStartTime, systemTimeBase, streamTimebase, view);
            }
        } while (picture.isComplete());

        // It is good practice to close demuxers when you're done to free
        // up file handles. Humble will EVENTUALLY detect if nothing else
        // references this demuxer and close it then, but get in the habit
        // of cleaning up after yourself, and your future girlfriend/boyfriend
        // will appreciate it.
        demuxer.close();

        // similar with the demuxer, for the windowing system, clean up after yourself.
//        window.dispose();
    }

    /**
     * Takes the video picture and displays it at the right time.
     */
    private static BufferedImage displayVideoAtCorrectTime(long streamStartTime,
                                                           final MediaPicture picture, final MediaPictureConverter converter,
                                                           BufferedImage image, final ImageFrame window, long systemStartTime,
                                                           final Rational systemTimeBase, final Rational streamTimebase, final ImageView view)
            throws InterruptedException {
        long streamTimestamp = picture.getTimeStamp();
        // convert streamTimestamp into system units (i.e. nano-seconds)
        streamTimestamp = systemTimeBase.rescale(streamTimestamp-streamStartTime, streamTimebase);
        // get the current clock time, with our most accurate clock
        long systemTimestamp = System.nanoTime();
        // loop in a sleeping loop until we're within 1 ms of the time for that video frame.
        // a real video player needs to be much more sophisticated than this.
        while (streamTimestamp > (systemTimestamp - systemStartTime + 1000000)) {
            Thread.sleep(1);
            systemTimestamp = System.nanoTime();
        }
        
        // finally, convert the image from Humble format into Java images.
        image = converter.toImage(image, picture);
        // And ask the UI thread to repaint with the new image.
        final WritableImage s = new WritableImage(image.getWidth(), image.getHeight());
        SwingFXUtils.toFXImage(image, s);
//        window.setImage(image);
        Platform.runLater(() -> {
            view.setImage(s);
        });
        return image;
    }
}
