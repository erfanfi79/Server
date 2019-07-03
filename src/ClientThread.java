import models.Account;
import packet.clientPocket.ClientChatRoomPacket;
import packet.clientPocket.ClientLoginPacket;
import packet.clientPocket.ClientPocket;

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
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (true) {
            try {

                ClientPocket packet = (ClientPocket) objectInputStream.readObject();

                if (packet instanceof ClientChatRoomPacket);
                if (packet instanceof ClientLoginPacket);


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {

    }
}
