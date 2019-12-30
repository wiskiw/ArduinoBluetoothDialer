package by.wiskiw.callmygranny.data.arduino;

import java.util.ArrayList;
import java.util.List;

import by.wiskiw.callmygranny.data.arduino.boardcommunicator.BoardCommunicator;
import by.wiskiw.callmygranny.data.arduino.encoding.ByteEncoder;
import by.wiskiw.callmygranny.data.arduino.sendqueue.BoardSendQueue;

/**
 * Реализует отправку массива байт любой длинны через {@link BoardCommunicator}
 * <ul>
 *     <li>Кодирует отправляемые байты</li>
 *     <li>Добавляет HEADER для отправляемых байт</li>
 *     <li>Разделяет байты на группы, отправляет по частям</li>
 * </ul>
 *
 * todo: добавить проверку на доступность BoardCommunicator
 *
 * @author Andrey Yablonsky on 26.12.2019
 */
public class TransmitController {

    private static final int HEADER_SIZE_BYTE = 4;
    private static final int PACK_SIZE_BYTE = 56;

    private final BoardSendQueue boardSendQueue;
    private final ByteEncoder encoder;

    private Listener listener;

    public TransmitController(BoardCommunicator boardCommunicator, ByteEncoder encoder) {
        this.boardSendQueue = new BoardSendQueue(boardCommunicator);
        this.encoder = encoder;
    }

    public void send(byte[] data, Listener listener) {
        this.listener = listener;

        byte[] encodedBytes = encoder.encode(data);

        List<byte[]> packs = divideForPackages(PACK_SIZE_BYTE, encodedBytes);
        byte[] header = createHeader(HEADER_SIZE_BYTE, encodedBytes, packs);

        startTransaction(header, packs);
    }

    private void startTransaction(byte[] header, List<byte[]> packs) {
        int headerPacksCount = 1;
        int allPacksCount = headerPacksCount + packs.size();
        listener.onProgressChanged(allPacksCount, 0);

        boardSendQueue.add(header, new SendListener(allPacksCount, 1));

        for (int packIndex = 0; packIndex < packs.size(); packIndex++) {
            byte[] pack = packs.get(packIndex);

            // packIndex + 1 - переход от индекса к номеру
            int packNumber = headerPacksCount + packIndex + 1;
            boardSendQueue.add(pack, new SendListener(allPacksCount, packNumber));
        }

        boardSendQueue.sendAll();
    }

    private final class SendListener implements BoardCommunicator.SendListener {

        private final int packsCount;
        private final int currentPackNumber;

        private SendListener(int packsCount, int currentPackNumber) {
            this.packsCount = packsCount;
            this.currentPackNumber = currentPackNumber;
        }

        @Override
        public void onSuccess() {
            listener.onProgressChanged(packsCount, currentPackNumber);

            if (packsCount == currentPackNumber) {
                // последний был отправлен успешно
                listener.onSuccess();
            }
        }

        @Override
        public void onFailed() {
            // ошибка отправки одного из пакетов
            listener.onFailed();
        }
    }

    private static byte[] createHeader(int headerSize, byte[] rawData, List<byte[]> packs) {
        // не должен содержать 0-байт символов
        // todo: createHeader
        return new byte[headerSize];
    }

    private static List<byte[]> divideForPackages(int packSize, byte[] rawData) {
        int packsCount = (int) Math.ceil(rawData.length / packSize);
        List<byte[]> packs = new ArrayList<>(packsCount);

        for (int packIndex = 0; packIndex < packsCount; packIndex++) {
            byte[] pack = new byte[packSize];

            int startIndex = packIndex * packSize;

            boolean isFullPack = startIndex + packSize <= rawData.length;
            int byteCount = isFullPack
                ? startIndex + packSize
                : rawData.length;

            System.arraycopy(rawData, startIndex, pack, 0, byteCount);
            packs.add(pack);
        }
        return packs;
    }

    private interface Listener {

        void onSuccess();

        void onProgressChanged(int allCount, int transferredCount);

        void onFailed();

    }

}
