package packet.serverPacket;

public class ServerLogPacket extends ServerPacket {

    private boolean isSuccessful = false;
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
