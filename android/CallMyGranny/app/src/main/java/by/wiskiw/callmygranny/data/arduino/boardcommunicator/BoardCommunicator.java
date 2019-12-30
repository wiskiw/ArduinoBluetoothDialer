package by.wiskiw.callmygranny.data.arduino.boardcommunicator;

/**
 * Интерфейс описывает способ взаимодействия(отправки и/или приема массива байт) для конкретной платой.
 * <p>Реализация может иметь ограничения, например, длинна отправляемых данных</p>
 * <ul>
 *     <li>Добавляет возможность отправки байт со слушателем успеха/ошибки</li>
 *     <li>Устраняет сервисную информацию, приходящую от платы</li>
 * </ul>
 *
 * @author Andrey Yablonsky on 26.12.2019
 */
public interface BoardCommunicator {

    /**
     * Добавляет слушатель получения данных
     */
    void addPayloadListener(PayloadListener payloadListener);

    /**
     * Убирает слушатель получения данных
     */
    void removePayloadListener(PayloadListener payloadListener);

    /**
     * Отправляет массив байт
     * @param data данные для отправки
     * @param sendListener слушатель состояния завершения отправки
     */
    void send(byte[] data, SendListener sendListener);

    interface SendListener {

        /**
         * Обрабатывает успешную отправку данных
         */
        void onSuccess();

        /**
         * Обрабатывает ошибку в при отправке данных
         */
        void onFailed();

    }

    /**
     * Слушатель получения данных
     */
    interface PayloadListener {

        /**
         * Обрабатывает полученные по Bluetooth данные
         * @param payload чистые данные
         */
        void onPayloadReceived(byte[] payload);

    }

}
