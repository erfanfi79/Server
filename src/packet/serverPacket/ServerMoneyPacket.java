package packet.serverPacket;

public class ServerMoneyPacket extends ServerPacket{
    String money;

    public void setMoney(int money) {
        this.money = String.valueOf(money);
    }

    public String getMoney() {
        return money;
    }
}
