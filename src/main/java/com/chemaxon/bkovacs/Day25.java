package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.google.common.collect.Lists;
import org.apache.commons.math3.analysis.function.Power;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day25 extends DaySolver {




    public Day25() {
        super(25);
    }

    private static List<Integer> toListRepr(String str) {
        String[] dat = str.split("");
        final ArrayList<Integer> ret = new ArrayList<>();
        Arrays.stream(dat).forEach(s -> {
            ret.add(switch (s) {
                case "-" -> -1;
                case "=" -> -2;
                default -> Integer.parseInt(s);
            });
        });

        return ret;
    }

    public static long toDecimal(String str) {
        long ret = 0;
        int power = 0;
        for (Integer i : Lists.reverse(toListRepr(str))) {
            ret += (long) new Power(power++).value(5)*i;
        }
        return ret;
    }

    private static String toString(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach(i->sb.append(switch (i) {
            case -2->"=";
            case -1->"-";
            default -> String.valueOf(i);
        }));
        return sb.toString();
    }

    public static List<Integer> toFiveSystem(long decimal) {
        return toFiveSystem(decimal, 1, findFirstDigitPosition(decimal));
    }

    private static List<Integer> toFiveSystem(long decimal, int sign, int firstDigitPosition) {
        LOG.debug("first digit pos: {}", firstDigitPosition);
        int firstDigitValue = findFirstDigitValue(decimal, firstDigitPosition);
        List<Integer> list = new ArrayList<>();
        list.add(firstDigitValue*sign);
        long remainder = decimal - (long) new Power(firstDigitPosition-1).value(5)*firstDigitValue;
        if (remainder == 0) {
            for (int i = 1; i < firstDigitPosition; i++) {
                list.add(0);
            }
        }else {
            int nextSign = (int) Math.signum(remainder);
            remainder = Math.abs(remainder);
            int nextFirstDigitPosition = findFirstDigitPosition(remainder);
            for (int i = 1; i < firstDigitPosition - nextFirstDigitPosition; i++) {
                list.add(0);
            }
            list.addAll(toFiveSystem(remainder, nextSign * sign, nextFirstDigitPosition));
        }

        return list;
    }

    private static int findFirstDigitValue(long decimal, int firstDigitPosition) {
        return decimal - new Power(firstDigitPosition-1).value(5) > getMaxWithDigits(firstDigitPosition-1) ? 2 : 1;
    }

    private static int findFirstDigitPosition(long decimal) {
        int pow = 1;
        while (getMaxWithDigits(pow) < decimal) {
            pow++;
        }

        return pow;
    }

    private static long getMaxWithDigits(int digits) {
        return (long) new Power(digits).value(5) / 2;
    }

    private List<String> input;

    @Override
    protected void init(List<String> input) {
        this.input = input;
//        long decimal = 25;
//        LOG.debug(findFirstDigitValue(decimal, findFirstDigitPosition(decimal)));
//        LOG.debug(toString(toFiveSystem(decimal)));
    }

    @Override
    protected Object algorithmPart1() {
        long sum = 0;
        for (String str : input) {
//            LOG.debug("{} --> {}", str, toDecimal(str));
            sum += toDecimal(str);
        }
        LOG.debug("sum = {}", sum);
        return toString(toFiveSystem(sum));
    }

    @Override
    protected Object algorithmPart2() {
        return null;
    }
}
