package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.google.common.collect.Lists;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import javax.sound.midi.Sequence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day16B extends DaySolver {

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

    private HashMap<String, Integer> valvesWithPress = new HashMap<>();

    private HashMap<String, GraphPath<String, DefaultEdge>> paths = new HashMap<>();

    private static int MAX_T = 30;

    public Day16B() {
        super(16);
    }

    @Override
    protected void init(List<String> input) {

        Graph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);


        Pattern pattern = Pattern.compile(PATTERN);
        for (String s : input) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                String valve = matcher.group(1);
                int flow = Integer.parseInt(matcher.group(2));
                graph.addVertex(valve);
                if (flow > 0) {
                    valvesWithPress.put(valve, flow);
                }
            }
        }

        valvesWithPress.put("AA", 0);

        for (String s : input) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                String valve = matcher.group(1);
                String[] neighbors = matcher.group(3).split(", ");
                for (String n : neighbors) {
                    graph.addEdge(valve, n);
                }
            }
        }

        LOG.debug("orig graph with {} valves and {} tunnels", graph.vertexSet().size(), graph.edgeSet().size());

        DijkstraShortestPath<String, DefaultEdge> dijkstraAlg =
                new DijkstraShortestPath<>(graph);

        for (String v1 : valvesWithPress.keySet()) {
            for (String v2 : valvesWithPress.keySet()) {
                if (v1.equals(v2)) {
                    continue;
                }
                GraphPath<String , DefaultEdge> path = dijkstraAlg.getPath(v1, v2);

                paths.put(v1+v2, path);
            }
        }

        LOG.debug("{} shortest paths", paths.size());
    }

    private int getDist(String v1, String v2) {
        return paths.get(v1+v2).getLength();
    }

    private static String[] getValves(String sequence) {
        String[] valves = new String[sequence.length()/2];
        for (int i = 0; i < sequence.length()-1; i+=2) {
            valves[i/2] = sequence.substring(i, i+2);
        }
        return valves;
    }

    private int getTime(String[] sequence) {

        int time = 0;
        for (int i = 0; i < sequence.length-1; i++) {
            time += getDist(sequence[i], sequence[i+1]);
            time++;
        }
        return time;
    }



    private int getScore(String[] sequence) {
        int count = 0;
        int time = 0;
        for (int i = 0; i < sequence.length-1 ; i++) {
            time += getDist(sequence[i], sequence[i+1]);
            if (time >= MAX_T) {
                break;
            }
            int flow = valvesWithPress.getOrDefault(sequence[i+1], 0);
            if (flow > 0) {
                time++;
                count += (MAX_T - time) * flow;
            }
        }

        return count;
    }


    private List<String[]> findAllNext(String[] seq) {
        Set<String> visited = Set.of(seq);
        Set<String> remaining = valvesWithPress.keySet().stream().filter(v -> !visited.contains(v)).collect(Collectors.toSet());

        List<String[]> ret = new ArrayList<>();

        for (String nextV : remaining) {
            String[] nextSeq = Arrays.copyOf(seq, seq.length+1);
            nextSeq[seq.length] = nextV;
            if (getTime(nextSeq) <= MAX_T) {
                ret.add(nextSeq);
            }
        }

        return ret;
    }

    private void walk(String seq) {
        String[] valves = getValves(seq);
        List<String[]> nexts = findAllNext(valves);
        for (String[] next : nexts) {
            String concat = concat(next);
            allScores.put(concat, getScore(next));
            walk(concat);
        }
    }

    private HashMap<String, Integer> allScores = new HashMap();

    private static String concat(String[] seq) {
        StringBuilder ret = new StringBuilder();
        for (String s : seq) {
            ret.append(s);
        }
        return ret.toString();
    }

    @Override
    protected Object algorithmPart1() {
        walk("AA");
        LOG.debug("there are {} paths", allScores.size());
        List<String> allSeq = new ArrayList<>(allScores.keySet());
        allSeq.sort(Comparator.comparingInt(allScores::get).reversed());
        return allScores.get(allSeq.get(0));
    }

    @Override
    protected Object algorithmPart2() {
        MAX_T = 26;
        walk("AA");



        List<String> allSeq = new ArrayList<>(allScores.keySet());
        allSeq.sort(Comparator.comparingInt(allScores::get).reversed());
        LOG.debug("there are {} paths", allScores.size());
        LOG.debug("max: {}", allScores.get(allSeq.get(0)));

        int maxPairScore = 0;
        for (int i = 0; i < allSeq.size(); i++) {
            String[] seq1 = getValves(allSeq.get(i));
            Set<String> valves1 = Set.of(seq1);
            int score1 = allScores.get(allSeq.get(i));
            if (score1 < maxPairScore/2) {
                break;
            }
            for (int j = i+1; j < allSeq.size(); j++) {
                String[] seq2 = getValves(allSeq.get(j));
                Set<String> valves2 = Set.of(seq2);
                if (valves2.stream().anyMatch(s -> !s.equals("AA") && valves1.contains(s))) {
                    continue;
                }

                int sumScore = score1 + allScores.get(allSeq.get(j));;
                if (sumScore > maxPairScore) {
                    maxPairScore = sumScore;
                }
            }
        }


        return maxPairScore;
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }
}
