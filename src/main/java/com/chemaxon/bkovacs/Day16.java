package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day16 extends DaySolver {

    private static final String TEST = "Valve AA has flow rate=0; tunnels lead to valves DD, II, BB\n" +
            "Valve BB has flow rate=13; tunnels lead to valves CC, AA\n" +
            "Valve CC has flow rate=2; tunnels lead to valves DD, BB\n" +
            "Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE\n" +
            "Valve EE has flow rate=3; tunnels lead to valves FF, DD\n" +
            "Valve FF has flow rate=0; tunnels lead to valves EE, GG\n" +
            "Valve GG has flow rate=0; tunnels lead to valves FF, HH\n" +
            "Valve HH has flow rate=22; tunnel leads to valve GG\n" +
            "Valve II has flow rate=0; tunnels lead to valves AA, JJ\n" +
            "Valve JJ has flow rate=21; tunnel leads to valve II";

    private static final String PATTERN = "Valve ([A-Z]+) has flow rate=(\\d+); tunnel[s]? lead[s]? to valve[s]? (.*)";
    private final Graph<Valve, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
    private final Graph<Valve, DefaultWeightedEdge> graphWithFlows = new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);

    private final Set<Valve> valvesWithFlow = new HashSet<>();

    private static final int MAX_T = 26;

    class Valve {
        final String ID;
        final int flow;

        boolean isOpen = false;

        final Map<String, Integer> distances = new HashMap<>();

        public Valve(String ID, int flow) {
            this.ID = ID;
            this.flow = flow;
        }

        public void setDistance(String other, int distance) {
            distances.put(other, distance);
        }

        public int getDistanceTo(String other) {
            return distances.get(other);
        }

        public void open() {
            this.isOpen = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Valve valve = (Valve) o;
            return ID.equals(valve.ID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ID);
        }

        public boolean hasFlow() {
            return flow > 0;
        }
    }

    public Day16() {
        super(16);
    }


    @Override
    protected void init(List<String> input) {
        Pattern pattern = Pattern.compile(PATTERN);
        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                graph.addVertex(new Valve(matcher.group(1), Integer.parseInt(matcher.group(2))));
            } else {
                throw new RuntimeException("no match found");
            }
        }

        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String[] dat = matcher.group(3).split(", ");
                for (String other : dat) {
                    graph.addEdge(getValve(matcher.group(1)), getValve(other));
                }
            } else {
                throw new RuntimeException("no match found");
            }
        }

        LOG.debug("Graph has {} valves and {} tunnels", graph.vertexSet().size(), graph.edgeSet().size());

        DijkstraShortestPath<Valve, DefaultEdge> dijkstraAlg =
                new DijkstraShortestPath<>(graph);

        for (Valve start : graph.vertexSet()) {
            if (start.hasFlow() || start.ID.equals("AA")) {
                graphWithFlows.addVertex(start);
            }
            for (Valve end : graph.vertexSet()) {
                if (start.equals(end)) {
                    continue;
                }
                GraphPath<Valve, DefaultEdge> path = dijkstraAlg.getPath(start, end);
                int d = path.getLength();
                start.setDistance(end.ID, d);
                if (end.hasFlow() && (start.hasFlow() || start.ID.equals("AA"))) {
                    graphWithFlows.addVertex(end);
                    if (!pathContainsValveWithFlow(path)) {
                        var e = new DefaultWeightedEdge();
                        graphWithFlows.addEdge(start, end, e);
                        graphWithFlows.setEdgeWeight(e, d);
                    }
                }

            }
        }

        LOG.debug("Meaningful graph has {} valves and {} edges (paths).", graphWithFlows.vertexSet().size(), graphWithFlows.edgeSet().size());
    }

    private boolean pathContainsValveWithFlow(GraphPath<Valve, DefaultEdge> path) {
        return path.getVertexList().stream().anyMatch(v -> v.hasFlow() && !v.equals(path.getStartVertex()) && !v.equals(path.getEndVertex()));
    }

    private Valve getValve(String id) {
        return graph.vertexSet().stream().filter(v -> v.ID.equals(id)).findFirst().get();
    }

    private Set<Valve> visited = new HashSet<>();


    @Override
    protected Object algorithmPart1() {

        List<Valve> path = new ArrayList<>();
        path.add(getValve("AA"));
        visited.add(getValve("AA"));

        Valve bestNext = getBestNextStep(path);
        while (bestNext != null) {
            path.add(bestNext);
            visited.add(bestNext);
            bestNext = getBestNextStep(path);
        }

        return getScoreOfPath(path);
    }

    private Valve getBestNextStep(List<Valve> path) {
        int maxScore = getScoreOfPath(path);
        Valve bestNext = null;
        List<Valve> tryThis;

        Set<Valve> tryTheseAsNext = getUnvisitedDirectConnections(path);
        if (tryTheseAsNext.isEmpty()) {
            tryTheseAsNext = getAllUnvisited();
        }

        for (Valve v : tryTheseAsNext) {
            if (visited.contains(v)) {
                continue;
            }

            tryThis = new ArrayList<>(path);
            tryThis.add(v);
            int score = getScoreOfPath(tryThis);
            if (score > maxScore) {
                maxScore = score;
                bestNext = v;
            }
        }

//        LOG.debug("Next best: " + (bestNext == null ? "null" : bestNext.ID));

        return bestNext;
    }

    private Set<Valve> getAllUnvisited() {
        return graphWithFlows.vertexSet().stream().filter(v -> !visited.contains(v)).collect(Collectors.toSet());
    }

    private Set<Valve> getUnvisitedDirectConnections(List<Valve> path) {
        Valve last = path.get(path.size() - 1);
        return graphWithFlows.edgesOf(last).stream()
                .map(e -> getOtherEndOf(e, last, graphWithFlows))
                .filter(v -> !visited.contains(v))
                .collect(Collectors.toSet());
    }

    private List<Valve> getBestPath() {
        var list = new ArrayList<Valve>();
        list.add(getValve("AA"));
        list.add(getValve("DD"));
        list.add(getValve("BB"));
        list.add(getValve("JJ"));
        list.add(getValve("HH"));
        list.add(getValve("EE"));
        list.add(getValve("CC"));

        return list;
    }


