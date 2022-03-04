package com.example.saper;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
    private void openAllDebugClick(ActionEvent event)
    {
        for (int i = 1;i <= 10;i++)
            for (int y = 1;y <= 10;y++)
                _field[i][y].setText((_field[i][y].IsMine)?"*":(_field[i][y].getMinesAround() == 0) ? "":Integer.toString(_field[i][y].getMinesAround()));
    }

    public void StartGame(GameDifficulty gameDifficulty){
        OverGame();
        int countField = gameDifficulty.GetCountField();

        _field = FieldGenerator.MineGeneration(10,10,countField / 4);

        Tile.CallNearby call = (p1,p2) -> CallNearby(p1,p2);
        Tile.setCall(call);

        for (int i = 1;i <= 10;i++)
            for (int y = 1;y <= 10;y++)
            {


                flowPane.getChildren().add(_field[i][y]);
            }
    }

    public void OverGame()
    {
        flowPane.getChildren().clear();
    }
    
    public void CallNearby(int i, int y)
    {
        interface CallTile{
            public void Call(Tile tile);
        }

        CallTile callTile = new CallTile(){

            public void Call(Tile tile){
                if (!tile.IsBorder && !tile.IsClicked)
                {
                    Tile.MouseHandle(tile, MouseButton.PRIMARY);
                }
            }
        };

        callTile.Call(_field[i + 1][y + 1]);
        callTile.Call(_field[i - 1][y - 1]);
        callTile.Call(_field[i + 1][y - 1]);
        callTile.Call(_field[i - 1][y + 1]);

        callTile.Call(_field[i][y + 1]);
        callTile.Call(_field[i][y - 1]);
        callTile.Call(_field[i + 1][y]);
        callTile.Call(_field[i - 1][y]);
    }
}