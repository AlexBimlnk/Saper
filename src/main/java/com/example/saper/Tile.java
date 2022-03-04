package com.example.saper;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.security.InvalidParameterException;

public class Tile extends Button {

    private static final int SIZE_WIDTH = 50;
    private static final int SIZE_HEIGHT = 50;
    private static int _size;

    private int _minesAround; //quantity of mines around
    private int _rowIndex;
    private int _columIndex;

    private static CallNearby _call; //используется при нажатии по пустой клетке

    public Tile(int rowIndex, int columnIndex){
        LoadDefaultSettiings(this);
        _rowIndex = rowIndex;
        _columIndex = columnIndex;
    }

    public boolean IsMine = false; //prop

    private void LoadDefaultSettiings(Tile tile){

        tile.setMinSize(_size, _size);
        tile.setMaxSize(_size, _size);

        tile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
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
                setText("*");
            else
            {
                if (GetMinesAround() == 0)
                {
                    if (_call != null)
                        _call.Invoke(_rowIndex,_columIndex);
                }
                else
                {
                    setText(Integer.toString(GetMinesAround()));
                }
                setDisable(true);
            }
        }
        if(button == MouseButton.SECONDARY){
            setText("!!");
        }
    }

    public static void SetCall(CallNearby call) {
        _call = call;
    }
    public interface CallNearby {
        void Invoke(int i,int y);
    }
}