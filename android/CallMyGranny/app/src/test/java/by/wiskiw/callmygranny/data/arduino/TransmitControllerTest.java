package by.wiskiw.callmygranny.data.arduino;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import by.wiskiw.callmygranny.TestUtils;

/**
 * @author Andrey Yablonsky on 26.12.2019
 */
public class TransmitControllerTest {

    @Test
    public void testDivideForOneNotFullPacks() {
        int fullPackCount = 0;
        int packSize = 56;
        int lastPackSize = 10;
        testDivideForPackages(fullPackCount, packSize, lastPackSize);
    }

    @Test
    public void testDivideForTwoFullPacks() {
        int fullPackCount = 2;
        int packSize = 56;
        int lastPackSize = 0;
        testDivideForPackages(fullPackCount, packSize, lastPackSize);
    }

    @Test
    public void testDivideForTwoNotFullPacks() {
        int fullPackCount = 2;
        int packSize = 56;
        int lastPackSize = 20;
        testDivideForPackages(fullPackCount, packSize, lastPackSize);
    }

    private void testDivideForPackages(int fullPackCount, int packSize, int lastPackSize) {
        Method divideForPackagesMethod = TestUtils.findMethod(TransmitController.class, "divideForPackages",
            int.class, byte[].class);

        assertNotNull(divideForPackagesMethod);

        try {
            byte[] twoNotFullPacksData = generateStubBytes(packSize * fullPackCount + lastPackSize);
            List<byte[]> packs = (List<byte[]>) divideForPackagesMethod.invoke(packSize, twoNotFullPacksData);

            assertNotNull(packs);
            assertEquals(fullPackCount, packs.size());

            if (fullPackCount > 0 || lastPackSize > 0) {
                assertEquals(packs.get(0), Arrays.copyOfRange(twoNotFullPacksData, 0, packSize));
            }

            if (lastPackSize > 0) {
                byte[] lastPack = packs.get(packs.size() - 1);
                byte[] expectedLastPack = new byte[packSize];

                System.arraycopy(twoNotFullPacksData, twoNotFullPacksData.length - lastPackSize,
                    expectedLastPack, 0, lastPackSize);

                assertEquals(lastPack, expectedLastPack);
            }
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            // not important for testing
        }
    }

    private static byte[] generateStubBytes(int count) {
        byte[] bytes = new byte[count];
        new Random().nextBytes(bytes);
        return bytes;
    }

}