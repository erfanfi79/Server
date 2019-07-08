import models.Account;
import packet.clientPacket.ClientChatRoomPacket;
import packet.serverPacket.Massage;
import packet.serverPacket.ServerChatRoomPacket;

import java.util.ArrayList;

public class ChatRoom {

    private static ChatRoom chatRoom;
    private ArrayList<Massage> massages = new ArrayList<>();
    ArrayList<ClientThread> clientThreads = new ArrayList<>();

    public static ChatRoom getInstance() {

        if (chatRoom == null) chatRoom = new ChatRoom();
        return chatRoom;
    }

    private ChatRoom() {
    }

    public void addToChatRoom(ClientThread clientThread) {
        clientThreads.add(clientThread);
        clientThread.sendPacketToClient(new ServerChatRoomPacket(massages));
    }

    public void removeFromChatRoom(ClientThread clientThread) {
        clientThreads.remove(clientThread);
    }

    public synchronized void sendMassage(Account account, ClientChatRoomPacket clientChatRoomPacket) {
        massages.add(new Massage(account.getUserName(), clientChatRoomPacket.getString()));
        sendToAll();

    }

    public void sendToAll() {
        for (ClientThread thread : clientThreads)
            thread.sendPacketToClient(new ServerChatRoomPacket(massages));
    }
}
