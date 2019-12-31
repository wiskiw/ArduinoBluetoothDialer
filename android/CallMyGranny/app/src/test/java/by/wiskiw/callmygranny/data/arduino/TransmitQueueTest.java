package by.wiskiw.callmygranny.data.arduino;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.FailedBoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.MirrorBoardCommunicator;

/**
 * @author Andrey Yablonsky on 30.12.2019
 */
public class TransmitQueueTest {

    private static final long POST_SEND_DELAY = 0;

    private TransmitQueue transmitQueue;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        transmitQueue = new TransmitQueue(new MirrorBoardCommunicator());
    }

    @Test
    public void sendListenerTest() {
        byte[] payload1 = "Hello".getBytes();
        byte[] payload2 = "World".getBytes();
        byte[] payload3 = "!".getBytes();

        BoardCommunicator.SendListener mockSendListener = mock(BoardCommunicator.SendListener.class);

        transmitQueue.send(payload1, POST_SEND_DELAY, mockSendListener);
        transmitQueue.send(payload2, POST_SEND_DELAY, mockSendListener);
        transmitQueue.send(payload3, POST_SEND_DELAY, mockSendListener);

        verify(mockSendListener, timeout(100).times(3)).onSuccess();
        verify(mockSendListener, never()).onFailed();
    }

    @Test
    public void payloadReceivedListenerTest() {
        byte[] payload1 = "Hello".getBytes();
        byte[] payload2 = "World".getBytes();
        byte[] payload3 = "!".getBytes();

        BoardCommunicator.PayloadListener mockPayloadListener = mock(BoardCommunicator.PayloadListener.class);
        transmitQueue.addPayloadListener(mockPayloadListener);

        BoardCommunicator.SendListener mockSendListener = mock(BoardCommunicator.SendListener.class);
        transmitQueue.send(payload1, POST_SEND_DELAY, mockSendListener);
        transmitQueue.send(payload2, POST_SEND_DELAY, mockSendListener);
        transmitQueue.send(payload3, POST_SEND_DELAY, mockSendListener);

        InOrder listenerInOrderWrapper = inOrder(mockPayloadListener);
        listenerInOrderWrapper.verify(mockPayloadListener).onPayloadReceived(payload1);
        listenerInOrderWrapper.verify(mockPayloadListener).onPayloadReceived(payload2);
        listenerInOrderWrapper.verify(mockPayloadListener).onPayloadReceived(payload3);
    }

    @Test
    public void failedSendTest() {
        byte[] payload1 = "Hello".getBytes();
        byte[] payload2 = "World".getBytes();
        byte[] payload3 = "!".getBytes();

        TransmitQueue failedTransmitQueue = new TransmitQueue(new FailedBoardCommunicator());

        BoardCommunicator.PayloadListener mockPayloadListener = mock(BoardCommunicator.PayloadListener.class);
        failedTransmitQueue.addPayloadListener(mockPayloadListener);

        BoardCommunicator.SendListener mockSendListener = mock(BoardCommunicator.SendListener.class);
        failedTransmitQueue.send(payload1, POST_SEND_DELAY, mockSendListener);
        failedTransmitQueue.send(payload2, POST_SEND_DELAY, mockSendListener);
        failedTransmitQueue.send(payload3, POST_SEND_DELAY, mockSendListener);

        verify(mockSendListener, times(3)).onFailed();
        verify(mockSendListener, never()).onSuccess();
    }

}