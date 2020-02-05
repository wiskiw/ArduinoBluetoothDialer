package by.wiskiw.callmygranny.data.arduino;

import java.util.ArrayList;
import java.util.List;

import by.wiskiw.callmygranny.ArrayUtils;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteDecoder;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteEncoder;
import by.wiskiw.callmygranny.data.arduino.header.TransmitHeaderBuilder;

/**
 * Реализует отправку массива байт любой длинны через {@link BoardCommunicator}
 * <ul>
 *     <li>Кодирует отправляемые байты</li>
 *     <li>Разделяет байты на чанки, отправляет по частям</li>
 *     <li>Добавляет HEADER для отправляемых чанков байт</li>
 * </ul>
 *
 * @author Andrey Yablonsky on 26.12.2019
 */
public class TransmitController {

    public static final int HEADER_SIZE_BYTE = 4;
    public static final int PACK_SIZE_BYTE = 56;

    private final List<ReceiveListener> listeners = new ArrayList<>();

    private final BoardCommunicator boardCommunicator;
    private final TransmitHeaderBuilder headerBuilder;
    private final ByteEncoder encoder;
    private final ByteDecoder decoder;


    public TransmitController(BoardCommunicator boardCommunicator, TransmitHeaderBuilder headerBuilder,
        ByteEncoder encoder, ByteDecoder decoder) {

        this.boardCommunicator = boardCommunicator;
        this.headerBuilder = headerBuilder;

        this.encoder = encoder;
        this.decoder = decoder;

        boardCommunicator.setPayloadListener(new BoardPayloadListener());
    }

    public void addReceiveListener(ReceiveListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeReceiveListener(ReceiveListener listener) {
        listeners.remove(listener);
    }

    public void send(byte[] data, SendListener sendListener) {
        byte[] encodedBytes = encoder.encode(data);

        List<byte[]> packs = ArrayUtils.divideForParts(PACK_SIZE_BYTE, encodedBytes);
        byte[] header = headerBuilder.build(HEADER_SIZE_BYTE, encodedBytes, packs);

        startTransaction(header, packs, sendListener);
    }

    private void startTransaction(byte[] header, List<byte[]> packs, SendListener sendListener) {
        int headerPacksCount = 1;
        int allPacksCount = headerPacksCount + packs.size();
        sendListener.onProgressChanged(allPacksCount, 0);

        boardCommunicator.send(header, new ProgressedSendListener(sendListener, allPacksCount, 1));

        for (int packIndex = 0; packIndex < packs.size(); packIndex++) {
            byte[] pack = packs.get(packIndex);

            // packIndex + 1 - переход от индекса к номеру
            int packNumber = headerPacksCount + packIndex + 1;
            boardCommunicator.send(pack, new ProgressedSendListener(sendListener, allPacksCount, packNumber));
        }
    }

    private final class ProgressedSendListener implements BoardCommunicator.SendListener {

        private final SendListener sendListener;
        private final int packsCount;
        private final int currentPackNumber;

        private ProgressedSendListener(SendListener sendListener, int packsCount, int currentPackNumber) {
            this.sendListener = sendListener;
            this.packsCount = packsCount;
            this.currentPackNumber = currentPackNumber;
        }

        @Override
        public void onSuccess() {
            sendListener.onProgressChanged(packsCount, currentPackNumber);

            if (packsCount == currentPackNumber) {
                // последний был отправлен успешно
                sendListener.onSuccess();
            }
        }

        @Override
        public void onFailed() {
            // ошибка отправки одного из пакетов
            sendListener.onFailed();
        }
    }

    private final class BoardPayloadListener implements BoardCommunicator.PayloadListener {

        @Override
        public void onPayloadReceived(byte[] payload) {
            // todo проверить на необходимость объединять несколько маленьких пакетов в большой
            // возможно добавить что-то вроде receive buffer или CatchingQueue
            // тогда реализовывать timeout получения пакетов тут

            byte[] decodedData = decoder.decode(payload);

            for (ReceiveListener listener : listeners) {
                listener.onReceive(decodedData);
            }
        }
    }

    public interface SendListener {

        void onSuccess();

        void onProgressChanged(int allCount, int transferredCount);

        void onFailed();

    }

    public interface ReceiveListener {

        void onReceive(byte[] data);

    }

}
