package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.analysis.function.Signum;
import org.apache.commons.numbers.core.Precision;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day9 extends DaySolver {

    enum Direction {
        U(Vector2D.of(0,-1)), D(Vector2D.of(0,1)), L(Vector2D.of(-1, 0)), R(Vector2D.of(1,0));
        private final Vector2D v;

        Direction(Vector2D v) {
            this.v = v;
        }

        static Direction parse(String s){
            return Direction.valueOf(s.toUpperCase());
        }
    }

    private static final Precision.DoubleEquivalence PRECISION = Precision.doubleEquivalenceOfEpsilon(0.01);

    private final Set<Vector2D> visited = new HashSet<>();
    private Vector2D H = Vector2D.of(0,0);
    private Vector2D T = Vector2D.of(0,0);

//    private List<Vector2D> rope = new ArrayList<>();
    private Map<Integer, Vector2D> rope = new HashMap<>();




    public Day9() {
        super(9);

    }

    @Override
    protected void init(List<String> input) {
        visited.add(Vector2D.of(0,0));
        rope.put(0, H);
        for (int i = 1; i <= 9; i++) {
            rope.put(i, Vector2D.of(0,0));
        }

        for (String line : input) {
            String[] dat = line.split("\\s+");
            for (int i = 0; i < Integer.parseInt(dat[1]); i++) {
                H = H.add(Direction.parse(dat[0]).v);
                rope.put(0, H);
                for (int j = 1; j <= 9; j++) {
                    rope.put(j, follow(rope.get(j-1), rope.get(j)));
                }
                visited.add(rope.get(9));

//                followHead();
//                LOG.debug("{}  {}",H,rope.get(9));
            }
        }
//        LOG.debug(H);

//        Vector2D v1 = Vector2D.of(0,0);
//        Vector2D v2 = Vector2D.of(1,1);
//        LOG.debug(v2.normSq());
    }

    private void followHead() {
        Vector2D diff = H.subtract(T);
        if (diff.normSq() > 2.1) {
            int x = (int) diff.getX();
            int y = (int) diff.getY();
            Signum sign = new Signum();
            Vector2D move = Vector2D.of(sign.value(x), sign.value(y));
            T = T.add(move);
            visited.add(T);
        }
    }

    private Vector2D follow(Vector2D v1, Vector2D v2) {
        Vector2D diff = v1.subtract(v2);
        if (diff.normSq() > 2.1) {
            int x = (int) diff.getX();
            int y = (int) diff.getY();
            Signum sign = new Signum();
            Vector2D move = Vector2D.of(sign.value(x), sign.value(y));
            v2 = v2.add(move);
        }
        return v2;
    }

    @Override
    public Object algorithmPart1() {
        return visited.size();
    }

    @Override
    public Object algorithmPart2() {
        return visited.size();
    }
}
