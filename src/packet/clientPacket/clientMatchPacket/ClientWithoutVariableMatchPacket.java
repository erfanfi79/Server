package packet.clientPacket.clientMatchPacket;

import packet.clientPacket.ClientPacket;

public class ClientWithoutVariableMatchPacket extends ClientPacket {

    private ClientWithoutVariableMatchPacketEnum packet;

    public ClientWithoutVariableMatchPacketEnum getPacket() {
        return packet;
    }

    public ClientWithoutVariableMatchPacket(ClientWithoutVariableMatchPacketEnum packet) {
        this.packet = packet;
    }
}
