import models.Account;
import packet.clientPacket.ClientChatRoomPacket;
import packet.serverPacket.Massage;
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

    public synchronized void sendMassage(ClientThread clientThread, ClientChatRoomPacket clientChatRoomPacket) {

        massages.add(new Massage(clientThread.getAccount().getUserName(), clientChatRoomPacket.getString()));
        sendMassagesToClient(clientThread);
    }

    public void sendMassagesToClient(ClientThread clientThread) {

        clientThread.sendPacketToClient(new ServerChatRoomPacket(massages));
    }

    public void sendMassagesToClients() {


    }
}
