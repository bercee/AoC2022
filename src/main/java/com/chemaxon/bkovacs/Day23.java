package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.chemaxon.bkovacs.util.Direction;
import com.chemaxon.bkovacs.util.Point2DInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.chemaxon.bkovacs.util.Direction.*;

public class Day23 extends DaySolver {

    private static final String TEST = "....#..\n" +
            "..###.#\n" +
            "#...#.#\n" +
            ".#...##\n" +
            "#.###..\n" +
            "##.#.##\n" +
            ".#..#..";


//    private static final String TEST = ".....\n" +
//            "..##.\n" +
//            "..#..\n" +
//            ".....\n" +
//            "..##.\n" +
//            ".....";

    private static Direction[] MOTIONS = {N, S, W, E};

    private static HashMap<Direction, Set<Direction>> LOOKING = new HashMap<>();
    static {
        LOOKING.put(N, Set.of(NW, N, NE));
        LOOKING.put(S, Set.of(SW, S, SE));
        LOOKING.put(W, Set.of(SW, W, NW));
        LOOKING.put(E, Set.of(NE, E, SE));
    }

    static class MotionProvider {
        private int i = -1;
        public List<Direction> nextLookingOrder() {
            i++;
            return List.of(MOTIONS[i%4], MOTIONS[(i+1)%4], MOTIONS[(i+2)%4], MOTIONS[(i+3)%4]);
        }
    }

    private static MotionProvider MOTION_PROVIDER = new MotionProvider();

    private static Set<Direction> lookingDirs(Direction d){
        return LOOKING.get(d);
    }

    private static List<Point2DInt> getBoundingRect(List<Point2DInt> points) {
        List<Point2DInt> clone = new ArrayList<>(points);
        clone.sort(Comparator.comparingInt(Point2DInt::getI));
        int iMin = clone.get(0).getI();
        int iMax = clone.get(clone.size()-1).getI();
        clone.sort(Comparator.comparingInt(Point2DInt::getJ));
        int jMin = clone.get(0).getJ();
        int jMax = clone.get(clone.size()-1).getJ();

        return List.of(
                new Point2DInt(iMin, jMin),
                new Point2DInt(iMax, jMin),
                new Point2DInt(iMax, jMax),
                new Point2DInt(iMin, jMax));
    }

    private static int getWidth(List<Point2DInt> points) {
        List<Point2DInt> bounds = getBoundingRect(points);
        return bounds.get(1).getI()-bounds.get(0).getI()+1;
    }

    private static int getHeight(List<Point2DInt> points) {
        List<Point2DInt> bounds = getBoundingRect(points);
        return bounds.get(2).getJ()-bounds.get(1).getJ()+1;
    }

    private static int getResult(List<Point2DInt> points) {
        return getWidth(points)*getHeight(points) - points.size();
    }

    private final ArrayList<Point2DInt> elves = new ArrayList<>();


    public Day23() {
        super(23);
    }

    @Override
    protected void init(List<String> input) {
        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.get(i).length(); j++) {
                if (input.get(i).charAt(j) == '#') {
                    elves.add(new Point2DInt(j, i));
                }
            }
        }
        printStatus();

    }

    private void printStatus() {
        LOG.debug("{} elves", elves.size());
        LOG.debug("width: {}", getWidth(elves));
        LOG.debug("height: {}", getHeight(elves));
        LOG.debug("empty tiles: {}", getResult(elves));
    }

    private void printPoints() {
        List<Point2DInt> bounds = getBoundingRect(elves);
        for (int j = bounds.get(0).getJ(); j <= bounds.get(2).getJ(); j++) {
            for (int i = bounds.get(0).getI(); i <= bounds.get(1).getI(); i++) {
                if (elves.contains(new Point2DInt(i, j))) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    @Override
    protected Object algorithmPart1() {
//        printPoints();
        for (int i = 0; i < 10; i++) {
//            LOG.debug("round {}", i+1);
            doNextRound(MOTION_PROVIDER.nextLookingOrder());
//            printPoints();
        }
        return getResult(elves);
    }

    private boolean doNextRound(List<Direction> lookingOrder) {
        HashMap<Point2DInt, Point2DInt> moveFromTo = new HashMap<>();
        Set<Point2DInt> duplicates = new HashSet<>();
        for (Point2DInt e :elves) {
            if (Arrays.stream(values()).noneMatch(d -> elves.contains(e.add(d.getV())))) {
                continue;
            }

            for (Direction d : lookingOrder) {
                Set<Direction> looks = lookingDirs(d);
                if (looks.stream().map(dir -> e.add(dir.getV())).noneMatch(elves::contains)) {
                    Point2DInt dest = e.add(d.getV());
                    if (moveFromTo.entrySet().stream().anyMatch(ee -> ee.getValue().equals(dest))) {
                        duplicates.add(e);
                        moveFromTo.entrySet().stream().filter(ee -> ee.getValue().equals(dest)).forEach(ee -> duplicates.add(ee.getKey()));
                    }
                    moveFromTo.put(e, e.add(d.getV()));
                    break;
                }
            }

        }

        duplicates.forEach(moveFromTo::remove);

        moveFromTo.forEach((key, value) -> {
            elves.remove(key);
            elves.add(value);
        });

        return !moveFromTo.isEmpty();

    }


    @Override
    protected Object algorithmPart2() {
        int c = 1;
        while (doNextRound(MOTION_PROVIDER.nextLookingOrder())) {
            LOG.debug("round {}",c);
//            printPoints();
            c++;
        }
        return c;
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }
}
