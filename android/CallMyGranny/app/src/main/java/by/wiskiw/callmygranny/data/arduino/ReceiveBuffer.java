package by.wiskiw.callmygranny.data.arduino;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import by.wiskiw.callmygranny.ArrayUtils;

/**
 * - таймаут получения
 * - объединение пакетов
 * @author Andrey Yablonsky on 10.01.2020
 */
public class ReceiveBuffer {

    private static final int PACK_RECEIVE_TIMEOUT = 80;

    private final Handler delayHandler = new Handler();
    private final ReceiveTimeoutRunnable receiveTimeoutRunnable = new ReceiveTimeoutRunnable();

    private final List<Byte> buffer = new ArrayList<>();

    private final int packReceiveTimeout;
    private final int expectedPackSize;

    private final PayloadUnloadListener listener;

    public ReceiveBuffer(int packReceiveTimeout, int expectedPackSize, PayloadUnloadListener listener) {
        this.packReceiveTimeout = packReceiveTimeout;
        this.expectedPackSize = expectedPackSize;
        this.listener = listener;
    }

    public ReceiveBuffer(int expectedPackSize, PayloadUnloadListener listener) {
        this(PACK_RECEIVE_TIMEOUT, expectedPackSize, listener);
    }

    private void unloadBuffer() {
        listener.onPayload(ArrayUtils.byteListToArray(buffer));
        buffer.clear();
    }

    public void onPayloadReceived(byte[] payload) {
        restartTimeoutWatcher();

        buffer.addAll(ArrayUtils.byteArrayToList(payload));
        if (buffer.size() >= expectedPackSize) {
            unloadBuffer();
        }
    }

    private void restartTimeoutWatcher() {
        delayHandler.removeCallbacks(receiveTimeoutRunnable);
        delayHandler.postDelayed(receiveTimeoutRunnable, packReceiveTimeout);
    }

    private final class ReceiveTimeoutRunnable implements Runnable {

        @Override
        public void run() {
            if (!buffer.isEmpty()) {
                unloadBuffer();
            }
        }
    }

    public interface PayloadUnloadListener {

        void onPayload(byte[] payload);
    }
}
