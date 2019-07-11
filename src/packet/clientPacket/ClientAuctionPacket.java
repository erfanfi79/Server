package packet.clientPacket;

public class ClientAuctionPacket extends ClientPacket {
    private String cardName;
    private int price;
    private boolean isInAuctionMenu;

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isInAuctionMenu() {
        return isInAuctionMenu;
    }

    public void setInAuctionMenu(boolean inAuctionMenu) {
        isInAuctionMenu = inAuctionMenu;
    }
}
