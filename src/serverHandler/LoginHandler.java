package serverHandler;

import models.Account;
import models.LoginMenu;
import packet.clientPacket.ClientLoginPacket;
import packet.serverPacket.ServerLogPacket;
import request.accountMenuRequest.AccountError;

public class LoginHandler {
    String message = null;

    public ServerLogPacket LoginHandler(ClientLoginPacket clientLoginPacket) {
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
            Account account = LoginMenu.getInstance().login(userName, password);

            if (account == null)
                message = AccountError.PASSWORD_IS_INCORRECT.toString();

        } else
            message = AccountError.USERNAME_DOESENT_EXIST.toString();
    }
}