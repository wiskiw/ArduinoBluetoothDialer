package by.wiskiw.callmygranny;

import android.util.Log;

/**
 * @author Andrey Yablonsky on 03.12.2019
 */
public class Logger {

    private static final String APP_TAG = "GRANNY";
    private static final int MAX_TAG_LENGTH = 16;

    public static void log(String tag, String message) {
        Log.d(tag, message);
    }

    public static void log(Class<?> tClass, String message) {
        log(createTag(tClass), message);
    }

    private static String createTag(Class<?> tClass) {
        String className = tClass.getCanonicalName() != null ? tClass.getCanonicalName() : "";

        String fullTag = String.format("%s:%s", APP_TAG, className);
        return fullTag.length() > MAX_TAG_LENGTH ? fullTag.substring(0, MAX_TAG_LENGTH) : fullTag;
    }

}
