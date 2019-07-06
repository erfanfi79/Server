package packet.clientPacket;

public class ClientBuyAndSellPacket extends ClientPacket {
    private boolean isBuy;
    private String cardName;

    public ClientBuyAndSellPacket(String cardName, boolean isBuy) {
        this.cardName = cardName;
        this.isBuy = isBuy;
    }

    public String getCardName() {
        return cardName;
    }

    public boolean isBuy() {
        return isBuy;
    }
}
