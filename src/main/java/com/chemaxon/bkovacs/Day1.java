package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Day1 extends DaySolver {

    HashMap<Integer, Integer> map = new HashMap<>();
    ArrayList<Integer> calories = new ArrayList<>();

    public Day1() {
        super(1);
    }

    @Override
    protected void init(List<String> input) {
        int elfID = 1;
        map.put(elfID, 0);
        for (String line : input) {
            if (line.isBlank()) {
                elfID++;
                map.put(elfID, 0);
            } else {
                map.put(elfID, map.get(elfID) + Integer.parseInt(line));
            }
        }
    }


    @Override
    public Object algorithmPart1() {
        int max = 0;

        for (Integer i : map.keySet()) {
            if (map.get(i) > max) {
                max = map.get(i);
            }
            calories.add(map.get(i));
        }
        return max;
    }

    @Override
    public Object algorithmPart2() {
        Collections.sort(calories);
        Collections.reverse(calories);
        return calories.get(0)+calories.get(1)+calories.get(2);
    }


}
