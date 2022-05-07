package com.example.saper;


import com.example.saper.gamefield.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


/**
Игровое поле сапёра, обрабатывающее события UI.
@version 1.0
*/
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
    private IntegerProperty mineCount;
    private IntegerProperty simpleTileCount;

    @FXML
    private Label _lMineCount;

    @FXML
    private Menu _debugMenu;

    private Field _field;

    private static boolean _isGameStarted;
    /**
     * Возвращает поле {@link GameController#_isGameStarted}.
     * @return Возвращает true, если игра начата, иначе - false.
     */
    public static boolean GetGameCondition() {
        return _isGameStarted;
    }

    public static FlagListener listener;
    public static ClickListener clickListener;

    private Config _config;

    private static GameDifficulty _gameDif = GameDifficulty.Easy;
    /**
     * Возврщает поле {@link GameController#_gameDif}.
     * @return Перечисление {@link GameDifficulty} определяющее сложность игры.
     */
    public static GameDifficulty GetGameDifficulty() {
        return _gameDif;
    }

    /**
     * Метод, вызывающийся при инициализации игровой сцены.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        _isGameStarted = false;

        if (!SaperApplication.getDebugOption()) {
            _debugMenu.setVisible(false);
        }
        if (SaperApplication.getDif() != null) {
            _gameDif = SaperApplication.getDif();
        }

        _lTimer.setText("Time 00:00");
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
    private void restartButtonClick(ActionEvent event) {

        if (_isGameStarted) {
            ClearGameSession();
            OverGame();
            _isGameStarted = false;
        }
        if (SaperApplication.getDif() != null) {
            StartGame();
        }
    }


    /**
     * Метод перезапускает игровой таймер.
     */
    private void ResetTimer() {
        _timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(
                        () -> {
                            _gameTimeInSeconds++;

                            int minutes = _gameTimeInSeconds / 60;
                            int seconds = _gameTimeInSeconds % 60;

                            boolean extraZeroCheck4Seconds = seconds / 10 == 0;
                            boolean extraZeroCheck4Minutes = minutes / 10 == 0;

                            String extraZero4Seconds = extraZeroCheck4Seconds ? "0" : "";
                            String extraZero4Minutes = extraZeroCheck4Minutes ? "0" : "";

                            if (minutes == 55) {
                                _lTimer.setTextFill(Color.RED);
                            }

                            _lTimer.setText("Time " + extraZero4Minutes + minutes + ":" + extraZero4Seconds + seconds);

                            if (minutes == 60) {
                                OverGame();
                            }
                        }
                );
            }
        };

        _timer = new Timer();
        _timer.schedule(_timerTask, 0, 1000);
    }

    /**
     * Открывает все игровые клетки.
     * @param isDisabling
     * @param isShowAll
     */
    private void OpenAll(boolean isDisabling, boolean isShowAll) {

        _field.ApplyToAll(tile -> {
            if (isShowAll) {
                if (!tile.IsMine) {
                    tile.TextView.Invoke();
                }
            }
            if (isDisabling) {
                tile.setDisable(true);
            }
            else {
                tile.getStyleClass().add("debug");
            }
        });
    }

    /**
     * Метод вызывает открытие соседних клеток относительно заданной.
     * @param iPos Позиция заданной клетки в координатах.
     * @param jPos Позиция заданной клетки в координатах.
     */
    private void CallNearby(int iPos, int jPos) {
        if (!_isGameStarted) {
            _isGameStarted = true;

            _field.setStartPoint(iPos,jPos);

            StartGen();

            _field.getTile(iPos, jPos).MouseHandler(MouseButton.PRIMARY);
            return;
        }

        _field.ApplyToAround(iPos,jPos, (coordiniates) -> {
            Tile tile = _field.getTile(coordiniates.getKey(),coordiniates.getValue());

            if (!tile.isClicked() && !tile.isFlag()) {
                tile.MouseHandler(MouseButton.PRIMARY);
            }
        }, 1);
    }

    /**
     * Метод, вызывающийся при закрытии приложения.
     */
    public static void CloseApp() {
        if(_timer != null) {
            _timer.cancel();
        }
    }

    /**
     * Метод, вызывающийся при старте игры. Генерирует
     * игровое поле и настройки к нему.
     */
    public void StartGame() {
        ClearGameSession();
        _config = _gameDif.GetConfigField();

        _bRestart.setText(": )");
        _lMineCount.setText(Integer.toString(_config.CountMines));

        Tile.setSize(_config.SizeTile);

        listener = new FlagListener();
        clickListener = new ClickListener();

        _field = new Field(_config);

        mineCount = new SimpleIntegerProperty(_field.countMines);

        mineCount.addListener(new IntegerListener());

        simpleTileCount = new SimpleIntegerProperty(_field.countSimpleTiles);

        simpleTileCount.addListener(new IntegerListener());

        Tile.CallNearby call = this::CallNearby;
        Tile.setCall(call);
        Tile.ExplosionEvent explosionEvent = this::OverGame;
        Tile.setExplosionEvent(explosionEvent);

        for (var coordinate : _field.getAllCoordinates()) {
            _flowPane.getChildren().add(_field.getTile(coordinate.getKey(), coordinate.getValue()));
        }
    }

    /**
     * Метод, вызывающий генерацию мин.
     */
    public void StartGen() {
        //FieldGenerator.MineGen(_field,_config.CountMines);
        FieldGenerator.MineGeneration(_field, _config.CountMines);

        ResetTimer();
    }

    /**
     * Метод очищает игровую сессию. Обнуляет таймер, очищает игровое поле.
     */
    public void ClearGameSession() {
        _lTimer.setText("Time 00:00");
        _lTimer.setTextFill(Color.BLACK);
        _flowPane.getChildren().clear();
        if (_timer != null) {
            _gameTimeInSeconds = 0;
            _timer.cancel();
        }
    }

    /**
     * Метод, вызывающийся при проигрыше.
     */
    public void OverGame() {
        _bRestart.setText(":(");
        _lTimer.setTextFill(Color.DARKBLUE);
        _gameTimeInSeconds = 0;
        _timer.cancel();

        OpenAll(true,false);
    }

    public class FlagListener implements javafx.beans.value.ChangeListener<Boolean>{
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
            mineCount.set(mineCount.getValue() +
                    (observableValue.getValue().booleanValue()
                            ? -1
                            : 1)
            );

            _lMineCount.setText(Integer.toString(mineCount.getValue()));
        }
    }

    public class ClickListener implements javafx.beans.value.ChangeListener<Boolean>{
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
            simpleTileCount.set(simpleTileCount.getValue() - 1);
        }
    }

    public class IntegerListener implements javafx.beans.value.ChangeListener<Number> {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            if (mineCount.getValue() == simpleTileCount.getValue() && mineCount.getValue() == 0) {
                OverGame();
            }
        }
    }

}