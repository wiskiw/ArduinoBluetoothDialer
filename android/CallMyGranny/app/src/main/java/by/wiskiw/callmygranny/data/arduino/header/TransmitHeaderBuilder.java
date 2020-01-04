package by.wiskiw.callmygranny.data.arduino.header;

import java.util.List;

/**
 * todo: comments
 * todo: tests
 * !! не должен содержать 0-байт символов
 *
 * @author Andrey Yablonsky on 04.01.2020
 */
public interface TransmitHeaderBuilder {

    byte[] build(int headerSize, byte[] rawData, List<byte[]> packs);

}
