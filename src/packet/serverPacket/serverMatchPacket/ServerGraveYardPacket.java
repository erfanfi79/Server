package packet.serverPacket.serverMatchPacket;

import models.Card;
import packet.serverPacket.ServerPacket;

import java.util.ArrayList;

public class ServerGraveYardPacket extends ServerPacket {

    private ArrayList<Card> deadCards;

    public ArrayList<Card> getDeadCards() {
        return deadCards;
    }

    public ServerGraveYardPacket(ArrayList<Card> deadCards) {
        this.deadCards = deadCards;
    }
}
