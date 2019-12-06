package by.wiskiw.callmygranny.data.bluetooth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * {@link BluetoothService} implementation based on {@link com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService}
 *
 * @author Andrey Yablonsky on 05.12.2019
 */
public class DouglasJuniorBluetoothService implements BluetoothService {

    private static final String DEVICE_NAME = "Call My Granny";
    private static final int BUFFER_SIZE = 1024;
    private static final char DELIMITER_CHARACTER = '\n';

    private Status status = Status.DISCONNECTED;

    private ScannerListener scannerListener;
    private ReceiveListener receiveListener;
    private StatusListener statusListener;

    private com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService douglasBluetooth;

    @Override
    public void setReceiveListener(ReceiveListener receiveListener) {
        this.receiveListener = receiveListener;
    }

    @Override
    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public void setup(Context context) {
        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = context;
        config.bluetoothServiceClass = BluetoothClassicService.class;
        config.bufferSize = BUFFER_SIZE;
        config.characterDelimiter = DELIMITER_CHARACTER;
        config.deviceName = DEVICE_NAME;
        config.callListenersInMainThread = true;

        com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService.init(config);
        douglasBluetooth = com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService.getDefaultInstance();

        douglasBluetooth.setOnEventCallback(new EventCallback());
        douglasBluetooth.setOnScanCallback(new ScanCallback());
    }

    @Override
    public void connect(String mac) {
        douglasBluetooth.disconnect();
        douglasBluetooth.getConfiguration().uuid = UUID.fromString(mac);
        douglasBluetooth.connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac));
    }

    @Override
    public void send(byte[] data) {
        douglasBluetooth.write(data);
    }

    @Override
    public void disconnect() {
        douglasBluetooth.disconnect();
    }

    @Override
    public void startScanning(ScannerListener scannerListener) {
        this.scannerListener = scannerListener;
        douglasBluetooth.startScan();
    }

    @Override
    public void stopScanning() {
        scannerListener = null;
        douglasBluetooth.startScan();
    }

    @Override
    public Status getStatus() {
        return status;
    }

    private void notifyStatusUpdated(Status status) {
        if (statusListener != null && !status.equals(this.status)) {
            statusListener.onStatusChanged(status);
        }
        this.status = status;
    }

    private final class EventCallback
        implements com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService.OnBluetoothEventCallback {

        private final Map<BluetoothStatus, Status> STATE_MAPPING = new HashMap<BluetoothStatus, Status>() {{
            put(BluetoothStatus.CONNECTING, Status.CONNECTING);
            put(BluetoothStatus.CONNECTED, Status.CONNECTED);
            put(BluetoothStatus.NONE, Status.DISCONNECTED);
        }};

        @Override
        public void onDataRead(byte[] buffer, int length) {
            if (receiveListener != null) {
                receiveListener.onDataReceived(buffer);
            }
        }

        @Override
        public void onStatusChange(BluetoothStatus status) {
            notifyStatusUpdated(STATE_MAPPING.get(status));
        }

        @Override
        public void onDeviceName(String deviceName) {
            notifyStatusUpdated(Status.DISCONNECTED);
        }

        @Override
        public void onToast(String message) {

        }

        @Override
        public void onDataWrite(byte[] buffer) {

        }
    }

    private final class ScanCallback
        implements com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService.OnBluetoothScanCallback {

        @Override
        public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
            if (scannerListener != null) {
                scannerListener.onDeviceFound(device, rssi);
            }
        }

        @Override
        public void onStartScan() {

        }

        @Override
        public void onStopScan() {

        }
    }
}
