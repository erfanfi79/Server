package packet.clientPacket;

public class ClientEnterPartPacket {

    private ClientEnterPartPacket part;

    public ClientEnterPartPacket getPart() {
        return part;
    }

    public ClientEnterPartPacket(ClientEnterPartPacket part) {
        this.part = part;
    }
}
