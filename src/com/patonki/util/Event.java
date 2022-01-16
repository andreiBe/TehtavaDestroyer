package com.patonki.util;

public interface Event<T> {
    void run(T val, boolean ctrlPressed);
}
