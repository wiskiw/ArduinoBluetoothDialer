package by.wiskiw.callmygranny.data.arduino;

/**
 * @author Andrey Yablonsky on 06.12.2019
 */
public interface TwoWayByteEncoder {

    byte[] encode(byte[] bytes);

    byte[] decode(byte[] bytes);

}
