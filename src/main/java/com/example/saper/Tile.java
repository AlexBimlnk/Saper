package com.example.saper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.security.InvalidParameterException;
import java.util.Random;

public class Tile extends Button {
    private static int _size;

    private int _minesAround; //quantity of mines around
    private final int _rowIndex;
    private final int _columIndex;

    private static CallNearby _callHandler; //используется при нажатии по пустой клетке
    private static ExplosionEvent _explosionEventHandler; //вызывается при взрыве мины

    public final ShownTextHandler TextView;

    private BooleanProperty _clicked;
    private BooleanProperty _flag;

    private final static Random rnd = (SaperApplication.getSeed() != -1 ? new Random(SaperApplication.getSeed()) : new Random());

    public Tile(int rowIndex, int columnIndex){
        _clicked = new SimpleBooleanProperty(false);
        _clicked.addListener( e -> pseudoClassStateChanged(PseudoClass.getPseudoClass("clicked"),_clicked.get()));

        _flag = new SimpleBooleanProperty(false);
        _flag.addListener( e -> pseudoClassStateChanged(PseudoClass.getPseudoClass("flag"),_flag.get()));

        LoadDefaultSettings();
        _rowIndex = rowIndex;
        _columIndex = columnIndex;


        if (GameController.GetGameDifficulty() == GameDifficulty.Hard) {
            TextView = (rnd.nextBoolean() ? this::ShowTextHard : this::ShowTextSimple);
        }
        else {
            TextView = this::ShowTextSimple;
        }
    }

    private void ShowTextSimple(){
        if (_minesAround == 0)
            return;

        setText(Integer.toString(_minesAround));

        if(_minesAround == 1)
            setTextFill(Color.BLUE);
        else if(_minesAround == 2)
            setTextFill(Color.GREEN);
        else if(_minesAround == 3)
            setTextFill(Color.RED);
        else if(_minesAround == 4)
            setTextFill(Color.PURPLE);
        else if(_minesAround == 5)
            setTextFill(Color.ORANGE);
        else if(_minesAround == 6)
            setTextFill(Color.YELLOW);
        else if(_minesAround == 7)
            setTextFill(Color.PINK);
        else if(_minesAround == 8)
            setTextFill(Color.BLACK);
    }
    private void ShowTextHard(){
        if (_minesAround == 0)
            return;

        setTextFill(Color.BLACK);
        int lowerBound = _minesAround - rnd.nextInt(3);
        int upperBound = _minesAround + rnd.nextInt(3);

        if(lowerBound < 0)
            lowerBound = 0;

        if(upperBound > 8)
            upperBound = 8;

        if(upperBound != lowerBound)
            setText(lowerBound + "-" + upperBound);
        else
            ShowTextSimple();
    }

    public void MouseHandler(MouseButton button) {
        //Если нажали на ЛКМ
        if(button == MouseButton.PRIMARY && !isFlag()) {
            if (IsMine) {
                this.setId("mine");
                if(_explosionEventHandler != null) {
                    _explosionEventHandler.Invoke();
                }
            }
            else {
                setClicked(true);
                if (getMinesAround() == 0) {
                    if (_callHandler != null) {
                        _callHandler.Invoke(_rowIndex,_columIndex);
                    }
                }
                else {
                    TextView.Invoke();
                }
            }
        }
        if(button == MouseButton.SECONDARY && GameController.GetGameCondition()) {
            setFlag(!isFlag());
        }
    }

    public boolean IsMine = false; //prop
    public boolean IsStartPoint = false; //prop

    private void LoadDefaultSettings() {

        setMinSize(_size, _size);
        setMaxSize(_size, _size);
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (isClicked()) {
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY){
                setClicked(true);
            }

            MouseHandler(event.getButton());
        });
    }

    public void setMinesAround(int value) {
        if(value < 0)
            throw new InvalidParameterException();
        _minesAround = value;
    }
    public int getMinesAround() {
        return _minesAround;
    }

    public static void setSize(int size) {
        _size = size;
    }
    public static int getSize() {
        return _size;
    }

    public static void setExplosionEvent(ExplosionEvent explosion) {
        _explosionEventHandler = explosion;
    }

    public boolean isClicked() {
        return _clicked.get();
    }

    public void setClicked(boolean clicked) {
        if (!isFlag()) {
            this._clicked.set(clicked);
        }
    }

    public boolean isFlag() {
        return _flag.get();
    }

    public void setFlag(boolean flag) {
        if (!isClicked()) {
            this._flag.set(flag);
        }

    }

    public interface CallNearby {
        void Invoke(int i,int y);
    }
    public static void setCall(CallNearby call) {
        _callHandler = call;
    }

    public interface ExplosionEvent {
        void Invoke();
    }

    public interface ShownTextHandler {
        void Invoke();
    }
}