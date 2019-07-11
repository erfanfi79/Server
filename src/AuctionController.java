import javafx.animation.PauseTransition;
import javafx.util.Duration;
import models.Account;
import models.Card;
import models.GlobalShop;
import packet.clientPacket.ClientAuctionPacket;
import packet.serverPacket.ServerAuctionPacket;
import packet.serverPacket.ServerPacket;

import java.util.ArrayList;

public class AuctionController implements Runnable {
    private static AuctionController auctionController;
    public static ArrayList<ClientThread> buyers = new ArrayList<>();
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

    public ServerPacket getPacket(ClientThread clientThread) {
        buyers.add(clientThread);
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

    public void finish() {
        if (buyer != null) {
            buyer.setMoney(buyer.getMoney() - highestPrice);
            seller.setMoney(seller.getMoney() + highestPrice);
            for (int i = seller.getCollection().getCards().size() - 1; i >= 0; i--)
                if (seller.getCollection().getCards().get(i).getCardName().equals(card.getCardName())) {
                    Card soldCard = seller.getCollection().getCards().get(i);
                    seller.getCollection().getCards().remove(soldCard);
                    break;
                }
            buyer.setID(card);
            buyer.getCollection().getCards().add(card);
            for (ClientThread clientThread : buyers) {
                if (clientThread.getAccount().equals(buyer))
                    clientThread.enterShop();
                if (clientThread.getAccount().equals(seller))
                    clientThread.enterShop();
            }
        }
        for (ClientThread clientThread : buyers) {
            ServerAuctionPacket packet = new ServerAuctionPacket();
            packet.card = card;
            packet.highestPrice = 0;
            packet.startTime = 1;
            clientThread.sendPacketToClient(packet);
        }
    }

    public void addPrice(int highestPrice, Account account) {
        buyer = account;
        this.highestPrice = highestPrice;
        for (ClientThread clientThread : buyers) {
            ServerAuctionPacket packet = new ServerAuctionPacket();
            packet.card = card;
            packet.highestPrice = highestPrice;
            packet.startTime = time;
            clientThread.sendPacketToClient(packet);
        }
    }

    public void buildAuction(ClientAuctionPacket packet, Account account) {
        seller = account;
        if (isEmpty) {
            highestPrice = packet.getPrice();
            buyer = account;
            card = GlobalShop.getGlobalShop().getCardByName(packet.getCardName());
            time = System.currentTimeMillis();
            isEmpty = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PauseTransition delay = new PauseTransition(Duration.seconds(180));
                    delay.setOnFinished(event -> finish());
                    delay.play();
                }
            });

        }
    }
}
