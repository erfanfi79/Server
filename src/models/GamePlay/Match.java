package models.GamePlay;

import models.*;

import java.util.Random;

public class Match {

    Table table = new Table();
    Account player1, player2;
    private GraveYard Player1GraveYard = new GraveYard();
    private GraveYard Player2GraveYard = new GraveYard();
    private GameLogic gameLogic;
    int turnNumber = 1;
    int player1Mana = 2, player2Mana = 2;
    int initialPlayer1ManaInBeginningOfEachTurn = 2, initialPlayer2ManaInBeginningOfEachTurn = 2;

    public Account getPlayer1() {
        return player1;
    }

    public Account getPlayer2() {
        return player2;
    }

    public Table getTable() {
        return table;
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }

    public int getPlayer1Mana() {
        return player1Mana;
    }

    public int getPlayer2Mana() {
        return player2Mana;
    }

    public GraveYard getPlayer1GraveYard() {
        return Player1GraveYard;
    }

    public GraveYard getPlayer2GraveYard() {
        return Player2GraveYard;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public Match(Account player1, Account player2) {

        this.player1 = player1;
        this.player2 = player2;
        gameLogic = new GameLogic(this);

        initializePlayerVariables();
        initializeTableModeKillTheHero();
    }

    public Account findPlayerPlayingThisTurn() {

        if (turnNumber % 2 == 1) return player1;
        return player2;
    }

    public Account findPlayerDoesNotPlayingThisTurn() {

        if (turnNumber % 2 == 1) return player2;
        return player1;
    }

    private void initializeTableModeKillTheHero() {

        Card hero1 = player1.getHand().getHero();
        Card hero2 = player2.getHand().getHero();

        Cell cellHero1 = table.getCells()[2][1];
        Cell cellHero2 = table.getCells()[2][7];
        cellHero1.setCard(hero1);
        cellHero2.setCard(hero2);
        hero1.setCell(cellHero1);
        hero2.setCell(cellHero2);

        gameLogic.cardsInTablePlayer1.add(hero1);
        gameLogic.cardsInTablePlayer2.add(hero2);
    }

    private void initializePlayerVariables() {

        player1.getHand().initializeHand(player1.getCollection().getSelectedDeck());
        player2.getHand().initializeHand(player2.getCollection().getSelectedDeck());
    }
}
