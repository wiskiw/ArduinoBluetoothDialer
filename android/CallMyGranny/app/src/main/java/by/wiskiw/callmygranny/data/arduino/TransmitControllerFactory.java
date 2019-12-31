package by.wiskiw.callmygranny.data.arduino;

import androidx.annotation.NonNull;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteEncoder;

/**
 * Фабрика создания {@link TransmitController}
 *
 * @author Andrey Yablonsky on 31.12.2019
 */
public class TransmitControllerFactory {

    private BoardCommunicator boardCommunicator;

    private ByteEncoder encoder = new NonByteEncoder();

    private boolean isSendDelayEnabled = true;

    public TransmitControllerFactory setBoardCommunicator(@NonNull BoardCommunicator boardCommunicator) {
        this.boardCommunicator = boardCommunicator;
        return this;
    }

    public TransmitControllerFactory setEncoder(@NonNull ByteEncoder encoder) {
        this.encoder = encoder;
        return this;
    }

    public TransmitControllerFactory setSendDelayEnabled(boolean sendDelayEnabled) {
        isSendDelayEnabled = sendDelayEnabled;
        return this;
    }

    public TransmitController create() {
        TransmitQueue transmitQueue = new TransmitQueue(boardCommunicator);
        TransmitController controller = new TransmitController(transmitQueue, encoder);
        controller.setSendDelayEnabled(isSendDelayEnabled);
        return controller;
    }

    private static final class NonByteEncoder implements ByteEncoder {

        @Override
        public byte[] encode(byte[] bytes) {
            return bytes;
        }
    }
}
