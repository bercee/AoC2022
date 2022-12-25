package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day19 extends DaySolver {

    private static final String TEST = "Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.\n" +
            "Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.";

    private static final String PATTERN = "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.";


    class Blueprint {
        public final int ID;
        public final int oreROreCost;

        public final int clayROreCost;
        public final int obsROreCost;
        public final int obsRClayCost;
        public final int geoROreCost;
        public final int geoRObsCost;

        public final int[][] costs = new int[4][];




        public Blueprint(int ID, int oreROreCost, int clayROreCost, int obsROreCost, int obsRClayCost, int geoROreCost, int geoRObsCost) {
            this.ID = ID;
            this.oreROreCost = oreROreCost;
            this.clayROreCost = clayROreCost;
            this.obsROreCost = obsROreCost;
            this.obsRClayCost = obsRClayCost;
            this.geoROreCost = geoROreCost;
            this.geoRObsCost = geoRObsCost;

            costs[0] = new int[1];
            costs[1] = new int[1];
            costs[2] = new int[2];
            costs[3] = new int[2];

            costs[0][0] = oreROreCost;
            costs[1][0] = clayROreCost;
            costs[2][0] = obsROreCost;
            costs[2][1] = obsRClayCost;
            costs[3][0] = geoROreCost;
            costs[3][1] = geoRObsCost;



        }
    }

    private final static int[][] costIndexes = {{0},{0},{0,1},{0,2}};

    private static final int MAX_T = 24;

    private static int oreInd = 0;
    private static int clayInd = 1;
    private static int obsInd = 2;
    private static int geodeInd = 3;

    private static int oreRInd = 0;
    private static int clayRInd = 1;
    private static int obsRInd = 2;
    private static int geoRInd = 3;

    class Resources {

        public int[] res = {0,0,0,0};

        public int[] robots = {1,0,0,0};

        @Override
        protected Resources clone()  {
            Resources c = new Resources();
            c.res = Arrays.copyOf(this.res, 4);
            c.robots = Arrays.copyOf(this.robots, 4);
            return c;
        }
    }

    private final ArrayList<Blueprint> blueprints = new ArrayList<>();
    public Day19() {
        super(19);
    }

    @Override
    protected void init(List<String> input) {
            Pattern pattern = Pattern.compile(PATTERN);
            for (String line : input) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    blueprints.add(new Blueprint(
                            Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(2)),
                            Integer.parseInt(matcher.group(3)),
                            Integer.parseInt(matcher.group(4)),
                            Integer.parseInt(matcher.group(5)),
                            Integer.parseInt(matcher.group(6)),
                            Integer.parseInt(matcher.group(7))
                            ));
                } else {
                    throw new RuntimeException("no match found");
                }
            }

            LOG.debug("We have {} blueprints.", blueprints.size());

        }

    private ArrayList<ArrayList<Integer>> generateSequencesFor(ArrayList<Integer> startSq, Blueprint b) {
        ArrayList<ArrayList<Integer>> lists = new ArrayList<>();

        lists.add(startSq);

        ArrayList<Integer> newList;
        for (int t = startSq.size(); t < MAX_T; t++) {
            for (ArrayList<Integer> list : new ArrayList<>(lists)) {
                newList = new ArrayList<>(list);
                newList.add(-1);
                lists.add(newList);
                for (Integer next : getWhatWeCanBuy(list, b)) {
                    newList = new ArrayList<>(list);
                    newList.add(next);
                    lists.add(newList);
                }

            }
            int finalT = t;
            lists.removeIf(l -> l.size() == finalT);

        }
        return lists;
    }

    private ArrayList<ArrayList<Integer>> getBestStartingSequences(Blueprint b) {
        ArrayList<ArrayList<Integer>> lists = new ArrayList<>();
        ArrayList<ArrayList<Integer>> ret;

        ArrayList<Integer> start = new ArrayList<>();
        lists.add(start);

        ArrayList<Integer> newList;
        for (int t = 0; t < MAX_T; t++) {
            LOG.debug("t={}", t);
            for (ArrayList<Integer> list : new ArrayList<>(lists)) {
                newList = new ArrayList<>(list);
                newList.add(-1);
                lists.add(newList);
                for (Integer next : getWhatWeCanBuy(list, b)) {
                    newList = new ArrayList<>(list);
                    newList.add(next);
                    lists.add(newList);
                }

            }
            int finalT = t;
            lists.removeIf(l -> l.size() == finalT);

            if (t == 16) {
                LOG.debug("for e.q: {}", lists.get(lists.size()-1));
            }

            ret = lists.stream().filter(l -> getResourceFor(l, b).res[3] != 0).collect(Collectors.toCollection(ArrayList::new));
            if (ret.size() > 0) {
                return ret;
            }
        }
        return null;

    }

    private ArrayList<Integer> getWhatWeCanBuy(ArrayList<Integer> sequence, Blueprint b) {
        Resources r = getResourceFor(sequence, b);

        ArrayList<Integer> ret = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            if (canBuyRobot(i, b, r)) {
                ret.add(i);
            }
        }

        return ret;
    }

    private Resources getResourceFor(List<Integer> sequence, Blueprint b) {
        Resources r = new Resources();
        for (Integer i : sequence) {
            mine(r);
            if (i >= 0) {
                buyRobot(i, b, r, false);
            }
        }
        return r;
    }



    @Override
    protected Object algorithmPart1() {

        int[] testSq = {-1, -1, -1, 1, -1, -1, 1, -1, -1, -1, 2, -1};
        ArrayList<Integer> test = new ArrayList<>();
        for (int i : testSq) {
            test.add(i);
        }

        LOG.debug("obs: {}, test sq for blueprint 2: {}", getResourceFor(test, blueprints.get(1)).res[3], test);


        ArrayList<ArrayList<Integer>> goodStartingSeqs = getBestStartingSequences(blueprints.get(1));
        LOG.debug("find {} sequences, t = {}", goodStartingSeqs.size(), goodStartingSeqs.get(0).size());

        ArrayList<ArrayList<Integer>> goodCandidates = new ArrayList<>();

        for (ArrayList<Integer> seqs : goodStartingSeqs) {
            goodCandidates.addAll(generateSequencesFor(seqs, blueprints.get(1)));
            LOG.debug("all squences: {}", goodCandidates.size());
        }

        ArrayList<Integer> scores = new ArrayList<>();
        for (ArrayList<Integer> seq : goodCandidates) {
            scores.add(getResourceFor(seq, blueprints.get(1)).res[3]);
        }

        scores.sort(Integer::compareTo);
        Collections.reverse(scores);
        LOG.debug("best score: {}", scores.get(0));



        return null;

//        HashMap<Integer, Integer> blueprintQuality = new HashMap<>();
//
//        for (Blueprint b : blueprints) {
//            Resources r = new Resources();
//            for (int t = 1; t <= MAX_T; t++) {
//                LOG.debug("t = {}", t);
//                nextStep(b, r);
//            }
//            blueprintQuality.put(b.ID, r.res[3]);
//        }
//
//
//        int sum = 0;
//        for (Map.Entry<Integer, Integer> e  :blueprintQuality.entrySet()) {
//            sum += e.getKey()*e.getValue();
//        }
//        return sum;
    }

    private void nextStep(Blueprint b, Resources r) {
        int bestRobot = bestRobotToBuy(b, r);
        mine(r);
        if (bestRobot != -1) {
            buyRobot(bestRobot, b, r);
        }
        LOG.debug("resources: {} ore, {} clay, {} obs, {} geod", r.res[0], r.res[1], r.res[2], r.res[3]);
        LOG.debug("robots   : {} ore, {} clay, {} obs, {} geod", r.robots[0], r.robots[1], r.robots[2], r.robots[3]);
    }

    private void mine(Resources r) {
        for (int i = 0; i < r.res.length; i++) {
            r.res[i] += r.robots[i];
        }
    }

    private int bestRobotToBuy(Blueprint b, Resources r) {
        int ret = -1;
        for (int robot = 3; robot >= 0; robot--) {
            if (isGoodIdeaAndCanBuy(robot, b, r)) {
                ret = robot;
                break;
            }
        }
        return ret;
    }

    private boolean isGoodIdeaAndCanBuy(int robot, Blueprint b, Resources r) {
        Resources rClone = r.clone();
        if (!canBuyRobot(robot, b, r)) {
            return false;
        }

        if (robot == 3) {
            return true;
        }

        int r1No = daysMissingToBuyRobot(robot+1, b, rClone);
        buyRobot(robot, b, rClone, false);
        int r1Yes = daysMissingToBuyRobot(robot+1, b, rClone);

        rClone = r.clone();
        int r2No = daysMissingToBuyRobot(robot+2, b, rClone);
        int r2Yes;
        if (canBuyRobot(robot+1, b, rClone)){
            buyRobot(robot+1, b, rClone, false);
            r2Yes = daysMissingToBuyRobot(robot+2, b, rClone);
        } else {
            r2Yes = Integer.MAX_VALUE;
        }

        rClone = r.clone();
        int r3No = daysMissingToBuyRobot(robot+3, b, rClone);
        int r3Yes;
        if (canBuyRobot(robot+2, b, rClone)){
            buyRobot(robot+2, b, rClone, false);
            r3Yes = daysMissingToBuyRobot(robot+3, b, rClone);
        } else {
            r3Yes = Integer.MAX_VALUE;
        }

        if (r3Yes < r3No) {
            return true;
        }

        if (r2Yes < r2No) {
            return true;
        }

        if (r1Yes < r1No || r1No == Integer.MAX_VALUE) {
            return true;
        }

        return false;
    }

    private int daysMissingToBuyRobot(int robot, Blueprint b, Resources rClone) {
        if (robot > 3) {
            return -1;
        }
        int daysLeftMax = 0;
        int[] costInds = costIndexes[robot];
        for (int i = 0; i < costInds.length; i++) {
            int missing = (b.costs[robot][i] - rClone.res[costInds[i]]);
            int robotCount = rClone.robots[costInds[i]];
            if (missing <= 0) {
                continue;
            }

            int daysLeft;

            if (robotCount == 0) {
                daysLeft = Integer.MAX_VALUE;
            }else {
                daysLeft = (missing/robotCount);
                if (missing % robotCount != 0) {
                    daysLeft++;
                }
            }

            if (daysLeft > daysLeftMax) {
                daysLeftMax = daysLeft;
            }

        }
        return daysLeftMax;
    }


    private boolean canBuyRobot(int robot, Blueprint b, Resources r) {
        if (robot > 3) {
            return false;
        }
        int[] costInds = costIndexes[robot];
        boolean canBuy = true;

        for (int i = 0; i < costInds.length; i++) {
            if (r.res[costInds[i]] < b.costs[robot][i]) {
                canBuy = false;
                break;
            }
        }
        return canBuy;
    }

    private void buyRobot(int robot, Blueprint b, Resources r) {
        buyRobot(robot, b, r, true);
    }

    private void buyRobot(int robot, Blueprint b, Resources r, boolean log) {
        int[] costInds = costIndexes[robot];

        for (int i = 0; i < costInds.length; i++) {
            if (r.res[costInds[i]] >= b.costs[robot][i]) {
                r.res[costInds[i]] -= b.costs[robot][i];
            } else {
                throw new RuntimeException("cannot buy this robot");
            }
        }
        r.robots[robot]++;
        if (log) {
            LOG.debug("buying robot {}", robot);
        }
    }


    @Override
    protected Object algorithmPart2() {
        return null;
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }
}
