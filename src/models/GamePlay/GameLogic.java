package models.GamePlay;

import controller.BattleController;
import models.*;

import java.util.ArrayList;
import java.util.Random;

import static models.GamePlay.MatchResult.*;
import static models.SpecialPowerType.ON_DEFEND;

public class GameLogic {

    ArrayList<Card> attackedCardsInATurn = new ArrayList<>();      //todo add attacker to array
    ArrayList<Card> movedCardsInATurn = new ArrayList<>();
    ArrayList<Card> cardsInTablePlayer1 = new ArrayList<>(); //todo fill that in game and delete when minion die
    ArrayList<Card> cardsInTablePlayer2 = new ArrayList<>();
    private Match match;

    GameLogic(Match match) {

        this.match = match;
    }

    public ArrayList<Card> getCardsInTablePlayerPlayingThisTurn() {

        if (match.findPlayerPlayingThisTurn().equals(match.player1))
            return getCardsInTablePlayer1();
        else
            return getCardsInTablePlayer2();
    }

    public ArrayList<Card> getCardsInTablePlayerDoesNotPlayingThisTurn() {

        if (match.findPlayerPlayingThisTurn().equals(match.player1))
            return getCardsInTablePlayer2();
        else
            return getCardsInTablePlayer1();
    }

    public ArrayList<Card> getCardsInTablePlayer2() {

        return cardsInTablePlayer2;
    }

    public ArrayList<Card> getCardsInTablePlayer1() {

        return cardsInTablePlayer1;
    }

    public ArrayList<Card> getAttackedCardsInATurn() {

        return attackedCardsInATurn;
    }

    public ArrayList<Card> getMovedCardsInATurn() {

        return movedCardsInATurn;
    }

    public void moveProcess(Card card, Cell destinationCell) {

        card.getCell().setCard(null);
        destinationCell.setCard(card);
        card.setCell(destinationCell);
        movedCardsInATurn.add(card);
    }


    public MatchResult getMatchResult() {

        ArrayList<Card> player1Cards = match.player1.getCollection().getSelectedDeck().getCards();
        ArrayList<Card> player2Cards = match.player2.getCollection().getSelectedDeck().getCards();
        ArrayList<Unit> player1Units = new ArrayList<>();
        ArrayList<Unit> player2Units = new ArrayList<>();

        for (Card card : player1Cards)
            if (card instanceof Unit)
                player1Units.add((Unit) card);

        for (Card card : player2Cards)
            if (card instanceof Unit)
                player2Units.add((Unit) card);

        return getMatchResultForKillTheHero();
    }

    private MatchResult getMatchResultForKillTheHero() {

        int player1HeroHP = ((Unit) match.getPlayer1().getHand().getHero()).getHP();
        int player2HeroHP = ((Unit) match.getPlayer2().getHand().getHero()).getHP();

        if (player1HeroHP <= 0 && player2HeroHP <= 0) return DRAW;
        if (player1HeroHP <= 0){
            BattleController.getInstance().setIsEndedGame(2);
            return PLAYER2;
        }
        if (player2HeroHP <= 0) {
            BattleController.getInstance().setIsEndedGame(1);
            return PLAYER1;
        }

        return MATCH_HAS_NOT_ENDED;
    }

    public void insertProcess(Unit unit, Cell cell) {

        cell.setCard(unit);
        unit.setCell(cell);
        decrementMana(unit.getManaCost());

        if (match.findPlayerPlayingThisTurn().equals(match.player1)) {

            match.getPlayer1().getHand().removeFromHand(unit);
            cardsInTablePlayer1.add(unit);
        } else {

            match.getPlayer2().getHand().removeFromHand(unit);
            cardsInTablePlayer2.add(unit);
        }
        if (unit.getSpecialPowerType() == SpecialPowerType.ON_SPAWN) {
            if (unit.getSpells() != null) {
                applySpell(unit.getSpells().get(0),
                        findTarget(unit.getSpells().get(0), cell, cell,
                                match.findPlayerPlayingThisTurn().getHand().getHero().getCell()));
            }
        }
    }

