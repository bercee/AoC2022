package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.analysis.function.Add;
import org.apache.commons.math3.analysis.function.Divide;
import org.apache.commons.math3.analysis.function.Multiply;
import org.apache.commons.math3.analysis.function.Subtract;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day21 extends DaySolver {

    private static final String TEST = "root: pppw + sjmn\n" +
            "dbpl: 5\n" +
            "cczh: sllz + lgvd\n" +
            "zczc: 2\n" +
            "ptdq: humn - dvpt\n" +
            "dvpt: 3\n" +
            "lfqf: 4\n" +
            "humn: 5\n" +
            "ljgn: 2\n" +
            "sjmn: drzm * dbpl\n" +
            "sllz: 4\n" +
            "pppw: cczh / lfqf\n" +
            "lgvd: ljgn * ptdq\n" +
            "drzm: hmdt - zczc\n" +
            "hmdt: 32";

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }


    private static final HashMap<String, BivariateFunction> OPERATIONS = new HashMap<>();


    static {
        OPERATIONS.put("+", new Add());
        OPERATIONS.put("-", new Subtract());
        OPERATIONS.put("*", new Multiply());
        OPERATIONS.put("/", new Divide());
    }

    private static final HashMap<BivariateFunction, BivariateFunction> REVERSE = new HashMap<>();

    static  {
        REVERSE.put(OPERATIONS.get("+"), OPERATIONS.get("-"));
        REVERSE.put(OPERATIONS.get("-"), OPERATIONS.get("+"));
        REVERSE.put(OPERATIONS.get("*"), OPERATIONS.get("/"));
        REVERSE.put(OPERATIONS.get("/"), OPERATIONS.get("*"));
    }

    interface Op {
        int value(int i1, int i2);
    }


    interface Monkey extends Supplier<Long> {

    }

    class MonkeyNum implements Monkey {
        private long num;

        public MonkeyNum(long num) {
            this.num = num;
        }

        @Override
        public Long get() {
            return num;
        }
    }

    class MonkeyOp implements Monkey {
        private final String m1;
        private final String m2;
        private final BivariateFunction op;

        public MonkeyOp(String m1, String op, String m2) {
            this.m1 = m1;
            this.m2 = m2;
            this.op = OPERATIONS.get(op);
        }

        public Long reverseFind1(Long result) {
            if (monkeyResults.get(m2) == null) {
                return null;
            }
            long other = monkeyResults.get(m2);
            return (long) REVERSE.get(op).value(result, other);

        }

        public Long reverseFind2(Long result) {
            if (monkeyResults.get(m1) == null) {
                return null;
            }
            long other = monkeyResults.get(m1);
            if (this.op == OPERATIONS.get("+") || this.op == OPERATIONS.get("*")) {
                return (long) REVERSE.get(op).value(result, other);
            } else  {
                return (long) this.op.value(other, result);
            }
        }

        @Override
        public Long get() {
            if (monkeyResults.get(m1) == null || monkeyResults.get(m2) == null) {
                return null;
            } else {
                return (long) op.value(monkeyResults.get(m1), monkeyResults.get(m2));
            }
//            return (long) op.value(monkeys.get(m1).get(), monkeys.get(m2).get());
        }
    }

    private final HashMap<String, Monkey> monkeys = new HashMap<>();
    private final HashMap<String, Long> monkeyResults = new HashMap<>();



    public Day21() {
        super(21);
    }

    private static final String PATTERN1 = "([a-z]{4}): (\\d+)";
    private static final String PATTERN2 = "([a-z]{4}): ([a-z]{4}) ([\\+\\-\\*\\/]) ([a-z]{4})";

    @Override
    protected void init(List<String> input) {
        LOG.debug("{} lines", input.size());
        Pattern p1 = Pattern.compile(PATTERN1);
        Pattern p2 = Pattern.compile(PATTERN2);

        Matcher m;
        for (String s : input) {
            m = p1.matcher(s);
            if (m.find()) {
                monkeys.put(m.group(1), new MonkeyNum(Integer.parseInt(m.group(2))));
                continue;
            }

            m = p2.matcher(s);
            if (m.find()) {
                monkeys.put(m.group(1), new MonkeyOp(m.group(2), m.group(3), m.group(4)));
                continue;
            }

            throw new RuntimeException("I don't get this line: "+s);
        }

        LOG.debug("{} monkeys", monkeys.size());
    }

    private static final String R = "root";
    private static final String H = "humn";

    @Override
    protected Object algorithmPart1() {
//        while (monkeyResults.get("root") == null) {
//            for (Map.Entry<String, Monkey> e : monkeys.entrySet()) {
//                monkeyResults.put(e.getKey(), e.getValue().get());
//            }
//        }

        return monkeys.get(R).get();
    }

    private long monkeyEntrySetCount() {
        return monkeyResults.entrySet().stream().filter(e -> e.getValue() != null).count();
    }

    @Override
    protected Object algorithmPart2() {

        String m1 = ((MonkeyOp) monkeys.get(R)).m1;
        String m2 = ((MonkeyOp) monkeys.get(R)).m2;

        monkeys.remove(H);
        monkeys.remove(R);

        int cycleCount = 0;
        long resCount = 0;

        do {
            LOG.debug("cycle: {}, results:{}, ", ++cycleCount, resCount);
            resCount = monkeyEntrySetCount();
            for (Map.Entry<String, Monkey> e : monkeys.entrySet()) {
                monkeyResults.put(e.getKey(), e.getValue().get());
            }
        } while (resCount != monkeyEntrySetCount());

        LOG.debug("cycle: {}, results:{}, ", ++cycleCount, resCount);

        LOG.debug("m1: {}, result: {}", m1, monkeyResults.get(m1));
        LOG.debug("m2: {}, result: {}", m2, monkeyResults.get(m2));

        long needToEqual = monkeyResults.get(m1) == null? monkeyResults.get(m2) : monkeyResults.get(m1);
        LOG.debug("need to equal: {}", needToEqual);

        monkeyResults.put(m1, needToEqual);
        monkeyResults.put(m2, needToEqual);

        String lastFound1 = m1;
        String lastFound2 = m2;
//
        LOG.debug("start 1: {}, start 2: {}", lastFound1, lastFound2);

//        LOG.debug("m1: {}, result: {}", m1, monkeyResults.get(m1));
//        LOG.debug("m2: {}, result: {}", m2, monkeyResults.get(m2));


        while (monkeyResults.get(H) == null) {
            lastFound1 = findAnOtherOne(lastFound1);
            lastFound2 = findAnOtherOne(lastFound2);
            if (lastFound1 == null && lastFound2 == null) {
                LOG.debug("I am stuck.");
                break;
            }
            LOG.debug("found 1: {}, found 2: {}", lastFound1, lastFound2);
        }
//
        return monkeyResults.get(H);
    }

    private String findAnOtherOne(String lastFound) {
        if (monkeyResults.get(lastFound) != null) {
            Long res = monkeyResults.get(lastFound);
            if (monkeys.get(lastFound) instanceof MonkeyOp){
                Long m1Res = ((MonkeyOp) monkeys.get(lastFound)).reverseFind1(res);
                Long m2Res = ((MonkeyOp) monkeys.get(lastFound)).reverseFind2(res);
                if (m1Res != null) {
                    LOG.debug("found m1 in {}. {} = {}", lastFound, ((MonkeyOp) monkeys.get(lastFound)).m1, m1Res);
                    lastFound = ((MonkeyOp) monkeys.get(lastFound)).m1;
                    monkeyResults.put(lastFound, m1Res);
                    if (monkeys.get(lastFound) instanceof MonkeyNum) {
                        ((MonkeyNum) monkeys.get(lastFound)).num = m1Res;
                    }
                } else if (m2Res != null) {
                    LOG.debug("found m2 in {}. {} = {}", lastFound, ((MonkeyOp) monkeys.get(lastFound)).m2, m2Res);
                    lastFound = ((MonkeyOp) monkeys.get(lastFound)).m2;
                    monkeyResults.put(lastFound, m2Res);
                    if (monkeys.get(lastFound) instanceof MonkeyNum) {
                        ((MonkeyNum) monkeys.get(lastFound)).num = m2Res;
                    }
                } else {
                    lastFound = null;
                }
            }
        }
        return lastFound;
    }

    private String getTheOneWithNoResults(String m1, String m2) {
        if (monkeyResults.get(m1) == null && monkeyResults.get(m2) != null) {
            return m1;
        } else if (monkeyResults.get(m1) != null && monkeyResults.get(m2) == null) {
            return m2;
        }
        return null;
    }
}
