package com.easycore.ChristmasTreeLights.firebase;

public interface ChangeHandler<T> {
    void onChange(T changedObject);
}
