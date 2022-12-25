package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Day11 extends DaySolver {

    private static final String TEST =
            "Monkey 0:\n" +
            "  Starting items: 79, 98\n" +
            "  Operation: new = old * 19\n" +
            "  Test: divisible by 23\n" +
            "    If true: throw to monkey 2\n" +
            "    If false: throw to monkey 3\n" +
            "\n" +
            "Monkey 1:\n" +
            "  Starting items: 54, 65, 75, 74\n" +
            "  Operation: new = old + 6\n" +
            "  Test: divisible by 19\n" +
            "    If true: throw to monkey 2\n" +
            "    If false: throw to monkey 0\n" +
            "\n" +
            "Monkey 2:\n" +
            "  Starting items: 79, 60, 97\n" +
            "  Operation: new = old * old\n" +
            "  Test: divisible by 13\n" +
            "    If true: throw to monkey 1\n" +
            "    If false: throw to monkey 3\n" +
            "\n" +
            "Monkey 3:\n" +
            "  Starting items: 74\n" +
            "  Operation: new = old + 3\n" +
            "  Test: divisible by 17\n" +
            "    If true: throw to monkey 0\n" +
            "    If false: throw to monkey 1";

    private final Map<Integer, Monkey> monkeys = new HashMap<>();

//    private final MonkeyOperation worryReducer = i -> i / 3;

    private static final int[] DIVISORS = {13, 19, 5, 2, 17, 11, 7, 3, 23};
//    private static final int[] DIVISORS = {23,19,13,17};


    public Day11() {
        super(11);
    }

    @Override
    protected void init(List<String> input) {

        int l = 0;
        while (l < input.size()) {
            int monkeyID = Integer.parseInt(String.valueOf(input.get(l).charAt(7)));
            l++;
            String[] dat = input.get(l).substring(18).split(", ");
            int[] items = Arrays.stream(dat).mapToInt(Integer::parseInt).toArray();
            l++;
            MonkeyOperation operation = readOperation(input.get(l));
            l++;
            int divisor = Integer.parseInt(input.get(l).substring(21));
            l++;
            int ifTrue = Integer.parseInt(input.get(l).substring(29));
            l++;
            int ifFalse = Integer.parseInt(input.get(l).substring(30));
            MonkeyChooser chooser = new MonkeyChooser(divisor, ifTrue, ifFalse);

            monkeys.put(monkeyID, new Monkey(operation, chooser, items));
            l++;
            l++;
        }

        LOG.debug("We have {} monkeys. ",monkeys.size());
    }

    private MonkeyOperation readOperation(String line) {
        MonkeyOperation operation;
        char operator = line.charAt(23);
        String operand = line.substring(25);
        boolean isAddition;
        if (operator == '*') {
            isAddition = false;
        } else {
            isAddition = true;
        }
        if (operand.equals("old")) {
            operation = null;
        }else {
            operation = new MonkeyOperation(isAddition, Integer.parseInt(operand));
        }

        return operation;
    }

    public Object algorithmPart1() {

        for (int i = 0; i < 20; i++) {
            for (Map.Entry<Integer, Monkey> e : monkeys.entrySet()) {
                while(e.getValue().hasNext()) {
                    e.getValue().inspectNext();
                }
            }
        }

        for (Map.Entry<Integer, Monkey> e : monkeys.entrySet()) {
            LOG.debug("monkey {} did {} inspections.", e.getKey(), e.getValue().count);
        }

        ArrayList<Monkey> monkeyList = new ArrayList<>(monkeys.values());
        monkeyList.sort(Comparator.comparingLong(m -> m.count));
        Collections.reverse(monkeyList);

        long result = monkeyList.get(0).count * monkeyList.get(1).count;

        LOG.debug("monkey business: {}", result);

        return result;
    }

    public Object algorithmPart2() {

        for (int i = 0; i < 10000; i++) {
            for (Map.Entry<Integer, Monkey> e : monkeys.entrySet()) {
                while(e.getValue().hasNext()) {
                    e.getValue().inspectNext();
                }
            }
            if ((i+1)%1000 == 0 || i+1 == 20 || i+1 == 1) {
                LOG.debug("round {}" ,i+1);
                for (Map.Entry<Integer, Monkey> ee : monkeys.entrySet()) {
                    LOG.debug("monkey {} did {} inspections.", ee.getKey(), ee.getValue().count);
                }
            }
        }

        LOG.debug("end");

        for (Map.Entry<Integer, Monkey> e : monkeys.entrySet()) {
            LOG.debug("monkey {} did {} inspections.", e.getKey(), e.getValue().count);
        }

        ArrayList<Monkey> monkeyList = new ArrayList<>(monkeys.values());
        monkeyList.sort(Comparator.comparingLong(m -> m.count));
        Collections.reverse(monkeyList);

        long result = (long) monkeyList.get(0).count * monkeyList.get(1).count;

        LOG.debug("monkey business: {}", result);

        return result;

    }

    @Override
    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }

    class Monkey {
        private final Queue<Item> queue = new LinkedList<>();

        private final MonkeyOperation operation;
        private final MonkeyChooser chooser;

        private int count = 0;

        public Monkey(MonkeyOperation operation, MonkeyChooser chooser, int[] items) {
            for (int item : items) {
                queue.add(new Item(item));
            }
            this.operation = operation;
            this.chooser = chooser;
        }

        private void inspectNext() {
            if (!hasNext()) {
                return;
            }

            count++;

            Item item = queue.poll();
            if (operation != null) {
                if (operation.isAddition) {
                    item.add(operation.num);
                } else {
                    item.mult(operation.num);
                }
            } else {
                item.sq();
            }
            int nextMonkey = chooser.test(item);
            monkeys.get(nextMonkey).receiveItem(item);
        }

        private boolean hasNext() {
            return !queue.isEmpty();
        }

        private void receiveItem(Item item) {
            queue.add(item);
        }

    }

    class MonkeyChooser{

        private final int divisor;
        private final int ifTrue;
        private final int ifFalse;

        public MonkeyChooser(int divisor, int ifTrue, int ifFalse) {
            this.divisor = divisor;
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
        }

        public int test(Item item) {
            return item.test(divisor) ? ifTrue : ifFalse;
        }
    }

    class MonkeyOperation {
        boolean isAddition;
        int num;

        public MonkeyOperation(boolean isAddition, int num) {
            this.isAddition = isAddition;
            this.num = num;
        }
    }

    class Item {
        HashMap<Integer, Integer> remainders = new HashMap<>();

        public Item(int startNum) {
            for (int div : DIVISORS) {
                remainders.put(div, startNum % div);
            }
        }

        public void add(int i) {
            for (int div : DIVISORS) {
                remainders.put(div, (remainders.get(div) + i) % div);
            }
        }

        public void mult(int i) {
            for (int div : DIVISORS) {
                remainders.put(div, (remainders.get(div) * i) % div);
            }
        }

        public void sq() {
            for (int div : DIVISORS) {
                remainders.put(div, (remainders.get(div)*remainders.get(div)) % div);
            }
        }

        public boolean test(int divisor) {
            return Integer.valueOf(0).equals(remainders.get(divisor));
        }
    }
}

