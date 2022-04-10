package com.example.saper;

public class Config {
    public final int CountTile;
    public final int SizeTile;
    public final int CountMines;
    public final String StyleName;


    public Config(int countTile, int sizeTile, int countMines, String styleName){
        CountTile = countTile;
        SizeTile = sizeTile;
        CountMines = countMines;
        StyleName = styleName;
    }
}
