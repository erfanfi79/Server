package models.ChatRoom;

import models.Account;
import packet.clientPacket.ClientChatRoomPacket;
import packet.serverPacket.ServerChatRoomPacket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ChatRoom {

    private static ChatRoom chatRoom;
    private ArrayList<Massage> massages = new ArrayList<>();
    //todo notification to all the users that exist in chat room

    public static ChatRoom getInstance() {

        if (chatRoom == null) chatRoom = new ChatRoom();
        return chatRoom;
    }

    private ChatRoom() {
    }

    public synchronized void sendMassage(Account account, ClientChatRoomPacket clientChatRoomPacket,
                                         ObjectOutputStream objectOutputStream) {

        massages.add(new Massage(account.getUserName(), clientChatRoomPacket.getString()));
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
