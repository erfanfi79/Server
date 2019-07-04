package models.ChatRoom;

import packet.clientPacket.ClientChatRoomPacket;
import packet.serverPacket.ServerChatRoomPacket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ChatRoom {

    private static ChatRoom chatRoom;
    private ArrayList<Massage> massages = new ArrayList<>();

    public static ChatRoom getInstance() {

        if (chatRoom == null) chatRoom = new ChatRoom();
        return chatRoom;
    }

    private ChatRoom() {
    }

    public synchronized void sendMassage(ClientChatRoomPacket clientChatRoomPacket, ObjectOutputStream objectOutputStream) {

        massages.add(new Massage(clientChatRoomPacket.getUserName(), clientChatRoomPacket.getString()));
        sendMassagesToClient(objectOutputStream);
    }

    public void sendMassagesToClient(ObjectOutputStream objectOutputStream) {

        try {
            objectOutputStream.writeObject(new ServerChatRoomPacket(massages));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
