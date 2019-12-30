package by.wiskiw.callmygranny.data.arduino;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.MirrorBoardCommunicator;

/**
 * @author Andrey Yablonsky on 30.12.2019
 */
public class TransmitQueueTest {

    private TransmitQueue communicatorService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        communicatorService = new TransmitQueue(new MirrorBoardCommunicator());
    }

    @Test
    public void sendListenerTest() {
        byte[] payload1 = "Hello".getBytes();
        byte[] payload2 = "World".getBytes();
        byte[] payload3 = "!".getBytes();

        BoardCommunicator.SendListener mockSendListener = mock(BoardCommunicator.SendListener.class);

        communicatorService.send(payload1, mockSendListener);
        communicatorService.send(payload2, mockSendListener);
        communicatorService.send(payload3, mockSendListener);

        verify(mockSendListener, timeout(100).times(3)).onSuccess();
        verify(mockSendListener, never()).onFailed();
    }

    @Test
    public void payloadReceivedListenerTest() {
        byte[] payload1 = "Hello".getBytes();
        byte[] payload2 = "World".getBytes();
        byte[] payload3 = "!".getBytes();

        BoardCommunicator.PayloadListener mockPayloadListener = mock(BoardCommunicator.PayloadListener.class);
        communicatorService.addPayloadListener(mockPayloadListener);

        BoardCommunicator.SendListener mockSendListener = mock(BoardCommunicator.SendListener.class);
        communicatorService.send(payload1, mockSendListener);
        communicatorService.send(payload2, mockSendListener);
        communicatorService.send(payload3, mockSendListener);

        InOrder listenerInOrderWrapper = inOrder(mockPayloadListener);
        listenerInOrderWrapper.verify(mockPayloadListener).onPayloadReceived(payload1);
        listenerInOrderWrapper.verify(mockPayloadListener).onPayloadReceived(payload2);
        listenerInOrderWrapper.verify(mockPayloadListener).onPayloadReceived(payload3);
    }

}