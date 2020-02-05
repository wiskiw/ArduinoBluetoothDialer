package by.wiskiw.callmygranny.data.arduino.boardcommunicator;

import java.util.ArrayList;
import java.util.List;

import by.wiskiw.callmygranny.ArrayUtils;

/**
 * Реализация {@link BoardCommunicator} возвращающая все отправленные данные в {@link PayloadListener#onPayloadReceived(byte[])}
 * после вызова {@link #invokeOnPayloadReceived()}.
 * <p>
 *     Позволяет "обрезать" HEADER из получаемых в onPayloadReceived() байт используя {@link #skipHeaderBytes(int)}.
 * </p>
 *
 * @author Andrey Yablonsky on 30.12.2019
 */
public class MirrorBoardCommunicator implements BoardCommunicator {

    private final List<Byte> receiveBuffer = new ArrayList<>();

    private PayloadListener payloadListener;
    private int headerSize = 0;
    private int skippedBytesForHeader = 0;

    public void skipHeaderBytes(int headerSize) {
        this.headerSize = headerSize;
    }

    @Override
    public void setPayloadListener(PayloadListener payloadListener) {
        this.payloadListener = payloadListener;
    }

    @Override
    public void send(byte[] data, SendListener sendListener) {
        for (byte dataByte : data) {
            if (skippedBytesForHeader < headerSize) {
                skippedBytesForHeader++;
                continue;
            }
            receiveBuffer.add(dataByte);
        }

        sendListener.onSuccess();
    }

    public void invokeOnPayloadReceived() {
        byte[] payload = ArrayUtils.byteListToArray(receiveBuffer);
        if (payloadListener != null) {
            payloadListener.onPayloadReceived(payload);
        }

        receiveBuffer.clear();
        skippedBytesForHeader = 0;
    }

}