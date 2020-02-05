package by.wiskiw.callmygranny.data.arduino.boardcommunicator;

import java.util.LinkedList;
import java.util.Queue;

import android.os.Handler;

/**
 * Обертка для {@link BoardCommunicator} позволяющая добавлять запросы в очередь
 * и отправлять их последовательно {@link BoardCommunicator}.
 * Гарантирует сохранение очередности отправки запросов.
 *
 * @author Andrey Yablonsky on 30.12.2019
 */
public class CommunicatorQueueWrapper implements BoardCommunicator {

    private static final int DEFAULT_POST_DELAY = 80;

    private final Handler delayHandler = new Handler();
    private final Queue<RequestMeta> requestQueue = new LinkedList<>();
    private final BoardCommunicator boardCommunicator;
    private final long postDelay;

    private boolean inProgress = false;


    public CommunicatorQueueWrapper(BoardCommunicator boardCommunicator, long postDelay) {
        this.boardCommunicator = boardCommunicator;
        this.postDelay = postDelay;
    }

    public CommunicatorQueueWrapper(BoardCommunicator boardCommunicator) {
        this(boardCommunicator, DEFAULT_POST_DELAY);
    }

    @Override
    public void setPayloadListener(PayloadListener payloadListener) {
        boardCommunicator.setPayloadListener(payloadListener);
    }

    @Override
    public void send(byte[] data, SendListener sendListener) {
        RequestMeta meta = new RequestMeta(data, postDelay, sendListener);
        requestQueue.add(meta);

        trySendNext();
    }

    private synchronized void trySendNext() {
        if (!inProgress && !requestQueue.isEmpty()) {
            RequestMeta next = requestQueue.poll();
            if (next != null) {
                inProgress = true;
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
            inProgress = false;
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
            inProgress = false;
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
        private final long postDelay;
        private final BoardCommunicator.SendListener sendListener;

        private RequestMeta(byte[] data, long postDelay, BoardCommunicator.SendListener sendListener) {
            this.data = data;
            this.postDelay = postDelay;
            this.sendListener = sendListener;
        }
    }
}
