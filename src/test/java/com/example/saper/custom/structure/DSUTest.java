package com.example.saper.custom.structure;

import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.security.InvalidParameterException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DSUTest {

    private final int _size = 10;
    private DSU _dsu;

    @BeforeEach
    void setUp() {
        _dsu = new DSU(_size);
    }

    @AfterEach
    void tearDown() {
        _dsu = null;
    }

    @Test
    void makeSet() {
        //arrange
        var simplePair = new Pair<>(1, 1);
        int firstIndexElement = 1;
        int secondIndexElement = -1;

        //act
        var indexOutOfBoundsException = assertThrows(IndexOutOfBoundsException.class,
                () -> _dsu.MakeSet(secondIndexElement, simplePair));

        //assert
        assertNotNull(indexOutOfBoundsException.getMessage());
        assertDoesNotThrow(() -> _dsu.MakeSet(firstIndexElement, simplePair));
    }

    @Test
    void findSet() {
        //arrange
        int firstIndexElement = 1;
        int secondIndexElement = 2;
        int thirdIndexElement = -3;
        var simplePair = new Pair<>(1, 1);
        _dsu.MakeSet(firstIndexElement, simplePair);

        //act
        var indexOutOfBoundsException = assertThrows(IndexOutOfBoundsException.class,
                () -> _dsu.FindSet(thirdIndexElement));
        var invalidParameterException = assertThrows(InvalidParameterException.class,
                () -> _dsu.FindSet(secondIndexElement));


        //assert
        assertEquals(firstIndexElement, _dsu.FindSet(firstIndexElement));
        assertNotNull(indexOutOfBoundsException.getMessage());
        assertNotNull(invalidParameterException.getMessage());
    }

    @Test
    void unionSets() {
        //arrange
        int[] indexElements = {0, 2, 3, 5, 1, 7, 8};
        int notExistingSetIndexElement = 9;
        int invalidIndexElement = -3;
        var simplePair = new Pair<>(1, 1);
        for (int indexElement : indexElements) {
            _dsu.MakeSet(indexElement, simplePair);
        }

        //act
        var indexOutOfBoundsException = assertThrows(IndexOutOfBoundsException.class,
                () -> _dsu.UnionSets(invalidIndexElement, indexElements[0]));
        var invalidParameterException = assertThrows(InvalidParameterException.class,
                () -> _dsu.UnionSets(notExistingSetIndexElement, indexElements[0]));
        _dsu.UnionSets(indexElements[0], indexElements[1]);
        _dsu.UnionSets(indexElements[5], indexElements[4]);
        _dsu.UnionSets(indexElements[0], indexElements[2]);
        _dsu.UnionSets(indexElements[1], indexElements[3]);

        //assert
        assertNotNull(indexOutOfBoundsException.getMessage());
        assertNotNull(invalidParameterException.getMessage());

        assertEquals(indexElements[0], _dsu.FindSet(indexElements[0]));
        assertEquals(indexElements[0], _dsu.FindSet(indexElements[1]));
        assertEquals(indexElements[0], _dsu.FindSet(indexElements[2]));
        assertEquals(indexElements[0], _dsu.FindSet(indexElements[3]));
        assertEquals(indexElements[5], _dsu.FindSet(indexElements[4]));
        assertEquals(indexElements[5], _dsu.FindSet(indexElements[5]));
        assertEquals(indexElements[6], _dsu.FindSet(indexElements[6]));
    }

    @Test
    void getAllSetElem() {
        //arrange
        int[] indexElements = {0, 2, 3, 5, 1, 7, 8};
        int notExistingSetIndexElement = 9;
        int invalidIndexElement = -3;
        var simplePair = new Pair<>(1, 1);
        for (int indexElement : indexElements) {
            _dsu.MakeSet(indexElement, simplePair);
        }
        ArrayList<Integer> firstListSet = new ArrayList<>(4);
        firstListSet.add(indexElements[0]);
        firstListSet.add(indexElements[1]);
        firstListSet.add(indexElements[2]);
        firstListSet.add(indexElements[3]);
        ArrayList<Integer> secondListSet = new ArrayList<>(2);
        secondListSet.add(indexElements[4]);
        secondListSet.add(indexElements[5]);
        ArrayList<Integer> thirdListSet = new ArrayList<>(1);
        thirdListSet.add(indexElements[6]);

        //act
        var indexOutOfBoundsException = assertThrows(IndexOutOfBoundsException.class,
                () -> _dsu.GetAllSetElem(invalidIndexElement));
        var invalidParameterException = assertThrows(InvalidParameterException.class,
                () -> _dsu.GetAllSetElem(notExistingSetIndexElement));
        _dsu.UnionSets(indexElements[0], indexElements[1]);
        _dsu.UnionSets(indexElements[5], indexElements[4]);
        _dsu.UnionSets(indexElements[0], indexElements[2]);
        _dsu.UnionSets(indexElements[1], indexElements[3]);

        //assert
        assertNotNull(indexOutOfBoundsException.getMessage());
        assertNotNull(invalidParameterException.getMessage());

        assertEquals(firstListSet, _dsu.GetAllSetElem(indexElements[0]));
        assertEquals(firstListSet, _dsu.GetAllSetElem(indexElements[1]));
        assertEquals(firstListSet, _dsu.GetAllSetElem(indexElements[2]));
        assertEquals(firstListSet, _dsu.GetAllSetElem(indexElements[3]));
        assertEquals(secondListSet, _dsu.GetAllSetElem(indexElements[4]));
        assertEquals(secondListSet, _dsu.GetAllSetElem(indexElements[5]));
        assertEquals(thirdListSet, _dsu.GetAllSetElem(indexElements[6]));
    }

    @Test
    void getAllUniqueSets() {
        //arrange
        int[] indexElements = {0, 2, 3, 5, 1, 7, 8};
        var simplePair = new Pair<>(1, 1);
        for (int indexElement : indexElements) {
            _dsu.MakeSet(indexElement, simplePair);
        }
        ArrayList<Integer> listLeadersOfSets = new ArrayList<>(4);
        listLeadersOfSets.add(indexElements[0]);

        listLeadersOfSets.add(indexElements[5]);

        listLeadersOfSets.add(indexElements[6]);

        //act
        _dsu.UnionSets(indexElements[0], indexElements[1]);
        _dsu.UnionSets(indexElements[5], indexElements[4]);
        _dsu.UnionSets(indexElements[0], indexElements[2]);
        _dsu.UnionSets(indexElements[1], indexElements[3]);

        //assert
        assertEquals(listLeadersOfSets, _dsu.GetAllUniqueSets());
    }

    @Test
    void getElemInSet() {
        //arrange
        int[] indexElements = {0, 2, 3, 5, 1, 7, 8};
        int notExistingSetIndexElement = 9;
        int invalidIndexElement = -3;
        for (int i = 0; i<indexElements.length; i++) {
            _dsu.MakeSet(indexElements[i], new Pair<>(i+1, i+1));
        }

        //act
        var indexOutOfBoundsException = assertThrows(IndexOutOfBoundsException.class,
                () -> _dsu.getElemInSet(invalidIndexElement));
        var invalidParameterException = assertThrows(InvalidParameterException.class,
                () -> _dsu.getElemInSet(notExistingSetIndexElement));
        _dsu.UnionSets(indexElements[0], indexElements[1]);
        _dsu.UnionSets(indexElements[5], indexElements[4]);
        _dsu.UnionSets(indexElements[0], indexElements[2]);
        _dsu.UnionSets(indexElements[1], indexElements[3]);

        //assert
        assertNotNull(indexOutOfBoundsException.getMessage());
        assertNotNull(invalidParameterException.getMessage());

        for (int i = 0; i<indexElements.length; i++) {
            assertEquals(new Pair<>(i+1, i+1), _dsu.getElemInSet(indexElements[i]));
        }
    }

    @Test
    void getSetSize() {
        //arrange
        int[] indexElements = {0, 2, 3, 5, 1, 7, 8};
        int notExistingSetIndexElement = 9;
        int invalidIndexElement = -3;
        var simplePair = new Pair<>(1, 1);
        for (int indexElement : indexElements) {
            _dsu.MakeSet(indexElement, simplePair);
        }

        //act
        var indexOutOfBoundsException = assertThrows(IndexOutOfBoundsException.class,
                () -> _dsu.GetAllSetElem(invalidIndexElement));
        var invalidParameterException = assertThrows(InvalidParameterException.class,
                () -> _dsu.GetAllSetElem(notExistingSetIndexElement));
        _dsu.UnionSets(indexElements[0], indexElements[1]);
        _dsu.UnionSets(indexElements[5], indexElements[4]);
        _dsu.UnionSets(indexElements[0], indexElements[2]);
        _dsu.UnionSets(indexElements[1], indexElements[3]);

        //assert
        assertNotNull(indexOutOfBoundsException.getMessage());
        assertNotNull(invalidParameterException.getMessage());

        assertEquals(4, _dsu.getSetSize(indexElements[0]));
        assertEquals(1, _dsu.getSetSize(indexElements[1]));
        assertEquals(1, _dsu.getSetSize(indexElements[2]));
        assertEquals(1, _dsu.getSetSize(indexElements[3]));
        assertEquals(1, _dsu.getSetSize(indexElements[4]));
        assertEquals(2, _dsu.getSetSize(indexElements[5]));
        assertEquals(1, _dsu.getSetSize(indexElements[6]));
    }

    @Test
    void getSetsSpace() {
        assertEquals(_size, _dsu.getSetsSpace());
    }
}