package by.wiskiw.callmygranny;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * todo: comments
 * @author Andrey Yablonsky on 03.12.2019
 */
public class ArrayUtils {

    public static byte[] concat(byte[]... arrays) {
        // Determine the length of the result array
        int totalLength = 0;
        for (int i = 0; i < arrays.length; i++) {
            totalLength += arrays[i].length;
        }

        // create the result array
        byte[] result = new byte[totalLength];

        // copy the source arrays into the result array
        int currentIndex = 0;
        for (int i = 0; i < arrays.length; i++) {
            System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
            currentIndex += arrays[i].length;
        }

        return result;
    }

    public static List<byte[]> divideForParts(int partSize, byte[] src) {
        int packsCount = (int) Math.ceil(src.length / (float) partSize);
        List<byte[]> packs = new ArrayList<>(packsCount);

        for (int packIndex = 0; packIndex < packsCount; packIndex++) {
            byte[] pack = new byte[partSize];

            int startIndex = packIndex * partSize;

            boolean isFullPack = startIndex + partSize <= src.length;
            int byteCount = isFullPack
                ? partSize
                : src.length - startIndex;

            System.arraycopy(src, startIndex, pack, 0, byteCount);
            packs.add(pack);
        }
        return packs;
    }

    public static byte[] byteListToArray(List<Byte> list) {
        int count = list.size();
        Byte[] array = list.toArray(new Byte[count]);
        return org.apache.commons.lang3.ArrayUtils.toPrimitive(array);
    }

    public static List<Byte> byteArrayToList(byte[] array) {
        Byte[] byteObjectArray = org.apache.commons.lang3.ArrayUtils.toObject(array);
        return Arrays.asList(byteObjectArray);
    }
}
