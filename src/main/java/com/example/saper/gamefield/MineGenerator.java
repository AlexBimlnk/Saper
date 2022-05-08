package com.example.saper.gamefield;


import com.example.saper.SaperApplication;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Генератор мин.
 */
public class MineGenerator {

    private static final int _mineWeight = 10;
    private static final int _startPointWeight = 180;
    private static final int _borderWeight = 15;
    private static final int[] _startPointPunish = {175, 55, 30, 15 ,5, 0};

    /**
     * Запускает генерацию мин.
     * @param field Поля, на котором нужно сгенерировать мины.
     * @param countMine Кол-во мин.
     */
    public static void MineGen(Field field, int countMine) {
        Random random = SaperApplication.getSeed() != -1
                        ? new Random(SaperApplication.getSeed())
                        : new Random();

        ArrayList<Pair<Integer,Pair<Integer,Integer>>> vectors = new ArrayList<>();

        int[][] indexes = new int[field.getSizes().getKey()][field.getSizes().getValue()];

        for (int i = 0; i < field.getSizes().getKey(); i++) {
            for (int j = 0; j < field.getSizes().getValue(); j++) {
                Pair<Integer,Integer> curTile = new Pair<>(i,j);
                if (!field.isStartPoint(curTile)) {
                    int distToStart = Field.GetDistanceToPoints(curTile, field.getStartPoint()) - 1;
                    int probability = 9 * _mineWeight + _startPointWeight;

                    if (!field.isNearWithBorder(curTile.getKey(), curTile.getValue())) {
                        probability += _borderWeight;
                    }

                    if (distToStart >= _startPointPunish.length) {
                        distToStart = _startPointPunish.length - 1;
                    }

                    probability -= _startPointPunish[distToStart];


                    indexes[i][j] = vectors.size();
                    vectors.add(new Pair<>(probability,curTile));
                    System.out.println(indexes[i][j]);
                }
            }
        }

        for (int i = 0; i < countMine; i++) {
            CustomRandom<Integer> customRandom = new CustomRandom<>(random);

            for (int j = 0; j < vectors.size(); j++) {
                var elem = vectors.get(j);
                if (indexes[elem.getValue().getKey()][elem.getValue().getValue()] == -1) {
                    continue;
                }

                customRandom.addNewElem(elem.getKey(),j);
            }

            int randomIndex = customRandom.GetRandomElem(false);

            Pair<Integer,Integer> newMineCord = vectors.get(randomIndex).getValue();

            field.getTile(newMineCord.getKey(),newMineCord.getValue()).IsMine = true;
            field.getTile(newMineCord.getKey(),newMineCord.getValue()).setId("mine");

            field.IncCountMinesAroundOfTile(newMineCord.getKey(),newMineCord.getValue());

            field.ApplyToAround(newMineCord.getKey(), newMineCord.getValue(), (coordinateAround) -> {
                if (indexes[coordinateAround.getKey()][coordinateAround.getValue()] != -1) {
                    var oldPair = vectors.get(indexes[coordinateAround.getKey()][coordinateAround.getValue()]);

                    if (field.CountMinesAround(coordinateAround.getKey(),coordinateAround.getValue()) > 1) {
                        Pair<Integer,Pair<Integer,Integer>> newPair = new Pair<>(oldPair.getKey() - _mineWeight,oldPair.getValue());

                        vectors.set(indexes[coordinateAround.getKey()][coordinateAround.getValue()],newPair);
                    }
                }
            },1);

            indexes[vectors.get(randomIndex).getValue().getKey()][vectors.get(randomIndex).getValue().getValue()] = -1;
        }

    }
}

