package packet.serverPacket;

import models.History;

import java.util.ArrayList;

public class ServerMatchHistory extends ServerPacket{
    ArrayList<History> histories;

    public void setHistories(ArrayList<History> histories) {
        this.histories = histories;
    }

    public ArrayList<History> getHistories() {
        return histories;
    }
}
