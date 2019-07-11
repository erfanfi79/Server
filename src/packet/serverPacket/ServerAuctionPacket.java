package packet.serverPacket;

import models.Card;

public class ServerAuctionPacket extends ServerPacket {
    public Card card;
    public long startTime;
    public int highestPrice;
    public boolean isSetForAuction = false;


}
