package packet.clientPacket.clientMatchPacket;

import models.Coordination;
import packet.clientPacket.ClientPacket;

public class ClientMovePacket extends ClientPacket {

    private int startRow, startColumn, destinationRow, destinationColumn;

    public void setDestinationColumn(int destinationColumn) {
        this.destinationColumn = destinationColumn;
    }

    public void setDestinationRow(int destinationRow) {
        this.destinationRow = destinationRow;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public Coordination getStartCoordination() {

        Coordination coordination = new Coordination();
        coordination.setRow(startRow);
        coordination.setColumn(startColumn);
        return coordination;
    }

    public Coordination getDestinationCoordination() {

        Coordination coordination = new Coordination();
        coordination.setRow(destinationRow);
        coordination.setColumn(destinationColumn);
        return coordination;
    }
}
