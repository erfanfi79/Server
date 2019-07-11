package models;

import java.util.ArrayList;

public class GlobalShop {
    private static GlobalShop globalShop;
    private Collection shopCollection;
    private int[] limitNumOfShop;
    private int limit = 5;

    private GlobalShop() {
        shopCollection = JsonToCard.initializeShopCollection();
        limitNumOfShop = new int[shopCollection.getCards().size()];
        for (int i = 0; i < limitNumOfShop.length; i++)
            limitNumOfShop[i] = limit;
    }

    public static GlobalShop getGlobalShop() {
        if (globalShop == null)
            globalShop = new GlobalShop();
        return globalShop;
    }

    public synchronized ShopError buy(String cardName, Account account) {
        for (int i = 0; i < shopCollection.getCards().size(); i++) {
            Card card = shopCollection.getCards().get(i);
            if (card.getCardName().equals(cardName)) {
                if (limitNumOfShop[i] <= 0)
                    return ShopError.CARD_CURRENTLY_DOESENT_EXIST;
                if (card.getType().equals(CardType.USABLE_ITEM) &&
                        checkBuyItem(account.getCollection().getCards())) {
                    return ShopError.ALREADY_3_ITEM;
                }
                if (account.getMoney() - card.getPrice() >= 0) {
                    Card newCard = Card.deepClone(card);
                    account.setID(newCard);
                    account.getCollection().getCards().add(newCard);
                    account.setMoney(account.getMoney() - card.getPrice());
                    limitNumOfShop[i]--;
                    return ShopError.SUCCESS;
                } else
                    return ShopError.NOT_ENOUGH_MONEY;
            }
        }
        return ShopError.CARD_NOT_FOUND;
    }

    public synchronized void addToShop(String cardName) {
        for (int i = 0; i < shopCollection.getCards().size(); i++)
            if (shopCollection.getCards().get(i).getCardName().equals(cardName)) {
                limitNumOfShop[i]++;
                return;
            }
    }

    public Collection getShopCollection() {
        Collection limitedCollection = new Collection();
        for (int i = 0; i < shopCollection.getCards().size(); i++)
            if (limitNumOfShop[i] > 0)
                limitedCollection.getCards().add(shopCollection.getCards().get(i));

        return limitedCollection;
    }

    private boolean checkBuyItem(ArrayList<Card> cards) {
        int numOfItemInCollection = 0;
        for (Card card : cards)
            if (card.getType().equals(CardType.USABLE_ITEM))
                numOfItemInCollection++;

        return numOfItemInCollection >= 3;
    }

    public int getCardCost(String name) {
        for (Card card : shopCollection.getCards())
            if (card.getCardName().equals(name))
                return card.getPrice();

        return 0;
    }

    public Card getCardByName(String name) {
        for (Card card : shopCollection.getCards())
            if (card.getCardName().equals(name)) {
                Card newCard = Card.deepClone(card);
                return newCard;
            }
        return null;
    }
}
