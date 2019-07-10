package packet.clientPacket.clientMatchPacket;

import models.Coordination;
import packet.clientPacket.ClientPacket;

public class ClientAttackPacket extends ClientPacket {

    private Coordination attacker, defender;

    public Coordination getAttacker() {
        return attacker;
    }

    public Coordination getDefender() {
        return defender;
    }

    public void setAttacker(Coordination attacker) {
        this.attacker = attacker;
    }

    public void setDefender(Coordination defender) {
        this.defender = defender;
    }
}
