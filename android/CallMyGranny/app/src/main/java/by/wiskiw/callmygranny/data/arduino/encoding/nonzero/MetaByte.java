package by.wiskiw.callmygranny.data.arduino.encoding.nonzero;

import by.wiskiw.callmygranny.BitUtils;

/**
 * @author Andrey Yablonsky on 10.01.2020
 */
class MetaByte {

    private static final byte EMPTY_META_BYTE = (byte) 0b10000000;

    // первый бит всегда 1. Добавлять начинаем со второго
    static final int START_BIT_INDEX = 1;

    private static final int BIT_COUNT = 8;

    // кол-во байт, информацию о которых способен содержать MetaByte
    // 1 бит в MetaByte всегда должен быть единицей для избежании потери при передачи
    static final int PAYLOAD_SIZE = BIT_COUNT - START_BIT_INDEX;


    private byte bitMap;
    private int nextBitIndex = START_BIT_INDEX;


    MetaByte(byte bitMap) {
        this.bitMap = bitMap;
    }

    MetaByte() {
        this(EMPTY_META_BYTE);
    }

    byte getBitMap() {
        return bitMap;
    }

    void setNextBit(boolean isUp) {
        if (isFull()) {
            throw new IllegalArgumentException(
                String.format("Meta byte can store only %d bits of info. That one is out of boarder!", PAYLOAD_SIZE));
        }

        if (isUp) {
            bitMap = BitUtils.setBitUp(bitMap, nextBitIndex);
        }
        nextBitIndex++;
    }

    boolean isFull() {
        return nextBitIndex >= BIT_COUNT;
    }

    int getUpBitCount() {
        int count = 0;
        for (int i = START_BIT_INDEX; i < START_BIT_INDEX + PAYLOAD_SIZE; i++) {
            if (BitUtils.isBitUp(bitMap, i)) {
                count++;
            }
        }
        return count;
    }

}