    public void insertProcess(Spell spell, Cell cell) {

        //todo some cell must fill and some cell must empty for poison
        if (match.findPlayerPlayingThisTurn().equals(match.player1))
            match.getPlayer1().getHand().removeFromHand(spell);
        else
            match.getPlayer2().getHand().removeFromHand(spell);

        decrementMana(spell.getManaCost());
        applySpell(spell, findTarget(spell, cell, cell,
                match.findPlayerPlayingThisTurn().getHand().getHero().getCell()));
    }

    private void decrementMana(int mana) {

        if (match.findPlayerPlayingThisTurn().equals(match.getPlayer1()))
            match.player1Mana -= mana;
        else
            match.player2Mana -= mana;
    }

    public void useItem(Spell spell, Cell cell) {
        applySpell(spell, findTarget(spell, cell, cell,
                match.findPlayerPlayingThisTurn().getHand().getHero().getCell()));
    }

    public void switchTurn() {
        match.turnNumber++;
        manaHandler();
        match.findPlayerPlayingThisTurn().getHand().fillHandEmptyPlace();
        attackedCardsInATurn.clear();
        movedCardsInATurn.clear();
    }

    private void manaHandler() {

        if (match.findPlayerPlayingThisTurn().equals(match.getPlayer1())) {

            if (match.turnNumber <= 15) {
                match.initialPlayer1ManaInBeginningOfEachTurn++;
            }
            match.player1Mana = match.initialPlayer1ManaInBeginningOfEachTurn;

        } else {

            if (match.turnNumber <= 14) {
                match.initialPlayer2ManaInBeginningOfEachTurn++;
            }
            match.player2Mana = match.initialPlayer2ManaInBeginningOfEachTurn;
        }
    }


    public void attack(Unit attacker, Unit defender) {

        if (!(defender.isNoDamageFromWeakers() && attacker.getAP() > defender.getAP())) {
            damage(attacker, defender);
            useOnAttackSpells(attacker, defender);
            useOnDefendSpells(defender, attacker);
            try {
                counterAttack(defender, attacker);
            } catch (NullPointerException e) {
            }
        }
    }


    private void cancelPositiveBuffs(Unit unit) {

        for (Buff buff : unit.getBuffs()) {
            if (buff.isPositive() && buff.getDuration() >= 0 && !buff.isLasts()) {
                unit.removeBuff(buff);
            }
        }
    }

    private void cancelNegativeBuffs(Unit unit) {

        for (Buff buff : unit.getBuffs()) {
            if (!buff.isPositive() && buff.getDuration() >= 0) {
                unit.removeBuff(buff);
            }
        }
    }

    private void killUnit(Unit unit) {

        ActivateOnDeathSpells(unit);
        unit.getCell().setCard(null);
        unit.setCell(null);
        if (unit.getTeam().equals(match.player1.getUserName())) {
            cardsInTablePlayer1.remove(unit);
            match.getPlayer1GraveYard().addCardToGraveYard(unit);
        } else {
            cardsInTablePlayer2.remove(unit);
            match.getPlayer2GraveYard().addCardToGraveYard(unit);
        }

    }

    private void ActivateOnDeathSpells(Unit unit) {

        Coordination coordination = new Coordination();
        coordination.setColumn(0);
        coordination.setRow(0);
        if (unit.getSpecialPowerType() == ON_DEFEND) {
            for (Spell spell : unit.getSpells()) {
                applySpell(spell, findTarget(spell, unit.getCell(),
                        match.table.getCellByCoordination(coordination),
                        match.findPlayerDoesNotPlayingThisTurn().getHand().getHero().getCell()));
            }
        }
    }

