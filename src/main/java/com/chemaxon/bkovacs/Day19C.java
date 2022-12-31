package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day19C extends DaySolver {

    private static final String TEST = "Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.\n" +
            "Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.";

    private static final String PATTERN = "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.";
    private static final int MAX_T = 32;


    List<int[][]> blueprints = new ArrayList<>();
    Map<Integer, Integer> blueprintMaxs = new HashMap<>();

    class Status {
        int[] rob = {1, 0, 0, 0};
        int[] res = {0, 0, 0, 0};

        int choice;


        @Override
        protected Status clone() {
            Status ret = new Status();
            ret.rob = this.rob.clone();
            ret.res = this.res.clone();
            return ret;
        }
    }

    public Day19C() {
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

    private void buy(int robotID, int[] resources, int[] robots, int[][] blueprint) {
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
        for (int i = 3; i >= -1; i--) {
            if (canBuy(i, resources, blueprint)) {
                ret.add(i);
            }
        }

        return ret;
    }

//    private void findMaxGeod(Status status, int blueprintID) {
//        int[][] blueprint = blueprints.get(blueprintID-1);
//        if (status.length() == MAX_T) {
//            int max = status.res[3];
//            if (max > blueprintMaxs.getOrDefault(blueprintID, 0)) {
//                blueprintMaxs.put(blueprintID, max);
//            }
//        } else {
//            List<Integer> poss = getPossibilities(status.res, blueprint);
//            poss.add(-1);
//            mine(status.res, status.rob);
//            for (Integer nextRob : poss) {
//
//            }
//        }
//    }

    private int c = 0;
    private Map<Integer, List<Integer>> maxSequence = new HashMap<>();

    private void dfs(Stack<Status> statuses, int blueprintID) {
        c++;
        int t = statuses.size();
        int[][] blueprint = blueprints.get(blueprintID - 1);

        if (t == MAX_T) {
            mine(statuses.peek().res, statuses.peek().rob);
            int max = statuses.peek().res[3];
            if (max > blueprintMaxs.getOrDefault(blueprintID, 0)) {
                blueprintMaxs.put(blueprintID, max);
//                LOG.debug("max: {}",max);
//                LOG.debug("{} --> {}", statuses.stream().map(s -> s.choice).collect(Collectors.toList()), statuses.peek().res);
            }
        } else {
            List<Integer> poss = getPossibilities(statuses.peek().res, blueprint);
            filterPoss(poss, statuses, blueprint);
            for (Integer i : poss) {
                Status newStatus = statuses.peek().clone();
                mine(newStatus.res, newStatus.rob);
                statuses.peek().choice = i;
                buy(i, newStatus.res, newStatus.rob, blueprint);
                statuses.push(newStatus);
                dfs(statuses, blueprintID);
                statuses.pop();
            }
        }
    }

    private void filterPoss(List<Integer> poss, Stack<Status> statuses, int[][] blueprint) {
        List<Integer> remaining = new ArrayList<>();
        int t = statuses.size();
        for (int i : poss) {
            if (i == -1) {
                remaining.add(i);
                continue;
            }

            if (i == 3) {
                remaining.add(i);
                continue;
            }

            if (i == 2 && t >= MAX_T - 1) {
                continue;
            }

            if (statuses.peek().rob[i] >= maxCostOf(i, blueprint)) {
                continue;
            }
            if (statuses.size() > 1) {
                Status prev = statuses.get(statuses.size() - 2);
                if (prev.choice == -1 && canBuy(i, prev.res, blueprint)) {
                    continue;
                }
            }
            remaining.add(i);
        }
        poss.clear();
        poss.addAll(remaining);
    }

    private int maxCostOf(int i, int[][] blueprint) {
        int max = 0;
        for (int[] r : blueprint) {
            if (r[i] > max) {
                max = r[i];
            }
        }
        return max;
    }


    @Override
    protected Object algorithmPart1() {
        for (int i = 0; i < blueprints.size(); i++) {
            Stack<Status> statuses = new Stack<>();
            statuses.push(new Status());
            dfs(statuses, i+1);
//        LOG.debug("all paths: {}", c);
        }
        return blueprintMaxs.entrySet().stream().mapToInt(e -> e.getValue()*e.getKey()).sum();
    }

    @Override
    protected Object algorithmPart2() {
        long sum = 1;
        for (int i = 0; i < Math.min(blueprints.size(), 3); i++) {
            LOG.debug("blueprint ID: {}", i+1);
            Stack<Status> statuses = new Stack<>();
            statuses.push(new Status());
            dfs(statuses, i+1);
            LOG.debug("all paths: {}", c);
            LOG.debug("MAX: {}", blueprintMaxs.get(i+1));
            sum*=blueprintMaxs.get(i+1);
        }


        return sum;
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }
}
