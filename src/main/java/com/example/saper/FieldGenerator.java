package com.example.saper;


import java.security.InvalidParameterException;
import java.util.*;

import javafx.util.Pair;

public class FieldGenerator{

    //Проверяет корректны ли заданные коориднаты, т.е. индексы не выходят за границы поля.
    private static boolean IsCorrectCoordinate(int iLen, int jLen, int iPos, int jPos) {
        if(0 <= iPos && iPos < iLen &&
                0 <= jPos && jPos < jLen){
            return true;
        }

        return false;
    }

    private static boolean IsCorrectCoordinate(int fieldLength, int iPos, int jPos){
        return IsCorrectCoordinate(fieldLength, fieldLength, iPos, jPos);
    }

    //Проверяет находится ли точка рядом с границей поля
    private static boolean IsNearWithBorder(Tile[][] field, int iPos, int jpos){
        return (iPos < 1 || jpos < 1 || iPos > field.length - 2 || jpos > field[0].length - 2);
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

    private static ArrayList<Tile> GetTilesAround(Tile[][] field, int iPos, int jPos, int depth){
        if (depth <= 0)
            throw new InvalidParameterException("depth has dispositive value");

        ArrayList<Tile> tiles = new ArrayList<Tile>();

        for (int i = -1*depth;i <= depth;i++)
        {
            int p1 = -1*depth;
            int p2 = i;

            if (IsCorrectCoordinate(field.length,field[0].length, p1 + iPos,p2 + jPos)){
                tiles.add(field[p1 + iPos][p2 + jPos]);
            }
            if (IsCorrectCoordinate(field.length,field[0].length, p2 + iPos,p1 + jPos) && p1 != p2){
                tiles.add(field[p2 + iPos][p1 + jPos]);
            }
        }

        for (int i = depth; i > -1*depth;i--)
        {
            int p1 = depth;
            int p2 = i;

            if (IsCorrectCoordinate(field.length,field[0].length, p1 + iPos,p2 + jPos)){
                tiles.add(field[p1 + iPos][p2 + jPos]);
            }
            if (IsCorrectCoordinate(field.length,field[0].length, p2 + iPos,p1 + jPos) && p1 != p2){
                tiles.add(field[p2 + iPos][p1 + jPos]);
            }
        }

        return tiles;
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
        int count = 0;
        for(Tile tile : GetTilesAround(field, i, j)){
            if (tile.IsMine){
                count++;
            }
        }
        return count;
    }

    private static int CheckStartPointAround(Tile[][] field, int iPos, int jPos){
        boolean check = true;

        int depth = 0;

        for (depth = 0; depth < TilePrio.debuff.length - 1 && check; depth++){
            for (var tile : GetTilesAround(field, iPos, jPos,depth + 1)){
                if (tile.IsStartPoint){
                    check = false;
                    break;
                }
            }
        }

        return TilePrio.debuff[depth];
    }



    private static TilePrio GetTilePrio(Tile[][] field, int iPos, int jPos){
        int sum = CountMinesAround(field, iPos, jPos) + (IsNearWithBorder(field, iPos, jPos) ? 5 : 0) + CheckStartPointAround(field, iPos, jPos);

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
            arr[y] = new ArrayList<>();

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

        int aS = field.length;
        int bS = field[0].length;

        int n = countMine * aS / bS;

        //целочисленно извекаем корень из n ( :
        int shift = 2;
        int nShift = n >> shift;

        while (nShift != 0 && nShift != n){
            shift +=2;
            nShift = n >> shift;
        }
        shift -= 2;

        int result = 0;

        while (shift >= 0){
            result <<= 1;
            int tmpRes = result + 1;
            if (tmpRes*tmpRes <= n >> shift){
                result = tmpRes;
            }
            shift -=2;
        }

        int aNMine = result;
        int bNMine = (countMine + result - 1)/ result;

        int aMine = aS / aNMine;
        int bMine = bS / bNMine;

        //очередь для запоминания координат раставленных мин
        ArrayDeque<Pair<Integer,Integer>> mines = new ArrayDeque<>();

        for (int i = 0;i < aNMine && countMine != 0;i++)
            for (int j = 0;j < bNMine && countMine != 0;j++)
            {
                int posI = (aS % aNMine != 0 ? 1 : 0) + i*aMine + random.nextInt(aMine);
                int posJ = (bS % bNMine != 0 ? 1 : 0) + j*bMine + random.nextInt(bMine);

                if (field[posI][posJ].IsStartPoint){
                    if (bNMine != 1){
                        int add = (posJ - j*bMine - (bS % bNMine != 0 ? 1 : 0) - (bMine-1) == 0 ? -1 : 1);
                        posJ += add;
                    }
                    else{
                        int add = (posI - i*aMine - (aS % aNMine != 0 ? 1 : 0) - (aMine-1) > 0 ? 1 : -1);
                        posI += add;
                    }
                }

                if (field[posI][posJ].IsMine)
                    throw new RuntimeException("collision happen");

                field[posI][posJ].IsMine = true;

                mines.add(new Pair<>(posI,posJ));

                countMine--;
            }

//        boolean startPorintCheck = false;
//        for (int i = 0;i < field.length;i++)
//            for (int y = 0;y < field[i].length;y++)
//            {
//                if (ind == 0 || startPorintCheck)
//                {
//                    ind = step;
//                    if (field[i][y].IsStartPoint)
//                    {
//                        startPorintCheck = true;
//                    }
//                    else
//                    {
//                        field[i][y].IsMine = true;
//                        mines.add(new Pair<Integer,Integer>(i,y));
//                    }
//                    if (startPorintCheck)
//                    {
//                        startPorintCheck = false;
//                        ind = step - 1;
//                    }
//                }
//                ind--;
//            }

        while (!mines.isEmpty())
        {
            int i = mines.getFirst().getKey();
            int j = mines.getFirst().getValue();
            mines.pop();

            //генерация кол-ва смещения мины
            int count = random.nextInt(6) + 5;

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

                field[i][y].getStyleClass().add("tile");
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
        final public static int[] debuff = new int[] {11,2,0};
    }
}

