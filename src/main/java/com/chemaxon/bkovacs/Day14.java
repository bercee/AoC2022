package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.chemaxon.bkovacs.util.Point2DInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chemaxon.bkovacs.util.Direction.*;

public class Day14 extends DaySolver {

    private enum Stuff {
        ROCK, SAND;
    }

    private static final String TEST = "498,4 -> 498,6 -> 496,6\n" +
            "503,4 -> 502,4 -> 502,9 -> 494,9";

    private final Map<Point2DInt, Stuff> map = new HashMap<>();

    private int bottom = 0;

    public Day14() {
        super(14);

//        input.clear();
//        String[] testDat = TEST.split("\n");
//        input.addAll(Arrays.asList(testDat));
    }

    @Override
    protected void init(List<String> input) {

        for (String line : input) {
            Point2DInt prev = null;
            String[] dat = line.split(" -> |,");
            for (int i = 0; i < dat.length; i+=2) {
                Point2DInt next = new Point2DInt(Integer.parseInt(dat[i]), Integer.parseInt(dat[i+1]));
                if (next.getJ() > bottom) {
                    bottom = next.getJ();
                }
                map.put(next, Stuff.ROCK);
                getAllBetween(prev, next).forEach(p -> map.put(p, Stuff.ROCK));
                prev = next;
            }
        }



        LOG.debug("rocks: {}", map.size());
    }

    private Point2DInt getNextSandPoint(Point2DInt start) {
        if (!map.containsKey(start.step(S))) {
            return start.step(S);
        }

        if (!map.containsKey(start.step(SW))) {
            return start.step(SW);
        }

        if (!map.containsKey(start.step(SE))) {
            return start.step(SE);
        }

        return start;
    }

    private Point2DInt simulateSand(Point2DInt start) {
        Point2DInt prev = start;
        Point2DInt next = getNextSandPoint(prev);
        while (!next.equals(prev) && next.getJ()<bottom) {
            prev = next;
            next = getNextSandPoint(prev);
        }

        return next;
    }

    private static List<Point2DInt> getAllBetween(Point2DInt p1, Point2DInt p2) {
        if (p1 == null || p2 == null) {
            return Collections.emptyList();
        }
        List<Point2DInt> list = new ArrayList<>();
        if (p1.getI() == p2.getI()) {
            int s = Math.min(p1.getJ(), p2.getJ());
            int e = Math.max(p1.getJ(), p2.getJ());
            for (int i = s; i <= e; i++) {
                list.add(new Point2DInt(p1.getI(), i));
            }
        } else if (p1.getJ() == p2.getJ()) {
            int s = Math.min(p1.getI(), p2.getI());
            int e = Math.max(p1.getI(), p2.getI());
            for (int i = s; i <= e; i++) {
                list.add(new Point2DInt(i, p1.getJ()));
            }
        }else {
            return Collections.emptyList();
        }

        return list;
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }

    @Override
    public Object algorithmPart1() {
        Point2DInt start = new Point2DInt(500,0);
        Point2DInt rest = simulateSand(start);
        int c = 0;

        while (rest.getJ() < bottom) {
            c++;
            map.put(rest, Stuff.SAND);
            rest = simulateSand(start);
        }

        LOG.debug("this many sands fell: {}", c);
        return c;
    }

    @Override
    public Object algorithmPart2() {
        for (int i = -10000; i < 10000; i++) {
            map.put(new Point2DInt(i, bottom+2), Stuff.ROCK);
        }

        bottom+=2;

        Point2DInt start = new Point2DInt(500,0);
        int c = 0;

        while(!simulateSand(start).equals(start)) {
            c++;
            map.put(simulateSand(start), Stuff.SAND);
        }
//        printMap();

//        while (!rest.equals(new Point2DInt(500,0))) {
//            c++;
//            map.put(rest, Stuff.SAND);
//            rest = simulateSand(start);
//        }

        LOG.debug("this many sands fell: {}", c+1);
        return c+1;
    }

    private void printMap() {
        for (int i = 0; i < bottom+3 ; i++) {
            for (int j = 450; j < 550; j++) {
                Stuff s = map.get(new Point2DInt(j, i));
                if (s == null) {
                    System.out.print(".");
                } else if (s.equals(Stuff.ROCK)) {
                    System.out.print("#");
                } else {
                    System.out.print("o");
                }
            }
            System.out.println();
        }
    }
}
