package com.example.saper;

public class Config {
    public int CountTile;
    public int SizeTile;
    public String StyleName;

    private int CountMines;

    public int getCountMines() {
        return CountMines;
    }

    public void setCountMines(int countMines) {

        if (CountTile / countMines <= 3)
            throw new RuntimeException("too hard");

        CountMines = countMines;
    }

    public Config(int countTile, int sizeTile, String styleName){
        CountTile = countTile;
        SizeTile = sizeTile;
        StyleName = styleName;
    }
}
