import models.Account;
import models.Card;
import models.GlobalShop;
import packet.clientPacket.ClientAuctionPacket;
import packet.serverPacket.ServerAuctionPacket;
import packet.serverPacket.ServerPacket;

import java.util.ArrayList;

public class AuctionController implements Runnable {
    private static AuctionController auctionController;
    private static ArrayList<ClientThread> buyers = new ArrayList<>();
    private boolean isEmpty = true;
    private Account buyer, seller;
    private long time;
    private Card card;
    private int highestPrice;

    public static AuctionController getInstance() {
        if (auctionController == null)
            auctionController = new AuctionController();
        return auctionController;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    @Override
    public void run() {

    }

    public ServerPacket getPacket() {
        ServerAuctionPacket serverAuctionPacket = new ServerAuctionPacket();
        serverAuctionPacket.card = card;
        serverAuctionPacket.startTime = time;
        serverAuctionPacket.highestPrice = highestPrice;
        if (card != null)
            serverAuctionPacket.isSetForAuction = true;
        return serverAuctionPacket;
    }

    public Card getCard() {
        return card;
    }

    public void addPrice(int highestPrice) {
        this.highestPrice = highestPrice;
    }

    public void buildAuction(ClientAuctionPacket packet, Account account) {
        seller = account;
        if (isEmpty) {
            card = GlobalShop.getGlobalShop().getCardByName(packet.getCardName());
            time = System.currentTimeMillis();
            isEmpty = false;
        }
    }
}
