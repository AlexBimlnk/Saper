package com.example.saper.gamefield;


import com.example.saper.GameController;
import com.example.saper.SaperApplication;
import com.example.saper.custom.structure.RandomWithProbability;
import com.example.saper.custom.structure.DSU;
import javafx.util.Pair;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Генератор мин.
 */
public class MineGenerator {

    private static final int _startPointWeight = 500;
    private static final int _borderWeight = 15;
    private static final int[] _startPointPunish = {499, 55, 30, 15 ,5, 0};
    private static int _mineSetStep = 1;

    /**
     * Функция получения вероятности по математической формуле
     * @param x Целое число
     * @return Полученная вероятность
     */
    private static int mathGetProbability(int x) {
        double res = (Math.exp(-(float)x/6+3))*2/3;

        return (int)res;
    }

    private static int countProbability(Pair<Integer, Integer> tile) {
        if (Field.isStartPoint(tile)) {
            return 0;
        }

        int distToStart = Field.getDistanceToPoints(tile, Field.getStartPoint()) - 1;
        int probability = _startPointWeight;

        if (!Field.isNearWithBorder(tile.getKey(), tile.getValue())) {
            probability += _borderWeight;
        }

        if (distToStart >= _startPointPunish.length) {
            distToStart = _startPointPunish.length - 1;
        }

        probability -= _startPointPunish[distToStart];

        return probability;
    }

    /**
     * <p>
     *     Алгоритм по следующему принципу: мины, находящиеся на расстоянии равном {@code mineSetStep},
     *     считаются находящимися в одном множестве. Управление множествами реализовано при помощи {@link DSU}.
     *     Механизм рандомизации с вероятностью реализована в {@link RandomWithProbability}.
     * </p>
     * <p>
     *     При итерации добавления мины сначала выбирается будет ли она добавлена в существующее
     *     множество или создастся новое. Вероятность добавление в новое множество считается равной {@code MathGetProbability(0)}.
     *     Вероятность в существующее - {@code MathGetProbability(<i>размер множесвта</i>)}.
     * </p>
     * @param countMine Кол-во мин.
     */
    public static void mineGen(int countMine) throws InvalidParameterException {
        if (countMine <= 0)
            throw new InvalidParameterException("Count of mine should be positive.");

        switch (GameController.getGameDifficulty()) {
            case Easy -> _mineSetStep = 2;
            case Normal, Hard -> _mineSetStep = 3;
        }

        Random random = SaperApplication.getSeed() != -1
                        ? new Random(SaperApplication.getSeed())
                        : new Random();

        ArrayList<Pair<Integer,Pair<Integer,Integer>>> tilesWithProbability = new ArrayList<>(); //массив с координатами клеток с дефолтной вероятностью

        AtomicInteger inSetTilesCount = new AtomicInteger(0); //счетчик клеток охватываемых множетсвами

        int[][] fieldCheck = new int[Field.getSizes().getKey()][Field.getSizes().getValue()];

        for (var cord : Field.getAllCoordinates()) {
            fieldCheck[cord.getKey()][cord.getValue()] = 0;
            if (Field.isStartPoint(cord)) {
                fieldCheck[cord.getKey()][cord.getValue()] = -1;
            }
        }

        DSU mineSets = new DSU(countMine);

        for (int mineIteration = 0; mineIteration < countMine; mineIteration++) {

            int setContinueProbability = 0;

            for (var set : mineSets.getAllUniqueSets()) {
                setContinueProbability += mathGetProbability(mineSets.getSetSize(set));
            }

            RandomWithProbability<Integer> choseBehave = new RandomWithProbability<>(random);

            if (inSetTilesCount.get() != Field.getSizes().getValue() * Field.getSizes().getKey() - 1) {
                choseBehave.addNewElem(2*mathGetProbability(0),0);
            }

            choseBehave.addNewElem(setContinueProbability,1);

            RandomWithProbability<Pair<Integer, Integer>> tileRandomize = new RandomWithProbability(random);

            if (choseBehave.getRandomElem() == 0) {
                for (var cord : Field.getAllCoordinates()) {
                    if (fieldCheck[cord.getKey()][cord.getValue()] == 0) {
                        tileRandomize.addNewElem(countProbability(cord), cord);
                    }
                }
            }
            else {
                //перебор всех клеток множеств - несвободных
                for (var setLeader : mineSets.getAllUniqueSets()) {
                    for (var setsMine : mineSets.getAllSetElem(setLeader)) {

                        Field.applyToAroundArea(mineSets.getElemInSet(setsMine).getKey(), mineSets.getElemInSet(setsMine).getValue(), (tile) -> {
                            if (fieldCheck[tile.getKey()][tile.getValue()] == setsMine + 1) {
                                int countMinesAround = Field.countMinesAround(tile.getKey(),tile.getValue());
                                int probabilityOfTile = mathGetProbability(mineSets.getSetSize(setLeader)) +
                                        countProbability(tile) + mathGetProbability(countMinesAround);

                                tileRandomize.addNewElem(probabilityOfTile, tile);
                            }

                        }, _mineSetStep);
                    }
                }
            }

            Pair<Integer,Integer> newMineCord = tileRandomize.getRandomElem();
            mineSets.makeSet(mineIteration, newMineCord);

            //произошло либо добавление либо создание
            if (fieldCheck[newMineCord.getKey()][newMineCord.getValue()] == 0) {
                inSetTilesCount.getAndIncrement();
            }
            else {

                int selectedSet = fieldCheck[newMineCord.getKey()][newMineCord.getValue()] - 1;

                mineSets.unionSets(selectedSet, mineIteration);
            }

            Field.getTile(newMineCord.getKey(),newMineCord.getValue()).isMine = true;
            Field.getTile(newMineCord.getKey(),newMineCord.getValue()).setId("mine");
            Field.incCountMinesAroundOfTile(newMineCord.getKey(),newMineCord.getValue());

            int finalMineIteration = mineIteration + 1;
            Field.applyToAroundArea(newMineCord.getKey(), newMineCord.getValue(),(coordinate) -> {
                if (fieldCheck[coordinate.getKey()][coordinate.getValue()] == 0) {
                    fieldCheck[coordinate.getKey()][coordinate.getValue()] = finalMineIteration;
                    inSetTilesCount.getAndIncrement();
                }
            },_mineSetStep);

            fieldCheck[newMineCord.getKey()][newMineCord.getValue()] = -1;
        }
    }
}

