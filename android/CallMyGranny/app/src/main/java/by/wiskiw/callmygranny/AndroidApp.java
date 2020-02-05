package by.wiskiw.callmygranny;

import android.app.Application;
import by.wiskiw.callmygranny.data.ContactsStorage;
import by.wiskiw.callmygranny.data.arduino.TransmitController;
import by.wiskiw.callmygranny.data.arduino.TransmitControllerFactory;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BK8000LCommunicator;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.CommunicatorQueueWrapper;
import by.wiskiw.callmygranny.data.arduino.encoding.nonzero.NonZeroTwoWayByteEncoder;
import by.wiskiw.callmygranny.data.bluetooth.service.Blue2SerialBluetoothService;
import by.wiskiw.callmygranny.data.bluetooth.service.BluetoothService;
import io.paperdb.Book;
import io.paperdb.Paper;

/**
 * @author Andrey Yablonsky on 03.12.2019
 */
public class AndroidApp extends Application {

    private static AndroidApp appInstance;

    private ContactsStorage contactsStorage;

    private BluetoothService bluetoothService;

    private TransmitController arduinoTransmitController;

    public static AndroidApp getInstance() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;

        Paper.init(this);
        Book mainBook = Paper.book();
        contactsStorage = new ContactsStorage(mainBook);

        bluetoothService = new Blue2SerialBluetoothService();
        bluetoothService.setup(this);

        initArduino();
    }

    private void initArduino() {
        NonZeroTwoWayByteEncoder twoWayEncoder = new NonZeroTwoWayByteEncoder();

        BK8000LCommunicator bk8000LCommunicator = new BK8000LCommunicator(bluetoothService);
        BoardCommunicator boardCommunicator = new CommunicatorQueueWrapper(bk8000LCommunicator);

        arduinoTransmitController = new TransmitControllerFactory()
            // todo .setHeaderBuilder()
            .setEncoder(twoWayEncoder)
            .setDecoder(twoWayEncoder)
            .setBoardCommunicator(boardCommunicator)
            .create();
    }

    public ContactsStorage getContactsStorage() {
        return contactsStorage;
    }

    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public TransmitController getArduinoTransmitController() {
        return arduinoTransmitController;
    }
}
