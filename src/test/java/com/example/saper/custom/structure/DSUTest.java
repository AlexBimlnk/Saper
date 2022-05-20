package com.example.saper.custom.structure;

import javafx.util.Pair;
import org.junit.jupiter.api.*;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class DSUTest {

    private final int _size = 10;
    private DSU _dsu;

    @BeforeEach
    public void setUp() {
        _dsu = new DSU(_size);
    }
    @AfterEach
    public void tearDown() {
        _dsu = null;
    }


    @TestFactory
    public Collection<DynamicTest> can_Make_Set() {
        // Arrange
        var simplePair = new Pair<>(1, 1);
        int validIndex = 1;
        int outOfBoundsIndex = -1;

        // Assert
        return Arrays.asList(
                dynamicTest("Throws IndexOutOfBoundsException when index is invalid",
                        () -> assertThrows(
                                IndexOutOfBoundsException.class,
                                () -> _dsu.MakeSet(outOfBoundsIndex, simplePair))),
                dynamicTest("Can make set",
                        () -> assertDoesNotThrow(
                                () -> _dsu.MakeSet(validIndex, simplePair)))
        );
    }


    @TestFactory
    public Collection<DynamicTest> can_Find_Set() {
        // Arrange
        var simplePair = new Pair<>(1, 1);
        int validIndex = 1;
        int expectedValue = 1;
        int outOfBoundsIndex = -1;
        int invalidParameterIndex = 2;

        // Act
        _dsu.MakeSet(validIndex, simplePair);

        // Assert
        return Arrays.asList(
                dynamicTest("Throws IndexOutOfBoundsException when index is invalid",
                        () -> assertThrows(
                                IndexOutOfBoundsException.class,
                                () -> _dsu.FindSet(outOfBoundsIndex))),
                dynamicTest("Throws InvalidParameterException when index is invalid",
                        () -> assertThrows(
                                InvalidParameterException.class,
                                () -> _dsu.FindSet(invalidParameterIndex))),
                dynamicTest("Can make set",
                        () -> assertEquals(expectedValue, _dsu.FindSet(validIndex)))
        );
    }


    @TestFactory
    public Collection<DynamicTest> can_Union_Sets() {
        // Arrange
        int[] indexElements = {0, 2, 3, 5, 1, 7, 8};
        var simplePair = new Pair<>(1, 1);
        int invalidParameterIndex = 9;
        int outOfBoundsIndex = -3;

        // Act
        for (int indexElement : indexElements) {
            _dsu.MakeSet(indexElement, simplePair);
        }

        _dsu.UnionSets(indexElements[0], indexElements[1]);
        _dsu.UnionSets(indexElements[5], indexElements[4]);
        _dsu.UnionSets(indexElements[0], indexElements[2]);
        _dsu.UnionSets(indexElements[1], indexElements[3]);

        // Assert
        return Arrays.asList(
                dynamicTest("Throws IndexOutOfBoundsException when index is invalid",
                        () -> assertThrows(
                                IndexOutOfBoundsException.class,
                                () -> _dsu.UnionSets(outOfBoundsIndex, 0))),
                dynamicTest("Throws InvalidParameterException when index is invalid",
                        () -> assertThrows(
                                InvalidParameterException.class,
                                () -> _dsu.UnionSets(invalidParameterIndex, 0))),
                dynamicTest("1 union test",
                        () -> assertEquals(0, _dsu.FindSet(0))),
                dynamicTest("2 union test",
                        () -> assertEquals(0, _dsu.FindSet(2))),
                dynamicTest("3 union test",
                        () -> assertEquals(0, _dsu.FindSet(3))),
                dynamicTest("4 union test",
                        () -> assertEquals(0, _dsu.FindSet(5))),
                dynamicTest("5 union test",
                        () -> assertEquals(7, _dsu.FindSet(1))),
                dynamicTest("6 union test",
                        () -> assertEquals(7, _dsu.FindSet(7))),
                dynamicTest("7 union test",
                        () -> assertEquals(8, _dsu.FindSet(8)))
        );
    }


    @TestFactory
    public Collection<DynamicTest> can_Get_All_Set_Elements() {
        // Arrange
        int[] indexElements = {0, 2, 3, 5, 1, 7, 8};
        var simplePair = new Pair<>(1, 1);
        int invalidParameterIndex = 9;
        int outOfBoundsIndex = -3;
        List<Integer> expectedListSet1 = Arrays.asList(0, 2, 3, 5);
        List<Integer> expectedListSet2 = Arrays.asList(1, 7);
        List<Integer> expectedListSet3 = Arrays.asList(8);

        // Act
        for (int indexElement : indexElements) {
            _dsu.MakeSet(indexElement, simplePair);
        }

        _dsu.UnionSets(indexElements[0], indexElements[1]);
        _dsu.UnionSets(indexElements[5], indexElements[4]);
        _dsu.UnionSets(indexElements[0], indexElements[2]);
        _dsu.UnionSets(indexElements[1], indexElements[3]);

        // Assert
        return Arrays.asList(
                dynamicTest("Throws IndexOutOfBoundsException when index is invalid",
                        () -> assertThrows(
                                IndexOutOfBoundsException.class,
                                () -> _dsu.GetAllSetElem(outOfBoundsIndex))),
                dynamicTest("Throws InvalidParameterException when index is invalid",
                        () -> assertThrows(
                                InvalidParameterException.class,
                                () -> _dsu.GetAllSetElem(invalidParameterIndex))),
                dynamicTest("1 get all set elements", () -> {
                    for (int i = 0; i <= 3; i++) {
                        assertIterableEquals(expectedListSet1, _dsu.GetAllSetElem(i));
                    }
                }),
                dynamicTest("2 get all set elements", () -> {
                    assertIterableEquals(expectedListSet2, _dsu.GetAllSetElem(1));
                    assertIterableEquals(expectedListSet2, _dsu.GetAllSetElem(7));
                }),
                dynamicTest("3 get all set elements", () -> assertIterableEquals(expectedListSet3, _dsu.GetAllSetElem(8)))
        );
    }


    @Test
    public void can_Get_All_Unique_Sets() {
        // Arrange
        int[] indexElements = {0, 2, 3, 5, 1, 7, 8};
        var simplePair = new Pair<>(1, 1);
        List<Integer> expectedList = Arrays.asList(0, 7, 8);

        // Act
        for (int indexElement : indexElements) {
            _dsu.MakeSet(indexElement, simplePair);
        }

        _dsu.UnionSets(indexElements[0], indexElements[1]);
        _dsu.UnionSets(indexElements[5], indexElements[4]);
        _dsu.UnionSets(indexElements[0], indexElements[2]);
        _dsu.UnionSets(indexElements[1], indexElements[3]);

        // Assert
        assertIterableEquals(expectedList, _dsu.GetAllUniqueSets());
    }


    @TestFactory
    public Collection<DynamicTest> can_Get_Elements_In_Set() {
        // Arrange
        int[] indexElements = {0, 2, 3, 5, 1, 7, 8};
        int invalidParameterIndex = 9;
        int outOfBoundsIndex = -3;

        // Act
        for (int i = 0; i < indexElements.length; i++) {
            _dsu.MakeSet(indexElements[i], new Pair<>(i+1, i+1));
        }

        _dsu.UnionSets(indexElements[0], indexElements[1]);
        _dsu.UnionSets(indexElements[5], indexElements[4]);
        _dsu.UnionSets(indexElements[0], indexElements[2]);
        _dsu.UnionSets(indexElements[1], indexElements[3]);

        // Assert
        return Arrays.asList(
                dynamicTest("Throws IndexOutOfBoundsException when index is invalid",
                        () -> assertThrows(
                                IndexOutOfBoundsException.class,
                                () -> _dsu.getElemInSet(outOfBoundsIndex))),
                dynamicTest("Throws InvalidParameterException when index is invalid",
                        () -> assertThrows(
                                InvalidParameterException.class,
                                () -> _dsu.getElemInSet(invalidParameterIndex))),
                dynamicTest("Can get elements", () -> {
                    for (int i = 0; i<indexElements.length; i++) {
                        assertEquals(new Pair<>(i+1, i+1), _dsu.getElemInSet(indexElements[i]));
                    }
                })
        );
    }


    @TestFactory
    public Collection<DynamicTest> can_Get_Size() {
        // Arrange
        int[] indexElements = {0, 2, 3, 5, 1, 7, 8};
        int invalidParameterIndex = 9;
        int outOfBoundsIndex = -3;
        var simplePair = new Pair<>(1, 1);

        // Act
        for (int indexElement : indexElements) {
            _dsu.MakeSet(indexElement, simplePair);
        }

        _dsu.UnionSets(indexElements[0], indexElements[1]);
        _dsu.UnionSets(indexElements[5], indexElements[4]);
        _dsu.UnionSets(indexElements[0], indexElements[2]);
        _dsu.UnionSets(indexElements[1], indexElements[3]);

        // Assert
        return Arrays.asList(
                dynamicTest("Throws IndexOutOfBoundsException when index is invalid",
                        () -> assertThrows(
                                IndexOutOfBoundsException.class,
                                () -> _dsu.getSetSize(outOfBoundsIndex))),
                dynamicTest("Throws InvalidParameterException when index is invalid",
                        () -> assertThrows(
                                InvalidParameterException.class,
                                () -> _dsu.getSetSize(invalidParameterIndex))),
                dynamicTest("1 union test",
                        () -> assertEquals(4, _dsu.getSetSize(0))),
                dynamicTest("2 union test",
                        () -> assertEquals(1, _dsu.getSetSize(2))),
                dynamicTest("3 union test",
                        () -> assertEquals(1, _dsu.getSetSize(3))),
                dynamicTest("4 union test",
                        () -> assertEquals(1, _dsu.getSetSize(5))),
                dynamicTest("5 union test",
                        () -> assertEquals(1, _dsu.getSetSize(1))),
                dynamicTest("6 union test",
                        () -> assertEquals(2, _dsu.getSetSize(7))),
                dynamicTest("7 union test",
                        () -> assertEquals(1, _dsu.getSetSize(8)))
        );
    }

    @Test
    void can_Get_Sets_Space() {
        assertEquals(_size, _dsu.getSetsSpace());
    }
}