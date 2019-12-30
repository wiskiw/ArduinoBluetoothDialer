package by.wiskiw.callmygranny.data.arduino;

import java.util.LinkedList;
import java.util.Queue;

import android.os.Handler;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;

/**
 * Последовательно отправляет запросы через {@link BoardCommunicator}. Гарантирует сохранение очередности отправки запросов.
 *
 * @author Andrey Yablonsky on 30.12.2019
 */
public class TransmitQueue {

    // ms
    public static final int POST_DELAY_SMALL = 80;
    public static final int POST_DELAY_REGULAR = 100;
    public static final int POST_DELAY_LARGE = 130;
    private static final int DEFAULT_POST_DELAY = POST_DELAY_SMALL;

    private final Handler delayHandler = new Handler();
    private final Queue<RequestMeta> requestQueue = new LinkedList<>();
    private final BoardCommunicator boardCommunicator;

    public TransmitQueue(BoardCommunicator boardCommunicator) {
        this.boardCommunicator = boardCommunicator;
    }

    public void addPayloadListener(BoardCommunicator.PayloadListener payloadListener) {
        boardCommunicator.addPayloadListener(payloadListener);
    }

    public void removePayloadListener(BoardCommunicator.PayloadListener payloadListener) {
        boardCommunicator.removePayloadListener(payloadListener);
    }

    /**
     * @param data
     * @param postDelay - задержка перед отправкой последующего запроса
     * @param sendListener
     */
    public void send(byte[] data, long postDelay, BoardCommunicator.SendListener sendListener) {
        RequestMeta meta = new RequestMeta(data, sendListener);
        meta.setPostDalay(postDelay);
        requestQueue.add(meta);

        trySendNext();
    }

    public void send(byte[] data, BoardCommunicator.SendListener sendListener) {
        send(data, DEFAULT_POST_DELAY, sendListener);
    }

    private void trySendNext() {
        if (!requestQueue.isEmpty()) {
            RequestMeta next = requestQueue.poll();
            if (next != null) {
                SendListenerWrapper listenerWrapper = new SendListenerWrapper(next.sendListener, next.postDelay);
                boardCommunicator.send(next.data, listenerWrapper);
            }
        }
    }

    private final class SendListenerWrapper implements BoardCommunicator.SendListener {

        private final BoardCommunicator.SendListener sendListener;
        private final long postDelay;

        private SendListenerWrapper(BoardCommunicator.SendListener sendListener, long postDelay) {
            this.sendListener = sendListener;
            this.postDelay = postDelay;
        }

        @Override
        public void onSuccess() {
            sendListener.onSuccess();
            delayHandler.postDelayed(new SendNextDelayedRunnable(), postDelay);
        }

        @Override
        public void onFailed() {
            sendListener.onFailed();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SendListenerWrapper that = (SendListenerWrapper) o;
            return sendListener.equals(that.sendListener);
        }

        @Override
        public int hashCode() {
            return sendListener.hashCode();
        }
    }

    private final class SendNextDelayedRunnable implements Runnable {

        @Override
        public void run() {
            trySendNext();
        }
    }

    private final class RequestMeta {

        private final byte[] data;
        private final BoardCommunicator.SendListener sendListener;
        private long postDelay = 0L;

        private RequestMeta(byte[] data, BoardCommunicator.SendListener sendListener) {
            this.data = data;
            this.sendListener = sendListener;
        }

        public void setPostDalay(long postDelay) {
            this.postDelay = postDelay;
        }
    }

}
