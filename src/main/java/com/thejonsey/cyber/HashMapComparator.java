package com.thejonsey.cyber;

import java.util.Comparator;
import java.util.HashMap;

public class HashMapComparator implements Comparator<HashMap<String, Object>>
{
    @Override
    public int compare (HashMap<String, Object> o1, HashMap<String, Object> o2)
    {
        Integer count1 = (Integer) o1.get("count");
        Integer count2 = (Integer) o2.get("count");


        return -count1.compareTo(count2);
    }
}