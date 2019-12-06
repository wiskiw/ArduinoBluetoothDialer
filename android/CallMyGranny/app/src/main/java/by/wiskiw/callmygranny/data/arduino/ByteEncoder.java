package by.wiskiw.callmygranny.data.arduino;

/**
 * @author Andrey Yablonsky on 06.12.2019
 */
public interface ByteEncoder {

    /**
     * Кодирует данные с сохранением длинны
     */
    byte[] encode(byte[] bytes);

}
