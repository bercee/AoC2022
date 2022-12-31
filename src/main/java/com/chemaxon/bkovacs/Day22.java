package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.chemaxon.bkovacs.util.Direction;
import com.chemaxon.bkovacs.util.Point2DInt;
import com.google.common.collect.Lists;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chemaxon.bkovacs.util.Direction.E;
import static com.chemaxon.bkovacs.util.Direction.N;
import static com.chemaxon.bkovacs.util.Direction.S;
import static com.chemaxon.bkovacs.util.Direction.W;

public class Day22 extends DaySolver {

    private static final String TEST = "" +
            "        ...#\n" +
            "        .#..\n" +
            "        #...\n" +
            "        ....\n" +
            "...#.......#\n" +
            "........#...\n" +
            "..#....#....\n" +
            "..........#.\n" +
            "        ...#....\n" +
            "        .....#..\n" +
            "        .#......\n" +
            "        ......#.\n" +
            "\n" +
            "10R5L5R10L4R5L5";
    private Point2DInt[] rectsCorners;

    class Edge {
        final Direction d;
        final Direction nextD;

        public Edge(Direction d) {
            this(d, d);
        }

        public Edge(Direction d, Direction nextD) {
            this.d = d;
            this.nextD = nextD;
        }

        @Override
        public String toString() {
            return d.toString();
        }
    }

    class Move {
//        Direction d;
        String turn;
        int steps;

        public Move(String turn, int steps) {
            this.turn = turn;
            this.steps = steps;
        }

        @Override
        public String toString() {
            return "Move{" +
                    "turn='" + turn + '\'' +
                    ", steps=" + steps +
                    '}';
        }
    }

    private static Direction[] DIRS = {E, S, W, N};
    private List<String> input;
    private DefaultDirectedGraph<Point2DInt, Edge> graph = new DefaultDirectedGraph<>(Edge.class);
    private int height;
    private int width;

    private String path;
    private List<Move> moves = new ArrayList<>();

    private Point2DInt start;

    public Day22() {
        super(22);
    }

    @Override
    protected void init(List<String> input) {
        this.input = new ArrayList<>(input);
        this.input.remove(this.input.size()-1);
        this.input.remove(this.input.size()-1);
        int h = 0;
        int w = 0;

        start = new Point2DInt(input.get(0).indexOf('.'), 0);
        LOG.debug("start: {}", start);

        for (String str : input) {
            if (str.trim().equals("")) {
                break;
            }
            h++;
            if (str.length() > w) {
                w = str.length();
            }
        }
        height = h;
        width = w;

        path = input.get(input.size()-1);

        LOG.debug("map width: {}, height: {}", width, height);

        path = "R"+path;

        String regex = "([RL]\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(path);


        while (matcher.find()) {
//            LOG.debug("found: {}",matcher.group());
            String str = matcher.group();
            String turn = str.substring(0,1);
            int steps = Integer.parseInt(str.substring(1));

//            LOG.debug("new step: {} {}", currentDir, steps);
            moves.add(new Move(turn, steps));

        }
        LOG.debug("{} moves", moves.size());

    }

    private Direction turnTo(Direction prevDir, String turnDir) {
        int ind = indOf(prevDir);
        if (turnDir.equals("R")) {
            ind = (ind+1)%4;
        } else if (turnDir.equals("L")){
            ind--;
            if (ind < 0) {
                ind += 4;
            }
        }
        return DIRS[ind];
    }

    private int indOf(Direction d) {
        for (int i = 0; i < 4; i++) {
            if (DIRS[i] == d) {
                return i;
            }
        }
        return -1;
    }

    private Direction opposite(Direction d) {
        return DIRS[(indOf(d) + 2)%4];
    }

    private String otherTurn(String turn) {
        if (turn.equals("R")) {
            return "L";
        }else if (turn.equals("L")) {
            return "R";
        }
        return turn;
    }


    private Point2DInt wrapPoint(Point2DInt p) {
        int i = p.getI();
        int j = p.getJ();

        if (i < 0) {
            i += width;
        } else if (i >= width) {
            i -= width;
        }

        if (j < 0) {
            j += height;
        } else if (j >= height) {
            j -= height;
        }

        return new Point2DInt(i, j);
    }

