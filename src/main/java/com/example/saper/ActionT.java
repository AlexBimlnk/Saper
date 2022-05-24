package com.example.saper;

/**
 * Функциональный интерфейс для метода, возвращающего void и принимаюешего два аргумента.
 * @param <T1> Первый арумент функции.
 * @param <T2> Второй аргумент функции.
 */
public interface ActionT<T1, T2> {
    public void Invoke(T1 arg1, T2 arg2);
}
