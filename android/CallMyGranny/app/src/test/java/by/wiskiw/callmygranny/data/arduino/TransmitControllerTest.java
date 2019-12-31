package by.wiskiw.callmygranny.data.arduino;

import org.junit.Test;
import org.mockito.InOrder;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import by.wiskiw.callmygranny.TestUtils;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.FailedBoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.MirrorBoardCommunicator;

/**
 * Tests for {@link TransmitController}
 *
 * @author Andrey Yablonsky on 26.12.2019
 */
public class TransmitControllerTest {

    @Test
    public void simpleSuccessSendTest() {
        TransmitController transmitController = new TransmitControllerFactory()
            .setBoardCommunicator(new MirrorBoardCommunicator())
            .setSendDelayEnabled(false)
            .create();

        byte[] payload = TestUtils.generateStubBytes(TransmitController.PACK_SIZE_BYTE * 3);
        TransmitController.Listener mockSendListener = mock(TransmitController.Listener.class);
        transmitController.send(payload, mockSendListener);

        verify(mockSendListener, timeout(30).atLeastOnce()).onSuccess();
        verify(mockSendListener, never()).onFailed();
    }

    @Test
    public void simpleFailedSendTest() {
        TransmitController transmitController = new TransmitControllerFactory()
            .setBoardCommunicator(new FailedBoardCommunicator())
            .setSendDelayEnabled(false)
            .create();

        byte[] payload = TestUtils.generateStubBytes(TransmitController.PACK_SIZE_BYTE * 4);
        TransmitController.Listener mockSendListener = mock(TransmitController.Listener.class);
        transmitController.send(payload, mockSendListener);

        verify(mockSendListener, never()).onSuccess();
        verify(mockSendListener, atLeastOnce()).onFailed();
    }


    @Test
    public void progressedSuccessSendTest() {
        TransmitController transmitController = new TransmitControllerFactory()
            .setBoardCommunicator(new MirrorBoardCommunicator())
            .setSendDelayEnabled(false)
            .create();

        byte[] payload = TestUtils.generateStubBytes(TransmitController.PACK_SIZE_BYTE * 3);
        TransmitController.Listener mockSendListener = mock(TransmitController.Listener.class);
        transmitController.send(payload, mockSendListener);

        InOrder listenerInOrderWrapper = inOrder(mockSendListener);
        listenerInOrderWrapper.verify(mockSendListener).onProgressChanged(anyInt(), eq(0));
        listenerInOrderWrapper.verify(mockSendListener).onProgressChanged(anyInt(), eq(1));
        listenerInOrderWrapper.verify(mockSendListener).onProgressChanged(anyInt(), eq(3));
        listenerInOrderWrapper.verify(mockSendListener, timeout(30).atLeastOnce()).onSuccess();

        verify(mockSendListener, never()).onFailed();
    }

}