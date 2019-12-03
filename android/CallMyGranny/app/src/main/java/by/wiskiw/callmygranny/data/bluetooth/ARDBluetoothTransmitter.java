package by.wiskiw.callmygranny.data.bluetooth;

import java.util.Arrays;

import android.content.Intent;
import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import by.wiskiw.callmygranny.Logger;

/**
 * @author Andrey Yablonsky on 03.12.2019
 */
public class ARDBluetoothTransmitter {

    private final BluetoothSPP bluetoothSPP;

    public ARDBluetoothTransmitter(BluetoothSPP bluetoothSPP) {
        this.bluetoothSPP = bluetoothSPP;

        bluetoothSPP.setupService();
        bluetoothSPP.startService(false);

        bluetoothSPP.setBluetoothStateListener(new StateListener());
        bluetoothSPP.setBluetoothConnectionListener(new ConnectionListener());
        bluetoothSPP.setOnDataReceivedListener(new ReceiveListener());
    }

    public void connect(String mac) {
        Intent macIntent = new Intent();
        macIntent.putExtra(BluetoothState.EXTRA_DEVICE_ADDRESS, mac);

        bluetoothSPP.connect(macIntent);
    }

    public void send(byte[] data) {
        bluetoothSPP.send(data, false);
    }

    private final class ReceiveListener implements BluetoothSPP.OnDataReceivedListener {

        @Override
        public void onDataReceived(byte[] data, String message) {
            Logger.log(getClass(), String.format("onDataReceived. data:%s", Arrays.toString(data)));
        }
    }

    private final class ConnectionListener implements BluetoothSPP.BluetoothConnectionListener {

        @Override
        public void onDeviceConnected(String name, String address) {
            Logger.log(getClass(), String.format("onDeviceConnected. name:%s address:%s", name, address));
        }

        @Override
        public void onDeviceDisconnected() {
            Logger.log(getClass(), "onDeviceDisconnected");
        }

        @Override
        public void onDeviceConnectionFailed() {
            Logger.log(getClass(), "onDeviceConnectionFailed");
        }
    }

    private final class StateListener implements BluetoothSPP.BluetoothStateListener {

        @Override
        public void onServiceStateChanged(int state) {
            Logger.log(getClass(), String.format("onServiceStateChanged. state:%s", state));
        }
    }

}
