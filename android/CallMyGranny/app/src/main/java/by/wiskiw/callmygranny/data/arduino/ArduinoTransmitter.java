package by.wiskiw.callmygranny.data.arduino;

import java.util.Arrays;

import android.os.Handler;
import by.wiskiw.callmygranny.Logger;
import by.wiskiw.callmygranny.data.arduino.encoding.NonZeroTwoWayByteEncoder;
import by.wiskiw.callmygranny.data.bluetooth.service.BluetoothService;

/**
 * @deprecated Требует переработки
 *
 * Выполняет передачу byte-s на Arduino через {@link BluetoothService}.
 * <p>
 * Особенности:
 * <ul>
 *     <li>позволяет передавать неограниченную последовательность byte (если не ограничивает PayloadWrapper)</li>
 *     <li>гарантирует получение byte-s на ARDUINO</li>
 *     <li>позволяет отслеживать прогресс передачи через {@link TransmitterListener#onProgress(int, int)}</li>
 *     <li>не способен выполнять передачу параллельно</li>
 * </ul>
 * Гарантирует доставку всех пакетов в случае успешной передачи.
 *
 * todo добавить проверку на подключение BluetoothService с устройством
 * todo добавить обработку разрыва соединения во время передачи
 * todo добавить timeout передачи пакета
 *
 * @author Andrey Yablonsky on 06.12.2019
 */
public class ArduinoTransmitter implements BluetoothService.ReceiveListener {

    private static ArduinoTransmitter instance;

    public static ArduinoTransmitter getInstance(BluetoothService service) {
        if (instance == null) {
            instance = new ArduinoTransmitter(service);
        }
        return instance;
    }

    // Ответ от Arduino об успешном приёме пакета
    private static final byte[] OK_TRANSIT_RESPONSE = "SEND_OK".getBytes();

    // Задержка перед отправкой следующей "порции" байт
    // Нужна для уверенности, что Arduino успеет обработать предыдущую
    private static final long PACK_SEND_DELAY = 10; // ms

    private static final NonZeroTwoWayByteEncoder packEncoder = new NonZeroTwoWayByteEncoder();


    private final BluetoothService service;
    private final Handler delayHandler = new Handler();

    private boolean isTransmitting = false;
    private int nextPackIndex = 0; // индекс следующего пакета байт для отправки
    private int transmitedPacksCounter = 0; // кол-во успешно доставленных пакетов

    //    private TransmitPayloadWrapper payload;
    private TransmitterListener transmitterListener;

    private ArduinoTransmitter(BluetoothService service) {
        this.service = service;
        service.setReceiveListener(this);
    }

    public void send(byte[] data, TransmitterListener transmitterListener) {
        this.transmitterListener = transmitterListener;
        if (isTransmitting) {
            throw new IllegalStateException("Cannot send next bytes until current transmission are not finished!");
        }

//        payload = new TransmitPayloadWrapper(packEncoder, data);

        resetTransmission();
//        sendHeader(payload.getHeader());
    }

    // Отправляет заголовочные байты
    private void sendHeader(byte[] header) {
        Logger.log(getClass(), "sendHeader: " + Arrays.toString(header));
        service.send(header);
    }

    private void sendNextOrFinish() {
//        List<byte[]> packList = payload.getPackList();
//
//        transmitterListener.onProgress(packList.size(), transmitedPacksCounter);
//        transmitedPacksCounter++;
//
//        if (nextPackIndex < packList.size()) {
//            delayHandler.postDelayed(new SendNextPackRunnable(), PACK_SEND_DELAY);
//        } else {
//            changeTransmitStatus(false);
//            transmitterListener.onSuccess();
//        }
    }

    private void sendNextPack() {
//        byte[] pack = payload.getPackList().get(nextPackIndex);
//        service.send(pack);
//        nextPackIndex++;
//        Logger.log(getClass(), "sendNextPack: " + Arrays.toString(pack));
    }

    @Override
    public void onDataReceived(byte[] data) {
        if (isTransmitting) {
            if (isTransmitResponseOk(data)) {
                sendNextOrFinish();
            } else {
                changeTransmitStatus(false);
                transmitterListener.onError();
            }
        }
    }

    private boolean isTransmitResponseOk(byte[] data) {
        return Arrays.equals(data, OK_TRANSIT_RESPONSE);
    }

    private void changeTransmitStatus(boolean isTransmitting) {
        this.isTransmitting = isTransmitting;
    }

    private void resetTransmission() {
        changeTransmitStatus(true);
        nextPackIndex = 0;
        transmitedPacksCounter = 0;
    }

    public boolean isTransmitting() {
        return isTransmitting;
    }

    public void abortTransmitting() {
        if (isTransmitting) {
            resetTransmission();
            transmitterListener.onCanceled();
        }
    }

    private final class SendNextPackRunnable implements Runnable {

        @Override
        public void run() {
            sendNextPack();
        }
    }

    public interface TransmitterListener {

        void onProgress(int all, int processed);

        void onSuccess();

        // todo добавить описание ошибки
        void onError();

        void onCanceled();

    }

}
