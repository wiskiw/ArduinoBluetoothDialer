package by.wiskiw.callmygranny;

/**
 * Побитовый утилиты
 *
 * @author Andrey Yablonsky on 14.12.2019
 */
public class BitUtils {

    private static final byte LAST_BIT_UP_MASK = (byte) 0b00000001;

    /**
     * Безопасный побитовый сдвиг вправо.
     */
    public static byte bitShiftRight(byte a, int count) {
        // про расширение тут - https://dark-barker.blogspot.com/2012/03/bit-operations-java-pitfalls.html
        int x = a & 0xFF; // побитовое & с маской 11111111 для устранения расширения знака при приведении отрицательных значений
        count %= 8;
        return (byte) (x >>> count);
    }

    /**
     * Безопасный побитовый сдвиг влево.
     */
    public static byte bitShiftLeft(byte a, int count) {
        count %= 8;
        return (byte) (a << count);
    }

    /**
     * Проверяет состояние бита в байте. Нумерация от крайнего левого бита, начиная с 0.
     * @return {@code true}, если значение бита с индексом #bitIndex == 1
     */
    public static boolean isBitUp(byte b, int bitIndex) {
        return (bitShiftRight(b, bitIndex) & LAST_BIT_UP_MASK) > 0;
    }

    /**
     * Устанавливает биту в байте - #b с индексом #bitIndex значение 1. Нумерация от крайнего левого бита, начиная с 0.
     * @return измененный байт
     */
    public static byte setBitUp(byte b, int bitIndex) {
        byte upBitMask = bitShiftLeft(LAST_BIT_UP_MASK, bitIndex);
        return (byte) (b | upBitMask);
    }

    public static String byteToStringBinary(byte value) {
        return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
    }

}
