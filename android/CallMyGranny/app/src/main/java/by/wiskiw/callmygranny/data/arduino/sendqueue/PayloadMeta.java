package by.wiskiw.callmygranny.data.arduino.sendqueue;

import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;

/**
 * Мета информация для отправки данных в {@link BoardSendQueue}
 *
 * @author Andrey Yablonsky on 27.12.2019
 */
class PayloadMeta {

    private final byte[] payload;
    private final BoardCommunicator.SendListener listener;

    PayloadMeta(byte[] payload, BoardCommunicator.SendListener listener) {
        this.payload = payload;
        this.listener = listener;
    }

    byte[] getPayload() {
        return payload;
    }

    BoardCommunicator.SendListener getListener() {
        return listener;
    }
}
