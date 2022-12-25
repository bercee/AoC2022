package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.chemaxon.bkovacs.util.Point2DInt;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;

public class Day12 extends DaySolver {

    private RealMatrix map;
    private Point2DInt start;
    private Point2DInt end;

    private final Graph<Point2DInt, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

    private final static Point2DInt[] DIRECTIONS = {
            new Point2DInt(-1, 0),
            new Point2DInt(1, 0),
            new Point2DInt(0, -1),
            new Point2DInt(0, 1),
    };

    public Day12() {
        super(12);
    }

    @Override
    protected void init(List<String> input) {
        map = MatrixUtils.createRealMatrix(input.size(), input.get(0).length());
        for (int i = 0; i < map.getRowDimension(); i++) {
            for (int j = 0; j < map.getColumnDimension(); j++){
                char c = input.get(i).charAt(j);
                if (c == 'S') {
                    start = new Point2DInt(i, j);
                    c = 'a';
                } else if (c == 'E') {
                    end = new Point2DInt(i, j);
                    c = 'z';
                }
                map.setEntry(i, j, c);
                graph.addVertex(new Point2DInt(i, j));
            }
        }

        for (int i = 0; i < map.getRowDimension(); i++) {
            for (int j = 0; j < map.getColumnDimension(); j++){
                List<Point2DInt> available = getAvailableNeighbors(new Point2DInt(i, j));
                int finalI = i;
                int finalJ = j;
                available.forEach(p -> graph.addEdge(new Point2DInt(finalI, finalJ), p));
            }
        }

        LOG.debug("vertexes: {}", graph.vertexSet().size());
        LOG.debug("edges: {}",graph.edgeSet().size());
    }

    private List<Point2DInt> getAvailableNeighbors(Point2DInt p) {
        List<Point2DInt> list = new ArrayList<>();
        int h = (int) map.getEntry(p.getI(), p.getJ());
        for (Point2DInt dir : DIRECTIONS) {
            Point2DInt next = p.add(dir);
            try {
                int n = (int) map.getEntry(next.getI(), next.getJ());
                if (n - h <= 1) {
                    list.add(next);
                }
            } catch (OutOfRangeException ignored) {
            }
        }
        return list;
    }

    public Object algorithmPart1() {
        DijkstraShortestPath<Point2DInt, DefaultEdge> dijkstraAlg =
                new DijkstraShortestPath<>(graph);
        GraphPath<Point2DInt, DefaultEdge> path = dijkstraAlg.getPath(start, end);
        LOG.debug("shortest: {}", path.getLength());
        return path.getLength();
    }

    private int getShortestLengthFromTo(Point2DInt start, Point2DInt end) {
        DijkstraShortestPath<Point2DInt, DefaultEdge> dijkstraAlg =
                new DijkstraShortestPath<>(graph);
        GraphPath<Point2DInt, DefaultEdge> path = dijkstraAlg.getPath(start, end);
        if (path == null) {
            return  Integer.MAX_VALUE;
        }
        return path.getLength();
    }

    public Object algorithmPart2() {

        int min = Integer.MAX_VALUE;

        for (int i = 0; i < map.getRowDimension(); i++) {
            for (int j = 0; j < map.getColumnDimension(); j++) {
                int h = (int) map.getEntry(i, j);
                if (h == 'a'){
                    int p = getShortestLengthFromTo(new Point2DInt(i, j), end);
                    if (p < min) {
                        min = p;
                    }
                }
            }
        }
        return min;
    }
}
