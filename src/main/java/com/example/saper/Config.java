package com.example.saper;

import java.security.InvalidParameterException;

/**
 * Класс представляющий сущность конфига.
 */
public class Config {
    /**
     * Поле, отражающее количество игровых клеток.
     */
    public final int CountTile;
    /**
     * Поле, отражающее размер игровой клетки.
     */
    public final int SizeTile;
    /**
     * Поле, отражающее количество мин в игровой сессии.
     */
    public final int CountMines;
    /**
     * Поле, хранящее название класса стилей для клетки.
     */
    public final String StyleName;

    /**
     * Констурктор конфига.
     * @param countTile Кол-во клеток на поле.
     * @param sizeTile Размер игровых клеток.
     * @param countMines Кол-во мин.
     * @param styleName Название класса стилей.
     * @throws InvalidParameterException Когда размер поля или клетки или кол-во мин меньше единицы,
     * а также, когда имя стиля отсутсвует.
     */
    public Config(int countTile, int sizeTile, int countMines, String styleName) throws InvalidParameterException {
        if (countTile <= 0 || sizeTile <= 0 || countMines <= 0 || styleName.isEmpty() || styleName == null)
            throw new InvalidParameterException("Parameters should be positive and can't be empty or null.");

        CountTile = countTile;
        SizeTile = sizeTile;
        CountMines = countMines;
        StyleName = styleName;
    }
}
