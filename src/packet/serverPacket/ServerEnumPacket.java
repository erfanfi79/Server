package packet.serverPacket;

public class ServerEnumPacket extends ServerPacket {

    ServerEnum serverEnum;

    public ServerEnum getServerEnum() {
        return serverEnum;
    }

    public ServerEnumPacket(ServerEnum serverEnum) {
        this.serverEnum = serverEnum;
    }
}
