package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.chemaxon.bkovacs.util.Direction;
import com.chemaxon.bkovacs.util.Point2DInt;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.chemaxon.bkovacs.util.Direction.*;

public class Day24 extends DaySolver {

    private static final String TEST = "#.######\n" +
            "#>>.<^<#\n" +
            "#.<..<<#\n" +
            "#>v.><>#\n" +
            "#<^v^^>#\n" +
            "######.#";

    private int width;
    private int height;
    private Point2DInt start;
    private Point2DInt end;

    private Point2DInt p;

    private Direction[] DIRS = {S,W,E,N,NULL};

    private class Blizzard {
        Point2DInt p;
        Direction d;

        public Blizzard(Point2DInt p, Direction d) {
            this.p = p;
            this.d = d;
        }

        void move() {
            p = new Point2DInt(normalize(p.add(d.getV()).getI(), width), normalize(p.add(d.getV()).getJ(), height));
        }

        int normalize(int n, int max) {
            if (n < 0) {
                return n + max;
            } else if (n >= max) {
                return n % max;
            } else {
                return n;
            }
        }

    }

    private Set<Blizzard> blizzards = new HashSet<>();


    public Day24() {
        super(24);
    }

    @Override
    protected void init(List<String> input) {
        width = input.get(0).length()-2;
        height = input.size()-2;
        start = new Point2DInt(0, -1);
        end = new Point2DInt(width-1, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                Direction d = getDir(input.get(j+1).charAt(i+1));
                if (d != null) {
                    blizzards.add(new Blizzard(new Point2DInt(i, j), d));
                }
            }
        }

        LOG.debug("{} blizzards", blizzards.size());
    }

    private static Direction getDir(char c) {
        return switch (c) {
            case '>' -> E;
            case '<' -> W;
            case '^' -> N;
            case 'v' -> S;
            default -> null;
        };
    }

    @Override
    protected Object algorithmPart1() {
        p = start;
        HashSet<Point2DInt> possPoints = new HashSet<>();
        HashSet<Point2DInt> newPossPoints = new HashSet<>();
        possPoints.add(start);
        LOG.debug("start");

        int c = 0;
        while (!possPoints.contains(end)) {
            blizzards.forEach(Blizzard::move);
            c++;
            newPossPoints.clear();
            possPoints.forEach(point -> Arrays.stream(DIRS).forEach(d -> {
                Point2DInt next = point.add(d.getV());
                if (canMoveTo(next)) {
                    newPossPoints.add(next);
                }
            }));
            possPoints.clear();
            possPoints.addAll(newPossPoints);

            LOG.debug("minute {}, possible locations: {}", c, possPoints.size());
        }


        return c;
    }

    private void printBlizzards() {
        HashMap<Point2DInt, Character> map = new HashMap<>();
        blizzards.forEach(b -> {
            Point2DInt p = b.p;
            Character c = switch (b.d) {
                case S -> 'v';
                case W -> '<';
                case E -> '>';
                case N -> '^';
                default -> null;
            };
            if (map.containsKey(p)) {
                try {
                    int i = Integer.parseInt(String.valueOf(map.get(p)));
                    map.put(p, String.valueOf(i+1).charAt(0));
                }catch (NumberFormatException e) {
                    map.put(p,'2');
                }
            } else {
                map.put(p, c);
            }
        });

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (map.containsKey(new Point2DInt(i, j))) {
                    System.out.print(map.get(new Point2DInt(i, j)));
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private boolean canMoveTo(Point2DInt p) {
        if (p.equals(start) || p.equals(end)) {
            return true;
        }
        else if (p.getJ() < 0 || p.getI() < 0 || p.getI() >= width || p.getJ() >= height) {
            return false;
        } else {
            return isFree(p);
        }
    }

    private boolean isFree(Point2DInt p) {
        return blizzards.stream().map(b -> b.p).noneMatch(pp -> pp.equals(p));
    }

    @Override
    protected Object algorithmPart2() {
        p = start;
        HashSet<Point2DInt> possPoints = new HashSet<>();
        HashSet<Point2DInt> newPossPoints = new HashSet<>();
        possPoints.add(start);
        LOG.debug("start");

        int c = 0;
        while (!possPoints.contains(end)) {
            blizzards.forEach(Blizzard::move);
            c++;
            newPossPoints.clear();
            possPoints.forEach(point -> Arrays.stream(DIRS).forEach(d -> {
                Point2DInt next = point.add(d.getV());
                if (canMoveTo(next)) {
                    newPossPoints.add(next);
                }
            }));
            possPoints.clear();
            possPoints.addAll(newPossPoints);

            LOG.debug("minute {}, possible locations: {}", c, possPoints.size());
        }

        possPoints.clear();
        newPossPoints.clear();

        possPoints.add(end);

        while (!possPoints.contains(start)) {
            blizzards.forEach(Blizzard::move);
            c++;
            newPossPoints.clear();
            possPoints.forEach(point -> Arrays.stream(DIRS).forEach(d -> {
                Point2DInt next = point.add(d.getV());
                if (canMoveTo(next)) {
                    newPossPoints.add(next);
                }
            }));
            possPoints.clear();
            possPoints.addAll(newPossPoints);

            LOG.debug("minute {}, possible locations: {}", c, possPoints.size());
        }

        possPoints.clear();
        newPossPoints.clear();

        possPoints.add(start);

        while (!possPoints.contains(end)) {
            blizzards.forEach(Blizzard::move);
            c++;
            newPossPoints.clear();
            possPoints.forEach(point -> Arrays.stream(DIRS).forEach(d -> {
                Point2DInt next = point.add(d.getV());
                if (canMoveTo(next)) {
                    newPossPoints.add(next);
                }
            }));
            possPoints.clear();
            possPoints.addAll(newPossPoints);

            LOG.debug("minute {}, possible locations: {}", c, possPoints.size());
        }

        return c;
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }

}
