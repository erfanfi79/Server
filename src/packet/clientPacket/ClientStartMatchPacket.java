package packet.clientPacket;

public class ClientStartMatchPacket extends ClientPacket{

    private boolean isMultiPlayerGame;
    private int gameMode, flagsNumber;

    public int getFlagsNumber() {
        return flagsNumber;
    }

    public int getGameMode() {
        return gameMode;
    }

    public boolean isMultiPlayerGame() {
        return isMultiPlayerGame;
    }

    public void setFlagsNumber(int flagsNumber) {
        this.flagsNumber = flagsNumber;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public void setMultiPlayerGame(boolean multiPlayerGame) {
        isMultiPlayerGame = multiPlayerGame;
    }
}
