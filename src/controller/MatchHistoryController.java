package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Account;
import models.GameStatus;
import models.History;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class MatchHistoryController implements Initializable {
    double x, y;
    @FXML
    private VBox vBoxMatchHistory;
    @FXML
    private HBox item;

    @FXML
    private Label labelRank;

    @FXML
    private Label labelUsername;

    @FXML
    private Label labelHighScore;

    @FXML
    void gotoStartMenu() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../view/StartMenuView.fxml"));
            Scene scene = new Scene(root);
            scene.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });

            scene.setOnMouseDragged(event -> {

                Controller.stage.setX(event.getScreenX() - x);
                Controller.stage.setY(event.getScreenY() - y);

            });
            Controller.stage.setScene(scene);
        } catch (IOException e) {
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<History> histories=Controller.getInstance().getAccount().getMatchHistories();
        Account account=Controller.getInstance().getAccount();
        Node[] nodes = new Node[histories.size()];
        for (int i = vBoxMatchHistory.getChildren().size()-1; i >= 0; i--) {
            vBoxMatchHistory.getChildren().remove(vBoxMatchHistory.getChildren().get(i));
        }

        for (int i = 0; i < nodes.length; i++) {
            try {
                final int j = i;
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("Item.fxml"));
                nodes[i] = fxmlLoader.load();
                MatchHistoryController personLeaderBoard = fxmlLoader.getController();
                personLeaderBoard.setInformation(
                         histories.get(i).getOponnentUserName()
                        , histories.get(i).getYourStatus()
                ,histories.get(i).getDifference());
                nodes[i].setOnMouseEntered(event -> {
                    nodes[j].setStyle("-fx-background-color : #0A0E3F");
                });
                nodes[i].setOnMouseExited(event -> {
                    nodes[j].setStyle("-fx-background-color : #3c63a3");
                });
                vBoxMatchHistory.getChildren().add(nodes[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public  void setInformation(String name, GameStatus status, String diff){
        labelRank.setText(name);
        labelHighScore.setText(status.toString());
        labelUsername.setText(diff);
    }
}
