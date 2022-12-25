package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.chemaxon.bkovacs.util.Direction;
import com.chemaxon.bkovacs.util.Point2DInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day17 extends DaySolver {

    private static final String TEST = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>";

    private static final String[] SHAPES_SRC = {
                    "####",

                    ".#.\n" +
                    "###\n" +
                    ".#.",

                    "..#\n" +
                    "..#\n" +
                    "###",

                    "#\n" +
                    "#\n" +
                    "#\n" +
                    "#",

                    "##\n" +
                    "##"
    };

    private final ShapeProvider SHAPE_PROVIDER = new ShapeProvider();
    private JetProvider JET_PROVIDER;

    private static final Direction DOWN = Direction.N;

    private static final int WIDTH = 7;
    private static int MAX_CYCLES = 2022;
    private int maxH = 0;
    private final HashSet<Point2DInt> objects = new HashSet<>();
    private String input;

    class ShapeProvider {
        int i = 0;
        Shape getNext() {
            Shape s = new Shape(SHAPES_SRC[i], i);
            i = (i+1)% SHAPES_SRC.length;
            return s;
        }

        public int getNextID() {
            return i;
        }
    }

    class JetProvider {
        int i = 0;
        String input;

        public JetProvider(String input) {
            this.input = input;
        }

        public Direction getNext() {
            char c = input.charAt(i);
            i = (i +1)%input.length();
            if (c == '<')
                return Direction.W;
            else
                return Direction.E;
        }

        public int getNextID() {
            return i;
        }
    }

    class Shape{
        List<Point2DInt> points = new ArrayList<>();
        int w;
        int h;

        int ID;

        Point2DInt rel = new Point2DInt(0,0);

        public Shape(String src, int ID) {
            String[] lines = src.split("\\n");
            this.ID = ID;

            int maxW = 0;
            int maxH = 0;

            for (int j = 0; j < lines.length; j++) {
                for (int i = 0; i < lines[j].length(); i++) {
                    if (lines[j].charAt(i) == '#') {
                        points.add(new Point2DInt(i, -j));
                        if (i >= maxW) {
                            maxW = i + 1;
                        }

                        if (j >= maxH) {
                            maxH = j + 1;
                        }

                    }
                }
            }
            this.w = maxW;
            this.h = maxH;
        }
    }

    public Day17() {
        super(17);
    }

    private void initShape(Shape s) {
        s.rel = new Point2DInt(3, maxH+3+s.h);
    }

    private boolean canMoveShape(Shape s, Direction d) {
        List<Point2DInt> moved = new ArrayList<>(s.points).stream().map(p -> p.add(s.rel).add(d.getV())).collect(Collectors.toList());
        return moved.stream().noneMatch(objects::contains)
                && moved.stream().noneMatch(p -> p.getJ() <= 0)
                && moved.stream().noneMatch(p -> p.getI() <= 0)
                && moved.stream().noneMatch(p -> p.getI() > WIDTH);
    }

    private void moveShape(Shape s, Direction d) {
//        LOG.debug("Move shape {} to {},   {} --> {}", s.ID, d, s.rel, s.rel.add(d.getV()));
        s.rel = s.rel.add(d.getV());
    }

    private void landShape(Shape s) {
        objects.addAll(s.points.stream().map(p -> p.add(s.rel)).collect(Collectors.toList()));
        maxH = Math.max(s.rel.getJ(), maxH);
//        LOG.debug("shape {} landed, max H: {}", s.ID, maxH);
    }

    @Override
    protected void init(List<String> input) {
        this.input = input.get(0);
        JET_PROVIDER = new JetProvider(input.get(0));
        LOG.debug("input length: {}",input.get(0).length());
    }

    @Override
    protected Object algorithmPart1() {
        Direction dir;
        Shape s;
        for (long i = 0; i < MAX_CYCLES; i++) {
            dropNewShape();
        }
        return maxH;
    }

    private void dropNewShape() {
        Shape s;
        Direction dir;
        s = SHAPE_PROVIDER.getNext();
        initShape(s);
        dir = JET_PROVIDER.getNext();
        if (canMoveShape(s, dir)) {
            moveShape(s, dir);
        }

        while (canMoveShape(s, DOWN)) {
            moveShape(s, DOWN);
            dir = JET_PROVIDER.getNext();
            if (canMoveShape(s, dir)) {
                moveShape(s, dir);
            }
        }

        landShape(s);
    }

    @Override
    protected Object algorithmPart2() {

        long MAX_CYCLES_LARGE = 10000000l;
        long c = 0;
        long time = System.currentTimeMillis();
        while (c < MAX_CYCLES_LARGE) {
//            Direction d = JET_PROVIDER.getNext();
            Shape s = SHAPE_PROVIDER.getNext();
            c++;
        }
        LOG.debug("elapsed: {}s",((double)System.currentTimeMillis() - time)/1000);

//        long cycle = 0;
//        while (cycle < 1000000000000l) {
//            cycle++;
//            if (SHAPE_PROVIDER.getNextID() == 0 && JET_PROVIDER.getNextID() == 0) {
//                LOG.debug("Jet and shape start over at cycle {}",cycle);
//            }
//            dropNewShape();
//            if ((cycle-1)%100000 == 0 && cycle > 1) {
////                LOG.debug("cycle: {}, max: {}, max/cycle: {}",cycle, maxH, (double)maxH / cycle);
//                System.out.println((double) maxH / cycle);
//            }
//        }

//        MAX_CYCLES = input.length() * SHAPES_SRC.length;
//        HashMap<Integer, Integer> maxs = null;
//        Set<HashMap<Integer, Integer>> maxProfiles = new HashSet<>();
//        int c = 0;
//        do {
//            if (maxs != null) {
//                maxProfiles.add(createRelMaxs(maxs));
//            }
//            maxs = new HashMap<>();
//            c++;
//            LOG.debug("trying cycle: {}", c);
//            algorithmPart1();
//            for (int i = 1; i <= 7; i++) {
//                int finalI = i;
//                List<Point2DInt> list = objects.stream().filter(p -> p.getI() == finalI).collect(Collectors.toList());
//                list.sort(Comparator.comparingInt(Point2DInt::getJ).reversed());
//                maxs.put(i, list.get(0).getJ());
////                LOG.debug("max in column {}: {}", i, list.get(0).getJ());
//            }
//        }while(!maxProfiles.contains(maxs));
//        LOG.debug("this many cycles: {}",c);
        return null;
    }

    private boolean maxsDiffer(HashMap<Integer, Integer> maxs) {
        return new HashSet<>(maxs.values()).size() > 1;
    }

    private HashMap<Integer, Integer> createRelMaxs(HashMap<Integer, Integer> maxs) {
        int min = Collections.min(maxs.values());
        maxs.entrySet().forEach(e -> e.setValue(e.getValue() - min));
        return maxs;
    }

    private <K,V> boolean areEqual(Map<K,V> first, Map<K,V> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                .allMatch(e -> e.getValue().equals(second.get(e.getKey())));
    }



    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }


}
