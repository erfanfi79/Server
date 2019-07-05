import models.Account;
import models.ChatRoom.ChatRoom;
import models.History;
import models.LoginMenu;
import packet.clientPacket.*;
import packet.clientPacket.clientMatchPacket.ClientAttackPacket;
import packet.clientPacket.clientMatchPacket.ClientInsertCardPacket;
import packet.clientPacket.clientMatchPacket.ClientMatchEnumPacket;
import packet.clientPacket.clientMatchPacket.ClientMovePacket;
import packet.serverPacket.*;
import serverHandler.LoginHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static packet.serverPacket.ServerEnum.MULTI_PLAYER_GAME_IS_READY;

public class ClientThread extends Thread {

    private Account account;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Socket socket;
    private MatchManager matchManager;      //todo see that is this necessary?

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

                if (packet instanceof ClientEnumPacket)
                    enumPacketHandler((ClientEnumPacket) packet);

                else if (packet instanceof ClientChatRoomPacket)
                    ChatRoom.getInstance().sendMassage(account, (ClientChatRoomPacket) packet, objectOutputStream);

                else if (packet instanceof ClientLoginPacket)
                    accountMenu((ClientLoginPacket) packet);

                else if (packet instanceof ClientStartMatchPacket) ;


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void enumPacketHandler(ClientEnumPacket clientEnumPacket) {

        switch (clientEnumPacket.getPart()) {

            case CHAT_ROOM:
                ChatRoom.getInstance().sendMassagesToClient(objectOutputStream);
                break;

            case GET_MONEY:
                ServerMoneyPacket serverMoneyPacket = new ServerMoneyPacket();
                serverMoneyPacket.setMoney(account.getMoney());
                sendPacketToClient(serverMoneyPacket);
                break;

            case LEADER_BOARD:
                sendLeaderboard();
                break;

            case MATCH_HISTORY:
                sendMatchHistory();
                break;
        }
    }

    private void sendMatchHistory() {
        ServerMatchHistory historyPacket=new ServerMatchHistory();
        historyPacket.setHistories(account.getMatchHistories());
        sendPacketToClient(historyPacket);
    }

    private void sendLeaderboard() {
        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<Integer> winNumber = new ArrayList<>();
        if (LoginMenu.getUsers().size() > 0)
            Collections.sort(LoginMenu.getUsers(), new Comparator<Account>() {
                public int compare(Account account1, Account account2) {
                    return account1.getWinsNumber() - account2.getWinsNumber();
                }
            });
        for (Account account : LoginMenu.getUsers()) {
            usernames.add(account.getUserName());
            winNumber.add(account.getWinsNumber());
        }
        ServerLeaderBoardPacket packet = new ServerLeaderBoardPacket();
        packet.setUsernames(usernames);
        packet.setWinNumber(winNumber);
        sendPacketToClient(packet);
    }

    private void startMatchPacketHandler(ClientStartMatchPacket packet) {

        if (packet.isMultiPlayerGame()) {

            if (Server.getWaitersForMultiPlayerGame().size() == 0)
                Server.getWaitersForMultiPlayerGame().add(this);

            else {
                sendPacketToClient(new ServerEnumPacket(MULTI_PLAYER_GAME_IS_READY));
                matchManager = new MatchManager(Server.getWaitersForMultiPlayerGame().get(0), this);
                Server.getWaitersForMultiPlayerGame().remove(0);
                matchInputHandler();
            }
        } else {


        }
    }


    private void matchInputHandler() {

        //todo if game finished exit from the while
        //todo pay attention to turn for who

        while (true) {
            try {
                ClientPacket packet = (ClientPacket) objectInputStream.readObject();

                if (packet instanceof ClientMovePacket) ;
                else if (packet instanceof ClientAttackPacket) ;
                else if (packet instanceof ClientInsertCardPacket) ;
                else if (packet instanceof ClientMatchEnumPacket) ;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void accountMenu(ClientLoginPacket clientLoginPacket) {

        LoginHandler loginHandler = new LoginHandler(clientLoginPacket);
        ServerLogPacket serverLogPacket = loginHandler.handleLogin();

        if (serverLogPacket.isSuccessful())
            this.account = loginHandler.getAccount();
        sendPacketToClient(serverLogPacket);
    }

    public void sendPacketToClient(ServerPacket serverPacket) {

        try {
            objectOutputStream.writeObject(serverPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
