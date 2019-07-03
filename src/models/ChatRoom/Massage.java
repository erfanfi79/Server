package models.ChatRoom;

public class Massage {

    private String userName, massage;

    public String getUserName() {
        return userName;
    }

    public String getMassage() {
        return massage;
    }

    Massage(String userName, String massage) {

        this.userName = userName;
        this.massage = massage;
    }
}
