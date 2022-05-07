package com.example.saper.gamefield;

import com.example.saper.Config;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.function.Consumer;

public class Field {

    private final Tile[][] _field;
    private Pair<Integer, Integer> _startPointCoordinates = new Pair<>(-1,-1);

    public final int countMines;
    public final int countSimpleTiles;

    public Field(Config config) {
        int rankOfTileMatrix = 500 / config.SizeTile;
        _field = new Tile[rankOfTileMatrix][rankOfTileMatrix];

        Tile.setSize(config.SizeTile);

        for (int i = 0; i < _field.length; i++) {
            for (int y = 0; y < _field[i].length; y++) {
                _field[i][y] = new Tile(i, y);
            }
        }

        countMines = config.CountMines;
        countSimpleTiles = config.CountTile - config.CountMines;

        ApplyToAll(tile -> {
            tile.getStyleClass().add("tile");
            tile.getStyleClass().add(config.StyleName);
            tile.setId("default");
        });
    }

    public static int GetDistanceToPoints(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        int d1 = Math.abs(p2.getKey() - p1.getKey());

        int d2 = Math.abs(p2.getValue() - p1.getValue());

        return Math.max(d1,d2);
    }

    public boolean IsCorrectCoordinate(int iPos, int jPos) {
        return 0 <= iPos && iPos < _field.length &&
                0 <= jPos && jPos < _field[0].length;
    }

    public void ApplyToAll(Consumer<Tile> action) {
        for (int i = 0; i < _field.length; i++) {
            for (int j = 0; j < _field.length; j++) {
                action.accept(_field[i][j]);
            }
        }
    }

    public void ApplyToAround(int iPos, int jPos, Consumer<Pair<Integer, Integer>> action, int step) {
        if (step < 0 ) {
            throw new InvalidParameterException("step has dispositive value");
        }

        for (int i = -step;i <= step;i++) {
            if (IsCorrectCoordinate(iPos - step, jPos + i)) {
                action.accept(new Pair<>(iPos - step, jPos + i));
            }

            if (-step != i) {
                if (IsCorrectCoordinate(iPos + i, jPos - step)) {
                    action.accept(new Pair<>(iPos + i, jPos - step));
                }
            }

            if (0 == step + i) {
                continue;
            }

            if (IsCorrectCoordinate(iPos + step, jPos + i)) {
                action.accept(new Pair<>(iPos + step, jPos + i));
            }

            if (step != i) {
                if (IsCorrectCoordinate(iPos + i, jPos + step)) {
                    action.accept(new Pair<>(iPos + i, jPos + step));
                }
            }
        }
    }

    //Инкрементирует свойство MinesAround у всех клеток вокруг заданной с помощью координат клетки
    public void IncCountMinesAroundOfTile(int iPos, int jPos) {
        ApplyToAround(iPos, jPos, (coordinatePair) -> {
            Tile tile = getTile(coordinatePair.getKey(),coordinatePair.getValue());
            tile.setMinesAround(tile.getMinesAround() + 1);
        },1);
    }

    public int CountMinesAround(int iPos, int jPos) {
        IntegerProperty value = new SimpleIntegerProperty(0);

        ApplyToAround(iPos, jPos, (coordinatePair) -> {
            Tile tile = getTile(coordinatePair.getKey(), coordinatePair.getValue());
            if (tile.IsMine) {
                value.set(value.getValue() + 1);
            }
        },1);

        return value.getValue();
    }

    public Tile getTile(int iPos, int jPos) {
        if (IsCorrectCoordinate(iPos,jPos)) {
            return _field[iPos][jPos];
        }
        return null;
    }

    public void setStartPoint(int iPos, int jPos) {
        if (IsCorrectCoordinate(iPos, jPos) && (_startPointCoordinates.getValue() == -1 && _startPointCoordinates.getKey() == -1)) {
            _startPointCoordinates = new Pair<>(iPos, jPos);
        }
    }

    public Pair<Integer,Integer> getStartPoint() {
        return _startPointCoordinates;
    }

    public boolean isStartPoint(Pair <Integer, Integer> point) {
        return (Objects.equals(point.getKey(), _startPointCoordinates.getKey()) && Objects.equals(point.getValue(), _startPointCoordinates.getValue()));
    }

    //Проверяет находится ли точка рядом с границей поля
    public boolean isNearWithBorder(int iPos, int jpos){
        return iPos < 1 || jpos < 1 || iPos > _field.length - 2 || jpos > _field.length - 2;
    }

    public Pair<Integer, Integer> getSizes() {
        return new Pair<>(_field.length, _field[0].length);
    }
}
