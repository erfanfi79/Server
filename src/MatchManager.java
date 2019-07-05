import models.*;
import models.GamePlay.GameLogic;
import models.GamePlay.Match;
import packet.serverPacket.ServerLogPacket;
import view.battleView.BattleLog;

import java.util.ArrayList;

public class MatchManager {

    private Match match;
    private GameLogic gameLogic;

    public MatchManager(ClientThread clientThread1, ClientThread clientThread2) {

        match = new Match(MatchType.KILL_THE_HERO, clientThread1.getAccount(), clientThread2.getAccount());
        gameLogic = match.getGameLogic();
    }

    public void move(ClientThread client, Coordination start, Coordination destination) {

        Card card = match.getTable().getCellByCoordination(start).getCard();
        Cell destinationCell = match.getTable().getCellByCoordination(destination);

        if (!isMoveRequestValid(client, card, destinationCell)) return;
        gameLogic.moveProcess(card, destinationCell);
    }

    private boolean isMoveRequestValid(ClientThread client, Card card, Cell destinationCell) {

        if (card == null || destinationCell == null) {
            System.err.println("In MatchManager in isMoveRequestValid we have null");
            return false;
        }
        if (!isCellAvailableForMove(card.getCell(), destinationCell)) {
            sendServerLogToClient(client, "Target is invalid");
            return false;
        }
        if (isUnitStunned((Unit) card)) {
            sendServerLogToClient(client, "Unit is stun");
            return false;
        }
        if (isAttackedPreviously(card)) {
            sendServerLogToClient(client, "Unit attacked previously");
            return false;
        }
        if (isMovedPreviously(card)) {
            sendServerLogToClient(client, "Unit moved previously");
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



    private void sendServerLogToClient(ClientThread client, String log) {

        ServerLogPacket serverLogPacket = new ServerLogPacket();
        serverLogPacket.setLog("Target is not valid");
        client.sendPacketToClient(serverLogPacket);
    }


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
}
