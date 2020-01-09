package by.wiskiw.callmygranny.data.arduino;

import java.util.List;

import by.wiskiw.callmygranny.ArrayUtils;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteDecoder;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteEncoder;
import by.wiskiw.callmygranny.data.arduino.header.TransmitHeaderBuilder;

/**
 * Реализует отправку массива байт любой длинны через {@link TransmitQueue}
 * <ul>
 *     <li>Кодирует отправляемые байты</li>
 *     <li>Добавляет HEADER для отправляемых байт</li>
 *     <li>Разделяет байты на группы, отправляет по частям</li>
 * </ul>
 *
 * @author Andrey Yablonsky on 26.12.2019
 */
public class TransmitController {

    // ms
    private static final int POST_SEND_DELAY = 80;

    public static final int HEADER_SIZE_BYTE = 4;
    public static final int PACK_SIZE_BYTE = 56;

    private final TransmitQueue transmitQueue;
    private final TransmitHeaderBuilder headerBuilder;
    private final ByteEncoder encoder;
    private final ByteDecoder decoder;

    private boolean isSendDelayEnabled = true;


    public TransmitController(TransmitQueue transmitQueue, TransmitHeaderBuilder headerBuilder,
        ByteEncoder encoder, ByteDecoder decoder) {

        this.transmitQueue = transmitQueue;
        this.headerBuilder = headerBuilder;

        this.encoder = encoder;
        this.decoder = decoder;
    }

    public void setSendDelayEnabled(boolean sendDelayEnabled) {
        isSendDelayEnabled = sendDelayEnabled;
    }

    public void addReceiveListener(ReceiveListener listener) {
        transmitQueue.addPayloadListener(new BoardPayloadListener(listener));
    }

    public void removeReceiveListener(ReceiveListener listener) {
        transmitQueue.removePayloadListener(new BoardPayloadListener(listener));
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

        transmitQueue.send(header, getSendDelay(), new BoardSendListener(sendListener, allPacksCount, 1));

        for (int packIndex = 0; packIndex < packs.size(); packIndex++) {
            byte[] pack = packs.get(packIndex);

            // packIndex + 1 - переход от индекса к номеру
            int packNumber = headerPacksCount + packIndex + 1;
            transmitQueue.send(pack, getSendDelay(), new BoardSendListener(sendListener, allPacksCount, packNumber));
        }
    }

    private long getSendDelay() {
        return isSendDelayEnabled ? POST_SEND_DELAY : 0;
    }

    private final class BoardSendListener implements BoardCommunicator.SendListener {

        private final SendListener sendListener;
        private final int packsCount;
        private final int currentPackNumber;

        private BoardSendListener(SendListener sendListener, int packsCount, int currentPackNumber) {
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

        private final ReceiveListener listener;

        private BoardPayloadListener(ReceiveListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPayloadReceived(byte[] payload) {
            // todo проверить на необходимость объединять несколько маленьких пакетов в большой
            // возможно добавить что-то вроде receive buffer или CatchingQueue
            // тогда реализовывать timeout получения пакетов тут

            byte[] decodedData = decoder.decode(payload);
            listener.onReceive(decodedData);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            BoardPayloadListener that = (BoardPayloadListener) o;

            return listener.equals(that.listener);
        }

        @Override
        public int hashCode() {
            return listener.hashCode();
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