    private boolean isRangeValidForAttack(Unit attacker, Unit defender) {

        if (attacker.getUnitType() == UnitType.MELEE) {
            if (!attacker.getCell().isAdjacent(defender.getCell()))
                return false;

        } else if (attacker.getUnitType() == UnitType.RANGED) {
            if (attacker.getCell().isAdjacent(defender.getCell()))
                return false;

        } else if (attacker.getUnitType() == UnitType.HYBRID) {
            if (attacker.getCell().isAdjacent(defender.getCell()))
                return false;
        }

        return true;
    }

    ///////Target
    private TargetData findTarget(Spell spell, Cell cardCell, Cell clickCell, Cell heroCell) {

        TargetData targetData = new TargetData();
        if (spell.getTarget().isTargetEnemy()) {
            setTargetData(spell, cardCell, clickCell, match.findPlayerDoesNotPlayingThisTurn(), targetData);
        } else {
            setTargetData(spell, cardCell, heroCell, match.findPlayerPlayingThisTurn(), targetData);
        }
        if (spell.getTarget().isRandom()) {
            if (targetData.getCards().size() > 0) {
                Card card = targetData.getCards().get(new Random().nextInt(targetData.getCards().size()));
                targetData.getCards().clear();
                targetData.getCards().add(card);
            }
        }
        return targetData;
    }


    private void setTargetData(Spell spell, Cell cardCell, Cell clickCell, Account account, TargetData targetData) {
        targetData.getAccounts().add(account);

        if (spell.getTarget().getRowsAffected() != 0 && spell.getTarget().getColumnsAffected() != 0) {

            Coordination centerPosition = getCenter(spell, cardCell, clickCell);
            Coordination coordination = new Coordination();
            coordination.setRow(spell.getTarget().getRowsAffected());
            coordination.setColumn(spell.getTarget().getColumnsAffected());
            ArrayList<Cell> targetCells = detectCells(centerPosition, coordination);
            addUnitsAndCellsToTargetData(spell, targetData, targetCells);

            if (spell.getTarget().isRandom()) {
                randomizeList(targetData.getUnits());
                randomizeList(targetData.getCells());
                randomizeList(targetData.getAccounts());
                randomizeList(targetData.getCards());
            }
        }
    }


    private void addUnitsAndCellsToTargetData(Spell spell, TargetData targetData, ArrayList<Cell> targetCells) {

        for (Cell cell : targetCells) {
            if (spell.getTarget().isAffectCells()) {
                targetData.getCells().add(cell);
            }
            Card unit = cell.getCard();
            if (unit != null) {
                if (spell.getTarget().getTargetType().isHybrid() && ((Unit) unit).getUnitType() == UnitType.HYBRID) {
                    addUnitToTargetData(spell, targetData, (Unit) unit);
                }
                if (spell.getTarget().getTargetType().isMelee() & ((Unit) unit).getUnitType() == UnitType.MELEE) {
                    addUnitToTargetData(spell, targetData, (Unit) unit);
                }
                if (spell.getTarget().getTargetType().isRanged() && ((Unit) unit).getUnitType() == UnitType.RANGED) {
                    addUnitToTargetData(spell, targetData, (Unit) unit);
                }
            }
            if (spell.getTarget().isAffectCells()) {
                targetData.getCells().add(cell);
            }
        }
    }

    private void addUnitToTargetData(Spell spell, TargetData targetData, Unit unit) {

        if (spell.getTarget().isAffectHero() && unit.getType() == CardType.HERO) {
            targetData.getUnits().add(unit);
        }
        if (spell.getTarget().isAffectMinion() && unit.getType() == CardType.MINION) {
            targetData.getUnits().add(unit);
        }
    }


