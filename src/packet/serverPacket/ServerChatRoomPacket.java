package packet.serverPacket;

import java.util.ArrayList;

public class ServerChatRoomPacket extends ServerPacket {

    private ArrayList<Massage> massages;

    public ArrayList<Massage> getMassages() {
        return massages;
    }

    public ServerChatRoomPacket(ArrayList<Massage> massages) {
        this.massages = massages;
    }
}
