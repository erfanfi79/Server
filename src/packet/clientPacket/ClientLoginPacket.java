package packet.clientPacket;

import java.io.Serializable;

public class ClientLoginPacket extends ClientPacket implements Serializable {

    public String userName, password;
    public boolean isLogin;

    public ClientLoginPacket(String userName, String password,boolean isLogin) {
        this.userName = userName;
        this.password = password;
        this.isLogin=isLogin;
    }
}