    private char charAt(Point2DInt p) {
        int i = p.getI();
        int j = p.getJ();
        if (j >= height || j < 0 || i >= width || i < 0) {
            return ' ';
        } else if (input.get(j).length()<i+1) {
            return ' ';
        } else {
            return input.get(j).charAt(i);
        }
    }

    @Override
    protected Object algorithmPart1() {

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < Math.min(width, input.get(j).length()); i++) {
                char c = input.get(j).charAt(i);
                if (c=='.') {
                    graph.addVertex(new Point2DInt(i, j));
                }
            }
        }

//        LOG.debug("graph with {} nodes, {} edges", graph.vertexSet().size(), graph.edgeSet().size());

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < Math.min(width, input.get(j).length()); i++) {
                Point2DInt source = new Point2DInt(i, j);
                if (graph.containsVertex(source)) {
                    Arrays.stream(DIRS).forEach(direction -> {
                        Point2DInt target = source;
                        int c;
                        do {
                            target = wrapPoint(target.add(direction.getV()));
                            c = charAt(target);
                        } while (c == ' ');
                        if (c == '.') {
//                            LOG.debug("edge added: {} to {}, dir: {}", source, target, direction);
                            graph.addEdge(source, target, new Edge(direction));
                        }
                    });
                }
            }
        }

//        LOG.debug(graph.outgoingEdgesOf(new Point2DInt(8, 0)));

        LOG.debug("graph with {} nodes, {} edges", graph.vertexSet().size(), graph.edgeSet().size());




        return doWalk();
    }

    private void printInput() {
        for (String s : input) {
            System.out.println(s);
        }
    }

    private String getDirString(Direction d) {
        return switch (d) {
            case S -> "v";
            case W -> "<";
            case E -> ">";
            case N -> "^";
            default -> null;
        };
    }

    private int cubeSize;

    @Override
    protected Object algorithmPart2() {
        cubeSize = input.get(0).trim().length()/2;
        LOG.debug("cube size: {}", cubeSize);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (charAt(new Point2DInt(i, j))=='.') {
                    graph.addVertex(new Point2DInt(i, j));
                }
            }
        }

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                Point2DInt p = new Point2DInt(i, j);
                if (graph.containsVertex(p)) {
                    Arrays.stream(DIRS).forEach(d -> {
                        Point2DInt o = p.add(d.getV());
                        if (graph.containsVertex(o)) {
                            graph.addEdge(p, o, new Edge(d));
                        }
                    });
                }
            }
        }

        LOG.debug("graph so far: {} nodes, {} edges", graph.vertexSet().size(), graph.edgeSet().size());


        rectsCorners = new Point2DInt[7];

        rectsCorners[1] = new Point2DInt(cubeSize*1, 0);
        rectsCorners[2] = new Point2DInt(cubeSize*2, 0);
        rectsCorners[3] = new Point2DInt(cubeSize, cubeSize);
        rectsCorners[4] = new Point2DInt(cubeSize, cubeSize*2);
        rectsCorners[5] = new Point2DInt(0, cubeSize*2);
        rectsCorners[6] = new Point2DInt(0, cubeSize*3);

        //1 top --> 6 left, N, E
        mapPoints(getTopEdge(1), getLeftEdge(6), N, E);

        //1 left --> 5 left (r), W, E
        mapPoints(getLeftEdge(1), Lists.reverse(getLeftEdge(5)), W, E);

        //3 left --> 5 top, W, S
        mapPoints(getLeftEdge(3), getTopEdge(5), W, S);

        //3 right --> 2 bottom, E, N
        mapPoints(getRightEdge(3), getBottomEdge(2), E, N);

        //4 right --> 2 right (r), E, W
        mapPoints(getRightEdge(4), Lists.reverse(getRightEdge(2)), E, W);

        //6 bottom --> 2 top, S, S
        mapPoints(getBottomEdge(6), getTopEdge(2), S, S);

        //6 right --> 4 bottom, E, N
        mapPoints(getRightEdge(6), getBottomEdge(4), E, N);


