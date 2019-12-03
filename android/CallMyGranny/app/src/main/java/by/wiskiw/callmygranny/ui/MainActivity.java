package by.wiskiw.callmygranny.ui;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import by.wiskiw.callmygranny.AndroidApp;
import by.wiskiw.callmygranny.Logger;
import by.wiskiw.callmygranny.R;
import by.wiskiw.callmygranny.data.ContactsSerializer;
import by.wiskiw.callmygranny.data.bluetooth.ARDBluetoothTransmitter;
import by.wiskiw.callmygranny.model.ARDContact;

public class MainActivity extends AppCompatActivity {

    private static final String GRANNY_PHONE_BLUETOOTH_MAC = "FC:58:FA:BA:02:7D";

    private AndroidApp app = AndroidApp.getInstance();

    private ARDBluetoothTransmitter transmitter = app.getARDBluetoothTransmitter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connect = findViewById(R.id.connect);
        connect.setOnClickListener(v -> connect());

        // todo
        Button contacts = findViewById(R.id.contacts);

        Button sync = findViewById(R.id.sync);
        sync.setOnClickListener(v -> syncData());
    }

    private void connect() {
        transmitter.connect(GRANNY_PHONE_BLUETOOTH_MAC);
    }

    private void syncData() {
        List<ARDContact> contacts = app.getContactsStorage().readContacts();
        byte[] bytes = new ContactsSerializer().serialise(contacts);

        Logger.log(getClass(), "size: " + bytes.length);
        Logger.log(getClass(), "bytes: " + Arrays.toString(bytes));
        //transmitter.send(bytes);
    }
}
