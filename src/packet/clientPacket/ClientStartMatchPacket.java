package packet.clientPacket;

public class ClientStartMatchPacket extends ClientPacket{

    private boolean isMultiPlayerGame;

    public boolean isMultiPlayerGame() {
        return isMultiPlayerGame;
    }

    public void setMultiPlayerGame(boolean multiPlayerGame) {
        isMultiPlayerGame = multiPlayerGame;
    }
}
