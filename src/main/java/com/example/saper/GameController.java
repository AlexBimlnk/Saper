package com.example.saper;


import javafx.application.Platform;
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
import java.util.Timer;
import java.util.TimerTask;

public class GameController implements Initializable {

    @FXML
    private FlowPane _flowPane;

    @FXML
    private Button _bRestart;

    @FXML
    private Label _lTimer;
    private int _gameTimeInSeconds = 0;
    private static Timer _timer;
    private static TimerTask _timerTask;

    @FXML
    private Label _lMineCount;

    @FXML
    private Menu _debugMenu;

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

        if (!SaperApplication.getDebugOption())
            _debugMenu.setVisible(false);
        if (SaperApplication.getDif() != null)
            _gameDif = SaperApplication.getDif();

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
    private void restartButtonClick(ActionEvent event){
        ClearField();
        _isGameStarted = false;
        if (SaperApplication.getDif() != null)
            StartGame();
    }


    private void ResetTimer(){
        _timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(
                        () -> {
                            _gameTimeInSeconds++;

                            int minutes = _gameTimeInSeconds / 60;
                            int seconds = _gameTimeInSeconds % 60;

                            _lTimer.setText("Time " + minutes + ":" + seconds);
                        }
                );
            }
        };

        _timer = new Timer();
        _timer.schedule(_timerTask, 0, 1000);
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


    public static boolean GetGameCondition()
    {
        return _isGameStarted;
    }
    public static void CloseApp(){
        if(_timer != null)
            _timer.cancel();
    }

    public void StartGame(){
        ClearField();
        _config = _gameDif.GetConfigField();

        _bRestart.setText(": )");
        _lMineCount.setText(Integer.toString(_config.getCountMines()));

        Tile.setSize(_config.SizeTile);
        int rankOfTileMatrix = 500 / _config.SizeTile;
        _field = FieldGenerator.FieldGeneration(rankOfTileMatrix, _config.StyleName);

        Tile.CallNearby call = this::CallNearby;
        Tile.setCall(call);
        Tile.ExplosionEvent explosionEvent = this::OverGame;
        Tile.setExplosionEvent(explosionEvent);


        for (int i = 0; i < _field.length; i++)
            for (int j = 0; j < _field.length; j++) {
                _flowPane.getChildren().add(_field[i][j]);

            }
    }

    public void StartGen(int i, int y) {
        FieldGenerator.MineGeneration(_field, _config.getCountMines());

        ResetTimer();
    }
    public void ClearField()
    {
        _flowPane.getChildren().clear();
    }
    public void OverGame(){
        _bRestart.setText(":(");

        _lTimer.setText("Time 0:0");
        _gameTimeInSeconds = 0;
        _timer.cancel();

        OpenAll(true,false);
    }
}