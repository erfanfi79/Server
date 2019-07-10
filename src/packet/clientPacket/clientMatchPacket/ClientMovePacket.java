package packet.clientPacket.clientMatchPacket;

import models.Coordination;
import packet.clientPacket.ClientPacket;

public class ClientMovePacket extends ClientPacket {

    private Coordination start, destination;

    public Coordination getStart() {
        return start;
    }

    public Coordination getDestination() {
        return destination;
    }

    public void setDestination(Coordination destination) {
        this.destination = destination;
    }

    public void setStart(Coordination start) {
        this.start = start;
    }
}
