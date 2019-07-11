package packet.serverPacket.serverMatchPacket;

public class ServerHandPacket extends ServerMatchPacket {

    VirtualCard[] hand;

    public VirtualCard[] getHand() {
        return hand;
    }

    public ServerHandPacket(VirtualCard[] hand) {
        this.hand = hand;
    }
}
