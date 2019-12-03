package by.wiskiw.callmygranny.ui;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import by.wiskiw.callmygranny.AndroidApp;
import by.wiskiw.callmygranny.R;
import by.wiskiw.callmygranny.data.ContactsSerializer;
import by.wiskiw.callmygranny.data.bluetooth.ARDBluetoothTransmitter;
import by.wiskiw.callmygranny.model.ARDContact;

public class MainActivity extends AppCompatActivity {

    private AndroidApp app = AndroidApp.getInstance();

    private Button contacts;
    private Button sync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contacts = findViewById(R.id.contacts);
        sync = findViewById(R.id.sync);

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncData();
            }
        });
    }

    private void syncData() {
        List<ARDContact> contacts = app.getContactsStorage().readContacts();
        byte[] bytes = new ContactsSerializer().serialise(contacts);

        // todo откравка данных
        new ARDBluetoothTransmitter().send(bytes);
    }
}
