package by.wiskiw.callmygranny.data.bluetooth.service;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * @author Andrey Yablonsky on 05.12.2019
 */
public interface BluetoothService {

    void setReceiveListener(ReceiveListener receiveListener);

    void setStatusListener(StatusListener statusListener);

    void setup(Context context);

    void connect(String mac);

    void send(byte[] data);

    void disconnect();

    void startScanning(ScannerListener scannerListener);

    void stopScanning();

    Status getStatus();

    interface ScannerListener {

        void onDeviceFound(BluetoothDevice device, int rssi);

    }

    interface ReceiveListener {

        void onDataReceived(byte[] data);

    }

    interface StatusListener {

        void onStatusChanged(Status status);

    }

    enum Status {

        CONNECTING,
        CONNECTED,
        DISCONNECTED

    }

}
