package com.example.saper;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.security.InvalidParameterException;

public class Field extends Button {

    private static final int SIZE_WIDTH = 50;
    private static final int SIZE_HEIGHT = 50;

    private int _distance; //distance to mine


    public Field(){
        LoadDefaultSettiings(this);
    }


    public boolean IsMine = false; //prop


    private static void LoadDefaultSettiings(Field field){

        field.setMinSize(SIZE_WIDTH, SIZE_HEIGHT);

        field.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Если нажали на ЛКМ
                if(event.getButton() == MouseButton.PRIMARY){
                    field.setText("123");
                }
                if(event.getButton() == MouseButton.SECONDARY){
                    field.setText("321");
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
