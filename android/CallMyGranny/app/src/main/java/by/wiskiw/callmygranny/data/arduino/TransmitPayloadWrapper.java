package by.wiskiw.callmygranny.data.arduino;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Andrey Yablonsky on 06.12.2019
 */
public class TransmitPayloadWrapper {

    private static final int MAX_DATA_PACKS_COUNT = Short.MAX_VALUE;
    private static final int PACK_SIZE = 56; // bytes

    private List<byte[]> packList;
    private byte[] header;

    public TransmitPayloadWrapper(ByteEncoder encoder, byte[] data) {
        packList = createPacks(encoder, data);
        header = createHeader(encoder, packList);
    }

    public TransmitPayloadWrapper(byte[] data) {
        this(new NoneByteEncoder(), data);
    }

    private List<byte[]> createPacks(ByteEncoder encoder, byte[] data) {
        List<byte[]> packs = new ArrayList<>();
        int headIndex = 0;

        while (headIndex < data.length) {
            int nextHeadIndex = Math.min(headIndex + PACK_SIZE, data.length);

            // fixme отправлятье последний pack размером PACK_SIZE
            byte[] pack = Arrays.copyOfRange(data, headIndex, nextHeadIndex);
            byte[] encoded = encoder.encode(pack);
            packs.add(encoded);

            headIndex = nextHeadIndex;
        }
        return packs;
    }

    private byte[] createHeader(ByteEncoder encoder, List<byte[]> packList) {
        if (packList.size() > MAX_DATA_PACKS_COUNT) {
            throw new IllegalArgumentException(String.format("Data size must be less than %d. But it's %d!",
                PACK_SIZE, packList.size()));
        }

        // в первые два байта записываем кол-во пакетов для передачи
        byte[] header = ByteBuffer
            .allocate(PACK_SIZE)
            .putShort((short) packList.size())
            .array();

        return encoder.encode(header);
    }

    public final byte[] getHeader() {
        return header;
    }

    public final List<byte[]> getPackList() {
        return packList;
    }

    private static final class NoneByteEncoder implements ByteEncoder {

        @Override
        public byte[] encode(byte[] bytes) {
            return bytes;
        }

        @Override
        public byte[] decode(byte[] bytes) {
            return bytes;
        }
    }

}
