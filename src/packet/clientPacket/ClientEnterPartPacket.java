package packet.clientPacket;

public class ClientEnterPartPacket extends ClientPacket {

    private ClientPartsPacket part;

    public ClientPartsPacket getPart() {
        return part;
    }

    public ClientEnterPartPacket(ClientPartsPacket part) {
        this.part = part;
    }
}
