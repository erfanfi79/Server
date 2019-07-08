package packet.serverPacket;

public class Massage extends ServerPacket {

    private String userName, massage;

    public Massage(String userName, String massage) {

        this.userName = userName;
        this.massage = massage;
    }

    public String getUserName() {
        return userName;
    }

    public String getMassage() {
        return massage;
    }
}
