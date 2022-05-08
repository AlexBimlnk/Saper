package com.example.saper.gamefield;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;
//TODO: Написать документацию к пользовательскому рандому
/**
 * Пользовательский рандом.
 * @param <T> TODO:
 */
public class CustomRandom<T extends Number> {

    private ArrayList<Pair<Integer,T>> _set;

    private Random _random;

    private int _upperBound;

    /**
     * Констуктор пользовательского рандома.
     * @param random TODO:дока
     */
    public CustomRandom(Random random) {
        _random = random == null ? new Random() : random;
        _upperBound = 0;
        _set = new ArrayList<>();
    }

    public void Clear() {
        _upperBound = 0;
        _set.clear();
    }

    /**
     *
     * @param probability
     * @param elem
     */
    public void addNewElem(int probability, T elem) {
        if (elem == null)
            return;
        _upperBound += probability;
        _set.add(new Pair<>(probability, elem));
    }

    /**
     *
     * @param removeAfterRandom
     * @return
     */
    public T GetRandomElem(boolean removeAfterRandom) {
        int randomVal = _random.nextInt(_upperBound);

        int selectedItem = -1; //выбранная "редкость"
        int currentBound = 0; //текущая граница

        do {
            currentBound += _set.get(++selectedItem).getKey();
        }
        while (currentBound < randomVal);

        T returnElem = _set.get(selectedItem).getValue();

        if (removeAfterRandom) {
            _upperBound -= _set.get(selectedItem).getKey();
            _set.remove(selectedItem);
        }

        return returnElem;
    }

    public void UpdateProbability(int index, int newValue) {
        if (index >= _set.size()) {
            throw new IndexOutOfBoundsException();
        }

        Pair<Integer, T> updatedPair = new Pair<>(newValue, _set.get(index).getValue());
        _set.remove(index);
        _set.add(index, updatedPair);
    }

    public int getSize() {
        return _set.size();
    }
}
