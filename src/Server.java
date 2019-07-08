import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;

public class Server {

    private static LinkedList<ClientThread> onlineUsers = new LinkedList<>();
    private static LinkedList<ClientThread> waitersForMultiPlayerGame = new LinkedList<>();

    public static LinkedList<ClientThread> getOnlineUsers() {
        return onlineUsers;
    }

    public static LinkedList<ClientThread> getWaitersForMultiPlayerGame() {
        return waitersForMultiPlayerGame;
    }


    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8888);

        while (true) {

            System.err.println("Waiting for connect a client ...");

            ClientThread clientThread = new ClientThread(serverSocket.accept());
            clientThread.start();
            //todo delete from online users if exception

            System.err.println("Client connected");
        }
    }
}

//todo show online user in server
