package com.example.saper;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.security.InvalidParameterException;
import java.util.Random;

public class Tile extends Button {

    private static int _size;

    private int _minesAround; //quantity of mines around
    private int _rowIndex;
    private int _columIndex;

    private boolean _isFlaged = false;

    private static CallNearby _callHandler; //используется при нажатии по пустой клетке
    private static ExplosionEvent _explosionEventHandler; //вызывается при взрыве мины


    private Random rnd = new Random();

    public Tile(int rowIndex, int columnIndex){

        LoadDefaultSettings();
        _rowIndex = rowIndex;
        _columIndex = columnIndex;
    }

    private void ShowText(){
        setText(Integer.toString(_minesAround));

        if(_minesAround == 1)
            setTextFill(Color.BLUE);
        if(_minesAround == 2)
            setTextFill(Color.GREEN);
        if(_minesAround == 3)
            setTextFill(Color.RED);
        if(_minesAround == 4)
            setTextFill(Color.PURPLE);
        if(_minesAround == 5)
            setTextFill(Color.ORANGE);
        if(_minesAround == 6)
            setTextFill(Color.YELLOW);
        if(_minesAround == 7)
            setTextFill(Color.PINK);
        if(_minesAround == 8)
            setTextFill(Color.BLACK);
    }
    private void ShowTextHard(){
        if(rnd.nextBoolean()){
            setTextFill(Color.BLACK);
            int lowerBound = _minesAround - rnd.nextInt(3);
            int upperBound = _minesAround + rnd.nextInt(3);

            if(lowerBound < 0)
                lowerBound = 0;
            if(lowerBound == 0)
                upperBound = _minesAround;

            if(upperBound < 0)
                upperBound = 0;
            if(upperBound == 0)
                lowerBound = _minesAround;

            if(upperBound != lowerBound)
                setText(lowerBound + "-" + upperBound);
            else
                ShowText();
        }
        else
            ShowText();
    }

    public void MouseHandler(MouseButton button) {
        //Если нажали на ЛКМ
        if(button == MouseButton.PRIMARY) {
            if (IsMine) {
                this.setId("mine");
                if(_explosionEventHandler != null) {
                    _explosionEventHandler.Invoke();
                }
            }
            else {
                setDisable(true);
                if (GetMinesAround() == 0) {
                    if (_callHandler != null) {
                        _callHandler.Invoke(_rowIndex,_columIndex);
                    }
                }
                else {
                    if(GameController.GetGameDifficulty() == GameDifficulty.Hard) {
                        ShowTextHard();
                    }
                    else {
                        ShowText();
                    }
                }
            }
        }
        if(button == MouseButton.SECONDARY && GameController.GetGameCondition()) {
            if (_isFlaged) {
                setId("default");
            }
            else {
                setId("flag");
            }
            _isFlaged = !_isFlaged;
        }
    }

    public boolean IsMine = false; //prop
    public boolean IsStartPoint = false; //prop

    private void LoadDefaultSettings() {

        setMinSize(_size, _size);
        setMaxSize(_size, _size);

        addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                MouseHandler(event.getButton());
            }
        });
    }

    public boolean IsFlagged() {
        return _isFlaged;
    }

    public void SetMinesAround(int value) {
        if(value < 0)
            throw new InvalidParameterException();
        _minesAround = value;
    }
    public int GetMinesAround() {
        return _minesAround;
    }

    public static void SetSize(int size) {
        _size = size;
    }
    public static int GetSize() {
        return _size;
    }


    public interface CallNearby {
        void Invoke(int i,int y);
    }
    public static void SetCall(CallNearby call) {
        _callHandler = call;
    }

    public interface ExplosionEvent {
        void Invoke();
    }
    public static void SetExplosionEvent(ExplosionEvent explosion) {
        _explosionEventHandler = explosion;
    }
}