import models.*;
import models.ChatRoom.ChatRoom;
import packet.clientPacket.*;
import packet.clientPacket.clientMatchPacket.ClientAttackPacket;
import packet.clientPacket.clientMatchPacket.ClientInsertCardPacket;
import packet.clientPacket.clientMatchPacket.ClientMatchEnumPacket;
import packet.clientPacket.clientMatchPacket.ClientMovePacket;
import packet.serverPacket.*;
import serverHandler.LoginHandler;

import java.io.*;
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

    public Account getAccount() {
        return account;
    }

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

                else if (packet instanceof ClientStartMatchPacket)
                    startMatchPacketHandler((ClientStartMatchPacket) packet);

                else if (packet instanceof ClientBuyAndSellPacket)
                    buyAndSell((ClientBuyAndSellPacket) packet);

                else if (packet instanceof ClientCollectionPacket) {
                    account.setCollection(((ClientCollectionPacket) packet).getMyCollection());
                    Account.save(account);
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void enumPacketHandler(ClientEnumPacket clientEnumPacket) {

        switch (clientEnumPacket.getPacket()) {

            case CHAT_ROOM:
                ChatRoom.getInstance().sendMassagesToClient(objectOutputStream);
                break;

            case GET_MONEY:
                ServerMoneyPacket serverMoneyPacket = new ServerMoneyPacket();
                serverMoneyPacket.setMoney(account.getMoney());
                sendPacketToClient(serverMoneyPacket);
                break;

            case LEADER_BOARD:
                sendLeaderBoard();
                break;

            case MATCH_HISTORY:
                sendMatchHistory();
                break;

            case CANCEL_WAITING_FOR_MULTI_PLAYER_GAME:
                Server.getWaitersForMultiPlayerGame().remove(this);
                break;

            case SAVE:
                Account.save(account);
                break;

            case SHOP:
                enterShop();
                break;

            case COLLECTION:
                enterCollection();
                break;

        }
    }

    private void sendMatchHistory() {

        ServerMatchHistory historyPacket = new ServerMatchHistory();
        historyPacket.setHistories(account.getMatchHistories());
        sendPacketToClient(historyPacket);
    }

    private void sendLeaderBoard() {

        ArrayList<String> userNames = new ArrayList<>();
        ArrayList<Integer> winNumber = new ArrayList<>();
        if (LoginMenu.getUsers().size() > 0)
            Collections.sort(LoginMenu.getUsers(), new Comparator<Account>() {
                public int compare(Account account1, Account account2) {
                    return account1.getWinsNumber() - account2.getWinsNumber();
                }
            });

        for (Account account : LoginMenu.getUsers()) {
            userNames.add(account.getUserName());
            winNumber.add(account.getWinsNumber());
        }

        ServerLeaderBoardPacket packet = new ServerLeaderBoardPacket();
        packet.setUsernames(userNames);
        packet.setWinNumber(winNumber);
        sendPacketToClient(packet);
    }

    private void startMatchPacketHandler(ClientStartMatchPacket packet) {

        if (packet.isMultiPlayerGame()) {

            if (Server.getWaitersForMultiPlayerGame().size() == 0)
                Server.getWaitersForMultiPlayerGame().add(this);

            else {
                matchManager = new MatchManager(Server.getWaitersForMultiPlayerGame().get(0), this);
                matchManager.sendStartMatchPacketToClients();
                matchManager.sendPlayersNameToClients();
                matchManager.sendMatchInfoToClients();

                Server.getWaitersForMultiPlayerGame().remove(0);

                matchInputHandler();
            }
        } else {

            //todo Single player game
            matchInputHandler();
        }
    }


    private void matchInputHandler() {

        //todo pay attention to turn for who

        while (true) {
            try {
                ClientPacket packet = (ClientPacket) objectInputStream.readObject();

                if (packet instanceof ClientMovePacket)
                    matchManager.move(this, ((ClientMovePacket) packet).getStartCoordination(),
                            ((ClientMovePacket) packet).getDestinationCoordination());

                else if (packet instanceof ClientAttackPacket)
                    matchManager.attack(this, ((ClientAttackPacket) packet).getAttackerCoordination(),
                            ((ClientAttackPacket) packet).getDefenderCoordination());

                else if (packet instanceof ClientInsertCardPacket)
                    matchManager.insert(this, ((ClientInsertCardPacket) packet).getWhichHandCard(),
                            ((ClientInsertCardPacket) packet).getCoordination());

                else if (packet instanceof ClientMatchEnumPacket)
                    matchEnumInputHandler((ClientMatchEnumPacket) packet);

                if (matchManager.isMatchFinished()) break;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void matchEnumInputHandler(ClientMatchEnumPacket packet) {

        switch (packet.getPacket()) {

            case END_TURN:
                matchManager.endTurn();
                break;

            case END_GAME:
                //todo
                break;

            case SPECIAL_POWER:
                matchManager.useSpecialPower();
                break;

            case GRAVE_YARD:
                matchManager.sendGraveYardToClient(this);
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

    public void buyAndSell(ClientBuyAndSellPacket packet) {
        if (packet.isBuy()) {
            ShopError error = GlobalShop.getGlobalShop().buy(packet.getCardName(), account);
            ServerLogPacket serverLogPacket = new ServerLogPacket();
            if (error.equals(ShopError.SUCCESS))
                serverLogPacket.setSuccessful(true);
            else serverLogPacket.setLog(error.toString());

        } else sell(packet.getCardName());
        enterShop();
    }

    public void sell(String cardName) {
        ArrayList<Card> cards = account.getCollection().getCards();
        for (int i = cards.size() - 1; i >= 0; i--)
            if (cards.get(i).getCardName().equals(cardName)) {
                Card soldCard = cards.get(i);
                account.setMoney(account.getMoney() + (soldCard.getPrice() * 3) / 4);
                ServerMoneyPacket serverMoneyPacket = new ServerMoneyPacket();
                serverMoneyPacket.setMoney(account.getMoney());
                sendPacketToClient(serverMoneyPacket);
                account.getCollection().getCards().remove(soldCard);
                GlobalShop.getGlobalShop().addToShop(cardName);
                ServerLogPacket serverLogPacket = new ServerLogPacket();
                serverLogPacket.setSuccessful(true);
                sendPacketToClient(serverLogPacket);
                return;
            }
        ServerLogPacket serverLogPacket = new ServerLogPacket();
        serverLogPacket.setLog(ShopError.CARD_NOT_FOUND.toString());
        sendPacketToClient(serverLogPacket);
    }

    private void enterShop() {
        Collection collection = GlobalShop.getGlobalShop().getShopCollection();
        sendPacketToClient(new ServerCollection(getNewCollection(), collection));
        ServerMoneyPacket serverMoneyPacket = new ServerMoneyPacket();
        serverMoneyPacket.setMoney(account.getMoney());
        sendPacketToClient(serverMoneyPacket);
    }

    private void enterCollection() {
        sendPacketToClient(new ServerCollection(getNewCollection()));
    }

    private Collection getNewCollection() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(account.getCollection());
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(bais);
            return (Collection) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
