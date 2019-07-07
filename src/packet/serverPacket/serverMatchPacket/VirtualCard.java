package packet.serverPacket.serverMatchPacket;

public class VirtualCard {

    private String cardName;
    private int manaPoint, healthPoint, attackPoint;

    public int getAttackPoint() {
        return attackPoint;
    }

    public int getHealthPoint() {
        return healthPoint;
    }

    public int getManaPoint() {
        return manaPoint;
    }

    public String getCardName() {
        return cardName;
    }

    public VirtualCard(String cardName, int manaPoint, int healthPoint, int attackPoint) {

        this.cardName = cardName;
        this.manaPoint = manaPoint;
        this.healthPoint = healthPoint;
        this.attackPoint = attackPoint;
    }
}
