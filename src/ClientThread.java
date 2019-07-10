import models.*;
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

public class ClientThread extends Thread {

    private Account account;
    private MatchManager matchManager;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private Socket socket;

    public ClientThread(Socket socket) {

        this.socket = socket;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            start();
        } catch (Exception e) {

            Server.getOnlineUsers().remove(this);
            e.printStackTrace();
        }
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ClientPacket packet = getPacketFromClient();

                if (packet instanceof ClientEnumPacket)
                    enumPacketHandler((ClientEnumPacket) packet);

                else if (packet instanceof ClientChatRoomPacket)
                    ChatRoom.getInstance().sendMassage(account, (ClientChatRoomPacket) packet);

                else if (packet instanceof ClientLoginPacket)
                    accountMenu((ClientLoginPacket) packet);

                else if (packet instanceof ClientBuyAndSellPacket)
                    buyAndSell((ClientBuyAndSellPacket) packet);

                else if (packet instanceof ClientCollectionPacket) {
                    account.setCollection(((ClientCollectionPacket) packet).getMyCollection());
                    Account.save(account);
                }
            }
        } catch (Exception e) {
            Server.getOnlineUsers().remove(this);
            close();
        }
    }

    public void close() {
        try {
            if (socket != null) socket.close();
//            if (inputStreamReader != null) inputStreamReader.close();
//            if (outputStreamWriter != null) outputStreamWriter.close();
        } catch (Exception e) {
        }
    }

    private void enumPacketHandler(ClientEnumPacket clientEnumPacket) {
        switch (clientEnumPacket.getPart()) {

            case CHAT_ROOM:
                ChatRoom.getInstance().addToChatRoom(this);
                break;

            case EXIT_CHATROOM:
                ChatRoom.getInstance().removeFromChatRoom(this);
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

            case CHECK_DECK:
                checkValidateDeck();
                break;

            case MULTI_PLAYER:
                createMatch(true);
                break;

            case SINGLE_PLAYER:
                createMatch(false);
                break;
        }
    }

    private void checkValidateDeck() {
        ServerLogPacket packet = new ServerLogPacket();
        if (account.getCollection().getSelectedDeck() == null) {
            packet.setLog("There is no selected deck");
            sendPacketToClient(packet);
            return;
        }
        if (!account.getCollection().getSelectedDeck().isDeckValidate()) {
            packet.setLog("Selected Deck is not Valid");
            sendPacketToClient(packet);
            return;
        }
        packet.setSuccessful(true);
        //saveDeckInDefaultMode(account.getCollection().getSelectedDeck());
        sendPacketToClient(packet);
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

    private void createMatch(boolean isMultiPlayer) {
        try {
            if (isMultiPlayer) {

                if (Server.getWaitersForMultiPlayerGame().size() == 0)
                    Server.getWaitersForMultiPlayerGame().add(this);

                else {
                    matchManager = new MatchManager(Server.getWaitersForMultiPlayerGame().get(0), this);
                    matchManager.sendStartMultiPlayerMatchPacketToClients();
                    matchManager.sendPlayersNameToClients();
                    matchManager.sendMatchInfoToClients();
                    matchManager.sendStartYourTurnToClient(Server.getWaitersForMultiPlayerGame().get(0));

                    Server.getWaitersForMultiPlayerGame().remove(0);

                    matchInputHandler();

                }
            } else {
                matchManager = new MatchManager(this);
                matchManager.sendPlayersNameToClients();
                matchManager.sendMatchInfoToClients();
                matchManager.sendStartYourTurnToClient(this);

                matchInputHandler();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void matchInputHandler() {

        while (true) {
            ClientPacket packet = getPacketFromClient();

            if (packet instanceof ClientMovePacket)
                matchManager.move(this, ((ClientMovePacket) packet).getStart(),
                        ((ClientMovePacket) packet).getDestination());

            else if (packet instanceof ClientAttackPacket)
                matchManager.attack(this, ((ClientAttackPacket) packet).getAttacker(),
                        ((ClientAttackPacket) packet).getDefender());

            else if (packet instanceof ClientInsertCardPacket)
                matchManager.insert(this, ((ClientInsertCardPacket) packet).getWhichHandCard(),
                        ((ClientInsertCardPacket) packet).getCoordination());

            else if (packet instanceof ClientMatchEnumPacket)
                matchEnumInputHandler((ClientMatchEnumPacket) packet);

            matchManager.sendMatchInfoToClients();

            if (matchManager.isMatchFinished()) break;
        }
    }

    private void matchEnumInputHandler(ClientMatchEnumPacket packet) {

        switch (packet.getPacket()) {

            case END_TURN:
                matchManager.endTurn(this);
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

    public ClientPacket getPacketFromClient() {

        try {
            return YaGsonChanger.readClientPacket(bufferedReader.readLine());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendPacketToClient(ServerPacket serverPacket) {

        try {
            bufferedWriter.write(YaGsonChanger.write(serverPacket));
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            close();
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

    public void saveDeckInDefaultMode(Deck deck) {
        try {
            FileOutputStream fos = new FileOutputStream("defaultDecks/mode1.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // write object to file
            oos.writeObject(deck);
            System.out.println("saved");
            // closing resources
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
