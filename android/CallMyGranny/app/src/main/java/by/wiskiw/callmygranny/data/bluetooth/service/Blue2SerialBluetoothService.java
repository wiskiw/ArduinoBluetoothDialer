package by.wiskiw.callmygranny.data.bluetooth.service;

import java.util.Arrays;

import com.macroyau.blue2serial.BluetoothSerial;
import com.macroyau.blue2serial.BluetoothSerialListener;

import android.content.Context;
import by.wiskiw.callmygranny.Logger;

public class Blue2SerialBluetoothService implements BluetoothService {

    private final SerailListener serailListener = new SerailListener();
    private BluetoothSerial bluetoothSerial;

    private ReceiveListener receiveListener;
    private StatusListener statusListener;
    private Context context;

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
        this.context = context;
        bluetoothSerial = new BluetoothSerial(context, serailListener);

        // Check Bluetooth availability on the device and set up the Bluetooth adapter
        bluetoothSerial.setup();

        // Open a Bluetooth serial port and get ready to establish a connection
        if (bluetoothSerial.checkBluetooth() && bluetoothSerial.isBluetoothEnabled()) {
            if (!bluetoothSerial.isConnected()) {
                bluetoothSerial.start();
            }
        }
    }

    @Override
    public void connect(String mac) {
        bluetoothSerial.connect(BluetoothSerial.getAdapter(context).getRemoteDevice(mac));
    }

    @Override
    public void send(byte[] data) {
        bluetoothSerial.write(data);
    }

    @Override
    public void disconnect() {
        throw new IllegalStateException("Not implemented!");
    }

    @Override
    public void startScanning(ScannerListener scannerListener) {
        throw new IllegalStateException("Not implemented!");
    }

    @Override
    public void stopScanning() {
        throw new IllegalStateException("Not implemented!");
    }

    @Override
    public Status getStatus() {
        throw new IllegalStateException("Not implemented!");
    }

    private final class SerailListener implements BluetoothSerialListener {

        @Override
        public void onBluetoothNotSupported() {

        }

        @Override
        public void onBluetoothDisabled() {

        }

        @Override
        public void onBluetoothDeviceDisconnected() {

        }

        @Override
        public void onConnectingBluetoothDevice() {

        }

        @Override
        public void onBluetoothDeviceConnected(String name, String address) {

        }

        @Override
        public void onBluetoothSerialRead(String message) {
            Logger.log(Blue2SerialBluetoothService.this.getClass(), "onBluetoothSerialRead: " + message);
            Logger.log(Blue2SerialBluetoothService.this.getClass(), "onBluetoothSerialRead: " + Arrays.toString(message.trim().getBytes()));

            receiveListener.onDataReceived(message.trim().getBytes());
        }

        @Override
        public void onBluetoothSerialWrite(String message) {

        }
    }
}
