package packet.clientPacket;

public class ClientCheatPacket extends ClientPacket {
    private boolean doubleMoney = false;
    private boolean halfPrice = false;
    private boolean equalSell = false;

    public void setDoubleMoney() {
        doubleMoney = true;
    }

    public void setEqualSell() {
        equalSell = true;
    }

    public void setHalfPrice() {
        halfPrice = true;
    }

    public boolean getDoubleMoney() {
        return doubleMoney;
    }

    public boolean getEqualSell() {
        return equalSell;
    }

    public boolean getHalfPrice() {
        return halfPrice;
    }
}
