package packet.clientPacket;

public class ClientChatRoomPacket extends ClientPacket {

    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
