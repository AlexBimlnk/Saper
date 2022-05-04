package com.example.saper.gamefield;

import javafx.scene.paint.Paint;
import javafx.util.Pair;

import java.net.Inet4Address;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Field {

    private Tile[][] _field;

    public Tile[][] GetField() { return _field; }

    private Pair<Integer, Integer> _startPointCoordinates = new Pair<>(-1,-1);

    public Field(int iSize, int ySize) {
        _field = new Tile[iSize][ySize];
        for (int i = 0; i < iSize; i++) {
            for (int y = 0; y < ySize; y++) {
                _field[i][y] = new Tile(i, y);
            }
        }
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

    public void ApplyToAround(int iPos, int jPos, Consumer<Pair<Integer, Integer>> action) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;
                if (IsCorrectCoordinate(iPos + i, jPos + j)) {
                    action.accept(new Pair<>(iPos + i, jPos + j));
                }
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
    //Проверяет находится ли точка рядом с границей поля
    public boolean IsNearWithBorder(int iPos, int jpos){
        return iPos < 1 || jpos < 1 || iPos > _field.length - 2 || jpos > _field.length - 2;
    }

    public Tile getTile(int iPos, int jPos) throws IndexOutOfBoundsException {
        if (IsCorrectCoordinate(iPos,jPos)) {
            return _field[iPos][jPos];
        }
        return null;
    }

    public ArrayList<Pair<Integer, Integer>> getAllCoordinates() {
        ArrayList<Pair <Integer,Integer>> coordinates = new ArrayList<>(_field.length * _field[0].length);

        for (int i = 0;i < _field.length; i++) {
            for (int y = 0;y < _field[0].length; y++) {
                coordinates.add(new Pair<>(i,y));
            }
        }

        return coordinates;
    }


    public ArrayList<Pair<Integer,Integer>> GetCoordinateTilesAround(int iPos, int jPos, int depth){
        if (depth <= 0) {
            throw new InvalidParameterException("depth has dispositive value");
        }

        ArrayList<Pair<Integer, Integer>> coordinateTiles = new ArrayList<>();

        ApplyToAround(iPos, jPos, coordinateTiles::add ,depth);

        return  coordinateTiles;
    }

    //Инкрементирует свойство MinesAround у всех клеток вокруг заданной с помощью координат клетки
    public void IncCountMinesAroundOfTile(int iPos, int jPos) {
        ApplyToAround(iPos, jPos, (coordinatePair) -> {
            //Если можно написать в джаве индекстор то надо написать
            //Если нет, то можно сделать метод возвращающий клетку по координате
            Tile tile = getTile(coordinatePair.getKey(),coordinatePair.getValue());
            tile.setMinesAround(tile.getMinesAround() + 1);
        });
    }

    //Здесь накостылено потому что простой инт не захватывается "делегатом".
    public int CountMinesAround(int iPos, int jPos) {
        class A{
            public int Value;
        }
        A a = new A();

        ApplyToAround(iPos, jPos, (coordinatePair) -> {
            Tile tile = getTile(coordinatePair.getKey(), coordinatePair.getValue());
            if (tile.IsMine) {
                a.Value++;
            }
        });

        return a.Value;
    }

    //Переделать, может старт поинт вообще в филд заинкапсулировать!!
    public int StartPointCheckAround(int iPos, int jPos, int[] debuff){
        int depth;

        for (depth = 0; depth < debuff.length - 1; depth++){
            for (var tile : GetCoordinateTilesAround(iPos, jPos,depth + 1)){
                if (tile.getKey() == _startPointCoordinates.getKey() && tile.getValue() == _startPointCoordinates.getValue()){
                    break;
                }
            }
        }

        return debuff[depth];
    }

    public boolean StartPointCheckInBlock(Pair<Integer,Integer> upLeft, Pair<Integer,Integer> downRight){
        for (int i = upLeft.getKey();i <= downRight.getKey();i++){
            for (int j = upLeft.getValue();j <= downRight.getValue();j++)
                if (i == _startPointCoordinates.getKey() && j == _startPointCoordinates.getValue()){
                    return true;
                }
        }

        return false;
    }

    public void setStartPoint(int iPos, int jPos) {
        if (IsCorrectCoordinate(iPos, jPos) && (_startPointCoordinates.getValue() == -1 && _startPointCoordinates.getKey() == -1)) {
            _startPointCoordinates = new Pair<>(iPos, jPos);
        }
    }

    public boolean isStartPoint(Pair <Integer, Integer> point) {
        return (point.getKey() == _startPointCoordinates.getKey() && point.getValue() == _startPointCoordinates.getValue());
    }

    public Pair<Integer, Integer> getSizes() {
        return new Pair<>(_field.length, _field[0].length);
    }

    public interface Action{
        void Invoke();
    }
}
