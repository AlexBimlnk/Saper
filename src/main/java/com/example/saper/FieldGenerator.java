package com.example.saper;


import java.util.Random;

public class FieldGenerator{

    //увелечение MinesAround в соседних клетках на 1
    private static void IncMinesAround(Tile[][] field, int i, int j) {
        //Пробуем инкрементировать число мин вокруг клетки
        if(0 <= i && i < field.length &&
           0 <= j && j < field.length){

            Tile tile = field[i][j];
            tile.SetMinesAround(tile.GetMinesAround() + 1);
        }
    }

    private static void IncAround(Tile[][] field, int i, int j){
        IncMinesAround(field, i + 1, j + 1);
        IncMinesAround(field, i - 1, j - 1);
        IncMinesAround(field, i + 1, j - 1);
        IncMinesAround(field, i - 1, j + 1);

        IncMinesAround(field, i, j + 1);
        IncMinesAround(field, i, j - 1);
        IncMinesAround(field, i + 1, j);
        IncMinesAround(field, i - 1, j);
    }

    public static void MineGeneration(Tile[][] field, int countMine){
        Random random;
        if (SaperApplication.getSeed() != -1)
            random = new Random(SaperApplication.getSeed());
        else
            random = new Random();

        for (int i = 0; i < field.length;i++)
            for (int j = 0;j < field.length;j++)
            {
                if (random.nextBoolean() && countMine != 0)
                {
                    field[i][j].IsMine = true;
                    IncAround(field, i, j);
                    countMine--;
                }

            }
    }

    public static Tile[][] FieldGeneration(int rank) {
        Tile[][] field = new Tile[rank][rank];

        for (int i = 0; i < rank; i++)
            for (int y = 0; y < rank; y++)
            {
                field[i][y] = new Tile(i,y);

                field[i][y].getStyleClass().add("tile");
//                field[i][y].getStyleClass().add("hard");
                field[i][y].SetMinesAround(0);
            }

        return field;
    }
}

