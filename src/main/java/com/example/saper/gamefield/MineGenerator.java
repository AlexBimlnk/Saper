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

    /**
     * <p>
     *     Алгоритм по следующему принципу: мины, находящиеся на расстоянии равном {@code mineSetStep},
     *     считаются находящимися в одном множестве. Управление множествами реализовано при помощи {@link DSU}.
     *     Механизм рандомизации с вероятностью реализована в {@link RandomWithProbability}.
     * </p>
     * <p>
     * При итерации добавления мины сначала выбирается будет ли она добавлена в существующее
     * множество или создастся новое. Вероятность добавление в новое множество считается равной {@code MathGetProbability(0)}.
     * Вероятность в существующее - {@code MathGetProbability(<i>размер множесвта</i>)}.
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

        int[][] indexes = new int[Field.getSizes().getKey()][Field.getSizes().getValue()]; //матрица индексов tilesWithProbability
        int[][] setCheckIteration = new int[Field.getSizes().getKey()][Field.getSizes().getValue()]; //проверка на принадлежность сету и обработке на текущей итерации

        for (int i = 0; i < Field.getSizes().getKey(); i++) {
            for (int j = 0; j < Field.getSizes().getValue(); j++) {
                Pair<Integer,Integer> curTile = new Pair<>(i,j);
                indexes[i][j] = -(countMine + 1);
                if (!Field.isStartPoint(curTile)) {
                    int distToStart = Field.getDistanceToPoints(curTile, Field.getStartPoint()) - 1;
                    int probability = _startPointWeight;

                    if (!Field.isNearWithBorder(curTile.getKey(), curTile.getValue())) {
                        probability += _borderWeight;
                    }

                    if (distToStart >= _startPointPunish.length) {
                        distToStart = _startPointPunish.length - 1;
                    }

                    probability -= _startPointPunish[distToStart];

                    indexes[i][j] = tilesWithProbability.size();
                    tilesWithProbability.add(new Pair<>(probability,curTile));
                }
                else {
                    setCheckIteration[i][j] = -1;
                }
            }
        }

        DSU mineSets = new DSU(countMine);

        for (int mineIteration = 0;mineIteration < countMine; mineIteration++) {
            int setContinueProbability = 0;
            for (var set : mineSets.getAllUniqueSets()) {
                setContinueProbability += mathGetProbability(mineSets.getSetSize(set));
            }

            RandomWithProbability<Integer> choseBehave = new RandomWithProbability<>(random);

            if (inSetTilesCount.get() != tilesWithProbability.size()) {
                choseBehave.addNewElem(mathGetProbability(0),0);
            }

            if (setContinueProbability != 0) {
                choseBehave.addNewElem(setContinueProbability,1);
            }

            RandomWithProbability<Integer> tileRandomize = new RandomWithProbability(random);

            if (choseBehave.getRandomElem() == 0) {
                for (var elem : tilesWithProbability) {
                    if (setCheckIteration[elem.getValue().getKey()][elem.getValue().getValue()] == 0) {
                        tileRandomize.addNewElem(elem.getKey(), indexes[elem.getValue().getKey()][elem.getValue().getValue()]);
                    }
                }
            }
            else {
                for (var setLeader : mineSets.getAllUniqueSets()) {
                    int finalMineIteration = (countMine + 1) * mineIteration + setLeader;
                    for (var setsMine : mineSets.getAllSetElem(setLeader)) {

                        Field.applyToAroundArea(mineSets.getElemInSet(setsMine).getKey(), mineSets.getElemInSet(setsMine).getValue(), (tile) -> {

                            if (setCheckIteration[tile.getKey()][tile.getValue()] != finalMineIteration &&
                                    setCheckIteration[tile.getKey()][tile.getValue()] >= 1) {
                                setCheckIteration[tile.getKey()][tile.getValue()] = finalMineIteration;

                                int probabilityOfTile = mathGetProbability(mineSets.getSetSize(setLeader)) +
                                        tilesWithProbability.get(indexes[tile.getKey()][tile.getValue()]).getKey();
                                tileRandomize.addNewElem(probabilityOfTile, indexes[tile.getKey()][tile.getValue()]);
                            }

                        }, _mineSetStep);
                    }
                }
            }

            int randomIndex = tileRandomize.getRandomElem();

            Pair<Integer,Integer> newMineCord = tilesWithProbability.get(randomIndex).getValue();

            if (setCheckIteration[newMineCord.getKey()][newMineCord.getValue()] == 0) {

                mineSets.makeSet(mineIteration, newMineCord);
                inSetTilesCount.getAndIncrement();
            }
            else {

                int selectedSet = setCheckIteration[newMineCord.getKey()][newMineCord.getValue()] % (countMine + 1);

                mineSets.makeSet(mineIteration, newMineCord);
                mineSets.unionSets(selectedSet, mineIteration);
            }

            Field.getTile(newMineCord.getKey(),newMineCord.getValue()).isMine = true;
            Field.getTile(newMineCord.getKey(),newMineCord.getValue()).setId("mine");
            Field.incCountMinesAroundOfTile(newMineCord.getKey(),newMineCord.getValue());


            Field.applyToAroundArea(newMineCord.getKey(), newMineCord.getValue(),(coordinate) -> {
                if (setCheckIteration[coordinate.getKey()][coordinate.getValue()] == 0) {
                    setCheckIteration[coordinate.getKey()][coordinate.getValue()]= 1;
                    inSetTilesCount.getAndIncrement();
                }
            },_mineSetStep);

            setCheckIteration[newMineCord.getKey()][newMineCord.getValue()] = -1;
        }
    }
}

