import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Account;
import models.LoginMenu;

import java.io.IOException;
import java.util.ArrayList;

public class ServerUsersViewController extends Application implements Runnable {
    private static ServerUsersViewController serverUsersViewController;
    private double x, y;
    @FXML
    private HBox item;

    @FXML
    private VBox vBoxLeaderBoard;

    @FXML
    private Label labelRank;

    @FXML
    private Label labelUsername;

    @FXML
    private Label labelHighScore;

    @FXML
    private CheckBox checkBox;

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    void onlineCheckBox() {
        if (checkBox.isSelected())
            showOnlineUsers();
        else showAllUsers();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("view/Users.fxml"));
        Parent root = fxmlLoader.load();
        serverUsersViewController = fxmlLoader.getController();
        serverUsersViewController.onlineCheckBox();
        new Thread(this).start();
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
    }

    public void initializeLeaderboard(ArrayList<String> usernames, ArrayList<Integer> winsNum) {
        Node[] nodes = new Node[usernames.size()];
        for (int i = vBoxLeaderBoard.getChildren().size() - 1; i >= 0; i--) {
            vBoxLeaderBoard.getChildren().remove(vBoxLeaderBoard.getChildren().get(i));
        }

        for (int i = 0; i < nodes.length; i++) {
            try {
                final int j = i;
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("view/Item.fxml"));
                nodes[i] = fxmlLoader.load();
                ServerUsersViewController userController = fxmlLoader.getController();
                userController.setInformation(i + 1, usernames.get(i), winsNum.get(i));
                nodes[i].setOnMouseEntered(event -> {
                    nodes[j].setStyle("-fx-background-color : #0A0E3F");
                });
                nodes[i].setOnMouseExited(event -> {
                    nodes[j].setStyle("-fx-background-color : #3c63a3");
                });
                vBoxLeaderBoard.getChildren().add(nodes[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setInformation(int rank, String name, int wins) {
        labelUsername.setText(name);
        labelRank.setText(String.valueOf(rank));
        labelHighScore.setText(String.valueOf(wins));
    }

    public void showAllUsers() {
        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<Integer> winsNum = new ArrayList<>();
        for (Account account : LoginMenu.getUsers()) {
            usernames.add(account.getUserName());
            winsNum.add(account.getWinsNumber());
        }
        initializeLeaderboard(usernames, winsNum);
    }

    public void showOnlineUsers() {
        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<Integer> winsNum = new ArrayList<>();
        for (ClientThread clientThread : Server.getOnlineUsers()) {
            usernames.add(clientThread.getAccount().getUserName());
            winsNum.add(clientThread.getAccount().getWinsNumber());
        }
        initializeLeaderboard(usernames, winsNum);
    }

    @Override
    public void run() {
        while (true) {
            Platform.runLater(() -> serverUsersViewController.onlineCheckBox());
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }

        }
    }
}