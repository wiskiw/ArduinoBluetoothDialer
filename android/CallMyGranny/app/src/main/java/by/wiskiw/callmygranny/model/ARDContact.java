package by.wiskiw.callmygranny.model;

/**
 * todo: добавить граничения по размеру полей
 *
 * @author Andrey Yablonsky on 03.12.2019
 */
public class ARDContact {

    private String name;
    private String phone;

    public ARDContact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
