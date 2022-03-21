package com.example.saper;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    private FlowPane flowPane;

    @FXML
    private Button bRestart;

    @FXML
    private Label lTimer;

    @FXML
    private Label lMineCount;

    @FXML
    private Menu debugMenu;

    private Tile[][] _field;

    private static boolean _isGameStarted;

    private Config _config;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        _isGameStarted = false;
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
        for (int i = 0;i < _field.length;i++)
            for (int y = 0;y < _field[i].length;y++)
            {
                _field[i][y].setText((_field[i][y].IsMine)?"*":(_field[i][y].GetMinesAround() == 0) ? "":Integer.toString(_field[i][y].GetMinesAround()));

                if (_field[i][y].IsMine)
                {
                    _field[i][y].getStyleClass().add("mine");

                }

                //_field[i][y].setDisable(true);
            }
    }
    @FXML
    public void restartButtonClick(ActionEvent event){
        ClearField();
        _isGameStarted = false;
        if (SaperApplication.getDif() != null)
            StartGame(SaperApplication.getDif());
    }


    private void OpenTile(int i, int j){
        if(0 <= i && i < _field.length &&
                0 <= j && j < _field.length){

            Tile tile = _field[i][j];
            if(!tile.isDisabled() && !tile.IsFlaged())
                tile.MouseHandler(MouseButton.PRIMARY);
        }
    }
    private void CallNearby(int i, int y) {
        if (!_isGameStarted)
        {
            _isGameStarted = true;
            _field[i][y].IsStartPoint = true;
            StartGen(i,y);

            _field[i][y].MouseHandler(MouseButton.PRIMARY);
            return;
        }
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
        ClearField();
        bRestart.setText(": )");

        _config = gameDifficulty.GetConfigField();
        Tile.SetSize(_config.SizeTile);
        int rankOfTileMatrix = 500 / _config.SizeTile;
        _field = FieldGenerator.FieldGeneration(rankOfTileMatrix, _config.StyleName);

        Tile.CallNearby call = (p1,p2) -> CallNearby(p1,p2);
        Tile.SetCall(call);
        Tile.ExplosionEvent explosionEvent = () -> OverGame();
        Tile.SetExplosionEvent(explosionEvent);

        for (int i = 0; i < _field.length; i++)
            for (int j = 0; j < _field.length; j++)
            {
                flowPane.getChildren().add(_field[i][j]);

            }
    }

    public static boolean GetGameCondition()
    {
        return _isGameStarted;
    }

    public void StartGen(int i, int y)
    {
        FieldGenerator.MineGeneration(_field, _config.getCountMines());
    }

    public void ClearField()
    {
        flowPane.getChildren().clear();
    }

    public void OverGame(){
        bRestart.setText(":(");
        for (int i = 0; i < _field.length; i++)
        {
            for (int j = 0; j < _field.length; j++)
            {
                _field[i][j].setDisable(true);
            }
        }
    }

}