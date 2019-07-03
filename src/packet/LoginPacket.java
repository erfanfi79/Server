package packet;

public class LoginPacket extends Packet {

    String userName, password;

    public LoginPacket(String userName, String password) {

        this.userName = userName;
        this.password = password;
    }
}
