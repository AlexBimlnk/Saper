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
        OverGame();
        int countField = gameDifficulty.GetCountField();

        Field gameField = new Field(countField);

        gameField.Display(flowPane);
    }

    public void OverGame()
    {
        flowPane.getChildren().clear();
    }
}