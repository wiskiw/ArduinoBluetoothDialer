package by.wiskiw.callmygranny.data.arduino.encoding;

/**
 * @author Andrey Yablonsky on 06.12.2019
 */
public interface ByteDecoder {

    byte[] decode(byte[] bytes);

}
