package by.wiskiw.callmygranny.data.arduino.encoding.nonzero;

import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

import by.wiskiw.callmygranny.TestUtils;
import by.wiskiw.callmygranny.data.arduino.encoding.nonzero.NonZeroTwoWayByteEncoder;

/**
 * Unit test for {@link NonZeroTwoWayByteEncoder}
 *
 * @author Andrey Yablonsky on 14.12.2019
 */
public class NonZeroTwoWayByteEncoderTest {

    private static final byte ZERO_BYTE = 0;

    private final NonZeroTwoWayByteEncoder twoWayEncoder = new NonZeroTwoWayByteEncoder();

    ///////////////////////////////////
    // ENCODE TESTS
    ///////////////////////////////////
    @Test
    public void encodeNoZeroTest() {
        byte[] sourceBytes = TestUtils.generateStubBytes(256);
        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);
        for (byte b : encodedBytes) {
            assertNotEquals(ZERO_BYTE, b);
        }
    }

    @Test
    public void encodeNoZeroBytesPartialSegmentTest() {
        byte[] sourceBytes = new byte[] {1, 2};
        byte metaByte = (byte) 0b11100000;
        byte[] expectedBytes = new byte[] {metaByte, 1, 2};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(String.format("\nExpected: %s\nActual  : %s",
            Arrays.toString(expectedBytes), Arrays.toString(encodedBytes)), expectedBytes, encodedBytes);
    }

    @Test
    public void encodeNoZeroBytesFullSegmentTest() {
        byte[] sourceBytes = new byte[] {1, 2, 3, 4, 5, 6, 7};
        byte metaByte = (byte) 0b11111111;
        byte[] expectedBytes = new byte[] {metaByte, 1, 2, 3, 4, 5, 6, 7};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(String.format("\nExpected: %s\nActual  : %s",
            Arrays.toString(expectedBytes), Arrays.toString(encodedBytes)), expectedBytes, encodedBytes);
    }

    @Test
    public void encodeZeroBytesPartialSegmentTest() {
        byte[] sourceBytes = new byte[] {1, 0, 3, 4, 0};
        byte metaByte = (byte) 0b11011000;
        byte[] expectedBytes = new byte[] {metaByte, 1, 3, 4};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(getErrorString(expectedBytes, encodedBytes), expectedBytes, encodedBytes);
    }

    @Test
    public void encodeZeroBytesFullSegmentTest() {
        byte[] sourceBytes = new byte[] {1, 0, 3, 4, 5, 0, 7};
        byte metaByte = (byte) 0b11011101;
        byte[] expectedBytes = new byte[] {metaByte, 1, 3, 4, 5, 7};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(getErrorString(expectedBytes, encodedBytes), expectedBytes, encodedBytes);
    }

    @Test
    public void encodeNoZeroBytesFewSegmentTest() {
        byte[] sourceBytes = new byte[] {1, 1, 3, 4, 5, 1, 7, 1, 1, 1};
        byte firstHeaderByte = (byte) 0b11111111;
        byte secondHeaderByte = (byte) 0b11110000;
        byte[] expectedBytes = new byte[] {firstHeaderByte, 1, 1, 3, 4, 5, 1, 7, secondHeaderByte, 1, 1, 1};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(getErrorString(expectedBytes, encodedBytes), expectedBytes, encodedBytes);
    }

    @Test
    public void encodeZeroBytesFewSegmentTest() {
        byte[] sourceBytes = new byte[] {1, 0, 3, 4, 5, 0, 7, 0, 1, 1};
        byte firstHeaderByte = (byte) 0b11011101;
        byte secondHeaderByte = (byte) 0b10110000;
        byte[] expectedBytes = new byte[] {firstHeaderByte, 1, 3, 4, 5, 7, secondHeaderByte, 1, 1};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(getErrorString(expectedBytes, encodedBytes), expectedBytes, encodedBytes);
    }

    ///////////////////////////////////
    // DECODE TESTS
    ///////////////////////////////////
    @Test
    public void decodeZeroBytesFullSegmentTest() {
        byte metaByte = (byte) 0b11011101;

        byte[] expectedBytes = new byte[] {1, 0, 3, 4, 5, 0, 1};
        byte[] sourceBytes = new byte[] {metaByte, 1, 3, 4, 5, 1};
        decodeTest(expectedBytes, sourceBytes);
    }

    @Test
    public void decodeZeroBytesFewSegmentTest() {
        byte firstMetaByte = (byte) 0b11011101;
        byte secondMetaByte = (byte) 0b10110000;

        byte[] expectedBytes = new byte[] {1, 0, 3, 4, 5, 0, 1, 0, 13, 41, 0, 0, 0, 0};
        byte[] sourceBytes = new byte[] {firstMetaByte, 1, 3, 4, 5, 1, secondMetaByte, 13, 41};
        decodeTest(expectedBytes, sourceBytes);
    }

    private void decodeTest(byte[] expected, byte[] src) {
        byte[] decodedBytes = twoWayEncoder.decode(src);
        assertArrayEquals(getErrorString(expected, decodedBytes), expected, decodedBytes);
    }


    ///////////////////////////////////
    // ENCODE-DECODE TESTS
    ///////////////////////////////////
    @Test
    public void fullNoZeroBytesPartialSegmentTest() {
        byte metaByte = (byte) 0b11100000;
        byte[] encodedBytes = new byte[] {metaByte, 1, 2};

        byte[] expectedBytes = new byte[] {1, 2, 0, 0, 0, 0, 0};
        byte[] decodedBytes = twoWayEncoder.decode(encodedBytes);

        assertArrayEquals(getErrorString(expectedBytes, decodedBytes), expectedBytes, decodedBytes);
    }

    @Test
    public void fullZeroBytesFullSegmentTest() {
        byte metaByte = (byte) 0b11111001;
        byte[] encodedBytes = new byte[] {metaByte, 1, 2, 3, 4, 7};

        byte[] expectedBytes = new byte[] {1, 2, 3, 4, 0, 0, 7};
        byte[] decodedBytes = twoWayEncoder.decode(encodedBytes);

        assertArrayEquals(getErrorString(expectedBytes, decodedBytes), expectedBytes, decodedBytes);
    }

    @Test
    public void fullZeroBytesFewSegmentTest() {
        byte firstHeaderByte = (byte) 0b11011101;
        byte secondHeaderByte = (byte) 0b10110000;
        byte[] encodedBytes = new byte[] {firstHeaderByte, 1, 3, 4, 5, 7, secondHeaderByte, 1, 1};

        byte[] expectedBytes = new byte[] {1, 0, 3, 4, 5, 0, 7, 0, 1, 1, 0, 0, 0, 0};
        byte[] decodedBytes = twoWayEncoder.decode(encodedBytes);

        assertArrayEquals(getErrorString(expectedBytes, decodedBytes), expectedBytes, decodedBytes);
    }

    private static String getErrorString(byte[] expectedBytes, byte[] actualBytes){
        return String.format("\nExpected: %s\nActual  : %s", Arrays.toString(expectedBytes), Arrays.toString(actualBytes));
    }

}