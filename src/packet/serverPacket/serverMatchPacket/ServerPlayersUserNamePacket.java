package packet.serverPacket.serverMatchPacket;

import packet.serverPacket.ServerPacket;

public class ServerPlayersUserNamePacket extends ServerMatchPacket {

    private String player1UserName, player2UserName;

    public String getPlayer1UserName() {
        return player1UserName;
    }

    public String getPlayer2UserName() {
        return player2UserName;
    }

    public ServerPlayersUserNamePacket(String player1UserName, String player2UserName) {

        this.player1UserName = player1UserName;
        this.player2UserName = player2UserName;
    }
}
