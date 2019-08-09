package com.leondeklerk.wheremybike;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2){
           return -o1.split(" - ")[3].compareTo(o2.split(" - ")[3]);
    }
}
