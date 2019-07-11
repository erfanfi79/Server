import models.*;
import models.GamePlay.GameLogic;
import models.GamePlay.Match;
import models.GamePlay.MatchResult;
import packet.serverPacket.ServerEnumPacket;
import packet.serverPacket.ServerLogPacket;
import packet.serverPacket.serverMatchPacket.*;
import view.battleView.BattleLog;

import java.util.ArrayList;
import java.util.Collections;

import static packet.serverPacket.ServerEnum.MATCH_ENDED;
import static packet.serverPacket.ServerEnum.MULTI_PLAYER_GAME_IS_READY;

public class MatchManager {

    private Match match;
    private GameLogic gameLogic;
    private ClientThread clientThread1, clientThread2;

    private boolean isMultiPlayer = true;

    public MatchManager(ClientThread clientThread1, ClientThread clientThread2) {       //multiPlayer

        this.clientThread1 = clientThread1;
        this.clientThread2 = clientThread2;
        match = new Match(clientThread1.getAccount(), clientThread2.getAccount());
        gameLogic = match.getGameLogic();
    }

    public MatchManager(ClientThread clientThread1) {       //singlePlayer

        isMultiPlayer = false;
        this.clientThread1 = clientThread1;
        match = new Match(clientThread1.getAccount(), Account.getAIAccount());
        gameLogic = match.getGameLogic();
    }

    public void sendStartMultiPlayerMatchPacketToClients() {

        ServerEnumPacket serverEnumPacket = new ServerEnumPacket(MULTI_PLAYER_GAME_IS_READY);
        clientThread1.sendPacketToClient(serverEnumPacket);
        clientThread2.sendPacketToClient(serverEnumPacket);
    }

    public void sendPlayersNameToClients() {

        ServerPlayersUserNamePacket packet;

        if (isMultiPlayer)
            packet = new ServerPlayersUserNamePacket(
                    clientThread1.getAccount().getUserName(), clientThread2.getAccount().getUserName());
        else
            packet = new ServerPlayersUserNamePacket(clientThread1.getAccount().getUserName(), "AI");

        clientThread1.sendPacketToClient(packet);
        if (isMultiPlayer) clientThread2.sendPacketToClient(packet);
    }

    public void sendMatchInfoToClients() {

        ServerMatchInfoPacket packet = new ServerMatchInfoPacket();
        Cell[][] cells = match.getTable().getCells();
        VirtualCard[][] table = new VirtualCard[5][9];

        for (int row = 0; row < 5; row++)
            for (int column = 0; column < 9; column++)
                if (cells[row][column].getCard() != null)
                    table[row][column] = getVirtualCard((Unit) cells[row][column].getCard());

        packet.setTable(table, match.getPlayer1Mana(), match.getPlayer2Mana());

        clientThread1.sendPacketToClient(packet);
        if (isMultiPlayer) clientThread2.sendPacketToClient(packet);
    }

    public void move(ClientThread client, Coordination start, Coordination destination) {

        Card card = match.getTable().getCellByCoordination(start).getCard();
        Cell destinationCell = match.getTable().getCellByCoordination(destination);

        if (!isMoveRequestValid(client, card, destinationCell)) return;
        gameLogic.moveProcess(card, destinationCell);
        sendMovePacketToClients((Unit) card, start, destination);
    }

    private boolean isMoveRequestValid(ClientThread client, Card card, Cell destinationCell) {

        if (card == null || destinationCell == null) {
            System.err.println("In MatchManager in isMoveRequestValid we have null");
            return false;
        }
        if (!isCellAvailableForMove(card.getCell(), destinationCell)) {
            sendServerLogToClient(client, "Target isn't available for move");
            return false;
        }
        if (isUnitStunned((Unit) card)) {
            sendServerLogToClient(client, "Unit is stun");
            return false;
        }
        if (isMovedPreviously(card)) {
            sendServerLogToClient(client, "Unit moved previously");
            return false;
        }
        if (isAttackedPreviously(card)) {
            sendServerLogToClient(client, "Unit attacked previously");
            return false;
        }
        if (!isDirectionWithoutEnemyForMove(card.getCell(), destinationCell)) {
            sendServerLogToClient(client, "Unit can not move");
            return false;
        }
        return true;
    }


    public void attack(ClientThread client, Coordination attackerCoordination, Coordination defenderCoordination) {

        Unit attacker = (Unit) match.getTable().getCellByCoordination(attackerCoordination).getCard();
        Unit defender = (Unit) match.getTable().getCellByCoordination(defenderCoordination).getCard();

        if (!isAttackRequestValid(client, attacker, defender)) return;
        gameLogic.attack(attacker, defender);
        sendAttackPacketToClients(attacker, attackerCoordination);
    }

