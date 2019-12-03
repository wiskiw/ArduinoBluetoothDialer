package by.wiskiw.callmygranny;

import android.app.Application;
import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import by.wiskiw.callmygranny.data.ContactsStorage;
import by.wiskiw.callmygranny.data.bluetooth.ARDBluetoothTransmitter;
import io.paperdb.Book;
import io.paperdb.Paper;

/**
 * @author Andrey Yablonsky on 03.12.2019
 */
public class AndroidApp extends Application {

    private static AndroidApp appInstance;

    private ContactsStorage contactsStorage;

    private ARDBluetoothTransmitter ardBluetoothTransmitter;

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

        initBluetooth();
    }

    private void initBluetooth() {
        BluetoothSPP bluetoothSPP = new BluetoothSPP(this);
        ardBluetoothTransmitter = new ARDBluetoothTransmitter(bluetoothSPP);
    }

    public ContactsStorage getContactsStorage() {
        return contactsStorage;
    }

    public ARDBluetoothTransmitter getARDBluetoothTransmitter() {
        return ardBluetoothTransmitter;
    }
}
