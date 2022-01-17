package com.patonki.util;

public interface KeyListener<T> {
    void run(T val, boolean ctrlPressed);
}