    private boolean isAttackRequestValid(ClientThread client, Unit attacker, Unit defender) {

        if (attacker == null || defender == null) {
            System.err.println("In MatchManager in isAttackRequestValid we have null");
            return false;
        }
        switch (attacker.getUnitType()) {

            case MELEE:
                if (!Cell.isTargetCellAvailableForMeleeAttack(attacker.getCell(), defender.getCell())) {
                    sendServerLogToClient(client, "This target not available for melee unit");
                    return false;
                }
                break;

            case RANGED:
                if (!Cell.isTargetCellAvailableForRangedAttack(attacker.getCell(), defender.getCell(), attacker.getRange())) {
                    sendServerLogToClient(client, "This target not available for melee unit");
                    return false;
                }
        }
        return true;
    }


    public void insert(ClientThread client, int whichHandCard, Coordination targetCoordination) {

        Card card = null;
        if (whichHandCard >= 0 && whichHandCard < 5)
            card = client.getAccount().getHand().getHandCards().get(whichHandCard);

        Cell target = match.getTable().getCellByCoordination(targetCoordination);
        if (!isInsertRequestValid(client, card, target)) return;
        if (card instanceof Unit) gameLogic.insertProcess((Unit) card, target);
        else gameLogic.insertProcess((Spell) card, target);
    }

