import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import packet.serverPacket.ServerEnum;
import packet.serverPacket.ServerEnumPacket;

import java.net.ServerSocket;
import java.util.ArrayList;

public class Server extends Application implements Runnable {

    private static ArrayList<ClientThread> onlineUsers = new ArrayList<>();
    private static ArrayList<ClientThread> waitersForMultiPlayerGame = new ArrayList<>();
    private static Stage stage;
    private double x, y;

    public static ArrayList<ClientThread> getOnlineUsers() {
        return onlineUsers;
    }

    public static ArrayList<ClientThread> getWaitersForMultiPlayerGame() {
        return waitersForMultiPlayerGame;
    }

    public static void main(String[] args) {
        launch(args);
    }

    void gotoServerShop() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("view/shopMenuView/ShopMenuView.fxml"));
            Parent root = fxmlLoader.load();
            ServerShopView.setServerShopView(fxmlLoader.getController());
            Thread thread = new Thread(new ServerShopView());
            thread.start();
            stage.initStyle(StageStyle.UNDECORATED);
            root.setOnMouseDragged(event -> {

                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);

            });
            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void gotoServerUsers() {
        try {
            Stage primaryStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("view/Users.fxml"));
            Parent root = fxmlLoader.load();
            ServerUsersViewController serverUsersViewController = fxmlLoader.getController();
            serverUsersViewController.onlineCheckBox();
            ServerUsersViewController.setServerUsersViewController(serverUsersViewController);
            new Thread(new ServerUsersViewController()).start();
            primaryStage.initStyle(StageStyle.UNDECORATED);
            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });

            root.setOnMouseDragged(event -> {

                primaryStage.setX(event.getScreenX() - x);
                primaryStage.setY(event.getScreenY() - y);

            });
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void updateScoreBoard() {
        for (ClientThread clientThread : onlineUsers)
            clientThread.sendPacketToClient(new ServerEnumPacket(ServerEnum.UPDATE_LEADER_BOARD));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        new Thread(this).start();
        //gotoServerShop();
        // gotoServerUsers();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            while (true) {
                System.err.println("Waiting for connect a client ...");
                onlineUsers.add(new ClientThread(serverSocket.accept()));
                System.err.println("Client connected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
