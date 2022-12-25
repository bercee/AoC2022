package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day10 extends DaySolver {

    Map<Integer, Integer> signal = new HashMap<>();
    int curr = 1;
    int cycle = 1;


    public Day10() {
        super(10);
    }

    @Override
    protected void init(List<String> input) {
        LOG.debug(input.size());

        for (String line : input) {
            if (line.startsWith("noop")) {
                signal.put(cycle, curr);
                cycle++;
            } else {
                int s = Integer.parseInt(line.split("\\s+")[1]);
                signal.put(cycle, curr);
                cycle++;
                signal.put(cycle, curr);
                cycle++;
                curr+=s;
            }
        }
    }

    @Override
    public Object algorithmPart1() {
        int sum = 0;

        for (int i = 20; i <= 220; i+=40) {
            sum += i*signal.get(i);
        }

        return sum;
    }

    @Override
    public Object algorithmPart2() {
        LOG.debug(signal.size());


        for (int i = 0; i < 240; i+=40) {
            char[] str = new char[40];
            for (int k = 0; k < 40; k++) {
                str[k] = ' ';
            }
            for (int j = 0; j < 40; j++) {
//                LOG.debug("{}: {}",i+j+1,signal.get(i+j+1));
//                str[signal.get(i+j)+1] = '#';
                int spr = signal.get(i+j+1);
                if (Math.abs(j-spr) <= 1) {
                    str[j] = '#';
                }
            }
            System.out.println(new String(str));
        }
        return "ZCBAJFJZ";
    }
}
