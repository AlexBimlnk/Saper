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

    private static GameDifficulty _gameDif = GameDifficulty.Easy;
    public static GameDifficulty GetGameDifficulty(){
        return _gameDif;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        _isGameStarted = false;
        if (!SaperApplication.getDebugOpton())
            debugMenu.setVisible(false);
        if (SaperApplication.getDif() != null)
            _gameDif = SaperApplication.getDif();

//        _mineImage = new ImageView(getClass().getResource("/Images/mine_style2.png").toExternalForm());
//        _flagImage = new ImageView(getClass().getResource("/Images/flag_style1.png").toExternalForm());

        StartGame();
    }

    @FXML
    private void easyItemClick(ActionEvent event) {
        _gameDif = GameDifficulty.Easy;
        _isGameStarted = false;
        StartGame();
    }
    @FXML
    private void normalItemClick(ActionEvent event) {
        _gameDif = GameDifficulty.Normal;
        _isGameStarted = false;
        StartGame();
    }
    @FXML
    private void hardItemClick(ActionEvent event) {
        _gameDif = GameDifficulty.Hard;
        _isGameStarted = false;
        StartGame();
    }
    @FXML
    private void openAllDebugClick(ActionEvent event) {
        OpenAll(false,true);
    }


    @FXML
    public void restartButtonClick(ActionEvent event){
        ClearField();
        _isGameStarted = false;
        if (SaperApplication.getDif() != null)
            StartGame();
    }


    private void OpenAll(boolean isDisabling,boolean isShowAll){
        for (int i = 0;i < _field.length;i++)
            for (int y = 0;y < _field[i].length;y++)
            {
                if (isShowAll){
                    _field[i][y].TextView.Invoke();
                }

                if (isDisabling){
                    _field[i][y].setDisable(true);
                }
                else {
                    _field[i][y].getStyleClass().add("debug");
                }

            }
    }

    private void OpenTile(int i, int j){
        if(0 <= i && i < _field.length &&
           0 <= j && j < _field.length){

            Tile tile = _field[i][j];
            if(!tile.isClicked() && !tile.isFlag())
                tile.MouseHandler(MouseButton.PRIMARY);
        }
    }
    private void CallNearby(int i, int y) {
        if (!_isGameStarted) {
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

    public void StartGame(){
        ClearField();
        _config = _gameDif.GetConfigField();

        bRestart.setText(": )");
        lMineCount.setText(Integer.toString(_config.getCountMines()));

        Tile.setSize(_config.SizeTile);
        int rankOfTileMatrix = 500 / _config.SizeTile;
        _field = FieldGenerator.FieldGeneration(rankOfTileMatrix, _config.StyleName);

        Tile.CallNearby call = this::CallNearby;
        Tile.setCall(call);
        Tile.ExplosionEvent explosionEvent = this::OverGame;
        Tile.setExplosionEvent(explosionEvent);


        for (int i = 0; i < _field.length; i++)
            for (int j = 0; j < _field.length; j++) {
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
        OpenAll(true,false);
    }
}