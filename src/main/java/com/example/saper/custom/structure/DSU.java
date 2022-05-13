package com.example.saper.custom.structure;

import javafx.util.Pair;

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
    public DSU(int len) {
        _parent = new int[len];
        _size = new int[len];

        _data = new Pair[len];

        _setsCount = 0;

        for (int i = 0;i  <len;i++) {
            _parent[i] = -1;
            _size[i] = 0;
        }
    }

    /**
     * Создание множества под элемент {@code number} в {@link DSU}.
     * @param number Номер элемента в {@link DSU}
     * @param data {@link  Pair} закрепленный за элментом {@code number}
     */
    public void MakeSet(int number, Pair<Integer,Integer> data) {
        if (number >= _parent.length || 0 > number) {
            throw new IndexOutOfBoundsException("No set found");
        }
        _setsCount++;
        _parent[number] = number;
        _size[number] = 1;

        _data[number] = data;
    }

    /**
     * Метод поиска индетиефикатора ("<i>лидера</i>") множества по его элементу {@code number}.
     * @param number элемент множества
     * @return <i>лидер</i> множества
     */
    public int FindSet(int number) {
        if (number >= _parent.length || 0 > number) {
            throw new IndexOutOfBoundsException("No set found");
        }

        if (number == _parent[number]) {
            return number;
        }

        return _parent[number] = FindSet(_parent[number]);
    }

    /**
     * Метод объединения двух множесв в одно новое.
     * @param a Номер элемента множества
     * @param b Номер элемента множества
     */
    public void UnionSets(int a, int b) {
        if (a >= _parent.length || b >= _parent.length || 0 > a || 0 > b) {
            throw new IndexOutOfBoundsException("No set found");
        }

        a = FindSet(a);
        b = FindSet(b);

        if (a == b)
            return;

        if (_size[a] < _size[b]) {
            a += (b - (b = a));
        }

        _parent[b] = a;

        _size[a] += _size[b];
        _setsCount--;


    }

    /**
     * Метод, возращающий все элементы множества, к тоторому относится элмент {@code number}.
     * @param number Элемент множества.
     * @return {@link ArrayList} со всеми элементами множества.
     */
    public ArrayList<Integer> GetAllSetElem(int number) {
        if (number >= _parent.length || 0 > number) {
            throw new IndexOutOfBoundsException("No set found");
        }

        if (_parent[number] == -1) {
            return null;
        }

        number = _parent[number];
        int size = _size[number];
        ArrayList<Integer> list = new ArrayList<>(size);

        for (int i = 0;i < _parent.length && size > 0;i++) {
            if (_parent[i] == number) {
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
    public ArrayList<Integer> GetAllUniqueSets() {
        ArrayList<Integer> list = new ArrayList<>(_setsCount);

        for (int i = 0;i < _parent.length;i++) {
            if (i == _parent[i]) {
                list.add(i);
            }
        }

        return list;
    }

    /**
     * Метод, возвращающий {@link Pair} хранящуюся в элменте {@code number}.
     * @param number Номер элемента
     * @return {@link Pair} в элементе {@code number}
     */
    public Pair<Integer,Integer> getElemInSet(int number) {
        if (number >= _parent.length || 0 > number) {
            throw new IndexOutOfBoundsException("No set found");
        }

        if (_parent[number] == -1) {
            return null;
        }

        return _data[number];
    }

    /**
     * Метод, возвращающий размер множества с <i>лидером</i> {@code number}.
     * @param number Номер <i>лидера</i> множества
     * @return Размер множества
     */
    public int getSetSize(int number) {
        if (number >= _parent.length || 0 > number) {
            throw new IndexOutOfBoundsException("No set found");
        }

        return _size[number];
    }

    /**
     * Метод, вовзращающий общее количество элементов в {@link DSU}.
     * @return Общее количество элементов в системе.
     */
    public int getSetsSpace() {
        return _parent.length;
    }
}
