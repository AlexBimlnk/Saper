package com.example.saper;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.security.InvalidParameterException;
import java.text.AttributedCharacterIterator;

public class Tile extends Button {

    private static final int SIZE_WIDTH = 50;
    private static final int SIZE_HEIGHT = 50;

    private int _distance; //distance to mine


    public Tile(){
        LoadDefaultSettiings(this);
    }

    public  boolean IsDisabled = false;
    public boolean IsMine = false; //prop
    public boolean IsClicked = false;

    public Event GameOver;
    public Event EmptyTileClick;


    private static void LoadDefaultSettiings(Tile tile){

        tile.setMinSize(SIZE_WIDTH, SIZE_HEIGHT);

        tile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Если нажали на ЛКМ
                if(event.getButton() == MouseButton.PRIMARY && !tile.IsDisabled){
                    if (tile.IsMine)
                        tile.setText("*");
                    else
                        tile.setText("_");
                    tile.IsClicked = true;
                }
                if(event.getButton() == MouseButton.SECONDARY && !tile.IsClicked){
                    if (tile.IsDisabled)
                        tile.setText("");
                    else
                        tile.setText("!");
                    tile.IsDisabled = !tile.IsDisabled;
                }
            }
        });
    }


    public void SetDistance(int value){
        if(value < 0)
            throw new InvalidParameterException();
        _distance = value;
    }
    public int GetDistance(){
        return _distance;
    }
}
