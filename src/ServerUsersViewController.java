import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Account;
import models.LoginMenu;

import java.io.IOException;
import java.util.ArrayList;

public class ServerUsersViewController implements Runnable {
    private static ServerUsersViewController serverUsersViewController;
    private double x, y;

    public static void setServerUsersViewController(ServerUsersViewController serverUsersViewController) {
        ServerUsersViewController.serverUsersViewController = serverUsersViewController;
    }

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

    @FXML
    void onlineCheckBox() {
        if (checkBox.isSelected())
            showOnlineUsers();
        else showAllUsers();
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
        for (Account account : LoginMenu.getOnlineUsers()) {
            usernames.add(account.getUserName());
            winsNum.add(account.getWinsNumber());
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