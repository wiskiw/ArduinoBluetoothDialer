package by.wiskiw.callmygranny.data.arduino.boardcommunicator;

/**
 * Реализация {@link BoardCommunicator}, вызывающая {@link SendListener#onFailed()}
 * при попытке {@link #send(byte[], SendListener)}.
 *
 * @author Andrey Yablonsky on 30.12.2019
 */
public class FailedBoardCommunicator implements BoardCommunicator {


    @Override
    public void addPayloadListener(PayloadListener payloadListener) {
        // not required
    }

    @Override
    public void removePayloadListener(PayloadListener payloadListener) {
        // not required
    }

    @Override
    public void send(byte[] data, SendListener sendListener) {
        sendListener.onFailed();
    }
}