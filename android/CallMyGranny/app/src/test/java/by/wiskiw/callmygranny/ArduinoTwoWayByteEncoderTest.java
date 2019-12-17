package by.wiskiw.callmygranny;

import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

import by.wiskiw.callmygranny.data.arduino.ArduinoTwoWayByteEncoder;

/**
 * Unit test for {@link ArduinoTwoWayByteEncoder}
 *
 * @author Andrey Yablonsky on 14.12.2019
 */
public class ArduinoTwoWayByteEncoderTest {

    private final ArduinoTwoWayByteEncoder twoWayEncoder = new ArduinoTwoWayByteEncoder();

    ///////////////////////////////////
    // ENCODE TESTS
    ///////////////////////////////////
    @Test
    public void testEncodeNoZeroBytesPartialSegment() {
        byte[] sourceBytes = new byte[] {1, 2};
        byte headerByte = (byte) 0b11100000;
        byte[] expectedBytes = new byte[] {headerByte, 1, 2};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(String.format("\nExpected: %s\nActual  : %s",
            Arrays.toString(expectedBytes), Arrays.toString(encodedBytes)), expectedBytes, encodedBytes);
    }

    @Test
    public void testEncodeNoZeroBytesFullSegment() {
        byte[] sourceBytes = new byte[] {1, 2, 3, 4, 5, 6, 7};
        byte headerByte = (byte) 0b11111111;
        byte[] expectedBytes = new byte[] {headerByte, 1, 2, 3, 4, 5, 6, 7};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(String.format("\nExpected: %s\nActual  : %s",
            Arrays.toString(expectedBytes), Arrays.toString(encodedBytes)), expectedBytes, encodedBytes);
    }

    @Test
    public void testEncodeZeroBytesPartialSegment() {
        byte[] sourceBytes = new byte[] {1, 0, 3, 4, 0};
        byte headerByte = (byte) 0b11011000;
        byte[] expectedBytes = new byte[] {headerByte, 1, 0, 3, 4, 0};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(getErrorString(expectedBytes, encodedBytes), expectedBytes, encodedBytes);
    }

    @Test
    public void testEncodeZeroBytesFullSegment() {
        byte[] sourceBytes = new byte[] {1, 0, 3, 4, 5, 0, 7};
        byte headerByte = (byte) 0b11011101;
        byte[] expectedBytes = new byte[] {headerByte, 1, 0, 3, 4, 5, 0, 7};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(getErrorString(expectedBytes, encodedBytes), expectedBytes, encodedBytes);
    }

    @Test
    public void testEncodeNoZeroBytesFewSegment() {
        byte[] sourceBytes = new byte[] {1, 1, 3, 4, 5, 1, 7, 1, 1, 1};
        byte firstHeaderByte = (byte) 0b11111111;
        byte secondHeaderByte = (byte) 0b11110000;
        byte[] expectedBytes = new byte[] {firstHeaderByte, 1, 1, 3, 4, 5, 1, 7, secondHeaderByte, 1, 1, 1};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(getErrorString(expectedBytes, encodedBytes), expectedBytes, encodedBytes);
    }

    @Test
    public void testEncodeZeroBytesFewSegment() {
        byte[] sourceBytes = new byte[] {1, 0, 3, 4, 5, 0, 7, 0, 1, 1};
        byte firstHeaderByte = (byte) 0b11011101;
        byte secondHeaderByte = (byte) 0b10110000;
        byte[] expectedBytes = new byte[] {firstHeaderByte, 1, 0, 3, 4, 5, 0, 7, secondHeaderByte, 0, 1, 1};

        byte[] encodedBytes = twoWayEncoder.encode(sourceBytes);

        assertArrayEquals(getErrorString(expectedBytes, encodedBytes), expectedBytes, encodedBytes);
    }


    ///////////////////////////////////
    // ENCODE-DECODE TESTS
    ///////////////////////////////////
    @Test
    public void testDecodeNoZeroBytesPartialSegment() {
        byte headerByte = (byte) 0b11100000;
        byte[] encodedBytes = new byte[] {headerByte, 1, 2};

        byte[] expectedBytes = new byte[] {1, 2, 0, 0, 0, 0, 0};
        byte[] decodedBytes = twoWayEncoder.decode(encodedBytes);

        assertArrayEquals(getErrorString(expectedBytes, decodedBytes), expectedBytes, decodedBytes);
    }

    @Test
    public void testDecodeZeroBytesFullSegment() {
        byte headerByte = (byte) 0b11111001;
        byte[] encodedBytes = new byte[] {headerByte, 1, 2, 3, 4, 7};

        byte[] expectedBytes = new byte[] {1, 2, 3, 4, 0, 0, 7};
        byte[] decodedBytes = twoWayEncoder.decode(encodedBytes);

        assertArrayEquals(getErrorString(expectedBytes, decodedBytes), expectedBytes, decodedBytes);
    }

    @Test
    public void testDecodeZeroBytesFewSegment() {
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