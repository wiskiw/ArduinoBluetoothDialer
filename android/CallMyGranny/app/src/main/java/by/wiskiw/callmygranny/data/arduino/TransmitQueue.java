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

            // для корректного тестирования
            if (postDelay > 0) {
                delayHandler.postDelayed(new SendNextDelayedRunnable(), postDelay);
            } else {
                trySendNext();
            }
        }

        @Override
        public void onFailed() {
            sendListener.onFailed();
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
