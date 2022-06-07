package com.example.saper.custom.structure;

import javafx.util.Pair;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Класс реализирующий рандомазацию с вероятностью.
 * @param <T> Тип данных, в виде которого будут возвращаться значения после рандомизации
 */
public class RandomWithProbability<T> {

    private final ArrayList<Pair<Integer, T>> _set;

    private final Random _random;

    private int _upperBound;

    /**
     * Создание нового экземпляра класса.
     * @param random Экземпляр класса {@link Random} используется для осуществления генерации чисел одним объектом {@link Random}
     */
    public RandomWithProbability(Random random) {
        _random = random == null ? new Random() : random;
        _upperBound = 0;
        _set = new ArrayList<>();
    }

    /**
     * Добавленеи элемента типа {@link T} в общее множество всех элментов и установление вероянтности его выпадения.
     * @param elem Новый элемнет типа {@link T}, добавляемый в множество остальных
     * @param probability Вероятность выпадения элемента {@code elem}
     */
    public void addNewElem(int probability, T elem) throws InvalidParameterException{
        if (elem == null) {
            return;
        }

        if (probability < 0) {
            throw new InvalidParameterException("Probability should has positive value.");
        }

        _upperBound += probability;
        _set.add(new Pair<>(probability, elem));
    }

    /**
     * Метод получения случайного элмента с учётом их вероятности.
     * @return Полученный элемент типа {@link T}
     */
    public T getRandomElem() throws InvalidParameterException {
        if (_upperBound == 0)
            throw new InvalidParameterException("Sum probability should has positive value.");

        int randomVal = _random.nextInt(_upperBound);

        int selectedItem = -1; //выбранная "редкость"
        int currentBound = 0; //текущая граница

        do {
            currentBound += _set.get(++selectedItem).getKey();
        }
        while (currentBound < randomVal);

        return _set.get(selectedItem).getValue();
    }

    /**
     * Метод, возвращающий общее колличество элментов множестве.
     * @return Размер множества
     */
    public int getSize() {
        return _set.size();
    }
}
