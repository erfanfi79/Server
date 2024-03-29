package models;

public enum ShopError {
    SUCCESS("the task sucsseded"),
    ALREADY_3_ITEM("you can not buy anymore Item"),
    NOT_ENOUGH_MONEY("you don't have enough money to buy this card"),
    CARD_NOT_FOUND("card not found"),
    CARD_CURRENTLY_DOESENT_EXIST("this card currently is not available");

    ShopError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private String errorMessage;

    public String toString() {
        return errorMessage;
    }
}
