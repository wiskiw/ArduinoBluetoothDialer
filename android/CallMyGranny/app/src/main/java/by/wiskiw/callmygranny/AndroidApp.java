package by.wiskiw.callmygranny;

import android.app.Application;
import by.wiskiw.callmygranny.data.bluetooth.ARDBluetoothTransmitter;
import by.wiskiw.callmygranny.data.ContactsStorage;
import io.paperdb.Book;
import io.paperdb.Paper;

/**
 * @author Andrey Yablonsky on 03.12.2019
 */
public class AndroidApp extends Application {

    private static AndroidApp appInstance;

    private ContactsStorage contactsStorage;

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
    }

    public ContactsStorage getContactsStorage() {
        return contactsStorage;
    }


}
