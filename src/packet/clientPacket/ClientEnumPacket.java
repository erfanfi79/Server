package packet.clientPacket;

public class ClientEnumPacket extends ClientPacket {

    private ClientEnum packet;

    public ClientEnum getPacket() {
        return packet;
    }

    public ClientEnumPacket(ClientEnum packet) {
        this.packet = packet;
    }
}
