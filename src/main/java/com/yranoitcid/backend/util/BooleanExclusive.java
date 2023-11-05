package com.yranoitcid.backend.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BooleanExclusive {
    private List<Boolean> list = new ArrayList<>();
    public static int ALL_FALSE_INDEX = -1;

    public BooleanExclusive(int numberOfBoolean) {
        assert numberOfBoolean > 0;
        for (int i = 0; i < numberOfBoolean; i++) {
            list.add(Boolean.FALSE);
        }
    }

    public int getTrue() {
        return IntStream.range(0, list.size())
                .filter(i -> list.get(i))
                .findFirst()
                .orElse(ALL_FALSE_INDEX);
    }

    public void setTrue(int index) {
        assert index >= 0 && index < list.size();
        list.replaceAll(ignored -> Boolean.FALSE);
        list.set(index, Boolean.TRUE);
    }

    public boolean get(int index) {
        assert index >= 0 && index < list.size();
        return list.get(index);
    }

    public void set(int index, boolean value) {
        if (value) {
            setTrue(index);
        } else {
            list.set(index, Boolean.FALSE);
        }
    }

    public int size() {
        return list.size();
    }

    public boolean booleanValue() {
        return list.stream().anyMatch(b -> b);
    }
}
