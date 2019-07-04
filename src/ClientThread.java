import models.Account;
import models.ChatRoom.ChatRoom;
import packet.clientPacket.*;
import packet.clientPacket.clientMatchPacket.ClientAttackPacket;
import packet.clientPacket.clientMatchPacket.ClientInsertCardPacket;
import packet.clientPacket.clientMatchPacket.ClientMovePacket;
import packet.clientPacket.clientMatchPacket.ClientWithoutVariableMatchPacket;
import packet.serverPacket.ServerLogPacket;
import packet.serverPacket.ServerMoneyPacket;
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
                    ChatRoom.getInstance().sendMassage(account, (ClientChatRoomPacket) packet, objectOutputStream);

                else if (packet instanceof ClientLoginPacket)
                    accountMenu((ClientLoginPacket)packet);

                else if (packet instanceof  ClientStartMatchPacket);



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
            case GET_MONEY:
                ServerMoneyPacket serverMoneyPacket=new ServerMoneyPacket();
                serverMoneyPacket.setMoney(account.getMoney());
                sendPacketToClient(serverMoneyPacket);
                break;
        }
    }


    private void matchInputHandler() {

        //todo if game finished exit from the while

        while (true) {
            try {
                ClientPacket packet = (ClientPacket) objectInputStream.readObject();

                if (packet instanceof ClientMovePacket);
                else if (packet instanceof ClientAttackPacket);
                else if (packet instanceof ClientInsertCardPacket);
                else if (packet instanceof ClientWithoutVariableMatchPacket);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private void accountMenu(ClientLoginPacket clientLoginPacket){
        LoginHandler loginHandler =new LoginHandler(clientLoginPacket);
        ServerLogPacket serverLogPacket=loginHandler.handleLogin();
        if (serverLogPacket.isSuccessful())
            this.account=loginHandler.getAccount();
        sendPacketToClient(serverLogPacket);
    }

    public void sendPacketToClient(ServerPacket serverPacket){

        try {
            objectOutputStream.writeObject(serverPacket);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
