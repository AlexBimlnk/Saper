package com.example.saper;

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
     */
    public Config(int countTile, int sizeTile, int countMines, String styleName){
        CountTile = countTile;
        SizeTile = sizeTile;
        CountMines = countMines;
        StyleName = styleName;
    }
}
