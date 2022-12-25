package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.chemaxon.bkovacs.util.Direction;
import com.chemaxon.bkovacs.util.Point2DInt;
import org.jgrapht.graph.DefaultDirectedGraph;

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

    class Edge {
        final Direction d;

        public Edge(Direction d) {
            this.d = d;
        }

        @Override
        public String toString() {
            return d.toString();
        }
    }

    class Move {
        Direction d;
        int steps;

        public Move(Direction d, int steps) {
            this.d = d;
            this.steps = steps;
        }

        @Override
        public String toString() {
            return "Move{" +
                    "d=" + d +
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

        path = "R"+path;

        String regex = "([RL]\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(path);
        Direction currentDir = N;


        while (matcher.find()) {
//            LOG.debug("found: {}",matcher.group());
            String str = matcher.group();
            String turn = str.substring(0,1);
            currentDir = turnTo(currentDir, turn);
            int steps = Integer.parseInt(str.substring(1));

//            LOG.debug("new step: {} {}", currentDir, steps);
            moves.add(new Move(currentDir, steps));
        }



    }

    private Direction turnTo(Direction prevDir, String turnDir) {
        int ind = indOf(prevDir);
        if (turnDir.equals("R")) {
            ind = (ind+1)%4;
        } else {
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


    private Point2DInt wrapPoint(Point2DInt p) {
        int i = p.getI();
        int j = p.getJ();

        if (i < 0) {
            i += width;
        } else if (i >= width) {
            i -= width;
        }

        if (j < 0) {
            j += width;
        } else if (j >= width) {
            j -= width;
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
        Point2DInt current = start;
        Direction d = null;
        for (Move m : moves) {
            LOG.debug("{}", m);
            d = m.d;
            for (int i = 0; i < m.steps; i++) {
                Direction finalD = d;
                Edge e = graph.outgoingEdgesOf(current).stream().filter(ee -> ee.d.equals(finalD)).findFirst().orElse(null);
                if (e != null) {
                    current = graph.getEdgeTarget(e);
                    String newLine = input.get(current.getJ()).substring(0, current.getI())+getDirString(d)+input.get(current.getJ()).substring(current.getI()+1);
                    input.set(current.getJ(), newLine);
                } else {
                    break;
                }
            }
            LOG.debug("current point: {}", current);
        }

        printInput();

        return (current.getJ()+1)*1000 + (current.getI()+1)*4 + indOf(d);
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

    @Override
    protected Object algorithmPart2() {
        return null;
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }

}
