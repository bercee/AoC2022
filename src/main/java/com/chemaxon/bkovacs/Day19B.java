package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day19B extends DaySolver {

    private static final String TEST = "Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.\n" +
            "Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.";

    private static final String PATTERN = "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.";
    private static final int MAX_T = 24;



    List<int[][]> blueprints = new ArrayList<>();

    public Day19B() {
        super(19);
    }

    @Override
    protected void init(List<String> input) {
        Pattern pattern = Pattern.compile(PATTERN);
        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                int[][] b = new int[4][3];
                b[0][0] = Integer.parseInt(matcher.group(2));
                b[1][0] = Integer.parseInt(matcher.group(3));
                b[2][0] = Integer.parseInt(matcher.group(4));
                b[2][1] = Integer.parseInt(matcher.group(5));
                b[3][0] = Integer.parseInt(matcher.group(6));
                b[3][2] = Integer.parseInt(matcher.group(7));
                blueprints.add(b);
            } else {
                throw new RuntimeException("no match found");
            }
        }

        LOG.debug("We have {} blueprints.", blueprints.size());
    }

    private boolean canBuy(int robotID, int[] resources, int[][] blueprint) {
        if (robotID == -1) {
            return true;
        }

        int[] cost = blueprint[robotID];
        return cost[0] <= resources[0] && cost[1] <= resources[1] && cost[2] <= resources[2];
    }

    private void buy(int robotID, int[] resources, int[] robots, int[][] blueprint){
        if (robotID == -1) {
            return;
        }

        int[] cost = blueprint[robotID];
        robots[robotID]++;
        for (int i = 0; i < 3; i++) {
            resources[i] -= cost[i];
        }
    }

    private void mine(int[] resources, int[] robots) {
        for (int i = 0; i < 4; i++) {
            resources[i] += robots[i];
        }
    }

    private List<Integer> getPossibilities(int[] resources, int[][] blueprint) {
        List<Integer> ret = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (canBuy(i, resources, blueprint)) {
                ret.add(i);
            }
        }

        return ret;
    }

    private int getBestScore(int[][] blueprint) {

        List<int[][]> statuses = new ArrayList<>();

        int[] resStart = new int[4];
        int[] robotStart = {1,0,0,0};
        int[][] start = {resStart, robotStart};
        statuses.add(start);

        List<Integer> poss;

        boolean obsCleansingDone = false;
        boolean obsBought = false;
        int waitObs = 0;

        boolean geodCleansingDone = false;
        boolean geodBought = false;
        int waitGeod = 0;


        for (int t = 0; t < MAX_T; t++) {
            int size = statuses.size();
            for (int i = 0; i < size; i++) {
                int[][] orig = statuses.get(i);
                poss = getPossibilities(orig[0], blueprint);
                if (!obsCleansingDone && poss.contains(2)) {
                    obsBought = true;
                }

                if (!geodCleansingDone && poss.contains(3)) {
                    geodBought = true;
                }

                for (Integer possRob : poss) {
                    int[] newRes = orig[0].clone();
                    int[] newRob = orig[1].clone();
                    mine(newRes, newRob);
                    buy(possRob, newRes, newRob, blueprint);
                    int[][] newStatus = {newRes, newRob};
                    statuses.add(newStatus);
                }
                mine(orig[0], orig[1]);


            }

            if (!obsCleansingDone && obsBought) {
                if (++waitObs >= 1) {
                    obsCleansingDone = true;
                    LOG.debug("eliminating options without obs...");
                    List<int[][]> remaining = statuses.stream().filter(s -> s[1][2] > 0).collect(Collectors.toList());
                    statuses.clear();
                    statuses.addAll(remaining);
                }
            }

            if (!geodCleansingDone && geodBought) {
                if (++waitGeod >= 1) {
                    geodCleansingDone = true;
                    LOG.debug("eliminating options without geod...");
                    List<int[][]> remaining = statuses.stream().filter(s -> s[1][3] > 0).collect(Collectors.toList());
                    statuses.clear();
                    statuses.addAll(remaining);
                }
            }



            LOG.debug("t = {}; possibilities: {}", t+1, statuses.size());

//            long countWithoutObsRobot = statuses.stream().filter(s -> s[1][2] == 0).count();
//            LOG.debug("t = {}; possibilities: {}, without obsidian robot: {} ({}%)", t+1, statuses.size(), countWithoutObsRobot, (double)countWithoutObsRobot/statuses.size()*100);

        }

        int max = findMaxGeode(statuses);
        LOG.debug("max geode: {}", max);

        return max;
    }

    private void reduceTo(List<int[][]> statuses, int max, int[][] blueprint) {
        statuses.sort((s1, s2) -> Integer.compare(computeScore(s1, blueprint), computeScore(s2, blueprint)));
        Lists.reverse(statuses);


        statuses.subList(max, statuses.size()).clear();
    }

    private int findMaxGeode(List<int[][]> statuses) {
        int max = 0;
        for (int[][] status : statuses) {
            if (status[0][3] > max) {
                max = status[0][3];
            }
        }
        return max;
    }

    private int computeScore(int[][] status, int[][] blueprint) {
        int score = 0;

        for (int i = 0; i < 4; i++) {
            score += status[1][i] * blueprint[i][0];
        }

        score += (((status[1][3]*blueprint[3][2]) + status[1][2]*blueprint[2][1]) + status[1][1])*blueprint[1][0];

        return score;
    }

    @Override
    protected Object algorithmPart1() {
        int c = 0;
        for (int i = 0; i < blueprints.size(); i++) {
            LOG.debug("blueprint {}", i+1);
            c += (i+1)*getBestScore(blueprints.get(i));
        }
        return c;
    }

    @Override
    protected Object algorithmPart2() {
        return null;
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }
}