    private boolean isInsertRequestValid(ClientThread client, Card card, Cell target) {

        if (card == null || target == null) {
            System.err.println("In MatchManager in isInsertRequestValid we have null");
            return false;
        }
        if (!hasEnoughMana(card)) {
            sendServerLogToClient(client, "You don't have enough mana");
            return false;
        }

        if (card instanceof Unit) {

            if (isCellFill(target)) {
                sendServerLogToClient(client, "Target is fill");
                return false;
            }
            if (!isCellAvailableForInsert(target.getCoordination())) {
                sendServerLogToClient(client, "Target is not available");
                return false;
            }

        } else {

            for (Spell spell : card.getSpells()) {

                if (!spell.getTarget().isAffectCells()) {   //for minions and hero

                    if (!isCellFill(target)) {
                        sendServerLogToClient(client, "Cell is empty");
                        return false;
                    }
                    if (spell.getTarget().isTargetEnemy() &&
                            !target.getCard().getTeam().equals(match.findPlayerDoesNotPlayingThisTurn().getUserName())) {
                        if (card.getSpells().size() == 1) {
                            sendServerLogToClient(client, "It is your unit");
                            return false;
                        }
                    }
                    if (!spell.getTarget().isTargetEnemy() &&
                            target.getCard().getTeam().equals(match.findPlayerDoesNotPlayingThisTurn().getUserName())) {
                        if (card.getSpells().size() == 1) {
                            sendServerLogToClient(client, "It is unit of enemy");
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }


    public void endTurn(ClientThread clientThread) {

        gameLogic.switchTurn();

        if (clientThread == clientThread2) sendStartYourTurnToClient(clientThread1);
        else if (isMultiPlayer) sendStartYourTurnToClient(clientThread2);

        if (!isMultiPlayer) {
            playAI();
            gameLogic.switchTurn();
            sendStartYourTurnToClient(clientThread1);
        }

        sendGraveYardToClient(clientThread1);
        if (isMultiPlayer) sendGraveYardToClient(clientThread2);
    }


    public void useSpecialPower() {
        gameLogic.useSpecialPower((Unit) match.findPlayerPlayingThisTurn().getHand().getHero());
    }


    void sendGraveYardToClient(ClientThread client) {

        if (client.getAccount().getUserName().equals(match.getPlayer1().getUserName()))
            client.sendPacketToClient(new ServerGraveYardPacket(match.getPlayer1GraveYard().getDeadCards()));
        else
            client.sendPacketToClient(new ServerGraveYardPacket(match.getPlayer2GraveYard().getDeadCards()));
    }


    boolean isMatchFinished() {

        MatchResult matchResult = gameLogic.getMatchResult();
        if (matchResult == MatchResult.MATCH_HAS_NOT_ENDED) return false;

        History historyForPlayer1 = new History();
        History historyForPlayer2 = new History();
        historyForPlayer1.setLocalDateTime();
        historyForPlayer2.setLocalDateTime();

        switch (matchResult) {

            case DRAW:
                historyForPlayer1.setYourStatus(GameStatus.DRAW);
                if (isMultiPlayer) {
                    historyForPlayer2.setYourStatus(GameStatus.DRAW);
                    sendServerLogToClient(clientThread2, "GAME DRAW");
                }
                sendServerLogToClient(clientThread1, "GAME DRAW");
                break;

            case PLAYER1:
                clientThread1.getAccount().incrementWinsNumber();
                clientThread1.getAccount().incrementMoney(1000 /*awardOfGame*/);
                historyForPlayer1.setYourStatus(GameStatus.WIN);
                if (isMultiPlayer) {
                    historyForPlayer2.setYourStatus(GameStatus.LOST);
                    sendServerLogToClient(clientThread2, "YOU LOST");
                }
                sendServerLogToClient(clientThread1, "YOU WIN");
                break;

            case PLAYER2:
                if (isMultiPlayer) {
                    clientThread2.getAccount().incrementWinsNumber();
                    clientThread2.getAccount().incrementMoney(1000 /*awardOfGame*/);
                    historyForPlayer2.setYourStatus(GameStatus.WIN);
                    sendServerLogToClient(clientThread2, "YOU WIN");
                }
                historyForPlayer1.setYourStatus(GameStatus.LOST);
                sendServerLogToClient(clientThread1, "YOU LOST");
                break;

        }

        clientThread1.getAccount().getMatchHistories().add(historyForPlayer1);
        clientThread1.sendPacketToClient(new ServerEnumPacket(MATCH_ENDED));

        if (isMultiPlayer) {
            clientThread2.getAccount().getMatchHistories().add(historyForPlayer2);
            clientThread2.sendPacketToClient(new ServerEnumPacket(MATCH_ENDED));
        }
        return true;
    }


    private void sendServerLogToClient(ClientThread client, String log) {

        ServerLogPacket serverLogPacket = new ServerLogPacket();
        serverLogPacket.setLog(log);
        client.sendPacketToClient(serverLogPacket);
    }

    private void sendMovePacketToClients(Unit card, Coordination start, Coordination destination) {

        ServerMovePacket serverMovePacket = new ServerMovePacket(getVirtualCard(card), start, destination);
        clientThread1.sendPacketToClient(serverMovePacket);
        if (isMultiPlayer) clientThread2.sendPacketToClient(serverMovePacket);
    }

    private void sendAttackPacketToClients(Unit card, Coordination coordination) {

        ServerAttackPacket serverAttackPacket = new ServerAttackPacket(getVirtualCard(card), coordination);
        clientThread1.sendPacketToClient(serverAttackPacket);
        if (isMultiPlayer) clientThread2.sendPacketToClient(serverAttackPacket);
    }

    void sendStartYourTurnToClient(ClientThread clientThread) {
        clientThread.sendPacketToClient(new ServerMatchEnumPacket(ServerMatchEnum.START_YOUR_TURN));
    }

    void sendNewHandToClient(ClientThread clientThread) {

        ArrayList<Card> handCards = clientThread.getAccount().getHand().getHandCards();
        VirtualCard[] handVirtualCards = new VirtualCard[6];

        for (int i = 0; i < handCards.size(); i++) {

            if (handCards.get(i) instanceof Unit)
                handVirtualCards[i] = getVirtualCard((Unit) handCards.get(i));
            else if (handCards.get(i) instanceof Spell)
                handVirtualCards[i] = getVirtualCard((Spell) handCards.get(i));
        }

        Card reserveCard = clientThread.getAccount().getHand().getReserveCard();

        if (reserveCard instanceof Unit) handVirtualCards[5] = getVirtualCard((Unit) reserveCard);
        else if (reserveCard instanceof Spell) handVirtualCards[5] = getVirtualCard((Spell) reserveCard);

        clientThread.sendPacketToClient(new ServerHandPacket(handVirtualCards));
    }


    /*
    down methods are logic of match
     */


    private boolean isAttackedPreviously(Card card) {

        ArrayList<Card> attackedCards = gameLogic.getAttackedCardsInATurn();

        for (Card attackedCard : attackedCards) {

            if (attackedCard.getCardName().equals(card.getCardName())) return true;
        }

        return false;
    }

    private boolean isMovedPreviously(Card card) {

        ArrayList<Card> movedCards = gameLogic.getMovedCardsInATurn();

        for (Card movedCard : movedCards) {

            if (movedCard.getCardName().equals(card.getCardName())) return true;
        }

        return false;
    }

    private boolean isCellAvailableForMove(Cell origin, Cell destination) {

        if (isCellFill(destination)) return false;
        return Cell.getManhattanDistance(origin, destination) <= 2;
    }

    private boolean isUnitStunned(Unit unit) {

        return unit.isCanMove();
    }

    private boolean hasEnoughMana(Card card) {

        if (match.findPlayerPlayingThisTurn().equals(match.getPlayer1())) {

            return card.getManaCost() <= match.getPlayer1Mana();

        } else {

            return card.getManaCost() <= match.getPlayer2Mana();
        }
    }

    private boolean isCellFill(Cell cell) {

        return cell.getCard() != null;
    }

    private boolean isDirectionWithoutEnemyForMove(Cell origin, Cell destination) {

        if (Cell.getManhattanDistance(origin, destination) == 2) {

            if (Math.abs(origin.getCoordination().getRow() - destination.getCoordination().getRow()) == 1) {
                //means direction is diagonal

                Coordination coordination1 = Coordination.getNewCoordination(
                        origin.getCoordination().getRow(), destination.getCoordination().getColumn());
                Coordination coordination2 = Coordination.getNewCoordination(
                        destination.getCoordination().getRow(), origin.getCoordination().getColumn());

                Card card1InThisCoordination = match.getTable().getCellByCoordination(coordination1).getCard();
                Card card2InThisCoordination = match.getTable().getCellByCoordination(coordination2).getCard();

                try {
                    if (!card1InThisCoordination.getTeam().equals(match.findPlayerPlayingThisTurn()))
                        if (!card2InThisCoordination.getTeam().equals(match.findPlayerPlayingThisTurn()))
                            return false;

                } catch (NullPointerException e) {
                    return true;
                }
                return true;

            } else {

                Coordination coordination = Coordination.getNewCoordination(
                        (origin.getCoordination().getRow() + destination.getCoordination().getRow()) / 2,
                        (origin.getCoordination().getColumn() + destination.getCoordination().getColumn()) / 2);

                try {
                    if (!match.getTable().getCellByCoordination(coordination).getCard()
                            .getTeam().equals(match.findPlayerPlayingThisTurn().getUserName()))
                        return false;

                } catch (NullPointerException e) {
                    return true;
                }
                return true;
            }
        } else return true;
    }

    private boolean isCellAvailableForInsert(Coordination coordination) {

        Table table = match.getTable();
        Coordination aroundCoordination;

        for (int row = -1; row <= 1; row++) {
            for (int column = -1; column <= 1; column++) {

                if (row == 0 && column == 0) continue;

                try {
                    aroundCoordination = Coordination.getNewCoordination(
                            coordination.getRow() + row, coordination.getColumn() + column);

                    if (table.getCellByCoordination(aroundCoordination).getCard().getTeam().equals(
                            match.findPlayerPlayingThisTurn().getUserName())) return true;

                } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                }
            }
        }

        BattleLog.errorCellNotAvailable();
        return false;
    }

    private VirtualCard getVirtualCard(Unit card) {
        return new VirtualCard(
                card.getCardName(), card.getManaCost(), card.getHealthPoint(), card.getAttackPoint());
    }

    private VirtualCard getVirtualCard(Spell card) {
        return new VirtualCard(
                card.getCardName(), card.getManaCost(), 0, 0);
    }

    private void playAI() {

        Card hero = match.getPlayer2().getHand().getHero();

        ArrayList<Integer> rows = new ArrayList<>();
        rows.add(-2);
        rows.add(-1);
        rows.add(0);
        rows.add(1);
        rows.add(2);
        Collections.shuffle(rows);

        for (Integer row : rows) {
            for (int column = -2 + Math.abs(row); column <= 2 - Math.abs(row); column++) {

                if (row == 0 && column == 0) continue;

                try {
                    Coordination heroCoordination = hero.getCell().getCoordination();
                    Coordination destinationCoordination = Coordination.getNewCoordination(
                            heroCoordination.getRow() + row, heroCoordination.getColumn() + column);
                    Cell destinationCell = match.getTable().getCellByCoordination(destinationCoordination);

                    //check validity of destination
                    if (!isCellAvailableForMove(hero.getCell(), destinationCell)) continue;
                    if (isUnitStunned((Unit) hero)) continue;
                    if (isAttackedPreviously(hero)) continue;
                    if (isMovedPreviously(hero)) continue;
                    if (!isDirectionWithoutEnemyForMove(hero.getCell(), destinationCell))
                        continue;

                    //move
                    gameLogic.moveProcess(hero, destinationCell);
                    sendMovePacketToClients((Unit) hero, heroCoordination, destinationCoordination);

                } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                }
            }
        }
    }
}
