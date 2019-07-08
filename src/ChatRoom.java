import models.Account;
import packet.clientPacket.ClientChatRoomPacket;
import packet.serverPacket.Massage;
import packet.serverPacket.ServerChatRoomPacket;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

public class ChatRoom {

    private static ChatRoom chatRoom;
    private ArrayList<Massage> massages = new ArrayList<>();
    private static LinkedList<ClientThread> clientThreads = new LinkedList<>();

    public static LinkedList<ClientThread> getClientThreads() {
        return clientThreads;
    }

    public static ChatRoom getInstance() {

        if (chatRoom == null) chatRoom = new ChatRoom();
        return chatRoom;
    }

    private ChatRoom() {
    }

    public synchronized void sendMassage(ClientThread clientThread, ClientChatRoomPacket clientChatRoomPacket) {

        massages.add(new Massage(clientThread.getAccount().getUserName(), clientChatRoomPacket.getString()));
        sendMassagesToClients();
    }

    public void sendMassagesToClient(ClientThread clientThread) {

        clientThread.sendPacketToClient(new ServerChatRoomPacket(massages));
    }

    public void sendMassagesToClients() {

        for (ClientThread clientThread : clientThreads)
            clientThread.sendPacketToClient(new ServerChatRoomPacket(massages));
    }
}