    private ArrayList<Cell> detectCells(Coordination centerPosition, Coordination dimensions) {

        int firstRow = calculateFirstCoordinate(centerPosition.getRow(), dimensions.getRow());
        int firstColumn = calculateFirstCoordinate(centerPosition.getColumn(), dimensions.getColumn());

        int lastRow = calculateLastCoordinate(firstRow, dimensions.getRow(), 5);
        int lastColumn = calculateLastCoordinate(firstColumn, dimensions.getColumn(), 9);

        ArrayList<Cell> targetCells = new ArrayList<>();
        for (int i = firstRow; i < lastRow; i++) {
            for (int j = firstColumn; j < lastColumn; j++) {
                if (match.table.isInMap(i, j))
                    targetCells.add(match.table.getCells()[i][j]);
            }
        }
        return targetCells;
    }


    private int calculateFirstCoordinate(int center, int dimension) {

        int firstCoordinate = center - (dimension - 1) / 2;
        if (firstCoordinate < 0)
            firstCoordinate = 0;
        return firstCoordinate;
    }


    private int calculateLastCoordinate(int first, int dimension, int maxNumber) {

        int lastRow = first + dimension;
        if (lastRow > maxNumber) {
            lastRow = maxNumber;
        }
        return lastRow;
    }


    private Coordination getCenter(Spell spell, Cell cardCell, Cell clickCell) {

        Coordination centerPosition;
        if (spell.getTarget().isDependentToCardLocation()) {
            centerPosition = new Coordination();
            centerPosition.copyCoordination(cardCell);
        } else {
            centerPosition = new Coordination();
            centerPosition.copyCoordination(clickCell);
        }
        return centerPosition;
    }

    private <T> void randomizeList(ArrayList<T> list) {

        if (list.size() == 0) return;

        int random = new Random().nextInt(list.size());
        T e = list.get(random);
        list.clear();
        list.add(e);
    }


    ///////////Target
    private void applySpell(Spell spell, TargetData targetData) {

        Buff buff = spell.getBuff();
        if (buff.getWaitingTime() > 0) {
            buff.setWaitingTime(buff.getWaitingTime() - 1);
            return;
        }

        castBuffOnCards(buff, targetData.getCards());
        //  castBuffOnCells(buff, targetData.getCells());
        castBuffOnUnits(buff, targetData.getUnits());
      //  castBuffOnUsers(buff, targetData.getAccounts());

        buff.decrementDuration();
    }


    private void castBuffOnUsers(Buff buff, ArrayList<Account> accounts) {
        String name = accounts.get(0).getUserName();
        if (match.player1.getUserName().equals(name)) {
            match.player1Mana += buff.getManaChange();
        } else {
            match.player2Mana += buff.getManaChange();
        }
    }


    private void castBuffOnCards(Buff buff, ArrayList<Card> cards) {
        for (Card card : cards) {
            if (buff.getItemSpell() != null) {
                card.addSpell(buff.getItemSpell());
            }
        }

    }

//    public void castBuffOnCells(Buff buff, ArrayList<Cell> cells) {
    //      ArrayList<Unit> inCellTroops = getIn
    //}


    private void useOnAttackSpells(Unit attacker, Unit defender) {
        if (attacker.getSpells() != null) {
            if (attacker.getSpecialPowerType() == SpecialPowerType.ON_ATTACK) {
                for (Spell spell : attacker.getSpells()) {
                    if (spell.getSpecialPowerType() == null || spell.getSpecialPowerType() == SpecialPowerType.ON_ATTACK) {
                        applySpell(spell, findTarget(spell, defender.getCell(), defender.getCell()
                                , match.findPlayerPlayingThisTurn().getHand().getHero().getCell()));
                    }
                }
            } else {
                for (Spell spell : attacker.getSpells()) {
                    if (spell.getSpecialPowerType() == SpecialPowerType.ON_ATTACK) {
                        applySpell(spell, findTarget(spell, defender.getCell(), defender.getCell()
                                , match.findPlayerPlayingThisTurn().getHand().getHero().getCell()));
                    }
                }
            }
        }
    }


