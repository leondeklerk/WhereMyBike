package com.leondeklerk.wheremybike;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class StringComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2){
        int result = 0;
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = df.parse(o1.substring(o1.length() - 18));
            Date date2 = df.parse(o2.substring(o2.length() - 18));
            result = date.compareTo(date2);
        } catch (Exception e){
            System.err.println(e.toString());
        }
        return -result;
    }
}
