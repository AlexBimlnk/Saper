package com.example.saper;


import java.security.InvalidParameterException;
import java.util.*;

import javafx.util.Pair;

public class FieldGenerator{

    //Проверяет корректны ли заданные коориднаты, т.е. индексы не выходят за границы поля.
    private static boolean IsCorrectCoordinate(int iLen, int jLen, int iPos, int jPos) {
        return 0 <= iPos && iPos < iLen &&
                0 <= jPos && jPos < jLen;
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
        ArrayList<Tile> tiles = new ArrayList<>();

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

//    не удолять
//    private static ArrayList<Tile> GetSetBorderTiles(Tile[][]field, Pair<Integer,Integer> upLeft, Pair<Integer,Integer> downRight)
//    {
//        if (field.length == 0)
//            throw new InvalidParameterException();
//
//        if (upLeft.getKey() > downRight.getKey() ||
//                upLeft.getValue() > downRight.getValue())
//            throw new InvalidParameterException();
//
//        ArrayList<Tile> ret = new ArrayList<>();
//
//        /*
//        * >@######
//        *  .......
//        *  .......
//        *  ######@<
//        */
//        int index = 0;
//        for (int i = 0;i <= downRight.getValue() - upLeft.getValue();i++)
//        {
//            if (IsCorrectCoordinate(field.length, field[0].length, upLeft.getKey(), upLeft.getValue() + i))
//            {
//                ret.add(index,field[upLeft.getKey()][upLeft.getValue() + i]);
//                field[upLeft.getKey()][upLeft.getValue() + i].SetMinesAround(index + 1);
//                index++;
//            }
//            if (IsCorrectCoordinate(field.length, field[0].length, downRight.getKey(), downRight.getValue() - i) &&
//                    (upLeft.getKey() != downRight.getKey() && upLeft.getValue() + i != downRight.getValue() - i)){
//                ret.add(field[downRight.getKey()][downRight.getValue()-i]);
//            }
//
//        }
//
//        /*
//         *  @......
//         * >#.....#
//         *  #.....#<
//         *  ......@
//         */
//
//        for (int i = 1;i < downRight.getKey() - upLeft.getKey();i++)
//        {
//
//            if (IsCorrectCoordinate(field.length, field[0].length, downRight.getKey() - i, downRight.getValue())){
//                ret.add(index,field[downRight.getKey() - i][downRight.getValue()]);
//                field[downRight.getKey() - i][downRight.getValue()].SetMinesAround(index+downRight.getKey() - upLeft.getKey()-i);
//                //index++;
//            }
//
//
//            if (IsCorrectCoordinate(field.length, field[0].length, upLeft.getKey() + i, upLeft.getValue()) &&
//                    (upLeft.getKey() + i != downRight.getKey() - i && upLeft.getValue() != downRight.getValue()))
//            {
//                ret.add(index,field[upLeft.getKey() + i][upLeft.getValue()]);
//            }
//        }
//        return ret;
//    }

    private static ArrayList<Tile> GetTilesAround(Tile[][] field, int iPos, int jPos, int depth){
        if (depth <= 0)
            throw new InvalidParameterException("depth has dispositive value");

        ArrayList<Tile> tiles = new ArrayList<>();

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
        ArrayList<Pair<Integer, Integer>> tiles = new ArrayList<>();

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
        int depth;

        for (depth = 0; depth < TilePrior.debuff.length - 1; depth++){
            for (var tile : GetTilesAround(field, iPos, jPos,depth + 1)){
                if (tile.IsStartPoint){
                    return TilePrior.debuff[depth];
                }
            }

        }

        return TilePrior.debuff[depth];
    }

    private static int GetRandomBinMatrix(int len, int unitCount, Random random){
        int result = 0;

        if(len <= 0 || unitCount < 0)
            throw new InvalidParameterException("Negative values");
        if (random == null)
            random = new Random();

        for (int i = unitCount-1;i >= 0;i--){
            int shift = random.nextInt(len - i) + i;
            int newUnit = 1 << shift;

            if ((newUnit & result) != 0) {
                newUnit = 1 << i;
            }
            result += newUnit;
        }
        return result;
    }

    private static int IntSqrt(int n){
        //целочисленное извлечение корня : ) пока без коментов

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
        return result;
    }

    private static boolean StartPointCheck(Tile[][] field, Pair<Integer,Integer> upLeft, Pair<Integer,Integer> downRight){
        for (int i = upLeft.getKey();i <= downRight.getKey();i++){
            for (int j = upLeft.getValue();j <= downRight.getValue();j++)
                if (field[i][j].IsStartPoint){
                    return true;
                }
        }

        return false;
    }

    private static TilePrior GetTilePrior(Tile[][] field, int iPos, int jPos){
        int sum = CountMinesAround(field, iPos, jPos) + (IsNearWithBorder(field, iPos, jPos) ? 2 : 0) + CheckStartPointAround(field, iPos, jPos);

        if (sum <= TilePrior.counts[0])
            return TilePrior.UltraHigh;
        else if (sum <= TilePrior.counts[1])
            return TilePrior.High;
        else if (sum <= TilePrior.counts[2])
            return TilePrior.Average;
        else if (sum <= TilePrior.counts[3])
            return TilePrior.Low;
        else
            return TilePrior.UltraLow;
    }

    private static ArrayList<Pair<Integer, Integer>>[] CheckAllWays(Tile[][] field,int iPos, int jPos){
        ArrayList<Pair<Integer,Integer>>[] arr = new ArrayList[5];

        for (int y = 0; y < arr.length; y++)
            arr[y] = new ArrayList<>();

        for (var way : GetCoordinateTilesAround(field, iPos, jPos))
        {
            int tI = way.getKey();
            int tJ = way.getValue();

            if (!field[tI][tJ].IsMine && !field[tI][tJ].IsStartPoint)
                arr[GetTilePrior(field,tI,tJ).GetInt()].add(new Pair<>(tI,tJ));
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

        int ind = 0;

        int aS = field.length; //a
        int bS = field[0].length; //b

        int n = countMine + 1;

        // a * b = s
        // p * q = n
        // a/b = p/q => qa = pb
        //
        // p * q * a = n * a => p^2 * b = n * a => p = (n * a / b)^1/2

        int p = n * aS / bS;

        p = IntSqrt(p);

        int q = n / p;

        int subMinesCount = n % p; //пока считается = 0 всегда

        int aMineSize = aS / p;
        //4 = for
        int add4AMIneSize = aS % p;
        int distribution4AMineSize = GetRandomBinMatrix(p,add4AMIneSize,random);  //случайное распдределение остатка от деленения межу всеми блоками

        int[] aMineSizes = new int[p]; //разбиаение поля по одной стороне

        for (int i = 0;i < p;i++){
            aMineSizes[i] = aMineSize;
            if ((1 << i & distribution4AMineSize) != 0){
                aMineSizes[i]++;
            }
        }

        int bMineSize = bS / q;

        int add4BMIneSize = bS % q;
        int distribution4BMineSize = GetRandomBinMatrix(q,add4BMIneSize,random); //случайное распдределение остатка от деленения межу всеми блоками

        int[] bMineSizes = new int[q]; //разбиаение поля по другой стороне

        for (int i = 0;i < q;i++){
            bMineSizes[i] = bMineSize;
            if ((1 << i & distribution4BMineSize) != 0){
                bMineSizes[i]++;
            }
        }


        //очередь для запоминания координат раставленных мин
        ArrayDeque<Pair<Integer,Integer>> mines = new ArrayDeque<>();

        int iLeft = 0;

        for (int iMineSize : aMineSizes) {
            int jUp = 0;
            for (int jMineSize : bMineSizes) {

                Pair<Integer, Integer> upLeft = new Pair<>(iLeft, jUp); //верхняя лева точка блока
                Pair<Integer, Integer> downRight = new Pair<>(iLeft + iMineSize - 1, jUp + jMineSize - 1); //правая нижняя

                if (!StartPointCheck(field, upLeft, downRight)) {
                    //рандомизация индекса внутри блока
                    int aI = random.nextInt(downRight.getKey() - upLeft.getKey() + 1) + upLeft.getKey();
                    int bJ = random.nextInt(downRight.getValue() - upLeft.getValue() + 1) + upLeft.getValue();

                    field[aI][bJ].IsMine = true;
                    mines.add(new Pair<>(aI, bJ));
                }

                jUp += jMineSize;
            }
            iLeft += iMineSize;
        }


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
                ArrayList<Pair<Integer,Integer>>[] waysWithPrior = CheckAllWays(field,i,j);

                int upperBound = 0;

                //для пропуска пуствх приоритетов
                int[] check = new int[] {0,0,0,0,0};

                for (int y = 0; y < TilePrior.probability.length; y++)
                    if (waysWithPrior[y].size() != 0)
                    {
                        upperBound += TilePrior.probability[y];
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
                        sum += TilePrior.probability[y];

                    if (randomVal < sum)
                        break;

                    y++;
                }
                while(y < TilePrior.probability.length);

                //случайный выбор мин среди выбраного приоритета
                int selectedIndex = random.nextInt(waysWithPrior[y].size());

                field[i][j].IsMine = false;

                i = waysWithPrior[y].get(selectedIndex).getKey();
                j = waysWithPrior[y].get(selectedIndex).getValue();

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

    private enum TilePrior {
        UltraLow {
            @Override
            public int GetInt() {
                return 4;
            }
        },
        Low {
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
        High {
            @Override
            public int GetInt() {
                return 1;
            }
        },
        UltraHigh {
            @Override
            public int GetInt() {
                return 0;
            }
        };

        public abstract int GetInt();
        final public static int[] probability = new int[] {45,30,20,5,1}; //вероятность
        final public static int[] counts = new int[] {0,2,5,7,10}; //кол-во мин во круг для соответсвующего типа
        final public static int[] debuff = new int[] {8,4,0};
    }
}

