package packet.clientPacket.clientMatchPacket;

import models.Coordination;
import packet.clientPacket.ClientPacket;

public class ClientAttackPacket extends ClientPacket {

    private int attackerRow, attackerColumn, defenderRow, defenderColumn;

    public void setAttackerColumn(int attackerColumn) {
        this.attackerColumn = attackerColumn;
    }

    public void setAttackerRow(int attackerRow) {
        this.attackerRow = attackerRow;
    }

    public void setDefenderColumn(int defenderColumn) {
        this.defenderColumn = defenderColumn;
    }

    public void setDefenderRow(int defenderRow) {
        this.defenderRow = defenderRow;
    }

    public Coordination getAttackerCoordination() {

        Coordination coordination = new Coordination();
        coordination.setColumn(attackerColumn);
        coordination.setRow(attackerRow);
        return coordination;
    }

    public Coordination getDefenderCoordination() {

        Coordination coordination = new Coordination();
        coordination.setColumn(defenderColumn);
        coordination.setRow(defenderRow);
        return coordination;
    }
}
