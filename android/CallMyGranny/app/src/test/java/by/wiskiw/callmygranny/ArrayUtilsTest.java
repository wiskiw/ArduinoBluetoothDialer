package by.wiskiw.callmygranny;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Andrey Yablonsky on 31.12.2019
 */
public class ArrayUtilsTest {

    @Test
    public void testDivideForOneNotFullPacks() {
        int fullPackCount = 0;
        int packSize = 56;
        int lastPackSize = 10;
        testDivideForParts(fullPackCount, packSize, lastPackSize);
    }

    @Test
    public void testDivideForTwoFullPacks() {
        int fullPackCount = 2;
        int packSize = 56;
        int lastPackSize = 0;
        testDivideForParts(fullPackCount, packSize, lastPackSize);
    }

    @Test
    public void testDivideForTwoNotFullPacks() {
        int fullPackCount = 2;
        int packSize = 56;
        int lastPackSize = 20;
        testDivideForParts(fullPackCount, packSize, lastPackSize);
    }

    private void testDivideForParts(int fullPackCount, int packSize, int lastPackSize) {
        byte[] twoNotFullPacksData = TestUtils.generateStubBytes(packSize * fullPackCount + lastPackSize);
        List<byte[]> packs = ArrayUtils.divideForParts(packSize, twoNotFullPacksData);

        assertNotNull(packs);

        if (fullPackCount > 0 || lastPackSize > 0) {
            assertFalse(packs.isEmpty());

            byte[] expected = packs.get(0);
            byte[] actual = Arrays.copyOfRange(twoNotFullPacksData, 0, packSize);
            compareArrays(expected, actual);
        }

        if (lastPackSize > 0) {
            byte[] lastPack = packs.get(packs.size() - 1);
            byte[] expectedLastPack = new byte[packSize];

            System.arraycopy(twoNotFullPacksData, twoNotFullPacksData.length - lastPackSize,
                expectedLastPack, 0, lastPackSize);

            compareArrays(expectedLastPack, lastPack);
        }
    }

    private static void compareArrays(byte[] expected, byte[] actual) {
        String message = String.format("Byte arrays not equals!\nexp: '%s'\nact: '%s'",
            Arrays.toString(expected), Arrays.toString(actual));
        assertArrayEquals(message, expected, actual);
    }
}