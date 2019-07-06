package models.GamePlay;

import models.Card;

import java.util.ArrayList;

public class GraveYard {

    private ArrayList<Card> deadCards = new ArrayList<>();

    public ArrayList<Card> getDeadCards() {

        return deadCards;
    }

    public void addCardToGraveYard(Card card) {

        deadCards.add(card);
    }
}
