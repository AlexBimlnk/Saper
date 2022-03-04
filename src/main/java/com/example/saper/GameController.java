package com.example.saper;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    private FlowPane flowPane;

    @FXML
    private Menu debugMenu;

    private Tile[][] _field;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!SaperApplication.getDebugOpton())
            debugMenu.setVisible(false);
        if (SaperApplication.getDif() != null)
            StartGame(SaperApplication.getDif());
    }

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
    @FXML
    private void openAllDebugClick(ActionEvent event) {
        for (int i = 1;i <= 10;i++)
            for (int y = 1;y <= 10;y++)
                _field[i][y].setText((_field[i][y].IsMine)?"*":(_field[i][y].GetMinesAround() == 0) ? "":Integer.toString(_field[i][y].GetMinesAround()));
    }

    private void OpenTile(int i, int j){
        if(0 <= i && i < _field.length &&
                0 <= j && j < _field.length){

            Tile tile = _field[i][j];
            if(!tile.isDisabled())
                tile.MouseHandler(MouseButton.PRIMARY);
        }
    }
    private void CallNearby(int i, int y) {
        OpenTile(i + 1,y + 1);
        OpenTile(i - 1,y - 1);
        OpenTile(i + 1,y - 1);
        OpenTile(i - 1,y + 1);

        OpenTile(i,y + 1);
        OpenTile(i,y - 1);
        OpenTile(i + 1, y);
        OpenTile(i - 1,y);
    }

    public void StartGame(GameDifficulty gameDifficulty){
        OverGame();
        Config config = gameDifficulty.GetConfigField();
        Tile.SetSize(config.SizeTile);
        int rankOfTileMatrix = 500 / config.SizeTile;

        _field = FieldGenerator.FieldGeneration(rankOfTileMatrix);
        FieldGenerator.MineGeneration(_field, config.CountTile / 4);


        Tile.CallNearby call = (p1,p2) -> CallNearby(p1,p2);
        Tile.SetCall(call);

        for (int i = 0; i < _field.length; i++)
            for (int j = 0; j < _field.length; j++)
            {
                flowPane.getChildren().add(_field[i][j]);
            }
    }

    public void OverGame()
    {
        flowPane.getChildren().clear();
    }

}