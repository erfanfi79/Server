package packet.clientPacket;

public class ClientEnumPacket extends ClientPacket {

    private ClientEnum part;

    public ClientEnum getPart() {
        return part;
    }

    public ClientEnumPacket(ClientEnum part) {
        this.part = part;
    }
}
