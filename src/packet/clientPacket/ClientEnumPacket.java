package packet.clientPacket;

public class ClientEnumPacket extends ClientPacket {

    private ClientEnum part;

    public ClientEnumPacket(ClientEnum part) {
        this.part = part;
    }

    public ClientEnum getPart() {
        return part;
    }
}
