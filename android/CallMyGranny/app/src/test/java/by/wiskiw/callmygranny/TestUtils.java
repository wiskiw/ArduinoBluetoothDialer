package by.wiskiw.callmygranny;

import java.lang.reflect.Method;
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

}
