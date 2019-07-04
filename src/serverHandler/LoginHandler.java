package serverHandler;

import models.Account;
import models.LoginMenu;
import packet.clientPacket.ClientLoginPacket;
import packet.serverPacket.ServerLogPacket;
import request.accountMenuRequest.AccountError;

public class LoginHandler {
    String message = null;
    ClientLoginPacket clientLoginPacket;

    public LoginHandler(ClientLoginPacket clientLoginPacket) {
        this.clientLoginPacket = clientLoginPacket;
    }

    public ServerLogPacket handleLogin() {
        if (clientLoginPacket.isLogin)
            signIn(clientLoginPacket.userName, clientLoginPacket.password);
        else
            signUp(clientLoginPacket.userName, clientLoginPacket.password);
        ServerLogPacket serverLogPacket = new ServerLogPacket();
        if (message == null)
            serverLogPacket.setSuccessful(true);
        serverLogPacket.setLog(message);
        return serverLogPacket;
    }

    public void signIn(String userName, String password) {
        if (LoginMenu.getInstance().checkIfAccountExist(userName)) {
            Account account = LoginMenu.getInstance().login(userName, password);

            if (account == null)
                message = AccountError.PASSWORD_IS_INCORRECT.toString();

        } else
            message = AccountError.USERNAME_DOESENT_EXIST.toString();
    }

    public void signUp(String userName, String password) {
        if (LoginMenu.getInstance().checkIfAccountExist(userName)) {
            message = AccountError.USERNAME_ALREADY_EXIST.toString();
            return;
        }
        Account account = LoginMenu.getInstance().createAccount(userName, password);
        Account.save(account);
    }
}
