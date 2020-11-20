package com.hwhhhh.taskmanager.util;

import java.util.List;

public class MySet<K, V, O> {
    private K key;
    private V value;
    private O btn;

    public MySet() {
    }

    public MySet(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public O getBtn() {
        return btn;
    }

    public void setBtn(O btn) {
        this.btn = btn;
    }

    public void put(K key, V value, O btn) {
        this.key = key;
        this.value = value;
        this.btn = btn;
    }

}
