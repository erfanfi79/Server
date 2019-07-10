package packet.serverPacket.serverMatchPacket;


public class ServerMatchInfoPacket extends ServerMatchPacket {

    private VirtualCard[][] table;  //Virtual[5][9]
    private int player1Mana, player2Mana;

    public VirtualCard[][] getTable() {
        return table;
    }

    public int getPlayer1Mana() {
        return player1Mana;
    }

    public int getPlayer2Mana() {
        return player2Mana;
    }

    public void setTable(VirtualCard[][] table, int player1Mana, int player2Mana) {

        this.table = table;
        this.player1Mana = player1Mana;
        this.player2Mana = player2Mana;
    }
}
