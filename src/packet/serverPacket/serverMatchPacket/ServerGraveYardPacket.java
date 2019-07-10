package packet.serverPacket.serverMatchPacket;

import models.Card;

import java.util.ArrayList;

public class ServerGraveYardPacket extends ServerMatchPacket {

    private ArrayList<Card> deadCards;

    public ArrayList<Card> getDeadCards() {
        return deadCards;
    }

    public ServerGraveYardPacket(ArrayList<Card> deadCards) {
        this.deadCards = deadCards;
    }
}
