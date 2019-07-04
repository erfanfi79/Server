package packet.clientPacket.clientMatchPacket;

import models.Coordination;
import packet.clientPacket.ClientPacket;

public class ClientInsertCardPacket extends ClientPacket {

    private int whichHandCard, row, column;

    public int getWhichHandCard() {
        return whichHandCard;
    }

    public Coordination getCoordination() {

        Coordination coordination = new Coordination();
        coordination.setRow(row);
        coordination.setColumn(column);
        return coordination;
    }
}
