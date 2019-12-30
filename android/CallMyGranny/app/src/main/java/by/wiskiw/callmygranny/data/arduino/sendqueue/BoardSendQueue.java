package by.wiskiw.callmygranny.data.arduino.sendqueue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;

/**
 * Реализует последовательную отправку данных через {@link BoardCommunicator}.
 * Очередь отправки будет прервана, если хотя бы один из запросов завершится ошибкой.
 *
 * todo: добавить методы проверки/изменения статуса
 *
 * @author Andrey Yablonsky on 26.12.2019
 */
public class BoardSendQueue {

    private TransmissionStatus status;

    private BoardCommunicator boardCommunicator;

    private List<PayloadMeta> payloadMetaQueue = new ArrayList<>();
    private Iterator<PayloadMeta> payloadMetaIterator;

    public BoardSendQueue(BoardCommunicator boardCommunicator) {
        this.boardCommunicator = boardCommunicator;
    }

    public BoardSendQueue add(byte[] payload, BoardCommunicator.SendListener listener) {
        PayloadMeta meta = new PayloadMeta(payload, listener);
        payloadMetaQueue.add(meta);
        return this;
    }

    public void clear() {
        // todo
        throw new IllegalStateException("Not implemented");
    }

    /**
     * Отправляет все добавленные в очередь запросы
     */
    public void sendAll() {
        if (TransmissionStatus.TRANSITING.equals(status)) {
            throw new IllegalStateException("Transmission in progress! Wait for the ending of previews transmission.");
        }

        status = TransmissionStatus.TRANSITING;
        payloadMetaIterator = payloadMetaQueue.iterator();
        sendNext();
    }

    /**
     * Отправляет первый запрос из очереди
     */
    public void sendNext() {
        if (payloadMetaIterator.hasNext()) {
            transmitNext(payloadMetaIterator.next());

        } else {
            onTransitionFinished(false);
        }
    }

    private void transmitNext(PayloadMeta transmitAction) {
        boardCommunicator.send(transmitAction.getPayload(), new BoardCommunicatorSendListener(transmitAction));
    }

    private void onTransitionFinished(boolean withError) {
        status = TransmissionStatus.AVAILABLE;
    }

    private final class BoardCommunicatorSendListener implements BoardCommunicator.SendListener {

        private final PayloadMeta payloadMeta;

        private BoardCommunicatorSendListener(PayloadMeta payloadMeta) {
            this.payloadMeta = payloadMeta;
        }

        @Override
        public void onSuccess() {
            payloadMeta.getListener().onSuccess();
            sendNext();
        }

        @Override
        public void onFailed() {
            payloadMeta.getListener().onFailed();
            //sendNext();
            onTransitionFinished(true);
        }
    }

    private enum TransmissionStatus {
        TRANSITING,
        AVAILABLE
    }

}