    private void useOnDefendSpells(Unit defender, Unit attacker) {
        if (defender.getSpells() != null) {
            if (defender.getSpecialPowerType() == ON_DEFEND) {
                for (Spell spell : attacker.getSpells()) {
                    if (spell.getSpecialPowerType() == null || spell.getSpecialPowerType() == ON_DEFEND) {
                        applySpell(spell, findTarget(spell, attacker.getCell(), attacker.getCell()
                                , match.findPlayerDoesNotPlayingThisTurn().getHand().getHero().getCell()));
                    }
                }
            } else {
                for (Spell spell : attacker.getSpells()) {
                    if (spell.getSpecialPowerType() == ON_DEFEND) {
                        applySpell(spell, findTarget(spell, attacker.getCell(), attacker.getCell()
                                , match.findPlayerDoesNotPlayingThisTurn().getHand().getHero().getCell()));
                    }
                }
            }
        }
    }


    private void castBuffOnUnits(Buff buff, ArrayList<Unit> units) {
        for (Unit victimUnit : units) {
            if (buff.getDuration() <= 0) {
                return;
            }
            victimUnit.setDamageChange(victimUnit.getDamageChange() + buff.getWeaknessHP());
            victimUnit.setAP(victimUnit.getAP() - buff.getWeaknessAP());

            if (buff.getPoison() > 0 || !victimUnit.isCantBePoisoned()) {
                victimUnit.setHP(victimUnit.getHP() - buff.getWeaknessHP());
                if (victimUnit.getHP() <= 0) {
                    killUnit(victimUnit);
                }
            }

            if (buff.isStun() && !victimUnit.isCantBeStunned()) {
                victimUnit.setCanMove(false);
            }

            if (buff.isDisarm() && !victimUnit.isCantBeDisarmed()) {
                victimUnit.setDisarm(true);
            }
            if (buff.getCancelBuff() > 0) {
                cancelPositiveBuffs(victimUnit);
            } else {
                cancelNegativeBuffs(victimUnit);
            }
            if (buff.isDisarm()) {
                victimUnit.setDisarm(true);
            }
            if (buff.isStun()) {
                victimUnit.setStunned(true);
            }
            if (buff.isNoDamageFromWeakers()) {
                victimUnit.setNoDamageFromWeakers(true);
            }

            if (buff.getWeaknessAP() > 10000) {
                killUnit(victimUnit);
            }
        }
    }


    public void counterAttack(Unit defender, Unit attacker) {

        if (defender.isDisarm()) return;
        if (!isRangeValidForAttack(defender, attacker)) return;
        if (!attacker.isNoBadEffect() || (attacker.isNoDamageFromWeakers() && defender.getAP() > attacker.getAP())) {
            damage(defender, attacker);
        }
    }


    private void damage(Unit attacker, Unit defender) {
        int ap = apCompute(attacker, defender);
        defender.setHP(defender.getHP() - ap);
        if (defender.getHP() <= 0) {
            killUnit(defender);
        }
    }


    private int apCompute(Unit attacker, Unit defender) {
        int ap = attacker.getAP();
        if (defender.getDamageChange() > 0) {
            ap += defender.getDamageChange();
        }
        return ap;
    }

    public void useSpecialPower(Unit hero) {
        if (hero.getSpells() != null) {
            Spell specialPower = hero.getSpells().get(0);

            applySpell(
                    specialPower, findTarget(specialPower, hero.getCell(), hero.getCell(), hero.getCell()));
        }
    }
    public void comboAttack(Unit[] attackers, Unit defender){

        comboAttackLogic(attackers, defender);
        useOnDefendSpells(defender, attackers[0]);
        counterAttack(defender, attackers[0]);
    }

    private void comboAttackLogic(Unit[] attackers, Unit defender){
        for (Unit attacker: attackers){
            damage(attacker,defender);
            useOnAttackSpells(attacker,defender);
        }
    }
}
