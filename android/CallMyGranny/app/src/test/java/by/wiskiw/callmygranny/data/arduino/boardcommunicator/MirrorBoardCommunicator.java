package by.wiskiw.callmygranny.data.arduino.boardcommunicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация {@link BoardCommunicator} возвращающая все отправленные данные в {@link PayloadListener#onPayloadReceived(byte[])}.
 *
 * @author Andrey Yablonsky on 30.12.2019
 */
public class MirrorBoardCommunicator implements BoardCommunicator {

    private final List<PayloadListener> payloadListeners = new ArrayList<>();

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
        for (PayloadListener listener : payloadListeners) {
            listener.onPayloadReceived(data);
        }
        sendListener.onSuccess();
    }
}