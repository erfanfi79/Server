package packet.clientPacket.clientMatchPacket;

import packet.clientPacket.ClientPacket;

public class ClientMatchEnumPacket extends ClientPacket {

    private ClientMatchEnum packet;

    public ClientMatchEnum getPacket() {
        return packet;
    }

    public ClientMatchEnumPacket(ClientMatchEnum packet) {
        this.packet = packet;
    }
}