//    private <V, E extends DefaultEdge> List<GraphPath<V, E>> getPossibleNextSteps(GraphPath<V, E> path, Graph<V, E> graph) {
//        V last = path.getEndVertex();
//        V penultimate = path.getVertexList().get(path.getLength()-2);
//        Set<E> edges = graph.edgesOf(last);
//        edges.removeIf(e ->  penultimate.equals( getOtherEndOf(e, last, graph)));
//        List<GraphPath<V,E>> ret = new ArrayList<>();
//
//    }

    private int getScoreOfPath(List<Valve> path) {

        if (path.size() == 0) {
            throw new RuntimeException();
        }

        if (!path.get(0).ID.equals("AA")) {
            throw new RuntimeException();
        }

        int sum = 0;
        int time = 0;
        Valve current = getValve("AA");

        for (int i = 1; i < path.size(); i++) {
            Valve next = path.get(i);
//            var e = graphWithFlows.getEdge(current, next);
            double weight = 0;
//            if (e == null) {
            weight = current.getDistanceTo(next.ID);
//            }else {
//                weight = graphWithFlows.getEdgeWeight(e);
//            }

            time += (int) weight;

            if (time >= MAX_T) {
                //we're out of time
                break;
            }

            time++; //one minute to open the valve
            sum += (MAX_T - time) * next.flow;

            current = next;
        }

        return sum;
    }

    private <V, E> V getOtherEndOf(E edge, V oneEnd, Graph<V, E> graph) {
        V target = graph.getEdgeTarget(edge);
        V source = graph.getEdgeSource(edge);

        if (target.equals(oneEnd)) {
            return source;
        } else if (source.equals(oneEnd)) {
            return target;
        } else {
            return null;
        }
    }


    @Override
    protected Object algorithmPart2() {




        ArrayList<ArrayList<Valve>> paths = new ArrayList<>(generateAllPath(getValve("AA")));
        List<Integer> scores = paths.stream().map(p -> getScoreOfPath(p)).collect(Collectors.toList());
        paths.sort((p1, p2) -> Integer.compare(getScoreOfPath(p2), getScoreOfPath(p1)));

        int maxTotalScore = 0;
        int best1 = 0;
        int best2 = 0;

        for (int i = 0; i < paths.size(); i++) {
            if (scores.get(i) < maxTotalScore / 2) {
                break;
            }
            for (int j = 1; j < paths.size(); j++) {
                if (!areCompatiblePaths(paths.get(i), paths.get(j))) {
                    continue;
                } else {
                    int score = scores.get(i) + scores.get(j);
                    if (maxTotalScore < score) {
                        maxTotalScore = score;
                        best1 = i;
                        best2 = j;
                    }
                    break;
                }
            }
        }

        LOG.debug("my path: {}", paths.get(best1).stream().map(v -> v.ID).collect(Collectors.toList()));
        LOG.debug("elephant's path: {}", paths.get(best2).stream().map(v -> v.ID).collect(Collectors.toList()));

        return maxTotalScore;

//        printGraphWithShortestDists();
//
////        LOG.debug("my path: {}", getScoreOfPath(getMyBestPath()));
//
//        List<Valve> pathMe = getMyStartingPath();
//        List<Valve> pathElephant = getElephantSartingPath();
//
//        visited.addAll(pathMe);
//        visited.addAll(pathElephant);
//
//        for (int i = 0; i < 15; i++) {
//            Valve nextMe = getBestNextStep(pathMe);
////            LOG.debug("next for me: {}", nextMe == null ? "null" : nextMe.ID);
//            if (nextMe != null) {
//                visited.add(nextMe);
//                pathMe.add(nextMe);
//            }
//
//            Valve nextElephant = getBestNextStep(pathElephant);
////            LOG.debug("next for elephant: \t\t{}", nextElephant == null? "null" : nextElephant.ID);
//            if (nextElephant != null) {
//                visited.add(nextElephant);
//                pathElephant.add(nextElephant);
//            }
//
//            if (nextMe == null && nextElephant == null) {
//                break;
//            }
//        }
//
//        LOG.debug("my path: {}", pathMe.stream().map(v -> v.ID).collect(Collectors.toList()));
//        LOG.debug("elephant's path: {}", pathElephant.stream().map(v -> v.ID).collect(Collectors.toList()));



//
//
//
//
//        List<Valve> pathMe = new ArrayList<>(Arrays.asList(getValve("AA")));
//        List<Valve> pathElephant = new ArrayList<>(Arrays.asList(getValve("AA")));
//        visited.add(getValve("AA"));
//
//        int nextStopMe = 0;
//        int nextStopElephant = 0;
//
//        Valve bestNextMe = getBestNextStep(pathMe);
//        if (bestNextMe != null) {
//            int dMe = bestNextMe.getDistanceTo(pathMe.get(pathMe.size()-1).ID);
//            nextStopMe = dMe;
//            visited.add(bestNextMe);
//            pathMe.add(bestNextMe);
//        }
//
//        Valve bestNextElephant = getBestNextStep(pathElephant);
//        if (bestNextElephant != null) {
//            int dElephant = bestNextElephant.getDistanceTo(pathElephant.get(pathElephant.size()-1).ID);
//            nextStopElephant = dElephant;
//            visited.add(bestNextElephant);
//            pathElephant.add(bestNextElephant);
//        }
//
//        for (int i = 1; i<=MAX_T;i++) {
//            if (i == nextStopMe) {
//                bestNextMe = getBestNextStep(pathMe);
//                if (bestNextMe != null) {
//                    int dMe = bestNextMe.getDistanceTo(pathMe.get(pathMe.size()-1).ID);
//                    nextStopMe = i + dMe;
//                    visited.add(bestNextMe);
//                    pathMe.add(bestNextMe);
//                }
//            }
//
//            if (i == nextStopElephant) {
//                bestNextElephant = getBestNextStep(pathElephant);
//                if (bestNextElephant != null) {
//                    int dElephant = bestNextElephant.getDistanceTo(pathElephant.get(pathElephant.size()-1).ID);
//                    nextStopElephant = i + dElephant;
//                    visited.add(bestNextElephant);
//                    pathElephant.add(bestNextElephant);
//                }
//            }
//
////            LOG.debug("t={}, me: {}, elephant: {}", i, pathMe, pathElephant);
//        }
//
//
//
//        return getScoreOfPath(pathMe) + getScoreOfPath(pathElephant);
//        return getScoreOfPath(getMyBestPath()) + getScoreOfPath(getElephantBestPath());
    }

    private boolean areCompatiblePaths(ArrayList<Valve> p1, ArrayList<Valve> p2) {
        ArrayList<Valve> clone1 = new ArrayList<>(p1);
        ArrayList<Valve> clone2 = new ArrayList<>(p2);
        clone1.remove(getValve("AA"));
        clone2.remove(getValve("AA"));

        return clone1.stream().noneMatch(clone2::contains);
    }

    private Set<ArrayList<Valve>> generateAllPath(Valve start) {
        Set<ArrayList<Valve>> paths = new HashSet<>();
        Set<ArrayList<Valve>> checkedPaths = new HashSet<>();
        paths.add(new ArrayList<>(Arrays.asList(start)));
        boolean change = false;
        int count = 0;
        do {
            change = false;
            for (ArrayList<Valve> path : new HashSet<>(paths)) {
                if (checkedPaths.contains(path)) {
                    continue;
                }
                Set<ArrayList<Valve>> increments = addOneTo(path);
                checkedPaths.add(path);
                if (!increments.isEmpty()) {
//                    paths.remove(path);
                    paths.addAll(increments);
                    change = true;
                }
            }
            LOG.debug("{} paths at cycle #{}", paths.size(), ++count);
        }while(change);


        return paths;
    }

    private Set<ArrayList<Valve>> addOneTo(List<Valve> path) {
        Set<ArrayList<Valve>> ret = new HashSet<>();
        for (Valve v : graphWithFlows.vertexSet()) {
            if (path.contains(v)) {
                continue;
            }
            ArrayList<Valve> newPath = new ArrayList<>(path);
            newPath.add(v);
            if (getTimeOf(newPath) <= MAX_T) {
                ret.add(newPath);
            }
        }
        return ret;
    }

    private int getTimeOf(List<Valve> path) {
        int t = 0;
        for (int i = 0; i < path.size()-1; i++) {
            t+= path.get(i).getDistanceTo(path.get(i+1).ID);
            t++;
        }
        return t;
    }

    private void printGraphWithShortestDists() {
        ArrayList<Valve> valvesWithFlow = new ArrayList<>(graphWithFlows.vertexSet());

        System.out.print(getString(""));
        for (int i = 0; i < valvesWithFlow.size(); i++) {
            System.out.print(getString(valvesWithFlow.get(i).ID + "." + valvesWithFlow.get(i).flow));
        }
        System.out.println();

        for (int i = 0; i < valvesWithFlow.size(); i++) {
            System.out.print(getString(valvesWithFlow.get(i).ID + "." + valvesWithFlow.get(i).flow));
            for (int j = 0; j < valvesWithFlow.size(); j++) {
                var e = graphWithFlows.getEdge(valvesWithFlow.get(i), valvesWithFlow.get(j));
                if (i == j) {
                    System.out.print(getString(""));
                } else if (e == null) {
                    System.out.print(getString("(" + valvesWithFlow.get(i).getDistanceTo(valvesWithFlow.get(j).ID) + ")"));
                } else {
                    System.out.print(getString(valvesWithFlow.get(i).getDistanceTo(valvesWithFlow.get(j).ID) + ""));
                }
            }
            System.out.println();
        }
    }

    private String getString(String s) {
        int length = 8;
        return String.format("%" + length + "s", s);
    }

    private List<Valve> getMyBestPath() {
        String[] myValves = {"AA", "TA", "DC", "QK", "JA", "VK", "ID", "DW"};
        return Arrays.stream(myValves).map(this::getValve).collect(Collectors.toList());
    }

    private List<Valve> getElephantBestPath() {
        String[] myValves = {"AA", "DD", "HH", "EE"};
        return Arrays.stream(myValves).map(this::getValve).collect(Collectors.toList());
    }

    private List<Valve> getMyStartingPath() {
        String[] myValves = {"AA", "TA", "DC", "QK"};
        return Arrays.stream(myValves).map(this::getValve).collect(Collectors.toList());
    }

    private List<Valve> getElephantSartingPath() {
        String[] myValves = {"AA", "EX"};
        return Arrays.stream(myValves).map(this::getValve).collect(Collectors.toList());
    }


    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }

}
