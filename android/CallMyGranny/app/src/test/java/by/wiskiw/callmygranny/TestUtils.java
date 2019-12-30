package by.wiskiw.callmygranny;

import java.lang.reflect.Method;

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

}
