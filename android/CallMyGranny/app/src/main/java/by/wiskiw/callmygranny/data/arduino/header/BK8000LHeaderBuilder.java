package by.wiskiw.callmygranny.data.arduino.header;

import java.util.List;

/**
 * Реализация для создания хедера для отправки через
 * {@link by.wiskiw.callmygranny.data.arduino.boardcommunicator.BK8000LCommunicator}
 *
 * Не должен содержать 0-байт!!!
 *
 * todo: add tests
 *
 * @author Andrey Yablonsky on 09.01.2020
 */
public class BK8000LHeaderBuilder implements TransmitHeaderBuilder {

    @Override
    public byte[] build(int maxHeaderSize, byte[] rawData, List<byte[]> packs) {
        // todo
        return new byte[maxHeaderSize];
    }
}
