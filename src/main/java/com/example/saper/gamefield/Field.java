package com.example.saper.gamefield;

import com.example.saper.Config;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class Field {

    private static Tile[][] _field;
    private static Pair<Integer, Integer> _startPointCoordinates;

    private static final int FIELD_SIZE = 500;
    private static int _countMines;
    public static int getCountMines() {
        return _countMines;
    }
    private static int _countSimpleTiles;
    public static int getCountSimpleTiles() {
        return _countSimpleTiles;
    }

    /**
     * Конструктор поля.
     * @param config Объект конфигурации.
     * @throws IllegalArgumentException Когда объект конфига равен null.
     */
    public Field(Config config) throws IllegalArgumentException {
        if (config == null)
            throw new IllegalArgumentException("Config can't be null.");

        int rankOfTileMatrix = FIELD_SIZE / config.SizeTile;
        _field = new Tile[rankOfTileMatrix][rankOfTileMatrix];

        _startPointCoordinates = new Pair<>(-1,-1);

        Tile.setSize(config.SizeTile);

        for (int i = 0; i < _field.length; i++) {
            for (int j = 0; j < _field[i].length; j++) {
                _field[i][j] = new Tile(i, j);
            }
        }

        _countMines = config.CountMines;
        _countSimpleTiles = config.CountTile - config.CountMines;

        applyToAll(tile -> {
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
     * @throws IllegalArgumentException Когда одна из пар равна null.
     */
    public static int getDistanceToPoints(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2)
            throws IllegalArgumentException {
        if (p1 == null || p2 == null)
            throw new IllegalArgumentException("Pairs can't be null.");

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
    public static boolean isCorrectCoordinate(int iPos, int jPos) {
        return 0 <= iPos && iPos < _field.length &&
               0 <= jPos && jPos < _field[0].length;
    }

    /**
     * Метод применяет ко всем клеткам поля действие, определенное в делегате.
     * @param action Действие, которое следует применить.
     * @throws IllegalArgumentException Когда делегат равен null.
     */
    public static void applyToAll(Consumer<Tile> action) throws IllegalArgumentException {
        if (action == null)
            throw new IllegalArgumentException("Action can't be null.");

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
     * @param step Расстяоние на котором просматриваются клетки от заданной
     * @throws IllegalArgumentException Когда делегат null.
     * @throws InvalidParameterException Когда шаг меньше нуля.
     */
    public static void applyToAround(int iPos, int jPos, Consumer<Pair<Integer, Integer>> action, int step)
            throws IllegalArgumentException, InvalidParameterException {
        if (action == null)
            throw new IllegalArgumentException("Action can't be null.");

        if (step < 0 ) {
            throw new InvalidParameterException("Step has dispositive value.");
        }

        for (int i = -step;i <= step;i++) {
            if (isCorrectCoordinate(iPos - step, jPos + i)) {
                action.accept(new Pair<>(iPos - step, jPos + i));
            }

            if (-step != i) {
                if (isCorrectCoordinate(iPos + i, jPos - step)) {
                    action.accept(new Pair<>(iPos + i, jPos - step));
                }
            }

            if (0 == step + i) {
                continue;
            }

            if (isCorrectCoordinate(iPos + step, jPos + i)) {
                action.accept(new Pair<>(iPos + step, jPos + i));
            }

            if (step != i) {
                if (isCorrectCoordinate(iPos + i, jPos + step)) {
                    action.accept(new Pair<>(iPos + i, jPos + step));
                }
            }
        }
    }

    /**
     * Метод применяет ко всем клеткам в области заданной действите, определенное в делегате.
     * @param iPos Координата строки клетки.
     * @param jPos Координата столбца клетки.
     * @param action Действие, которое следует применить.
     * @param step Радиус области
     * @throws IllegalArgumentException Когда делегат null.
     * @throws InvalidParameterException Когда шаг меньше нуля.
     */
    public static void applyToAroundArea(int iPos, int jPos, Consumer<Pair<Integer, Integer>> action, int step)
            throws IllegalArgumentException, InvalidParameterException {
        if (action == null)
            throw new IllegalArgumentException("Action can't be null.");

        if (step < 0 ) {
            throw new InvalidParameterException("step has dispositive value");
        }

        for (int i = -step; i <= step; i++) {
            for (int j = -step; j <= step; j++) {
                if ((i == 0 && j == 0) || !isCorrectCoordinate(iPos + i, jPos + j)) {
                    continue;
                }

                action.accept(new Pair<>(iPos + i, jPos + j));
            }
        }
    }

    /**
     * Инкрементирует свойство MinesAround у всех клеток вокруг заданной с помощью координат клетки
     * @param iPos Координата строки клетки.
     * @param jPos Координата столбца клетки.
     */
    public static void incCountMinesAroundOfTile(int iPos, int jPos) {
        applyToAround(iPos, jPos, (coordinatePair) -> {
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
    public static int countMinesAround(int iPos, int jPos) {
        IntegerProperty value = new SimpleIntegerProperty(0);

        applyToAround(iPos, jPos, (coordinatePair) -> {
            Tile tile = getTile(coordinatePair.getKey(), coordinatePair.getValue());
            if (tile.isMine) {
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
        if (isCorrectCoordinate(iPos,jPos)) {
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
        if (isCorrectCoordinate(iPos, jPos) && (_startPointCoordinates.getValue() == -1 && _startPointCoordinates.getKey() == -1)) {
            _startPointCoordinates = new Pair<>(iPos, jPos);
        }
    }

    /**
     * Метод возвращаюий список со всеми координатами клеткок
     * @return список координат
     */
    public static ArrayList<Pair<Integer,Integer>> getAllCoordinates() {
        ArrayList<Pair<Integer,Integer>> coordinates = new ArrayList<>(_countMines + _countSimpleTiles);

        for (int i = 0; i < _field.length; i++) {
            for (int j = 0; j < _field.length; j++) {
                coordinates.add(new Pair<>(i, j));
            }
        }

        return coordinates;
    }

    /**
     * Возвращает пару координат клетки, с которой была начата игра.
     * @return Координаты клетки
     */
    public static Pair<Integer,Integer> getStartPoint() {
        return _startPointCoordinates;
    }

    /**
     * Проверяет является ли клетка стартовой.
     * @param point Пара координат клетки.
     * @return true, если точка является начальной, иначе - false.
     * @throws IllegalArgumentException Когда точка равна null.
     */
    public static boolean isStartPoint(Pair <Integer, Integer> point) throws IllegalArgumentException {
        if (point == null)
            throw new IllegalArgumentException("Point can't be null.");

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
