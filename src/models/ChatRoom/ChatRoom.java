package models.ChatRoom;

import packet.clientPacket.ClientChatRoomPacket;

import java.util.ArrayList;

public class ChatRoom {

    private static ChatRoom chatRoom;
    private ArrayList<Massage> massages = new ArrayList<>();


    public ArrayList<Massage> getMassages() {
        return massages;
    }

    public static ChatRoom getInstance() {

        if (chatRoom == null) chatRoom = new ChatRoom();
        return chatRoom;
    }

    private ChatRoom() {
    }

    public synchronized void sendMassage(ClientChatRoomPacket clientChatRoomPacket) {

        massages.add(new Massage(clientChatRoomPacket.getUserName(), clientChatRoomPacket.getString()));
    }
}
