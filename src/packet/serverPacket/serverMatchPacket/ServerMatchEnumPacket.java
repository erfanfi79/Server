package packet.serverPacket.serverMatchPacket;

import packet.serverPacket.ServerPacket;

public class ServerMatchEnumPacket extends ServerPacket {

    private ServerMatchEnum packet;

    public ServerMatchEnum getPacket() {
        return packet;
    }

    public ServerMatchEnumPacket(ServerMatchEnum packet) {
        this.packet = packet;
    }
}
