package packet.serverPacket.serverMatchPacket;

import models.Coordination;

public class ServerMovePacket {

    private VirtualCard card;
    private Coordination start, destination;

    public VirtualCard getCard() {
        return card;
    }

    public Coordination getDestination() {
        return destination;
    }

    public Coordination getStart() {
        return start;
    }

    public ServerMovePacket(VirtualCard card, Coordination start, Coordination destination) {

        this.card = card;
        this.start = start;
        this.destination = destination;
    }
}
