package packet.clientPocket;

public class ClientLoginPacket extends ClientPocket {

    String userName, password;
    boolean isLogin;

    public ClientLoginPacket(String userName, String password,boolean isLogin) {
        this.userName = userName;
        this.password = password;
        this.isLogin=isLogin;
    }
}
