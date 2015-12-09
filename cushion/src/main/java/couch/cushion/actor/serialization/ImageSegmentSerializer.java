package couch.cushion.actor.serialization;

import java.nio.ByteBuffer;

import akka.serialization.JSerializer;
import couch.cushion.media.ImageSegment;

public class ImageSegmentSerializer extends JSerializer {

    @Override
    public int identifier() {
        return 123456;
    }

    @Override
    public boolean includeManifest() {
        return false;
    }

    @Override
    public byte[] toBinary(final Object o) {
        if (o instanceof ImageSegment) {
            final ImageSegment segment = (ImageSegment) o;
            final ByteBuffer buffer = ByteBuffer.allocate((Long.SIZE / Byte.SIZE) + (Integer.SIZE / Byte.SIZE) * 3 + 1 + segment.getNum())
                    .putLong(segment.getTimestamp())
                    .putInt(segment.getId())
                    .putInt(segment.getIndex())
                    .putInt(segment.getNum())
                    .put(segment.isLast() ? (byte)1 : (byte)0)
                    .put(segment.getData(), 0, segment.getNum());
            return buffer.array();
        }
        return null;
    }

    @Override
    public Object fromBinaryJava(final byte[] bytes, final Class<?> manifest) {
        if (ImageSegment.class.isAssignableFrom(manifest)) {
            final ByteBuffer buffer = ByteBuffer.wrap(bytes);
            final long timestamp = buffer.getLong();
            final int id = buffer.getInt();
            final int index = buffer.getInt();
            final int num = buffer.getInt();
            final boolean isLast = buffer.get() > 0;
            final byte[] data = new byte[num];
            buffer.get(data);
            return new ImageSegment(timestamp, id, index, data, num, isLast);
        }
        return null;
    }
}
