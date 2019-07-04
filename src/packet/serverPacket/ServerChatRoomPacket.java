package packet.serverPacket;

import models.ChatRoom.Massage;

import java.util.ArrayList;

public class ServerChatRoomPacket {

    private ArrayList<Massage> massages = new ArrayList<>();

    public ArrayList<Massage> getMassages() {
        return massages;
    }

    public ServerChatRoomPacket(ArrayList<Massage> massages) {
        this.massages = massages;
    }
}
