package by.wiskiw.callmygranny;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link BitUtils}
 *
 * @author Andrey Yablonsky on 14.12.2019
 */
public class BitUtilsTest {

    @Test
    public void testBitShiftRight() {
        byte src = (byte) 0b11101100;
        byte exp = (byte) 0b00011101;

        byte result = BitUtils.bitShiftRight(src, (byte) 3);

        //        logSrcExpResBytes(src, exp, result);
        assertEquals(exp, result);
    }

    @Test
    public void testBitShiftLeft() {
        byte src = (byte) 0b11101101;
        byte exp = (byte) 0b10110100;

        byte result = BitUtils.bitShiftLeft(src, (byte) 2);

//        logSrcExpResBytes(src, exp, result);
        assertEquals(exp, result);
    }

    private static void logSrcExpResBytes(byte exp, byte src, byte res) {
        System.out.println(String.format("src: %s", BitUtils.byteToStringBinary(src)));
        System.out.println(String.format("exp: %s", BitUtils.byteToStringBinary(exp)));
        System.out.println(String.format("res: %s", BitUtils.byteToStringBinary(res)));
    }

    @Test
    public void testIsBitUp() {
        byte src1 = (byte) 0b11101100;
        assertFalse(BitUtils.isBitUp(src1, 0));
        assertFalse(BitUtils.isBitUp(src1, 1));
        assertTrue(BitUtils.isBitUp(src1, 2));
        assertTrue(BitUtils.isBitUp(src1, 3));
        assertFalse(BitUtils.isBitUp(src1, 4));
        assertTrue(BitUtils.isBitUp(src1, 5));
        assertTrue(BitUtils.isBitUp(src1, 6));
        assertTrue(BitUtils.isBitUp(src1, 7));

        byte src2 = (byte) 0b11111001;
        assertTrue(BitUtils.isBitUp(src2, 0));
        assertFalse(BitUtils.isBitUp(src2, 1));
        assertFalse(BitUtils.isBitUp(src2, 2));
        assertTrue(BitUtils.isBitUp(src2, 3));
        assertTrue(BitUtils.isBitUp(src2, 4));
        assertTrue(BitUtils.isBitUp(src2, 5));
        assertTrue(BitUtils.isBitUp(src2, 6));
        assertTrue(BitUtils.isBitUp(src2, 7));
    }

    @Test
    public void testSetBitUp() {
        byte src1 = (byte) 0b11100101;
        byte exp1 = (byte) 0b11101101;
        byte result1 = BitUtils.setBitUp(src1, 3);

//        logSrcExpResBytes(src1, exp1, result1);
        assertEquals(exp1, result1);

        byte src2 = (byte) 0b10000000;
        byte exp2 = (byte) 0b11000000;
        byte result2 = BitUtils.setBitUp(src2, 6);

//        System.out.println();
//        logSrcExpResBytes(src2, exp2, result2);
        assertEquals(exp2, result2);
    }

}