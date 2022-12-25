package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day13 extends DaySolver {

    private static final String TEST = "[1,1,3,1,1]\n" +
            "[1,1,5,1,1]\n" +
            "\n" +
            "[[1],[2,3,4]]\n" +
            "[[1],4]\n" +
            "\n" +
            "[9]\n" +
            "[[8,7,6]]\n" +
            "\n" +
            "[[4,4],4,4]\n" +
            "[[4,4],4,4,4]\n" +
            "\n" +
            "[7,7,7,7]\n" +
            "[7,7,7]\n" +
            "\n" +
            "[]\n" +
            "[3]\n" +
            "\n" +
            "[[[]]]\n" +
            "[[]]\n" +
            "\n" +
            "[1,[2,[3,[4,[5,6,7]]]],8,9]\n" +
            "[1,[2,[3,[4,[5,6,0]]]],8,9]";

    class Pair {
        final JSONArray a1;
        final JSONArray a2;

        public Pair(JSONArray a1, JSONArray a2) {
            this.a1 = a1;
            this.a2 = a2;
        }
    }

    final ArrayList<Pair> list = new ArrayList<>();
    final ArrayList<String> input = new ArrayList<>();

    public Day13() {
        super(13);
    }

    @Override
    protected void init(List<String> input) {
        this.input.addAll(input);
        for (int i = 0; i < input.size(); i+=3) {
            String line1 = input.get(i);
            String line2 = input.get(i+1);


            list.add(new Pair(new JSONArray(line1), new JSONArray(line2)));


        }

        LOG.debug("there are {} paris",list.size());
    }


    public Object algorithmPart1() {

        int sum = 0;

        for (int i = 0; i < list.size(); i++) {
            int c = compare(list.get(i).a1, list.get(i).a2) ;
            if (c < 0) {
                sum += (i+1);
            }
        }

        LOG.debug("solution: {}",sum);

        return sum;
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }

    private static int compare (JSONArray a1, JSONArray a2) {
        int l = Math.min(a1.length(), a2.length());
        for (int i = 0; i < l; i++) {
            Object o1 = a1.get(i);
            Object o2 = a2.get(i);
            if (o1 instanceof JSONArray && o2 instanceof JSONArray) {
                int c = compare((JSONArray) o1, (JSONArray) o2);
                if (c != 0) {
                    return c;
                }
            } else if (o1 instanceof JSONArray && !(o2 instanceof JSONArray)) {
                JSONArray _a2 = new JSONArray("["+o2.toString()+"]");
                int c = compare((JSONArray) o1, _a2);
                if (c!= 0) {
                    return c;
                }
            } else if (o2 instanceof JSONArray && !(o1 instanceof JSONArray)) {
                JSONArray _a1 = new JSONArray("["+o1.toString()+"]");
                int c = compare(_a1, (JSONArray) o2);
                if (c!= 0) {
                    return c;
                }
            } else if (!(o1 instanceof JSONArray) && !(o2 instanceof JSONArray)) {
                Integer i1 = Integer.parseInt(o1.toString());
                Integer i2 = Integer.parseInt(o2.toString());
                int c = Integer.compare(i1, i2);
                if (c != 0) {
                    return c;
                }
            }
        }
        return Integer.compare(a1.length(), a2.length());
    }

    public Object algorithmPart2() {
        ArrayList<String> l = new ArrayList<>();
        for (String line : input) {
            if (!line.trim().equals("")) {
                l.add(line);
            }
        }

        l.add("[[2]]");
        l.add("[[6]]");


        l.sort((o1, o2) -> compare(new JSONArray(o1), new JSONArray(o2)));

        int i1 = l.indexOf("[[2]]");
        int i2 = l.indexOf("[[6]]");

        int res = (i1+1)*(i2+1);

        LOG.debug(res);
        return res;
    }
}
