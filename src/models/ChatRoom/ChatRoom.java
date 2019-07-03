package models.ChatRoom;

import java.util.ArrayList;

public class ChatRoom {

    private ChatRoom chatRoom;
    private ArrayList<Massage> massages = new ArrayList<>();

    public ArrayList<Massage> getMassages() {
        return massages;
    }

    public ChatRoom getChatRoom() {

        if (chatRoom == null) chatRoom = new ChatRoom();
        return chatRoom;
    }

    private ChatRoom() {
    }

    public synchronized void sendMassage(String userName, String massage) {

        massages.add(new Massage(userName, massage));
    }
}
