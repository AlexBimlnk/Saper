package com.example.saper.gamefield;


import com.example.saper.Config;
import com.example.saper.SaperApplication;
import javafx.util.Pair;

import java.net.Inet4Address;
import java.security.InvalidParameterException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

public class FieldGenerator{

    private static final int _mineWeight = 10;
    private static final int _startPointWeight = 180;
    private static final int _borderWeight = 15;
    private static final int[] _startPointPunish = {175, 55, 30, 15 ,5, 0};

    private static int GetDistanceToPoints(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        int d1 = Math.abs(p2.getKey() - p1.getKey());

        int d2 = Math.abs(p2.getValue() - p1.getValue());

        return Math.max(d1,d2);
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

    private static TilePrior GetTilePrior(Field field, int iPos, int jPos){
        int sum = field.CountMinesAround(iPos, jPos) +
                (field.IsNearWithBorder(iPos, jPos) ? 2 : 0) +
                field.StartPointCheckAround(iPos, jPos, TilePrior.debuff);

        if (sum <= TilePrior.counts[0]) {
            return TilePrior.UltraHigh;
        }
        if (sum <= TilePrior.counts[1]) {
            return TilePrior.High;
        }
        if (sum <= TilePrior.counts[2]) {
            return TilePrior.Average;
        }
        if (sum <= TilePrior.counts[3]) {
            return TilePrior.Low;
        }

        return TilePrior.UltraLow;
    }

    private static ArrayList<Pair<Integer, Integer>>[] CheckAllWays(Field field,int iPos, int jPos){
        ArrayList<Pair<Integer,Integer>>[] arr = new ArrayList[5];

        for (int y = 0; y < arr.length; y++) {
            arr[y] = new ArrayList<>();
        }

        field.ApplyToAround(iPos,jPos, (coordinatePair) -> {
            if (!field.getTile(coordinatePair.getKey(), coordinatePair.getValue()).IsMine &&
                !field.isStartPoint(coordinatePair)) {
                arr[GetTilePrior(field,coordinatePair.getKey(),coordinatePair.getValue()).GetInt()].add(new Pair<>(coordinatePair.getKey(),coordinatePair.getValue()));
            }
        });

        return arr;
    }

    public static Pair<int[],int[]> SplitFieldOnBlocks(Field field, int number,Random random) {
        int fieldHeight = field.getSizes().getKey(); //a
        int fieldWidth = field.getSizes().getValue(); //b

        int iNumberOfBlocks = number * fieldHeight / fieldWidth; //кол-во блоков по i

        iNumberOfBlocks = (int) Math.sqrt(iNumberOfBlocks);

        int jNumberOfBlocks = number / iNumberOfBlocks; //кол-во блоков по j

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

        return new Pair<>(tilesHeight,tilesWidth);
    }

    public static void MineGen(Field field, int countMine) {
        Random random;
        if (SaperApplication.getSeed() != -1) {
            random = new Random(SaperApplication.getSeed());
        }
        else {
            random = new Random();
        }

        ArrayList<Pair<Integer,Pair<Integer,Integer>>> vectors = new ArrayList<>();

        int[][] indexes = new int[field.getSizes().getKey()][field.getSizes().getValue()];

        for (int i = 0;i < field.getSizes().getKey();i++) {
            for (int y = 0;y < field.getSizes().getValue();y++) {
                //indexes[i][y] = -1;
                Pair<Integer,Integer> curTile = new Pair<>(i,y);
                if (!field.isStartPoint(curTile)) {
                    int distToStart = GetDistanceToPoints(curTile, field.getStartPoint()) - 1;
                    int probability = 9 * _mineWeight + _startPointWeight;

                    if (!field.IsNearWithBorder(curTile.getKey(), curTile.getValue())) {
                        probability += _borderWeight;
                    }

                    if (distToStart >= _startPointPunish.length) {
                        distToStart = _startPointPunish.length - 1;
                    }

                    probability -= _startPointPunish[distToStart];


                    indexes[i][y] = vectors.size();
                    vectors.add(new Pair<>(probability,curTile));
                    System.out.println(indexes[i][y]);
                }
            }
        }

        for (int i = 0;i < countMine;i++) {
            CustomRandom<Integer> customRandom = new CustomRandom<>(random);

            for (int y = 0;y < vectors.size();y++) {
                var elem = vectors.get(y);
                if (indexes[elem.getValue().getKey()][elem.getValue().getValue()] == -1) {
                    continue;
                }

                customRandom.addNewElem(elem.getKey(),y);
            }

            int randomIndex = customRandom.GetRandomElem(false);

            Pair<Integer,Integer> newMineCord = vectors.get(randomIndex).getValue();

            field.getTile(newMineCord.getKey(),newMineCord.getValue()).IsMine = true;
            field.getTile(newMineCord.getKey(),newMineCord.getValue()).setId("mine");

            field.IncCountMinesAroundOfTile(newMineCord.getKey(),newMineCord.getValue());

            field.ApplyToAround(newMineCord.getKey(),newMineCord.getValue(), (coordinateAround) -> {
                if (indexes[coordinateAround.getKey()][coordinateAround.getValue()] != -1) {
                    var oldPair = vectors.get(indexes[coordinateAround.getKey()][coordinateAround.getValue()]);

                    if (field.CountMinesAround(coordinateAround.getKey(),coordinateAround.getValue()) > 1) {
                        Pair<Integer,Pair<Integer,Integer>> newPair = new Pair<>(oldPair.getKey() - _mineWeight,oldPair.getValue());

                        vectors.set(indexes[coordinateAround.getKey()][coordinateAround.getValue()],newPair);
                    }
                }
            },1);

            indexes[vectors.get(randomIndex).getValue().getKey()][vectors.get(randomIndex).getValue().getValue()] = -1;
            //System.out.println(i);
        }

    }

    public static void MineGeneration(Field field, int countMine){

        Random random;
        if (SaperApplication.getSeed() != -1) {
            random = new Random(SaperApplication.getSeed());
        }
        else {
            random = new Random();
        }

        Pair<int[],int[]> fieldBlocks = SplitFieldOnBlocks(field, countMine + 1, random);

        int subMinesCount = (countMine + 1) % (fieldBlocks.getKey().length);

        boolean[][] subMineForBlockCheck = new boolean[fieldBlocks.getKey().length][fieldBlocks.getValue().length];

        if (subMinesCount != 0) {

            CustomRandom<Integer> customRandom = new CustomRandom<>(random);

            for (int i = 0;i < fieldBlocks.getKey().length;i++) {
                for (int j = 0; j < fieldBlocks.getValue().length;j++) {
                    subMineForBlockCheck[i][j] = false;

                    int areaOfBlock = fieldBlocks.getKey()[i] * fieldBlocks.getValue()[j];

                    if (areaOfBlock == 1)
                        continue;

                    customRandom.addNewElem((areaOfBlock),
                            ((i + 1) * fieldBlocks.getValue().length + j));
                }
            }

            for (int subMine = 1; subMine <= subMinesCount;subMine++) {
                int randomval = customRandom.GetRandomElem(true);

                int j = randomval % fieldBlocks.getValue().length;
                int i = randomval / fieldBlocks.getValue().length - 1;

                subMineForBlockCheck[i][j]=true;
            }
        }

        ArrayDeque<Pair<Integer,Integer>> mines = new ArrayDeque<>(); //очередь для запоминания положения мин

        int iUp = 0; //верхняя граница выбраного блока

        for (int i = 0; i < fieldBlocks.getKey().length; i++) {
            int iHeight = fieldBlocks.getKey()[i];
            int jLeft = 0; //левая граница выбраного блока
            for (int j = 0; j < fieldBlocks.getValue().length;j++) {
                int jWidth = fieldBlocks.getValue()[j];
                Pair<Integer, Integer> upLeft = new Pair<>(iUp, jLeft); //верхняя лева точка блока выбраного блока
                Pair<Integer, Integer> downRight = new Pair<>(iUp + iHeight - 1, jLeft + jWidth - 1); //правая нижняя точка выбранного блока

                if (!field.StartPointCheckInBlock(upLeft, downRight)) {
                    //рандомизация координат внутри текущего блока
                    int aI = random.nextInt(downRight.getKey() - upLeft.getKey() + 1) + upLeft.getKey(); //координата по i
                    int bJ = random.nextInt(downRight.getValue() - upLeft.getValue() + 1) + upLeft.getValue(); //координата по j

                    if (subMinesCount != 0 && subMineForBlockCheck[i][j]) {
                        int subAI, subBJ;
                        subAI = random.nextInt(downRight.getKey() - upLeft.getKey() + 1) - (aI);

                        if (subAI+aI < upLeft.getKey()) {
                            subAI = 0;
                        }

                        if (subAI == 0) {
                            subBJ = random.nextInt(downRight.getValue() - upLeft.getValue()) - (bJ);
                            if (subBJ >= 0)
                                subBJ++;
                        }
                        else{
                            subBJ = random.nextInt(downRight.getValue() - upLeft.getValue() + 1) - (bJ);
                        }

                        if ((subBJ == 0 && subAI == 0) ||
                                subAI + aI < upLeft.getKey() || subAI + aI > downRight.getKey() ||
                                subBJ + bJ < upLeft.getValue() || subBJ + bJ > downRight.getValue())
                            throw new RuntimeException(
                                    Integer.toString(subAI) +" "+
                                    Integer.toString(aI) +" |"+
                                    Integer.toString(subBJ) +" "+
                                    Integer.toString(bJ) +" | upl"+
                                    Integer.toString(upLeft.getKey()) +" "+
                                    Integer.toString(upLeft.getValue()) +" drt"+
                                    Integer.toString(downRight.getKey()) +" "+
                                    Integer.toString(downRight.getValue()) +" "
                            );


                        field.getTile(aI + subAI,bJ + subBJ).IsMine = true;
                        mines.add(new Pair<>(aI + subBJ, bJ + subBJ));
                        subMinesCount--;
                    }

                    field.getTile(aI,bJ).IsMine = true;
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

                CustomRandom<Integer> customRandom = new CustomRandom<Integer>(random);

                for (int y = 0; y < TilePrior.probability.length; y++) {
                    if (waysWithPrior[y].size() != 0) {
                        customRandom.addNewElem(TilePrior.probability[y],y);
                    }
                }

                if (customRandom.getSize() == 0)
                    break;

                int selectedRarity = customRandom.GetRandomElem(false);

                int selectedIndex = random.nextInt(waysWithPrior[selectedRarity].size());

                //процесс смещения мины в выбранную клетку

                field.getTile(i, j).IsMine = false;

                i = waysWithPrior[selectedRarity].get(selectedIndex).getKey();
                j = waysWithPrior[selectedRarity].get(selectedIndex).getValue();

                field.getTile(i, j).IsMine = true;
            }
            //утверждение положения мины
            field.IncCountMinesAroundOfTile(i, j);
            field.getTile(i, j).setId("mine");
            System.out.println(Integer.toString(i) + '-' + Integer.toString(j));
        }

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