//        //1 left --> 3 top, W, S
//        mapPoints(getLeftEdge(rectsCorners[1]), getTopEdge(rectsCorners[3]), W, S);
//
//        //1 top --> 2 top (r), N, S
//        mapPoints(getTopEdge(rectsCorners[1]), Lists.reverse(getTopEdge(rectsCorners[2])), N, S);
//
//        //1 right --> 6 right (r), E, W
//        mapPoints(getRightEdge(rectsCorners[1]), Lists.reverse(getRightEdge(rectsCorners[6])), E, W);
//
//        //2 left --> 6 bottom (r), W, N
//        mapPoints(getLeftEdge(rectsCorners[2]), Lists.reverse(getBottomEdge(rectsCorners[6])), W, N);
//
//        //2 bottom --> 5 bottom (r), S, N
//        mapPoints(getBottomEdge(rectsCorners[2]), Lists.reverse(getBottomEdge(rectsCorners[5])), S, N);
//
//
//        //3 bottom --> 5 left (3), S, E
//        mapPoints(getBottomEdge(rectsCorners[3]), getLeftEdge(rectsCorners[5]), S, E);
//
//        //4 right --> 6 top(r), E, S
//        mapPoints(getRightEdge(rectsCorners[4]), Lists.reverse(getTopEdge(rectsCorners[6])), E, S);




        LOG.debug("graph so far: {} nodes, {} edges", graph.vertexSet().size(), graph.edgeSet().size());


        return doWalk();



    }

    private int doWalk() {
        Point2DInt current = start;
        Direction currentDir = N;
        for (Move m : moves) {
//            LOG.debug("{}", m);
            currentDir = turnTo(currentDir, m.turn);
            for (int i = 0; i < m.steps; i++) {
                Direction finalD = currentDir;
                Edge e = graph.outgoingEdgesOf(current).stream().filter(ee -> ee.d.equals(finalD)).findFirst().orElse(null);
                if (e != null) {
                    current = graph.getEdgeTarget(e);
                    currentDir = e.nextD;
                    String newLine = input.get(current.getJ()).substring(0, current.getI())+getDirString(currentDir)+input.get(current.getJ()).substring(current.getI()+1);
                    input.set(current.getJ(), newLine);
                } else {
                    break;
                }
            }
//            LOG.debug("current point: {}", current);
        }

//        printInput();

        return (current.getJ()+1)*1000 + (current.getI()+1)*4 + indOf(currentDir);
    }

    private List<Point2DInt> getTopEdge(int side) {
        Point2DInt topLeft = rectsCorners[side];
        return getHorizontalCubeEdge(topLeft);
    }

    private List<Point2DInt> getLeftEdge(int side) {
        Point2DInt topLeft = rectsCorners[side];
        return getVerticalCubeEdge(topLeft);
    }

    private List<Point2DInt> getRightEdge(int side) {
        Point2DInt topLeft = rectsCorners[side];
        return getVerticalCubeEdge(topLeft.add(new Point2DInt(cubeSize-1, 0)));
    }

    private List<Point2DInt> getBottomEdge(int side) {
        Point2DInt topLeft = rectsCorners[side];
        return getHorizontalCubeEdge(topLeft.add(new Point2DInt(0, cubeSize-1)));
    }


    private void mapPoints(List<Point2DInt> points1, List<Point2DInt> points2, Direction d, Direction nextD) {
        for (int i = 0; i < points1.size(); i++) {
            Point2DInt p1 = points1.get(i);
            Point2DInt p2 = points2.get(i);

            if (charAt(p1) == '.' && charAt(p2) == '.') {
                graph.addEdge(p1, p2, new Edge(d, nextD));
                graph.addEdge(p2, p1, new Edge(opposite(nextD), opposite(d)));
            }

        }
    }

    private List<Point2DInt> getHorizontalCubeEdge(Point2DInt start) {
        List<Point2DInt> ret = new ArrayList<>();
        for (int i = 0; i < cubeSize; i++) {
            ret.add(start.add(new Point2DInt(i, 0)));
        }

        return ret;
    }

    private List<Point2DInt> getVerticalCubeEdge(Point2DInt start) {
        List<Point2DInt> ret = new ArrayList<>();
        for (int i = 0; i < cubeSize; i++) {
            ret.add(start.add(new Point2DInt(0, i)));
        }

        return ret;
    }


    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }

}
