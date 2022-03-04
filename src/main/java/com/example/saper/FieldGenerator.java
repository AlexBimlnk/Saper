package com.example.saper;


import java.util.Random;

public class FieldGenerator{

    //увелечение MinesAround в соседних клетках на 1
    private static void IncAround(Tile[][] field, int i, int y)
    {
        interface Operation {

            void Inc(Tile tile);

        }
        Operation op = tile -> tile.setMinesAround(tile.getMinesAround() + 1);

        op.Inc(field[i + 1][y + 1]);
        op.Inc(field[i - 1][y - 1]);
        op.Inc(field[i + 1][y - 1]);
        op.Inc(field[i - 1][y + 1]);

        op.Inc(field[i][y + 1]);
        op.Inc(field[i][y - 1]);
        op.Inc(field[i + 1][y]);
        op.Inc(field[i - 1][y]);
    }

    public static Tile[][] MineGeneration(int height, int width, int count)
    {
        Random random;
        if (SaperApplication.getSeed() != -1)
            random = new Random(SaperApplication.getSeed());
        else
            random = new Random();

        Tile[][] field = new Tile[height + 2][width + 2];

        for (int i = 0;i < height + 2;i++)
            for (int y = 0;y < width + 2;y++)
            {
                //инциализация
                field[i][y] = new Tile(i,y);
                field[i][y].setMinesAround(0);

                if (i == 0 || y == 0 || i == height+1 || y == width+1)
                    field[i][y].IsBorder = true;
            }

        for (int i = 1;i <= height;i++)
            for (int y = 1;y <= width;y++)
            {
                if (random.nextBoolean() && count != 0)
                {
                    field[i][y].IsMine = true;
                    IncAround(field,i,y);
                    count--;
                }

            }

        return field;
    }

}

