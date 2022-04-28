package com.example.saper;

import javafx.util.Pair;

import java.util.function.Consumer;

public class Field {



    private Tile[][] _field;
    public Tile[][] GetField() { return _field; }

    public Field(int size) {
        _field = new Tile[size][size];
        for (int i = 0; i < size; i++) {
            for (int y = 0; y < size; y++) {
                _field[i][y] = new Tile(i, y);
            }
        }
    }

    public boolean IsCorrectCoordinate(int iPos, int jPos) {
        return 0 <= iPos && iPos < _field.length &&
               0 <= jPos && jPos < _field.length;
    }

    public void ApplyToAll(Consumer<Tile> action) {
        for (int i = 0; i < _field.length; i++) {
            for (int j = 0; j < _field.length; j++) {
                action.accept(_field[i][j]);
            }
        }
    }

    public void ApplyToAround(int iPos, int jPos, Consumer<Pair<Integer, Integer>> action) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;
                if (IsCorrectCoordinate(iPos + i, jPos + j)) {
                    action.accept(new Pair<>(iPos + 1, jPos + 1));
                }
            }
        }
    }

    public interface Action{
        void Invoke();
    }
}
