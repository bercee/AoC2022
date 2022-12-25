package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.chemaxon.bkovacs.util.Point2DInt;
import org.apache.commons.math3.analysis.function.Abs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day15 extends DaySolver {

    private static final String TEST =
            "Sensor at x=2, y=18: closest beacon is at x=-2, y=15\n" +
            "Sensor at x=9, y=16: closest beacon is at x=10, y=16\n" +
            "Sensor at x=13, y=2: closest beacon is at x=15, y=3\n" +
            "Sensor at x=12, y=14: closest beacon is at x=10, y=16\n" +
            "Sensor at x=10, y=20: closest beacon is at x=10, y=16\n" +
            "Sensor at x=14, y=17: closest beacon is at x=10, y=16\n" +
            "Sensor at x=8, y=7: closest beacon is at x=2, y=10\n" +
            "Sensor at x=2, y=0: closest beacon is at x=2, y=10\n" +
            "Sensor at x=0, y=11: closest beacon is at x=2, y=10\n" +
            "Sensor at x=20, y=14: closest beacon is at x=25, y=17\n" +
            "Sensor at x=17, y=20: closest beacon is at x=21, y=22\n" +
            "Sensor at x=16, y=7: closest beacon is at x=15, y=3\n" +
            "Sensor at x=14, y=3: closest beacon is at x=15, y=3\n" +
            "Sensor at x=20, y=1: closest beacon is at x=15, y=3";

    private static final int TEST_ROW = 2000000;

    private int minI = Integer.MAX_VALUE;
    private int maxI = Integer.MIN_VALUE;

    private final Map<Point2DInt, String> map = new HashMap<>();

    static Set<Point2DInt> getPoints(Point2DInt from, int distance) {
        HashSet<Point2DInt> points = new HashSet<>();
        int hdif;
        int vdif;
        for (int i = 0; i <= distance; i++) {
            hdif = i;
            vdif = distance-i;
            points.add(from.add(new Point2DInt(hdif, vdif)));
            points.add(from.add(new Point2DInt(-hdif, vdif)));
            points.add(from.add(new Point2DInt(hdif, -vdif)));
            points.add(from.add(new Point2DInt(-hdif, -vdif)));
        }
        return Collections.unmodifiableSet(points);
    }

    class SBD {
        Point2DInt s;
        Point2DInt b;
        int d;

        public SBD(Point2DInt s, Point2DInt b, int d) {
            this.s = s;
            this.b = b;
            this.d = d;
        }

        boolean isInside(Point2DInt p) {
            return getManhattanDistance(p, s) <= d;
        }
    }

    List<SBD> list = new ArrayList<>();

    public Day15() {
        super(15);
    }


    @Override
    protected void init(List<String> input) {
        for (String line : input) {
            String[] dat = line.split("\\s+|[,]+|[:]+");
            int si = Integer.parseInt(dat[2].substring(2));
            int sj = Integer.parseInt(dat[4].substring(2));
            int bi = Integer.parseInt(dat[10].substring(2));
            int bj = Integer.parseInt(dat[12].substring(2));

            Point2DInt s = new Point2DInt(si, sj);
            Point2DInt b = new Point2DInt(bi, bj);
            map.put(s, "S");
            map.put(b, "B");

            int d = getManhattanDistance(s, b);

            list.add(new SBD(s, b, d));

//            int difToRow = Math.abs(TEST_ROW-sj);
//            LOG.debug("s at {}, b at {}, dist = {}, diffToRow = {}", s, b, d, difToRow);
//            if (difToRow > d) {
//                LOG.debug("continue");
//                continue;
//            }
//
//            for (int n = 0; n <= d-difToRow; n++) {
//                if (si-n < minI) {
//                    minI = si-n;
//                }
//                if (si+n > maxI) {
//                    maxI = si+n;
//                }
//                Point2DInt p1 = new Point2DInt(si-n, TEST_ROW);
//                Point2DInt p2 = new Point2DInt(si+n, TEST_ROW);
//                if (!map.containsKey(p1)) {
//                    map.put(p1, "#");
//                }
//                if (!map.containsKey(p2)) {
//                    map.put(p2, "#");
//                }
//            }

            LOG.debug("s at {}, b at {}, dist = {}", s, b, d);

//            for (int hDif = 0; hDif <= d; hDif++) {
//                int vDif = d-hDif;
////                    LOG.debug("vdif: {}, hdif: {}", vDif, hDif);
////                if (Math.abs(TEST_ROW-sj) > d){
////                    continue;
////                }
//
//
//                    for (int i=si-vDif; i<=si+vDif; i++) {
//                        for (int j=sj-hDif; j<=sj+hDif; j++) {
//                              Point2DInt p = new Point2DInt(i, j);
//                            if (!map.containsKey(p)) {
//                                map.put(p, "#");
//                            }
//                        }
//                    }
//            }
        }

    }

    private static final Abs ABS = new Abs();


    private int getManhattanDistance(Point2DInt p1, Point2DInt p2) {

        return (int) (ABS.value(p1.getI()- p2.getI()) + ABS.value(p1.getJ()-p2.getJ()));
    }


    @Override
    protected Object algorithmPart1() {
        int c = 0;
        for (int i = minI; i<=maxI; i++) {
            if ("#".equals(map.get(new Point2DInt(i, TEST_ROW)))) {
                c++;
            }
        }
        return c;
    }

    private static long getScore(Point2DInt p) {
        return 4000000l * p.getI() + p.getJ();
    }

    private static boolean isWithinBox(Point2DInt p, int scope) {
        return p.getI() >= 0 && p.getI() <= scope && p.getJ() >= 0 && p.getJ() <= scope;
    }

    @Override
    protected Object algorithmPart2() {

        int scope = 4000000;
//        int scope = 20;
        for (SBD sbd : list) {
            Set<Point2DInt> outsides = getPoints(sbd.s, sbd.d + 1);
            for (Point2DInt p : outsides) {
                if (!isWithinBox(p, scope)) {
                    continue;
                }
                boolean contains = false;
                for (SBD sbd2 : list) {
                    if (sbd2.isInside(p)) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    LOG.debug("found the point: {}",p);
                    return getScore(p);
                }
            }
        }


        return null;

//        Set<Point2DInt> beacons = new HashSet<>();
//        Set<Point2DInt> sensors = new HashSet<>();
//        list.forEach(sbd -> beacons.add(sbd.b));
//        list.forEach(sbd -> sensors.add(sbd.s));
//
//        LOG.debug("beacons: {}", beacons.size());
//        LOG.debug("sensors: {}", sensors.size());

//        Set<Integer> rowsEx = new HashSet<>();
//        Set<Integer> columnsEx = new HashSet<>();
//
//        for (SBD sbd : list) {
//            for (int i = sbd.s.getI()-sbd.d; i <= sbd.s.getI()+sbd.d; i++) {
//                columnsEx.add(i);
//            }
//
//            for (int i = sbd.s.getJ()-sbd.d; i <= sbd.s.getJ()+sbd.d; i++) {
//                rowsEx.add(i);
//            }
//        }
//
//        int space = 20;
//        int iFinal = 0;
//        int jFinal = 0;
//
//        LOG.debug("rows ex: {}, columns ex: {}", rowsEx.size(), columnsEx.size());
//
//        for (int i = 0; i <= space; i++) {
//            if (!columnsEx.contains(i)) {
//                iFinal = i;
//                break;
//            }
//        }
//
//        for (int j = 0; j <= space; j++) {
//            if (!rowsEx.contains(j)) {
//                jFinal = j;
//                break;
//            }
//        }

//
//        Point2DInt p = null;
//        Point2DInt res = null;
//
//        int space = 4000000;
//        for (int i = 0; i <= space; i++) {
//            if (i%1000 == 0) {
//                LOG.debug(i);
//            }
//            for (int j = 0; j <= space; j++) {
//
////                p = new Point2DInt(i, j);
////                res = p;
////
////                for (SBD sbd : list) {
////                    res = null;
////                    if (getManhattanDistance(p, sbd.s) <= sbd.d) {
////                        res = null;
////                        break;
////                    }
////                }
////                if (res != null) {
////                    LOG.debug(res);
////                    break;
////                }
//            }
////            if (res != null) {
////                break;
////            }
//        }
//        return res.getI()*4000000 + res.getJ();
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }



}
