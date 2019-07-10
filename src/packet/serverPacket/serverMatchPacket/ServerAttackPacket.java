package packet.serverPacket.serverMatchPacket;

import models.Coordination;

public class ServerAttackPacket extends ServerMatchPacket {

    private VirtualCard card;
    private Coordination coordination;

    public VirtualCard getCard() {
        return card;
    }

    public Coordination getCoordination() {
        return coordination;
    }

    public ServerAttackPacket(VirtualCard card, Coordination coordination) {

        this.card = card;
        this.coordination = coordination;
    }
}
