import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8888);

        while(true) {

            System.err.println("Waiting for connect a client ...");

            ClientThread clientThread = new ClientThread(serverSocket.accept());
            clientThread.start();

            System.err.println("Client connected");
        }
    }
}
