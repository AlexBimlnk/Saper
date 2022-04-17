package com.example.saper;


import javafx.util.Pair;

import java.security.InvalidParameterException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

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
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
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
            tile.setMinesAround(tile.getMinesAround() + 1);
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

        if(len <= 0 || unitCount < 0) {
            throw new InvalidParameterException("Negative values");
        }
        if (random == null) {
            random = new Random();
        }

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

        if (sum <= TilePrior.counts[0]) {
            return TilePrior.UltraHigh;
        }
        else if (sum <= TilePrior.counts[1]) {
            return TilePrior.High;
        }
        else if (sum <= TilePrior.counts[2]) {
            return TilePrior.Average;
        }
        else if (sum <= TilePrior.counts[3]) {
            return TilePrior.Low;
        }
        else {
            return TilePrior.UltraLow;
        }
    }

    private static ArrayList<Pair<Integer, Integer>>[] CheckAllWays(Tile[][] field,int iPos, int jPos){
        ArrayList<Pair<Integer,Integer>>[] arr = new ArrayList[5];

        for (int y = 0; y < arr.length; y++) {
            arr[y] = new ArrayList<>();
        }

        for (var way : GetCoordinateTilesAround(field, iPos, jPos))
        {
            int tI = way.getKey();
            int tJ = way.getValue();

            if (!field[tI][tJ].IsMine && !field[tI][tJ].IsStartPoint) {
                arr[GetTilePrior(field,tI,tJ).GetInt()].add(new Pair<>(tI,tJ));
            }
        }

        return arr;
    }

    public static void MineGeneration(Tile[][] field, int countMine){
        Random random;
        if (SaperApplication.getSeed() != -1) {
            random = new Random(SaperApplication.getSeed());
        }
        else {
            random = new Random();
        }

        int fieldHeight = field.length; //a
        int fieldWidth = field[0].length; //b


        int numberOfBlocks = countMine + 1; //кол-во блоков на которое надо разбить поле один блок под одну мину и один блок будет зарезервирован для начальной точки

        int iNumberOfBlocks = numberOfBlocks * fieldHeight / fieldWidth; //кол-во блоков по i

        iNumberOfBlocks = (int) Math.sqrt(iNumberOfBlocks);

        int jNumberOfBlocks = numberOfBlocks / iNumberOfBlocks; //кол-во блоков по j

        int subMinesCount = numberOfBlocks % iNumberOfBlocks; // кол-во мин для которых не хватило блоков и которые будут помещенны в блоки с другими минами  пока считается = 0 всегда

        int blockHeight = fieldHeight / iNumberOfBlocks; //высота блока в Tile-ах

        //4 = for

        //распределение остатка от деления между высотами кадого блока в проекции на i так чтобы в сумме они давали высоту всего поля
        int add4BlockHeight = fieldHeight % iNumberOfBlocks;
        int distribution4BlockHeight = GetRandomBinMatrix(iNumberOfBlocks,add4BlockHeight,random);  //случайное распдределение остатка от деленения между высотами всех блоков в проекции на i

        int[] tilesHeight = new int[iNumberOfBlocks]; //массив с высотами блоков в проекции на i

        //заполнение массива с учётом распределния остатка
        for (int i = 0;i < iNumberOfBlocks;i++){
            tilesHeight[i] = blockHeight;
            if ((1 << i & distribution4BlockHeight) != 0){
                tilesHeight[i]++;
            }
        }

        int blockWidth = fieldWidth / jNumberOfBlocks; //ширина блока в Tile-ах

        //распределение остатка от деления между ширинами кадого блока в проекции на j так чтобы в сумме они давали ширину всего поля
        int add4BlockWidth = fieldWidth % jNumberOfBlocks;
        int distribution4TileWidth = GetRandomBinMatrix(jNumberOfBlocks,add4BlockWidth,random); //случайное распдределение остатка от деленения между широтами всех блоков в проекции на j

        int[] tilesWidth = new int[jNumberOfBlocks]; //массив с широтами блоков в проекции на j

        //заполнение массива с учётом распределния остатка
        for (int i = 0;i < jNumberOfBlocks;i++){
            tilesWidth[i] = blockWidth;
            if ((1 << i & distribution4TileWidth) != 0){
                tilesWidth[i]++;
            }
        }



        ArrayDeque<Pair<Integer,Integer>> mines = new ArrayDeque<>(); //очередь для запоминания положения мин


        int iUp = 0; //верхняя граница выбраного блока

        for (int iHeight : tilesHeight) {
            int jLeft = 0; //левая граница выбраного блока
            for (int jWidth : tilesWidth) {

                Pair<Integer, Integer> upLeft = new Pair<>(iUp, jLeft); //верхняя лева точка блока выбраного блока
                Pair<Integer, Integer> downRight = new Pair<>(iUp + iHeight - 1, jLeft + jWidth - 1); //правая нижняя точка выбранного блока

                if (!StartPointCheck(field, upLeft, downRight)) {
                    //рандомизация координат внутри текущего блока
                    int aI = random.nextInt(downRight.getKey() - upLeft.getKey() + 1) + upLeft.getKey(); //координата по i
                    int bJ = random.nextInt(downRight.getValue() - upLeft.getValue() + 1) + upLeft.getValue(); //координата по j

                    field[aI][bJ].IsMine = true;
                    mines.add(new Pair<>(aI, bJ));
                }

                jLeft += jWidth; //смещение левой границы при переходе на другой блок
            }
            iUp += iHeight; //смещение верхней границы при переходе на другой блок
        }

        //обход всех добавленных мин
        while (!mines.isEmpty()) {
            int i = mines.getFirst().getKey(); //координата мины по i
            int j = mines.getFirst().getValue(); //координата мины по j
            mines.pop();


            int changePositionCount = random.nextInt(6) + 5; // кол-во смещений одной мины

            //процесс повторения смещения для одной мины одна итерация цикла одно смещение
            for (int changePos = 0; changePos < changePositionCount;changePos++) {

                ArrayList<Pair<Integer,Integer>>[] waysWithPrior = CheckAllWays(field,i,j); //присваивание всем доступным путям "редкости" (шанса выпадения) в зависмиомти от различных параметров

                int upperBound = 0; //верхняя граница для рандомизации с вероятностью

                int[] priorityCheck = new int[] {0,0,0,0,0}; //используется для пропуска не используемых "редкостей"

                for (int y = 0; y < TilePrior.probability.length; y++)
                    if (waysWithPrior[y].size() != 0) {
                        upperBound += TilePrior.probability[y];
                        priorityCheck[y] = 1;
                    }
                //если нет путей куда сдвинуть мину инициализация перехода к следующей
                if (upperBound == 0)
                    break;

                //процесс рандомизации с верояностью

                int randomVal = random.nextInt(upperBound);

                int selectedRarity = 0; //выбранная "редкость"
                int currentBound = 0; //текущая граница

                //определение получившейся вероятности
                do {
                    if (priorityCheck[selectedRarity] == 1)
                        currentBound += TilePrior.probability[selectedRarity];

                    if (randomVal < currentBound)
                        break;

                    selectedRarity++;
                }
                while(selectedRarity < TilePrior.probability.length);

                //выбор мин из получившиеся "редкости"
                int selectedIndex = random.nextInt(waysWithPrior[selectedRarity].size());

                //процесс смещения мины в выбранную клетку

                field[i][j].IsMine = false;

                i = waysWithPrior[selectedRarity].get(selectedIndex).getKey();
                j = waysWithPrior[selectedRarity].get(selectedIndex).getValue();

                field[i][j].IsMine = true;
            }
            //утверждение положения мины
            IncCountMinesAroundOfTile(field, i, j);
            field[i][j].setId("mine");
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
                field[i][y].setId("default");

                field[i][y].setMinesAround(0);
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

