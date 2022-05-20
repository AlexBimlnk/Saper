package com.example.saper.custom.structure;

import javafx.util.Pair;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Система непресекающихся множеств ({@code DSU} - <u>disjoint-set-union</u>)
 * Структура данных, позволяющая выполнять с множествами следующие операции:
 * <i>добавление элемента в новое множество</i>, <i>идентификация множества по его элементу</i> и <i>обЪединенеи двух множеств в одно новое</i>.
 *
 * Данная реализация {@code DSU} отличается от стандартной и имеет следующие модфикации: <i>поддержка размеров множеств и подножеств</i>,
 * <i>а также за каждым элементом </i>{@code DSU}<i> будет закреплен экзепляр типа {@link Pair}.
 */
public class DSU {
    private final int[] _parent;
    private int _setsCount;
    private final int[] _size;

    private final Pair<Integer,Integer>[] _data;

    /**
     * Инициализация нового экземпляра {@link DSU}.
     * @param len максимальное кол-во элементов в {@link DSU}.
     */
    public DSU(int len) throws InvalidParameterException{
        if (len <= 0) {
            throw new InvalidParameterException("Length of DSU can't be negative");
        }

        _parent = new int[len];
        _size = new int[len];
        _data = new Pair[len];

        _setsCount = 0;

        for (int i = 0; i < len; i++) {
            _parent[i] = -1;
            _size[i] = 0;
        }
    }

    /**
     * Создание множества под элемент {@code indexElement} в {@link DSU}.
     * @param indexElement Номер элемента в {@link DSU}
     * @param dataElement {@link  Pair} закрепленный за элментом {@code indexElement}
     */
    public void makeSet(int indexElement, Pair<Integer,Integer> dataElement) throws IndexOutOfBoundsException {
        if (indexElement >= _parent.length || 0 > indexElement) {
            throw new IndexOutOfBoundsException("Invalid set element index");
        }
        _setsCount++;
        _parent[indexElement] = indexElement;
        _size[indexElement] = 1;

        _data[indexElement] = dataElement;
    }

    /**
     * Метод поиска индетиефикатора ("<i>лидера</i>") множества по его элементу {@code indexElement}.
     * @param indexElement элемент множества
     * @return <i>лидер</i> множества
     */
    public int findSet(int indexElement) throws IndexOutOfBoundsException, InvalidParameterException {
        if (indexElement >= _parent.length || 0 > indexElement) {
            throw new IndexOutOfBoundsException("Invalid index element of set");
        }

        if (_parent[indexElement] == -1) {
            throw new InvalidParameterException("No set with index element exists");
        }

        if (indexElement == _parent[indexElement]) {
            return indexElement;
        }

        return _parent[indexElement] = findSet(_parent[indexElement]);
    }

    /**
     * Метод объединения двух множесв в одно новое.
     * @param firstSetIndexElement Номер элемента множества
     * @param secondSetIndexElement Номер элемента множества
     */
    public void unionSets(int firstSetIndexElement, int secondSetIndexElement) throws IndexOutOfBoundsException, InvalidParameterException {
        if (firstSetIndexElement >= _parent.length || secondSetIndexElement >= _parent.length || 0 > firstSetIndexElement || 0 > secondSetIndexElement) {
            throw new IndexOutOfBoundsException("Invalid index element of set");
        }

        if (_parent[firstSetIndexElement] == -1 || _parent[secondSetIndexElement] == -1) {
            throw new InvalidParameterException("No set with index element exists");
        }

        firstSetIndexElement = findSet(firstSetIndexElement);
        secondSetIndexElement = findSet(secondSetIndexElement);

        if (firstSetIndexElement == secondSetIndexElement) {
            return;
        }

        if (_size[firstSetIndexElement] < _size[secondSetIndexElement]) {
            firstSetIndexElement += (secondSetIndexElement - (secondSetIndexElement = firstSetIndexElement));
        }

        _parent[secondSetIndexElement] = firstSetIndexElement;

        _size[firstSetIndexElement] += _size[secondSetIndexElement];
        _setsCount--;
    }

    /**
     * Метод, возращающий все элементы множества, к тоторому относится элмент {@code indexElement}.
     * @param indexElement Элемент множества.
     * @return {@link ArrayList} со всеми элементами множества.
     */
    public ArrayList<Integer> getAllSetElem(int indexElement) throws IndexOutOfBoundsException, InvalidParameterException {
        if (indexElement >= _parent.length || 0 > indexElement) {
            throw new IndexOutOfBoundsException("Invalid index element of set");
        }

        if (_parent[indexElement] == -1) {
            throw new InvalidParameterException("No set with index element exists");
        }

        indexElement = _parent[indexElement];
        int size = _size[indexElement];
        ArrayList<Integer> list = new ArrayList<>(size);

        for (int i = 0; i < _parent.length && size > 0; i++) {
            if (_parent[i] == indexElement) {
                list.add(i);
                size--;
            }
        }

        return list;
    }

    /**
     * Метод, возвращающий <i>лидеров</i> для каждого множества в {@link DSU}.
     * @return {@link  ArrayList} с <i>лидерами</i> для кадого множсетва.
     */
    public ArrayList<Integer> getAllUniqueSets() {
        ArrayList<Integer> list = new ArrayList<>(_setsCount);

        for (int i = 0; i < _parent.length; i++) {
            if (i == _parent[i]) {
                list.add(i);
            }
        }

        return list;
    }

    /**
     * Метод, возвращающий {@link Pair} хранящуюся в элменте {@code indexElement}.
     * @param indexElement Номер элемента
     * @return {@link Pair} в элементе {@code indexElement}
     */
    public Pair<Integer,Integer> getElemInSet(int indexElement) throws InvalidParameterException, IndexOutOfBoundsException {
        if (indexElement >= _parent.length || 0 > indexElement) {
            throw new IndexOutOfBoundsException("Invalid set element index");
        }

        if (_parent[indexElement] == -1) {
            throw new InvalidParameterException("No set with index element exists");
        }

        return _data[indexElement];
    }

    /**
     * Метод, возвращающий размер множества с <i>лидером</i> {@code setLeaderIndex}.
     * @param setLeaderIndex Номер <i>лидера</i> множества
     * @return Размер множества
     */
    public int getSetSize(int setLeaderIndex) throws  IndexOutOfBoundsException, InvalidParameterException {
        if (setLeaderIndex >= _parent.length || 0 > setLeaderIndex) {
            throw new IndexOutOfBoundsException("Invalid set element index");
        }

        if (_parent[setLeaderIndex] == -1) {
            throw new InvalidParameterException("No set with index leader exists");
        }

        return _size[setLeaderIndex];
    }

    /**
     * Метод, вовзращающий общее количество элементов в {@link DSU}.
     * @return Общее количество элементов в системе.
     */
    public int getSetsSpace() {
        return _parent.length;
    }
}
