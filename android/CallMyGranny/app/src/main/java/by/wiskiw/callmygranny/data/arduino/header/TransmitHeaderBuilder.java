package by.wiskiw.callmygranny.data.arduino.header;

import java.util.List;

/**
 * Билдер для создания заголовочных байт запроса.
 * Не кодируется при отправке через {@link by.wiskiw.callmygranny.data.arduino.TransmitController},
 * следовательно может иметь ограничения в зависимости от используемого
 * {@link by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator}.
 *
 * @author Andrey Yablonsky on 04.01.2020
 */
public interface TransmitHeaderBuilder {

    /**
     * Создает хедер
     * @param maxHeaderSize максимальное кол-во байт в хедере
     * @param rawData данные, для которых создается хедер
     * @param packs данные, разбитые на пакеты для отправки
     * @return последовательность байт HEADER
     */
    byte[] build(int maxHeaderSize, byte[] rawData, List<byte[]> packs);

}
