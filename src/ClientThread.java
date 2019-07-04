import models.Account;
import models.ChatRoom.ChatRoom;
import packet.clientPacket.*;
import packet.serverPacket.ServerPacket;
import serverHandler.LoginHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread {

    private Account account;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Socket socket;

    private boolean isPlaying = false;

    public ClientThread(Socket socket) {
        this.socket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (true) {
            try {
                ClientPacket packet = (ClientPacket) objectInputStream.readObject();

                if (packet instanceof ClientEnterPartPacket)
                    enterPartPacketHandler((ClientEnterPartPacket) packet);

                else if (packet instanceof ClientChatRoomPacket)
                    ChatRoom.getInstance().sendMassage((ClientChatRoomPacket) packet, objectOutputStream);

                else if (packet instanceof ClientLoginPacket)
                    accountMenu((ClientLoginPacket)packet);


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void enterPartPacketHandler(ClientEnterPartPacket clientEnterPartPacket) {

        switch (clientEnterPartPacket.getPart()) {

            case CHAT_ROOM:
                ChatRoom.getInstance().sendMassagesToClient(objectOutputStream);
                break;
        }
    }
    private void accountMenu(ClientLoginPacket clientLoginPacket){
        sendPacketToClient(new LoginHandler(clientLoginPacket).handleLogin());
    }

    public void sendPacketToClient(ServerPacket serverPacket){

        try {
            objectOutputStream.writeObject(serverPacket);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
