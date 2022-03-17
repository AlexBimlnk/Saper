package com.example.saper;


import java.util.*;

import javafx.util.Pair;

public class FieldGenerator{

    final private int _RandomCount = 2;

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

    private static boolean IsNearWithBorder(Tile[][] field, int i, int j)
    {
        return (i <= 1 || j <= 1 || i >= field.length - 2 || j >= field[0].length - 2);
    }

    private static int CountMinesAround(Tile [][] field, int i, int j)
    {
        return (i - 1 >= 0 && j - 1 >= 0 ?
                (field[i-1][j-1].IsMine ? 1 : 0) + (field[i-1][j].IsMine ? 1 : 0) + (field[i][j-1].IsMine ? 1 : 0) :
                (i - 1 >= 0 ? (field[i-1][j].IsMine ? 1 : 0) : (j - 1 >= 0 ? (field[i][j - 1].IsMine ? 1 : 0):0))) +

                (i + 1 <= field.length - 1 && j + 1 <= field[0].length - 1 ?
                        (field[i+1][j+1].IsMine ? 1 : 0) + (field[i+1][j].IsMine ? 1 : 0) + (field[i][j+1].IsMine ? 1 : 0) :
                        (i + 1 <= field.length - 1 ? (field[i+1][j].IsMine ? 1 : 0) : (j + 1 <= field[0].length - 1?(field[i][j + 1].IsMine ? 1 : 0):0))) +

                (i + 1 <= field.length - 1 && j - 1 >= 0 && field[i+1][j-1].IsMine? 1: 0) +
                (j + 1 <= field[0].length - 1 && i - 1 >= 0 && field[i-1][j+1].IsMine? 1: 0);
    }

    private static TilePrio GetTilePrio(Tile[][] field, int i, int j)
    {
        int sum = CountMinesAround(field, i, j) + (IsNearWithBorder(field, i, j) ? 1 : 0);

        if (sum <= TilePrio.counts[0])
            return TilePrio.UltraLow;
        else if (sum <= TilePrio.counts[1])
            return TilePrio.Low;
        else if (sum <= TilePrio.counts[2])
            return TilePrio.Average;
        else
            return TilePrio.High;
    }

   private static ArrayList<Pair<Integer,Integer>>[] CheckAllWays(Tile[][] field,int i, int j)
    {
        ArrayList<Pair<Integer,Integer>>[] arr = new ArrayList[4];

        for (int y = 0;y < arr.length;y++)
            arr[y] = new ArrayList<Pair<Integer,Integer>>();

        final Pair<Integer,Integer>[] ways = new Pair[]{
                new Pair(-1,-1),
                new Pair(-1,0),
                new Pair(-1,1),
                new Pair(0,1),
                new Pair(1,1),
                new Pair(1,0),
                new Pair(1,-1),
                new Pair(0,-1)
        };

        for (var way : ways)
        {
            int tI = i + way.getKey();
            int tJ = j + way.getValue();

            if (tI >= 0 && tJ >= 0 && tI < field.length && tJ < field[0].length && !field[tI][tJ].IsMine)
                arr[GetTilePrio(field,tI,tJ).GetInt()].add(new Pair<>(tI,tJ));
        }

        return arr;
    }

    public static void MineGeneration(Tile[][] field, int countMine){
        Random random;
        if (SaperApplication.getSeed() != -1)
            random = new Random(SaperApplication.getSeed());
        else
            random = new Random();

        int s = field.length * field[0].length;

        int step = s / countMine;

        if (step < 2)
            step = 2;

        int ind = 0;

        //очередь для запоминания координат раставленных мин
        ArrayDeque<Pair<Integer,Integer>> mines = new ArrayDeque<>();

        for (int i = 0;i < field.length;i++)
            for (int y = 0;y < field[i].length;y++)
            {
                if (ind == 0)
                {
                    field[i][y].IsMine = true;
                    mines.add(new Pair<Integer,Integer>(i,y));
                    ind = step;
                }
                ind--;
            }

        while (!mines.isEmpty())
        {
            int i = mines.getFirst().getKey();
            int j = mines.getFirst().getValue();
            mines.pop();

            //генерация кол-ва смещения мины
            int count = random.nextInt(2) + 1;

            for (int tri = 0; tri < count;tri++)
            {
                //разбиение всех возможных путей наприоритеты
                ArrayList<Pair<Integer,Integer>>[] waysWithPrio = CheckAllWays(field,i,j);

                int upperBound = 0;

                //для пропуска пуствх приоритетов
                int[] check = new int[] {0,0,0,0};

                for (int y = 0;y < TilePrio.probability.length;y++)
                    if (waysWithPrio[y].size() != 0)
                    {
                        upperBound += TilePrio.probability[y];
                        check[y] = 1;
                    }
                //если вариантов куда сдвинуть мину нет
                if (upperBound == 0)
                    break;

                //радомизация с вероятностью
                int randomVal = random.nextInt(upperBound);

                int y = 0;
                int sum = 0;

                do
                {
                    if (check[y] == 1)
                        sum += TilePrio.probability[y];

                    if (randomVal < sum)
                        break;

                    y++;
                }
                while(y < TilePrio.probability.length);

                //случайный выбор мин среди выбраного приоритета
                int selectedIndex = random.nextInt(waysWithPrio[y].size());

                field[i][j].IsMine = false;
                i = waysWithPrio[y].get(selectedIndex).getKey();
                j = waysWithPrio[y].get(selectedIndex).getValue();

                field[i][j].IsMine = true;
            }
            IncAround(field, i, j);
        }

    }

    public static Tile[][] FieldGeneration(int rank, String style) {
        Tile[][] field = new Tile[rank][rank];

        for (int i = 0; i < rank; i++)
            for (int y = 0; y < rank; y++)
            {
                field[i][y] = new Tile(i,y);

                field[i][y].getStyleClass().add(style);
//                field[i][y].getStyleClass().add("hard");
                field[i][y].SetMinesAround(0);
            }

        return field;
    }

    private enum TilePrio{
        High {
            @Override
            public int GetInt() {
                return 3;
            }
        },
        Average {
            @Override
            public int GetInt() {
                return 2;
            }
        },
        Low {
            @Override
            public int GetInt() {
                return 1;
            }
        },
        UltraLow {
            @Override
            public int GetInt() {
                return 0;
            }
        };

        public abstract int GetInt();
        final public static int[] probability = new int[] {45,30,15,10}; //вероятность
        final public static int[] counts = new int[] {0,2,5,7}; //кол-во мин во круг для соответсвующего типа
    }
}

