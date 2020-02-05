package by.wiskiw.callmygranny.data.arduino;

import org.junit.Test;
import org.mockito.InOrder;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.CommunicatorQueueWrapper;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.FailedBoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.MirrorBoardCommunicator;

/**
 * Tests for {@link CommunicatorQueueWrapper}
 *
 * @author Andrey Yablonsky on 30.12.2019
 */
public class CommunicatorQueueWrapperTest {

    private static final int DISABLED_POST_DELAY = 0;

    @Test
    public void sendListenerTest() {
        byte[] payload1 = "Hello".getBytes();
        byte[] payload2 = "World".getBytes();
        byte[] payload3 = "!".getBytes();

        BoardCommunicator boardCommunicator = new CommunicatorQueueWrapper(new MirrorBoardCommunicator(), DISABLED_POST_DELAY);
        BoardCommunicator.SendListener mockSendListener = mock(BoardCommunicator.SendListener.class);

        boardCommunicator.send(payload1, mockSendListener);
        boardCommunicator.send(payload2, mockSendListener);
        boardCommunicator.send(payload3, mockSendListener);

        verify(mockSendListener, timeout(100).times(3)).onSuccess();
        verify(mockSendListener, never()).onFailed();
    }

    @Test
    public void payloadReceivedListenerTest() {
        byte[] payload1 = "Hello".getBytes();
        byte[] payload2 = "World".getBytes();
        byte[] payload3 = "!".getBytes();

        MirrorBoardCommunicator mirrorBoardCommunicator = new MirrorBoardCommunicator();
        BoardCommunicator boardCommunicator = new CommunicatorQueueWrapper(mirrorBoardCommunicator, DISABLED_POST_DELAY);

        BoardCommunicator.PayloadListener mockPayloadListener = mock(BoardCommunicator.PayloadListener.class);
        boardCommunicator.setPayloadListener(mockPayloadListener);

        BoardCommunicator.SendListener mockSendListener = mock(BoardCommunicator.SendListener.class);
        boardCommunicator.send(payload1, mockSendListener);
        mirrorBoardCommunicator.invokeOnPayloadReceived();

        boardCommunicator.send(payload2, mockSendListener);
        mirrorBoardCommunicator.invokeOnPayloadReceived();

        boardCommunicator.send(payload3, mockSendListener);
        mirrorBoardCommunicator.invokeOnPayloadReceived();

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

        BoardCommunicator boardCommunicator = new CommunicatorQueueWrapper(new FailedBoardCommunicator(), DISABLED_POST_DELAY);

        BoardCommunicator.PayloadListener mockPayloadListener = mock(BoardCommunicator.PayloadListener.class);
        boardCommunicator.setPayloadListener(mockPayloadListener);

        BoardCommunicator.SendListener mockSendListener = mock(BoardCommunicator.SendListener.class);
        boardCommunicator.send(payload1, mockSendListener);
        boardCommunicator.send(payload2, mockSendListener);
        boardCommunicator.send(payload3, mockSendListener);

        verify(mockSendListener, times(3)).onFailed();
        verify(mockSendListener, never()).onSuccess();
    }

}