package by.wiskiw.callmygranny.data.arduino.encoding.nonzero;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.wiskiw.callmygranny.ArrayUtils;
import by.wiskiw.callmygranny.BitUtils;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteDecoder;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteEncoder;

/**
 * Двухсторонний энкодер. Кодирует последовательность байт, не используя ноль-байты.
 */
public final class NonZeroTwoWayByteEncoder implements ByteEncoder, ByteDecoder {

    private static final byte ZERO_BYTE = 0;

    @Override
    public byte[] encode(byte[] bytes) {
        List<Byte> encoded = new ArrayList<>();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        while (inputStream.available() > 0) {
            BytesSegment segment = createBytesSegment(inputStream);
            encoded.addAll(segment.toBytes());
        }
        return ArrayUtils.byteListToArray(encoded);
    }

    private BytesSegment createBytesSegment(ByteArrayInputStream inputStream) {
        BytesSegment bytesSegment = new BytesSegment();
        while (inputStream.available() > 0 && !bytesSegment.isFull()) {
            bytesSegment.addSourceByte((byte) inputStream.read());
        }
        return bytesSegment;
    }

    @Override
    public byte[] decode(byte[] bytes) {
        List<Byte> encoded = new ArrayList<>();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        while (inputStream.available() > 0) {
            List<Byte> segmentBytes = decodedNextSegment(inputStream);
            encoded.addAll(segmentBytes);
        }
        return ArrayUtils.byteListToArray(encoded);
    }

    private List<Byte> decodedNextSegment(ByteArrayInputStream inputStream) {
        List<Byte> segmentBytes = new ArrayList<>();

        if (inputStream.available() <= 0) {
            return segmentBytes;
        }

        MetaByte metaByte = new MetaByte((byte) inputStream.read());

        int upBitCount = metaByte.getUpBitCount();
        byte byteMap = metaByte.getBitMap();

        if (inputStream.available() < upBitCount) {
            throw new IllegalArgumentException(String.format("Not enough up-bytes for segment. Required %d, available %d.",
                upBitCount, inputStream.available()));
        }


        for (int payloadBitIndex = MetaByte.START_BIT_INDEX; payloadBitIndex <= MetaByte.PAYLOAD_SIZE; payloadBitIndex++) {
            boolean isBitUp = BitUtils.isBitUp(byteMap, payloadBitIndex);
            if (isBitUp) {
                segmentBytes.add((byte) inputStream.read());
            } else {
                segmentBytes.add(ZERO_BYTE);
            }
//            System.out.println(String.format(Locale.getDefault(),
//                "byteMap[%d]: %s = %b", payloadBitIndex, BitUtils.byteToStringBinary(byteMap), isBitUp));
        }
//        System.out.println(String.format("Segment %s", ArrayUtils.toString(segmentBytes)));
        return segmentBytes;
    }

    private final class BytesSegment {

        private MetaByte meta = new MetaByte();
        private List<Byte> payload = new ArrayList<>();

        void addSourceByte(byte b) {
            if (isFull()) {
                throw new IllegalStateException("Cannot add byte cause bytes segment is full!");
            }

            boolean isBitUp = b != 0;
            meta.setNextBit(isBitUp);

            if (b != 0) {
                payload.add(b);
            }
        }

        List<Byte> toBytes() {
            List<Byte> bytes = new ArrayList<>();
            bytes.add(meta.getBitMap());
            bytes.addAll(payload);
            return bytes;
        }

        private boolean isFull() {
            return meta.isFull();
        }

    }
}