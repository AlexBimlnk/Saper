package com.example.saper;


import java.util.*;

import javafx.util.Pair;

public class FieldGenerator{

    //Проверяет корректны ли заданные коориднаты, т.е. индексы не выходят за границы поля.
    private static boolean IsCorrectCoordinate(int fieldLength, int iPos, int jPos){
        if(0 <= iPos && iPos < fieldLength &&
           0 <= jPos && jPos < fieldLength){
            return true;
        }

        return false;
    }

    //Проверяет находится ли точка рядом с границей поля
    private static boolean IsNearWithBorder(Tile[][] field, int iPos, int jpos){
        return (iPos <= 1 || jpos <= 1 || iPos >= field.length - 2 || jpos >= field[0].length - 2);
    }

    //Возвращает список всех клеток, находящихся вокруг заданной индексами клеткой.
    private static ArrayList<Tile> GetTilesAround(Tile[][] field, int iPos, int jPos){
        ArrayList<Tile> tiles = new ArrayList<Tile>();

        //Перебор всех индексов, находящихся вокруг
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                if(i == 0 && j == 0)
                    continue;
                if(IsCorrectCoordinate(field.length, iPos + i, jPos + j)){
                    tiles.add(field[iPos + i][jPos + j]);
                }
            }
        }

        return  tiles;
    }

    //Возвращает список координат всех клеток, находящихся вокруг заданной индексами клеткой.
    private static ArrayList<Pair<Integer, Integer>> GetCoordinateTilesAround(Tile[][] field, int iPos, int jPos){
        ArrayList<Pair<Integer, Integer>> tiles = new ArrayList<Pair<Integer, Integer>>();

        //Перебор всех индексов, находящихся вокруг
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                if(i == 0 && j == 0)
                    continue;
                if(IsCorrectCoordinate(field.length, iPos + i, jPos + j)){
                    tiles.add(new Pair<>(iPos + i, jPos + j));
                }
            }
        }

        return  tiles;
    }

    //Инкрементирует свойство MinesAround у всех клеток вокруг заданной с помощью координат клетки
    private static void IncCountMinesAroundOfTile(Tile[][] field, int iPos, int jPos){
        for(Tile tile : GetTilesAround(field, iPos, jPos)){
            tile.SetMinesAround(tile.GetMinesAround() + 1);
        }
    }

    private static int CountMinesAround(Tile [][] field, int i, int j){
        return GetTilesAround(field, i, j).size();
    }

    //?
    private static int CheckStartPointAround(Tile[][] field, int iPos, int jPos){
        int[] debuf = new int[] {9,2,0};

        int tri = 0;
        while (tri != 2)
        {
            for (var tile : GetTilesAround(field, iPos, jPos))
            {
                if (tile.IsStartPoint)
                    return debuf[tri];
            }
            tri++;
        }

        return debuf[tri];
    }



    private static TilePrio GetTilePrio(Tile[][] field, int iPos, int jPos){
        int sum = CountMinesAround(field, iPos, jPos) + (IsNearWithBorder(field, iPos, jPos) ? 1 : 0) + CheckStartPointAround(field, iPos, jPos);

        if (sum <= TilePrio.counts[0])
            return TilePrio.UltraLow;
        else if (sum <= TilePrio.counts[1])
            return TilePrio.Low;
        else if (sum <= TilePrio.counts[2])
            return TilePrio.Average;
        else
            return TilePrio.High;
    }

    private static ArrayList<Pair<Integer, Integer>>[] CheckAllWays(Tile[][] field,int iPos, int jPos){
        ArrayList<Pair<Integer,Integer>>[] arr = new ArrayList[4];

        for (int y = 0; y < arr.length; y++)
            arr[y] = new ArrayList<Pair<Integer,Integer>>();

        for (var way : GetCoordinateTilesAround(field, iPos, jPos))
        {
            int tI = way.getKey();
            int tJ = way.getValue();

            if (!field[tI][tJ].IsMine && !field[tI][tJ].IsStartPoint)
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
        boolean startPorintCheck = false;
        for (int i = 0;i < field.length;i++)
            for (int y = 0;y < field[i].length;y++)
            {
                if (ind == 0 || startPorintCheck)
                {
                    ind = step;
                    if (field[i][y].IsStartPoint)
                    {
                        startPorintCheck = true;
                    }
                    else
                    {
                        field[i][y].IsMine = true;
                        mines.add(new Pair<Integer,Integer>(i,y));
                    }
                    if (startPorintCheck)
                    {
                        startPorintCheck = false;
                        ind = step - 1;
                    }
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
            IncCountMinesAroundOfTile(field, i, j);
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
        final public static int[] probability = new int[] {45,30,20,5}; //вероятность
        final public static int[] counts = new int[] {0,2,5,7}; //кол-во мин во круг для соответсвующего типа
    }
}

