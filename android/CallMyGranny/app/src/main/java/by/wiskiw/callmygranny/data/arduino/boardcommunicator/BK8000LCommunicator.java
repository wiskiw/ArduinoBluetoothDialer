package by.wiskiw.callmygranny.data.arduino.boardcommunicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import by.wiskiw.callmygranny.data.bluetooth.service.BluetoothService;

/**
 * Реализация {@link BoardCommunicator} через Bluetooth для платы BK8000L.
 * <p>Не поддерживает очередное/параллельное использование!
 *
 * todo: добавить обработку timeout при отправке
 * todo: добавить тесты
 *
 * @author Andrey Yablonsky on 26.12.2019
 */
public class BK8000LCommunicator implements BoardCommunicator {

    // 64 - arduino buffer size
    // -4 - APR+
    // -2 - /r/n
    // =58
    private static final int MAX_DATA_SIZE_BYTES = 58;

    private static final byte ZERO_BYTE = 0;


    private final List<BoardCommunicator.PayloadListener> payloadListeners = new ArrayList<>();
    private final BluetoothService bluetoothService;

    private State state = State.LISTENING;
    private SendListener sendListener = new EmptySendListener();

    public BK8000LCommunicator(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
        bluetoothService.setReceiveListener(new BluetoothReceiveListener());
    }

    @Override
    public void addPayloadListener(PayloadListener payloadListener) {
        payloadListeners.add(payloadListener);
    }

    @Override
    public void removePayloadListener(PayloadListener payloadListener) {
        payloadListeners.remove(payloadListener);
    }

    @Override
    public void send(byte[] data, SendListener sendListener) {
        if (State.TRANSFERRING.equals(state)) {
            throw new IllegalStateException("Cannot send bytes while communicator is transmitting!");
        }

        if (data.length > MAX_DATA_SIZE_BYTES) {
            throw new IllegalArgumentException(String.format("Data is too big! Max available size is %d, but %d received.",
                MAX_DATA_SIZE_BYTES, data.length));
        }

        int zeroByteIndex = Arrays.asList(data).indexOf(ZERO_BYTE);
        if (zeroByteIndex >= 0) {
            throw new IllegalArgumentException(String.format("Payload must not contains ZERO-bytes. But it found at index %d.",
                zeroByteIndex));
        }

        this.sendListener = sendListener;
        changeState(State.TRANSFERRING);
        bluetoothService.send(data);
    }

    private void changeState(State state) {
        this.state = state;
    }

    private void onPayloadReceived(byte[] rawPayload) {
        for (BoardCommunicator.PayloadListener listener : payloadListeners) {
            // todo убрать из rawPayload техническую информацию: "APR+"
            byte[] payload = rawPayload;
            listener.onPayloadReceived(payload);
        }
    }

    private void onSendResponseReceived(byte[] response) {
        // todo обработка ответа "AT+OK" или как его там...
        if (response == "OK".getBytes()) {
            sendListener.onSuccess();

        } else {
            // ошибка передачи - BK8000L вернул не OK статус.
            // или BK8000L начала отправлять данные "навстречу"
            sendListener.onFailed();
        }

        sendListener = new EmptySendListener();
        changeState(State.LISTENING);
    }

    private final class BluetoothReceiveListener implements BluetoothService.ReceiveListener {

        @Override
        public void onDataReceived(byte[] data) {
            switch (state) {
                case LISTENING:
                    onPayloadReceived(data);
                    break;
                case TRANSFERRING:
                    onSendResponseReceived(data);
                    break;
            }
        }
    }

    private static final class EmptySendListener implements SendListener {

        @Override
        public void onSuccess() {

        }

        @Override
        public void onFailed() {

        }
    }

    private enum State {

        TRANSFERRING,

        LISTENING,

    }

}
