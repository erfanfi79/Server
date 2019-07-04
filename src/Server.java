import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ArrayList<ClientThread> clientThreads;
    private int port;
    private boolean keepGoing;

    public Server(int port) {
        this.port = port;
        clientThreads = new ArrayList<ClientThread>();
    }

    public void start() {
        keepGoing = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (keepGoing) {
                System.err.println("Waiting for connect a client ...");
                Socket socket = serverSocket.accept();
                if (!keepGoing)
                    break;
                ClientThread t = new ClientThread(socket);
                t.start();
                System.err.println("Client connected");
            }
            try {
                serverSocket.close();

            } catch (Exception e) {
                showMessage("Exception closing the server and clients: " + e);
            }
        } catch (IOException e) {
        }
    }

    private void showMessage(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) {
        Server server = new Server(8888);
        server.start();
    }
}