package by.wiskiw.callmygranny.data.arduino;

import java.util.List;

import androidx.annotation.NonNull;
import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteDecoder;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteEncoder;
import by.wiskiw.callmygranny.data.arduino.header.TransmitHeaderBuilder;

/**
 * Фабрика создания {@link TransmitController}
 *
 * @author Andrey Yablonsky on 31.12.2019
 */
public class TransmitControllerFactory {

    private BoardCommunicator boardCommunicator;

    private ByteEncoder encoder = new NonByteEncoder();
    private ByteDecoder decoder = new NonByteDecoder();

    private TransmitHeaderBuilder headerBuilder = new EmptyHeadBuilder();

    public TransmitControllerFactory setBoardCommunicator(@NonNull BoardCommunicator boardCommunicator) {
        this.boardCommunicator = boardCommunicator;
        return this;
    }

    public TransmitControllerFactory setHeaderBuilder(@NonNull TransmitHeaderBuilder headerBuilder) {
        this.headerBuilder = headerBuilder;
        return this;
    }

    public TransmitControllerFactory setEncoder(@NonNull ByteEncoder encoder) {
        this.encoder = encoder;
        return this;
    }

    public TransmitControllerFactory setDecoder(@NonNull ByteDecoder decoder) {
        this.decoder = decoder;
        return this;
    }

    public TransmitController create() {
        return new TransmitController(boardCommunicator, headerBuilder, encoder, decoder);
    }

    public static final class NonByteEncoder implements ByteEncoder {

        @Override
        public byte[] encode(byte[] bytes) {
            return bytes;
        }
    }

    public static final class NonByteDecoder implements ByteDecoder {

        @Override
        public byte[] decode(byte[] bytes) {
            return bytes;
        }
    }

    public static final class EmptyHeadBuilder implements TransmitHeaderBuilder {

        @Override
        public byte[] build(int maxHeaderSize, byte[] rawData, List<byte[]> packs) {
            return new byte[0];
        }
    }
}
