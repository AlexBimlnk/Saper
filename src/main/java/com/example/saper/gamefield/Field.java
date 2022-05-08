package com.example.saper.gamefield;

import com.example.saper.Config;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.function.Consumer;

public class Field {

    private static Tile[][] _field;
    private static Pair<Integer, Integer> _startPointCoordinates = new Pair<>(-1,-1);

    private static int countMines;
    public static int GetCountMines() {
        return countMines;
    }
    private static int countSimpleTiles;
    public static int GetCountSimleTiles() {
        return countSimpleTiles;
    }

    /**
     * Конструктор поля.
     * @param config Объект конфигурации.
     */
    public Field(Config config) {
        int rankOfTileMatrix = 500 / config.SizeTile;
        _field = new Tile[rankOfTileMatrix][rankOfTileMatrix];

        Tile.setSize(config.SizeTile);

        for (int i = 0; i < _field.length; i++) {
            for (int j = 0; j < _field[i].length; j++) {
                _field[i][j] = new Tile(i, j);
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

    /**
     * Возвращает дистанцию между точками.
     * @param p1 Первая точка.
     * @param p2 Вторая точка.
     * @return Расстояние между точками.
     */
    public static int GetDistanceToPoints(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        int d1 = Math.abs(p2.getKey() - p1.getKey());

        int d2 = Math.abs(p2.getValue() - p1.getValue());

        return Math.max(d1,d2);
    }

    /**
     * Метод проверяет корректность переданных координат.
     * @param iPos Координата строки клетки.
     * @param jPos Координата столбца клетки.
     * @return Возвращает true, если пара представляет точку внутри поля, false - если вышли за границы.
     */
    public static boolean IsCorrectCoordinate(int iPos, int jPos) {
        return 0 <= iPos && iPos < _field.length &&
               0 <= jPos && jPos < _field[0].length;
    }

    /**
     * Метод применяет ко всем клеткам поля действие, определенное в делегате.
     * @param action Действие, которое следует применить.
     */
    public static void ApplyToAll(Consumer<Tile> action) {
        for (int i = 0; i < _field.length; i++) {
            for (int j = 0; j < _field.length; j++) {
                action.accept(_field[i][j]);
            }
        }
    }

    /**
     * Метод применяет ко всем клеткам вокруг заданной действите, определенное в делегате.
     * @param iPos Координата строки клетки.
     * @param jPos Координата столбца клетки.
     * @param action Действие, которое следует применить.
     * @param step TODO: описать step в документации
     */
    public static void ApplyToAround(int iPos, int jPos, Consumer<Pair<Integer, Integer>> action, int step) {
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

    /**
     * Инкрементирует свойство MinesAround у всех клеток вокруг заданной с помощью координат клетки
     * @param iPos Координата строки клетки.
     * @param jPos Координата столбца клетки.
     */
    public static void IncCountMinesAroundOfTile(int iPos, int jPos) {
        ApplyToAround(iPos, jPos, (coordinatePair) -> {
            Tile tile = getTile(coordinatePair.getKey(),coordinatePair.getValue());
            tile.setMinesAround(tile.getMinesAround() + 1);
        },1);
    }

    /**
     * Возвращает кол-во мин вокруг клетки, заданной в координатах.
     * @param iPos Координата строки.
     * @param jPos Координата столбца.
     * @return Количество мин.
     */
    public static int CountMinesAround(int iPos, int jPos) {
        IntegerProperty value = new SimpleIntegerProperty(0);

        ApplyToAround(iPos, jPos, (coordinatePair) -> {
            Tile tile = getTile(coordinatePair.getKey(), coordinatePair.getValue());
            if (tile.IsMine) {
                value.set(value.getValue() + 1);
            }
        },1);

        return value.getValue();
    }

    /**
     * Возвращает объект типа {@link Tile}, соотвествующий заданным координатам.
     * @param iPos Координата строки клетки.
     * @param jPos Координата столбца клетки.
     * @return Объект типа {@link Tile}
     */
    public static Tile getTile(int iPos, int jPos) {
        if (IsCorrectCoordinate(iPos,jPos)) {
            return _field[iPos][jPos];
        }
        return null;
    }

    /**
     * Устанавливает координаты клетки с которой была начата игра.
     * @param iPos Координата строки клетки.
     * @param jPos Координата столбца клетки.
     */
    public static void setStartPoint(int iPos, int jPos) {
        if (IsCorrectCoordinate(iPos, jPos) && (_startPointCoordinates.getValue() == -1 && _startPointCoordinates.getKey() == -1)) {
            _startPointCoordinates = new Pair<>(iPos, jPos);
        }
    }

    /**
     * Возвращает пару координат клетки, с которой была начата игра.
     * @return
     */
    public static Pair<Integer,Integer> getStartPoint() {
        return _startPointCoordinates;
    }

    /**
     * Проверяет является ли клетка стартовой.
     * @param point Пара координат клетки.
     * @return true, если точка является начальной, иначе - false.
     */
    public static boolean isStartPoint(Pair <Integer, Integer> point) {
        return Objects.equals(point.getKey(), _startPointCoordinates.getKey()) &&
               Objects.equals(point.getValue(), _startPointCoordinates.getValue());
    }

    /**
     * Проверяет находится ли точка рядом с границей поля.
     * @param iPos Координата строки клетки.
     * @param jpos Координата столбца клетки.
     * @return true, если точка находиться рядом с границей, иначе - false.
     */
    public static boolean isNearWithBorder(int iPos, int jpos) {
        return iPos < 1 || jpos < 1 || iPos > _field.length - 2 || jpos > _field.length - 2;
    }

    /**
     * Возвращает размер игрового поля.
     * @return Пару {@link Pair}, представляющую размеры поля.
     */
    public static Pair<Integer, Integer> getSizes() {
        return new Pair<>(_field.length, _field[0].length);
    }
}
