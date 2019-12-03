package by.wiskiw.callmygranny.data;

import java.util.ArrayList;
import java.util.List;

import by.wiskiw.callmygranny.model.ARDContact;
import io.paperdb.Book;

/**
 * @author Andrey Yablonsky on 03.12.2019
 */
public class ContactsStorage {

    private static final String FIELD_NAME = "ard_contacts";

    private final Book mainBook;

    public ContactsStorage(Book mainBook) {
        this.mainBook = mainBook;

        // TODO: stub
        saveContacts(createFakeContacts());
    }

    public List<ARDContact> readContacts() {
        return mainBook.read(FIELD_NAME, new ArrayList<ARDContact>());
    }

    public void saveContacts(List<ARDContact> contacts) {
        mainBook.write(FIELD_NAME, contacts);
    }

    private List<ARDContact> createFakeContacts() {
        List<ARDContact> contacts = new ArrayList<>();

        contacts.add(new ARDContact("Andrey", "+375292203087"));
        contacts.add(new ARDContact("Andrey 2", "+375292203087"));
        contacts.add(new ARDContact("Bob", "+3752911111111"));

        return contacts;
    }

}
