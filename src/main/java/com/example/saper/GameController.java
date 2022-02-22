package com.example.saper;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

public class GameController {
    @FXML
    private FlowPane flowPane;

    @FXML
    private void easyItemClick(ActionEvent event) {
        StartGame(GameDifficulty.Easy);
    }
    @FXML
    private void normalItemClick(ActionEvent event) {
        StartGame(GameDifficulty.Normal);
    }
    @FXML
    private void hardItemClick(ActionEvent event) {
        StartGame(GameDifficulty.Hard);
    }

    public void StartGame(GameDifficulty gameDifficulty){
        int countField = gameDifficulty.GetCountField();

        for(int i = 0; i < countField; i++){
            flowPane.getChildren().add(new Field());
        }
    }
}