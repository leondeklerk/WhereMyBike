package com.leondeklerk.wheremybike;

import android.util.Log;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        o1 = o1.substring(o1.length() - 16);
        o2 = o2.substring(o2.length() - 16);
        return -o1.compareTo(o2);
    }
}
