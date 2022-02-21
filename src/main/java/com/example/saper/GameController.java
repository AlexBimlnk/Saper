package com.example.saper;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

public class GameController {
    @FXML
    private FlowPane flowPane;

    @FXML
    private Button btn;

    private int countMine;

    @FXML
    private void click(ActionEvent event) {
        btn.setText("You've clicked!");
    }
    @FXML
    private void easyItemClick(ActionEvent event) {
        countMine = 0;
        StartGame(GameDifficulty.Easy);
    }
    @FXML
    private void normalItemClick(ActionEvent event) {
        countMine = 10;
        StartGame(GameDifficulty.Normal);
    }
    @FXML
    private void hardItemClick(ActionEvent event) {
        countMine = 0;
        StartGame(GameDifficulty.Hard);
    }

    public void StartGame(GameDifficulty gameDifficulty){
        if(gameDifficulty == GameDifficulty.Easy){
            flowPane.getChildren().add(new Button("EASY"));
        }
        if(gameDifficulty == GameDifficulty.Normal){
            flowPane.getChildren().add(new Button("NORMAL"));
        }
        if(gameDifficulty == GameDifficulty.Hard){
            flowPane.getChildren().add(new Button("HARD"));
        }
    }
}