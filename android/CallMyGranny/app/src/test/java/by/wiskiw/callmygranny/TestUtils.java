package by.wiskiw.callmygranny;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Andrey Yablonsky on 26.12.2019
 */
public class TestUtils {

    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameters) {
        try {
            return clazz.getDeclaredMethod(methodName, parameters);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static byte[] generateStubBytes(int count) {
        byte[] bytes = new byte[count];
        new Random().nextBytes(bytes);
        return bytes;
    }

    public static void logBytesComparison(byte[] arrayA, byte[] arrayB) {
        String arrayAString = Arrays.toString(arrayA);
        String arrayBString = Arrays.toString(arrayB);

        String message = String.format("array A: %s\narray B: %s", arrayAString, arrayBString);
        System.out.println(message);
    }

}
