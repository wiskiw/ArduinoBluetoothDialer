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
import by.wiskiw.callmygranny.data.arduino.TransmitController;
import by.wiskiw.callmygranny.data.bluetooth.service.BluetoothService;
import by.wiskiw.callmygranny.model.ARDContact;

public class MainActivity extends AppCompatActivity {

    private static final String GRANNY_PHONE_BLUETOOTH_MAC = "FC:58:FA:BA:02:7D";

    private AndroidApp app = AndroidApp.getInstance();

    private final ReceiveListener receiveListener = new ReceiveListener();
    private final TransmitterListener transmitterListener = new TransmitterListener();

    private BluetoothService bluetoothService;
    private TransmitController arduinoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothService = app.getBluetoothService();

        arduinoController = app.getArduinoTransmitController();
        arduinoController.addReceiveListener(receiveListener);

        Button connect = findViewById(R.id.connect);
        connect.setOnClickListener(v -> connect());

        // todo
        Button contacts = findViewById(R.id.contacts);

        Button sync = findViewById(R.id.sync);
        sync.setOnClickListener(v -> syncData());
    }

    private void connect() {
        bluetoothService.connect(GRANNY_PHONE_BLUETOOTH_MAC);
    }

    private void syncData() {
        List<ARDContact> contacts = app.getContactsStorage().readContacts();
        byte[] bytes = new ContactsSerializer().serialise(contacts);

        Logger.log(getClass(), "size: " + bytes.length);
        Logger.log(getClass(), "bytes: " + Arrays.toString(bytes));

        arduinoController.send(bytes, transmitterListener);
    }

    private final class ReceiveListener implements TransmitController.ReceiveListener {

        @Override
        public void onReceive(byte[] data) {
            Logger.log(getClass(), "onReceive: " + Arrays.toString(data));
        }
    }


    private final class TransmitterListener implements TransmitController.SendListener {

        @Override
        public void onSuccess() {
            Logger.log(getClass(), "onSuccess");
        }

        @Override
        public void onProgressChanged(int allCount, int transferredCount) {
            Logger.log(getClass(), String.format("onProgressChanged: %d/%d", transferredCount, allCount));
        }

        @Override
        public void onFailed() {
            Logger.log(getClass(), "onFailed");
        }

    }
}
