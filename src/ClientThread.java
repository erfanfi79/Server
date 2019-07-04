import models.Account;
import packet.clientPacket.ClientChatRoomPacket;
import packet.clientPacket.ClientEnterPartPacket;
import packet.clientPacket.ClientLoginPacket;
import packet.clientPacket.ClientPacket;

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

                ClientPacket packet = (ClientPacket) objectInputStream.readObject();

                if (packet instanceof ClientEnterPartPacket);
                else if (packet instanceof ClientChatRoomPacket);
                else if (packet instanceof ClientLoginPacket);


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {

    }
}
