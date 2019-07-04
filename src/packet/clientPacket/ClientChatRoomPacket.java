package packet.clientPacket;

public class ClientChatRoomPacket extends ClientPacket {

    private String userName, string;

    public String getUserName() {
        return userName;
    }

    public String getString() {
        return string;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setString(String string) {
        this.string = string;
    }
}
