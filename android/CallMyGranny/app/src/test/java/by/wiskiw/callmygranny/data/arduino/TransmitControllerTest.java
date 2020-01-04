package by.wiskiw.callmygranny.data.arduino;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import androidx.annotation.NonNull;
import by.wiskiw.callmygranny.TestUtils;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.FailedBoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.HandleMirrorBoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.MirrorBoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteDecoder;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteEncoder;
import by.wiskiw.callmygranny.data.arduino.encoding.NonZeroTwoWayByteEncoder;

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
        TransmitController.SendListener mockSendListener = mock(TransmitController.SendListener.class);
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
        TransmitController.SendListener mockSendListener = mock(TransmitController.SendListener.class);
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
        TransmitController.SendListener mockSendListener = mock(TransmitController.SendListener.class);
        transmitController.send(payload, mockSendListener);

        InOrder listenerInOrderWrapper = inOrder(mockSendListener);
        listenerInOrderWrapper.verify(mockSendListener).onProgressChanged(anyInt(), eq(0));
        listenerInOrderWrapper.verify(mockSendListener).onProgressChanged(anyInt(), eq(1));
        listenerInOrderWrapper.verify(mockSendListener).onProgressChanged(anyInt(), eq(3));
        listenerInOrderWrapper.verify(mockSendListener, timeout(30).atLeastOnce()).onSuccess();

        verify(mockSendListener, never()).onFailed();
    }

    @Test
    public void controllerWithNoEncoderTest() {
        byte[] payload = TestUtils.generateStubBytes(256);

        controllerWithEncodingTest(new TransmitControllerFactory.NonByteEncoder(), new TransmitControllerFactory.NonByteDecoder(),
            payload);
    }

    @Test
    public void controllerWithNonZeroTwoWayByteEncoderTest() {
        NonZeroTwoWayByteEncoder encoderDecoder = new NonZeroTwoWayByteEncoder();
        byte[] payload = TestUtils.generateStubBytes(
            TransmitController.PACK_SIZE_BYTE * 4 + TransmitController.PACK_SIZE_BYTE / 3);

        controllerWithEncodingTest(encoderDecoder, encoderDecoder, payload);
    }

    private static void controllerWithEncodingTest(ByteEncoder encoder, ByteDecoder decoder, byte[] payload) {
        HandleMirrorBoardCommunicator communicator = new HandleMirrorBoardCommunicator();
        communicator.skipHeaderBytes(TransmitController.HEADER_SIZE_BYTE);

        TransmitController transmitController = new TransmitControllerFactory()
            .setBoardCommunicator(communicator)
            .setEncoder(encoder)
            .setDecoder(decoder)
            .setSendDelayEnabled(false)
            .create();

        TransmitController.ReceiveListener mockReceiveListener = mock(TransmitController.ReceiveListener.class);
        transmitController.addReceiveListener(mockReceiveListener);

        TransmitController.SendListener mockSendListener = mock(TransmitController.SendListener.class);
        transmitController.send(payload, mockSendListener);

        communicator.invokeOnPayloadReceived();

        InOrder listenerInOrderWrapper = inOrder(mockReceiveListener, mockSendListener);
        listenerInOrderWrapper.verify(mockSendListener, atLeastOnce()).onSuccess();
        listenerInOrderWrapper.verify(mockReceiveListener).onReceive(argThat(new BytesStartWithMatcher(payload, payload.length)));
        verify(mockSendListener, never()).onFailed();
    }

    private static final class BytesStartWithMatcher implements ArgumentMatcher<byte[]> {

        private final byte[] expected;
        private final int requiredBytesCount;

        private BytesStartWithMatcher(byte[] expected, int requiredBytesCount) {
            this.expected = expected;
            this.requiredBytesCount = requiredBytesCount > 0 ? requiredBytesCount : 1;
        }

        @Override
        public boolean matches(byte[] actual) {
            int matchByteIndex;
            int minSize = Math.min(expected.length, actual.length);

            for (matchByteIndex = 0; matchByteIndex < minSize; matchByteIndex++) {
                if (actual[matchByteIndex] != expected[matchByteIndex]) {
                    break;
                }
            }

            return matchByteIndex >= requiredBytesCount;
        }

        @NonNull
        @Override
        public String toString() {
            byte[] cropped = new byte[requiredBytesCount];
            System.arraycopy(expected, 0, cropped, 0, Math.min(requiredBytesCount, expected.length));
            return Arrays.toString(cropped);
        }
    }

}