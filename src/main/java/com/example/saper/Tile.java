package com.example.saper;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.security.InvalidParameterException;

public class Tile extends Button {

    private static int _size;

    private int _minesAround; //quantity of mines around
    private int _rowIndex;
    private int _columIndex;
    private boolean _isFlaged = false;

    public boolean IsFlaged() {
        return _isFlaged;
    }

    private static CallNearby _callHandler; //используется при нажатии по пустой клетке
    private static ExplosionEvent _explosionEventHandler; //вызывается при взрыве мины

    public Tile(int rowIndex, int columnIndex){

        LoadDefaultSettiings();
        _rowIndex = rowIndex;
        _columIndex = columnIndex;
    }

    public boolean IsMine = false; //prop

    private void LoadDefaultSettiings(){

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

    public void SetMinesAround(int value){
        if(value < 0)
            throw new InvalidParameterException();
        _minesAround = value;
    }
    public int GetMinesAround(){
        return _minesAround;
    }

    public static void SetSize(int size){
        _size = size;
    }
    public static int GetSize(){
        return _size;
    }

    public void MouseHandler(MouseButton button) {
        //Если нажали на ЛКМ
        if(button == MouseButton.PRIMARY){
            if (IsMine)
            {
                setText("*");
                if(_explosionEventHandler != null)
                    _explosionEventHandler.Invoke();
            }
            else
            {
                setDisable(true);
                if (GetMinesAround() == 0)
                {
                    if (_callHandler != null)
                        _callHandler.Invoke(_rowIndex,_columIndex);
                }
                else
                {
                    setText(Integer.toString(GetMinesAround()));
                }
            }
        }
        if(button == MouseButton.SECONDARY){
            if (_isFlaged)
            {
                setText("");
            }
            else
            {
                setText("!!");
            }
            _isFlaged = !_isFlaged;
        }
    }


    public interface CallNearby {
        void Invoke(int i,int y);
    }
    public static void SetCall(CallNearby call) {
        _callHandler = call;
    }

    public interface ExplosionEvent{
        void Invoke();
    }
    public static void SetExplosionEvent(ExplosionEvent explosion) {
        _explosionEventHandler = explosion;
    }
}