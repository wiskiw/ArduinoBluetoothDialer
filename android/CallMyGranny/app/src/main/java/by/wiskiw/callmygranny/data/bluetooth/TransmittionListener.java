package by.wiskiw.callmygranny.data.bluetooth;

/**
 * @author Andrey Yablonsky on 03.12.2019
 */
public interface TransmittionListener {

    void onProgressChanged(int percent);

    void onStart();

    void onSuccess();

    void onError();

}
