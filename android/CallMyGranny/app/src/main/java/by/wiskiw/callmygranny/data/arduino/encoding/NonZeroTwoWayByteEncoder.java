package by.wiskiw.callmygranny.data.arduino.encoding;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import by.wiskiw.callmygranny.BitUtils;

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
            encoded.addAll(segment.getEncodeSegment());
        }
        return byteListToArray(encoded);
    }

    @Override
    public byte[] decode(byte[] bytes) {
        List<Byte> encoded = new ArrayList<>();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        while (inputStream.available() > 0) {
            List<Byte> segmentBytes = decodedSegmentBytes(inputStream);
            encoded.addAll(segmentBytes);
        }
        return byteListToArray(encoded);
    }

    private static byte[] byteListToArray(List<Byte> list) {
        int count = list.size();
        Byte[] array = list.toArray(new Byte[count]);
        return ArrayUtils.toPrimitive(array);
    }

    private BytesSegment createBytesSegment(ByteArrayInputStream inputStream) {
        BytesSegment bytesSegment = new BytesSegment();
        while (inputStream.available() > 0 && !bytesSegment.isFull()) {
            bytesSegment.addSourceByte((byte) inputStream.read());
        }
        return bytesSegment;
    }

    private List<Byte> decodedSegmentBytes(ByteArrayInputStream inputStream) {
        List<Byte> segmentBytes = new ArrayList<>(HeadByte.PAYLOAD_SIZE);

        if (inputStream.available() <= 0) {
            return segmentBytes;
        }

        HeadByte headByte = new HeadByte((byte) inputStream.read());

        int upBitCount = headByte.getUpBitCount();
        byte byteMap = headByte.getByteMap();

        if (inputStream.available() < upBitCount) {
            throw new IllegalArgumentException(String.format("Not enough upper bytes for segment. Required %d, available %d.",
                upBitCount, inputStream.available()));
        }

        // TAIL_SIZE - 1 - приводим размер к индексу последнего элемента
        for (int bitMapIndex = HeadByte.PAYLOAD_SIZE - 1; bitMapIndex >= 0; bitMapIndex--) {
            boolean isBitUp = BitUtils.isBitUp(byteMap, bitMapIndex);
            if (isBitUp) {
                segmentBytes.add((byte) inputStream.read());
            } else {
                segmentBytes.add(ZERO_BYTE);
            }
//            System.out.println(String.format(Locale.getDefault(),
//                "byteMap[%d]: %s = %b", bitMapIndex, BitUtils.byteToStringBinary(byteMap), isBitUp));
        }
//        System.out.println(String.format("Segment %s", ArrayUtils.toString(segmentBytes)));
        return segmentBytes;
    }

    private final class HeadByte {

        private static final byte EMPTY_HEAD_BYTE = (byte) 0b10000000;

        // кол-во байт, информацию о которых способен содержать HeadByte
        // 1 бит в HeadByte всегда должен быть единицей для избежании потери при передачи
        static final int PAYLOAD_SIZE = 7;

        private byte bitMap;

        HeadByte(byte bitMap) {
            this.bitMap = bitMap;
        }

        HeadByte() {
            this(EMPTY_HEAD_BYTE);
        }

        byte getByteMap() {
            return bitMap;
        }

        void setPayloadBitUp(int payloadBitIndex) {
            int bitIndex = PAYLOAD_SIZE - 1 - payloadBitIndex;
            bitMap = BitUtils.setBitUp(bitMap, bitIndex);
        }

        int getUpBitCount() {
            int count = 0;
            for (int i = 0; i < PAYLOAD_SIZE; i++) {
                if (BitUtils.isBitUp(bitMap, i)) {
                    count++;
                }
            }
            return count;
        }
    }

    private final class BytesSegment {

        private HeadByte head = new HeadByte();
        private List<Byte> tail = new ArrayList<>(HeadByte.PAYLOAD_SIZE);

        void addSourceByte(byte b) {
            if (isFull()) {
                throw new IllegalStateException("Cannot add byte cause bytes segment is full!");
            }
            if (b != 0) {
                head.setPayloadBitUp(tail.size());
            }
            tail.add(b);
        }

        List<Byte> getEncodeSegment() {
            List<Byte> bytes = new ArrayList<>();
            bytes.add(head.getByteMap());
            bytes.addAll(tail);
            return bytes;
        }

        private boolean isFull() {
            return tail.size() >= HeadByte.PAYLOAD_SIZE;
        }

    }
}