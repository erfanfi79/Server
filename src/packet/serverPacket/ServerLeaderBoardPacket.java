package packet.serverPacket;

import java.util.ArrayList;

public class ServerLeaderBoardPacket extends ServerPacket {
    ArrayList<String> usernames=new ArrayList<>();
    ArrayList<Integer> winNumber=new ArrayList<>();

    public void setUsernames(ArrayList<String> usernames) {
        this.usernames = usernames;
    }

    public void setWinNumber(ArrayList<Integer> winNumber) {
        this.winNumber = winNumber;
    }

    public ArrayList<Integer> getWinNumber() {
        return winNumber;
    }

    public ArrayList<String> getUsernames() {
        return usernames;
    }
}
