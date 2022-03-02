package com.example.saper;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

import java.security.InvalidParameterException;

public class Field extends Button {

    final int width;
    final int height;


    Tile[][] field;

    Field(int size)
    {
        width = 5;
        height = (size + width - 1)/width;

        field = new Tile[height][width];

        MineGeneration();
    }

    private void MineGeneration()
    {
        for (int i = 0;i < height;i++)
            for (int y =0;y < width;y++)
            {
                field[i][y]=new Tile();
                if (i == y || i + y == width)
                    field[i][y].IsMine = true;
            }
    }

    public void Display(FlowPane displayField)
    {
        for (int i = 0;i < height;i++)
            for (int y = 0;y < width;y++)
                displayField.getChildren().add(field[i][y]);
    }
}
