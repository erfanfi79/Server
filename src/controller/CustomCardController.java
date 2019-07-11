package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import models.*;

import java.util.ArrayList;

public class CustomCardController {

    private ArrayList<Buff> buffs = new ArrayList<>();
    private ArrayList<Target> targets = new ArrayList<>();
    private ArrayList<Spell> spells = new ArrayList<>();
    private ArrayList<Unit> units = new ArrayList<>();
    private CustomCard customCard = new CustomCard();

    @FXML
    public void startBuff(ActionEvent event) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CustomBuff.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void startTarget(ActionEvent event) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CustomTarget.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void startSpell(ActionEvent event) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CustomSpell.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void startUnit(ActionEvent event) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CustomUnit.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backToMainMenu(ActionEvent event) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml")); //todo change for network
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backToCustomCard(ActionEvent event) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CustomSelector.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    TextArea target_name1;
    @FXML
    TextArea rows;
    @FXML
    TextArea columns;
    @FXML
    TextArea affect_target;
    @FXML
    TextArea hybrid;
    @FXML
    TextArea melee;
    @FXML
    TextArea ranged;
    @FXML
    TextArea friend_or_enemy;

    @FXML
    public void saveTarget(ActionEvent event) throws Exception {
        try {
            int row = Integer.parseInt(rows.getText());
            int col = Integer.parseInt(columns.getText());
            String affect_tar = affect_target.getText();
            boolean hybrid_1 = Boolean.parseBoolean(hybrid.getText());
            boolean melee_1 = Boolean.parseBoolean(melee.getText());
            boolean ranged_1 = Boolean.parseBoolean(ranged.getText());
            String friend_enemy = friend_or_enemy.getText();
            Target target = customCard.customTarget(target_name1.getText(), row, col, affect_tar, hybrid_1, melee_1, ranged_1, friend_enemy);
            targets.add(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    TextArea spell_name;
    @FXML
    TextArea spell_price;
    @FXML
    TextArea special_power_type;
    @FXML
    TextArea target_name;
    @FXML
    TextArea card_type;
    @FXML
    TextArea buff_name;
    @FXML
    TextArea cooldown;

    @FXML
    public void saveSpell(ActionEvent event) throws Exception {
        try {
            String b_name = buff_name.getText();
            Buff result = new Buff();
            if (buffs.size() > 0) {
                for (Buff buff : buffs) {
                    if (b_name.equals(buff.getName())) {
                        result = buff;
                        break;
                    }
                }
            }
            String t_name = target_name.getText();
            Target res = new Target();
            if (targets.size() > 0) {
                for (Target target : targets) {
                    if (t_name.equals(target.getName())) {
                        res = target;
                        break;
                    }
                }
            }
            Spell spell = customCard.customSpell(spell_name.getText(),
                    CardType.valueOf(card_type.getText()), result,
                    Integer.parseInt(spell_price.getText()), Integer.parseInt(cooldown.getText())
                    , SpecialPowerType.valueOf(special_power_type.getText()), res);
            spells.add(spell);
            DataBase.initialize("custom_cards");
            DataBase.addValue("custom_cards", spell_name.getText(), spell);
            //todo add to shop and store database and json
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    TextArea buff_name1;
    @FXML
    TextArea buff_type;
    @FXML
    TextArea effect_value;
    @FXML
    TextArea delay;
    @FXML
    TextArea lasts;

    @FXML
    public void saveBuff(ActionEvent event) throws Exception {
        try {
            Buff buff = customCard.customBuff(buff_name1.getText(), buff_type.getText(),
                    Integer.parseInt(effect_value.getText()), Integer.parseInt(delay.getText()), Boolean.parseBoolean(lasts.getText()));
            buffs.add(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    TextArea hero_or_minion;
    @FXML
    TextArea health_point;
    @FXML
    TextArea attack_point;
    @FXML
    TextArea range;
    @FXML
    TextArea unit_type;
    @FXML
    TextArea unit_name;
    @FXML
    TextArea unit_spell;

    @FXML
    public void saveUnit(ActionEvent event) throws Exception {
        try {
            int hp = Integer.parseInt(health_point.getText());
            int ap = Integer.parseInt(attack_point.getText());
            int rang = Integer.parseInt(range.getText());
            String unit_nam = unit_name.getText();
            String u_spel = unit_spell.getText();
            Spell spell = new Spell();
            for (Spell spell1 : spells) {
                if (spell1.getSpellName().equals(u_spel)) {
                    spell = spell1;
                    break;
                }
            }
            String h_or_m = hero_or_minion.getText();
            if (h_or_m.equals("hero")) {
                Unit unit = customCard.customHero(unit_nam, hp, ap, UnitType.valueOf(unit_type.getText()),
                        rang, Integer.parseInt(spell_price.getText()), spell);
                units.add(unit);
                DataBase.initialize("custom_cards");
                DataBase.addValue("custom_cards", unit_nam, unit);
            } else {
                Unit unit = customCard.customMinion(unit_nam, hp, ap,
                        UnitType.valueOf(unit_type.getText()),
                        rang, Integer.parseInt(spell_price.getText()), spell);
                units.add(unit);
                DataBase.initialize("custom_cards");
                DataBase.addValue("custom_cards", unit_nam, unit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

