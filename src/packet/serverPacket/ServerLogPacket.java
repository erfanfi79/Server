package packet.serverPacket;

import java.io.Serializable;

public class ServerLogPacket extends ServerPacket implements Serializable {

    private boolean isSuccessful=false;
    private String log;

    public String getLog() {
        return log;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }
}
