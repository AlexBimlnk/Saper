package com.example.saper;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.security.InvalidParameterException;

public class Tile extends Button {

    private static final int SIZE_WIDTH = 50;
    private static final int SIZE_HEIGHT = 50;

    private int _minesAround; //quantity of mines around
    private int _rowIndex;
    private int _columIndex;

    private static CallNearby _call; //используется при нажатии по пустой клетке

    public Tile(int i,int y){
        LoadDefaultSettiings(this);
        _rowIndex = i;
        _columIndex = y;
    }

    public  boolean IsDisabled = false;
    public boolean IsMine = false; //prop
    public boolean IsClicked = false;
    public boolean IsBorder = false; //являеься ли кнопкой лежащей на краю

    public static  void MouseHandle(Tile tile,MouseButton button)
    {
        //Если нажали на ЛКМ
        if(button == MouseButton.PRIMARY && !tile.IsDisabled){
            tile.IsClicked = true;
            if (tile.IsMine)
                tile.setText("*");
            else
            {
                if (tile.getMinesAround() == 0)
                {
                    if (tile._call != null)
                        tile._call.Invoke(tile._rowIndex,tile._columIndex);
                }
                else
                {
                    tile.setText(Integer.toString(tile.getMinesAround()));
                }
            }
        }
        if(button == MouseButton.SECONDARY && !tile.IsClicked){
            if (tile.IsDisabled)
                tile.setText("");
            else
                tile.setText("!");
            tile.IsDisabled = !tile.IsDisabled;
        }
    }

    private static void LoadDefaultSettiings(Tile tile){

        tile.setMinSize(SIZE_WIDTH, SIZE_HEIGHT);

        tile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                Tile.MouseHandle(tile,event.getButton());
            }
        });
    }


    public void setMinesAround(int value){
        if(value < 0)
            throw new InvalidParameterException();
        _minesAround = value;
    }
    public int getMinesAround(){
        return _minesAround;
    }

    public static void setCall(CallNearby call) {
        _call = call;
    }

    public interface CallNearby
    {
        void Invoke(int i,int y);
    }
}