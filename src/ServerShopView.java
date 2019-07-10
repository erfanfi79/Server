import controller.CardController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Card;
import models.GlobalShop;

import java.io.IOException;
import java.util.ArrayList;

public class ServerShopView extends Application implements Runnable {
    private static ServerShopView serverShopView;
    private double x, y;

    @FXML
    private HBox hBox2;

    @FXML
    private HBox hBox1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Platform.runLater(() -> serverShopView.showCards(GlobalShop.getGlobalShop().getShopCollection().getCards()));
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("view/shopMenuView/ShopMenuView.fxml"));
        Parent root = fxmlLoader.load();
        serverShopView = fxmlLoader.getController();
        new Thread(this).start();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMouseDragged(event -> {

            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);

        });
        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void showCards(ArrayList<Card> cards) {
        Node[] nodes;
        nodes = new Node[cards.size()];
        for (int i = hBox1.getChildren().size() - 1; i >= 0; i--) {
            hBox1.getChildren().remove(hBox1.getChildren().get(i));
        }
        for (int i = hBox2.getChildren().size() - 1; i >= 0; i--) {
            hBox2.getChildren().remove(hBox2.getChildren().get(i));
        }
        int i = 0;
        for (; i < nodes.length / 2 + nodes.length % 2; i++) {
            try {
                Card card = cards.get(i);
                FXMLLoader fxmlLoader = new FXMLLoader();
                switch (card.getType()) {
                    case USABLE_ITEM:
                        fxmlLoader.setLocation(getClass().getResource("view/cardBackground/Item.fxml"));
                        break;
                    case MINION:
                        fxmlLoader.setLocation(getClass().getResource("view/cardBackground/MinionCard.fxml"));
                        break;
                    case HERO:
                        fxmlLoader.setLocation(getClass().getResource("view/cardBackground/General.fxml"));
                        break;
                    case SPELL:
                        fxmlLoader.setLocation(getClass().getResource("view/cardBackground/SpellCard.fxml"));
                        break;
                }
                nodes[i] = fxmlLoader.load();
                CardController cardController = fxmlLoader.getController();
                cardController.setInformation(card);
                hBox1.getChildren().add(nodes[i]);
            } catch (IOException e) {

            }
        }
        for (; i < nodes.length; i++) {
            try {
                Card card = cards.get(i);
                FXMLLoader fxmlLoader = new FXMLLoader();
                boolean isContinue = false;
                switch (card.getType()) {
                    case USABLE_ITEM:
                        fxmlLoader.setLocation(getClass().getResource("view/cardBackground/Item.fxml"));
                        break;
                    case MINION:
                        fxmlLoader.setLocation(getClass().getResource("view/cardBackground/MinionCard.fxml"));
                        break;
                    case HERO:
                        fxmlLoader.setLocation(getClass().getResource("view/cardBackground/General.fxml"));
                        break;
                    case SPELL:
                        fxmlLoader.setLocation(getClass().getResource("view/cardBackground/SpellCard.fxml"));
                        break;
                    default:
                        isContinue = true;

                }
                if (isContinue)
                    continue;
                nodes[i] = fxmlLoader.load();
                CardController cardController = fxmlLoader.getController();
                cardController.setInformation(card);
                hBox2.getChildren().add(nodes[i]);
            } catch (IOException e) {

            }
        }
    }
}
