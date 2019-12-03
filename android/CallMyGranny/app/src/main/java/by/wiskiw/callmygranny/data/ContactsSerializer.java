package by.wiskiw.callmygranny.data;

import java.nio.charset.Charset;
import java.util.List;

import by.wiskiw.callmygranny.ArrayUtils;
import by.wiskiw.callmygranny.model.ARDContact;

/**
 * @author Andrey Yablonsky on 03.12.2019
 */
public class ContactsSerializer {

    private static final byte[] FIELD_DIVIDER = new byte[] {0}; // ascii NULL code
    private static final byte[] RECORD_DIVIDER = new byte[] {8}; // ascii TAB code

    public byte[] serialise(List<ARDContact> contactList) {
        byte[] result = new byte[0];
        for (ARDContact contact : contactList) {
            byte[] contactBytes = contactToBytes(contact);

            if (result.length == 0) {
                result = ArrayUtils.concat(result, contactBytes);
            } else {
                result = ArrayUtils.concat(result, RECORD_DIVIDER, contactBytes);
            }
        }

        return result;
    }

    private byte[] contactToBytes(ARDContact contact) {
        byte[] nameBytes = toByteArray(contact.getName());
        byte[] phoneBytes = toByteArray(contact.getName());
        return ArrayUtils.concat(nameBytes, FIELD_DIVIDER, phoneBytes);
    }

    private static byte[] toByteArray(String string) {
        return string.getBytes(Charset.forName("UTF-8"));
    }

}
