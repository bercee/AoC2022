package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.math3.util.IntegerSequence;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day20 extends DaySolver {

    private static final String TEST = "1\n" +
            "2\n" +
            "-3\n" +
            "3\n" +
            "-2\n" +
            "0\n" +
            "4";


    private class Item {
        public long v;

        public Item(long v) {
            this.v = v;
        }
    }

    BiMap<Long, Item> map = HashBiMap.create();

    private long size;

    private final List<Item> itemList = new ArrayList<>();



    public Day20() {
        super(20);
    }

    @Override
    protected void init(List<String> input) {
        for (int i = 0; i < input.size(); i++) {
            map.put((long) i, new Item(Long.parseLong(input.get(i))));
        }
        size = map.size();

        for (long i = 0; i < size; i++) {
            itemList.add(map.get(i));
        }
    }

    private long normalize(long index) {
        while (index < 0) {
            index += size;
        }
        if (index >= size) {
            return index % size;
        } else {
            return index;
        }
    }

    private void move(Item item) {
//        LOG.debug("moving item {}", item.v);
        long oldIndex = map.inverse().get(item);
//
        long moveBy = normalizeMove(item);
        long newIndex = oldIndex + moveBy;
        if (newIndex < 0) {
            newIndex += (size-1);
        }
        if (newIndex >= size) {
            newIndex-= (size-1);
        }




//        moveBy =  item.v + numberOfFlipsDuringMove(item, item.v) * (long)Math.signum(item.v);

//        long newIndex = normalize(oldIndex + moveBy);

        long sign = (long) Math.signum(newIndex-oldIndex);

        for (long i = oldIndex; i != newIndex; i += sign) {
            map.forcePut(i, map.get(i+sign));
        }
        map.put(newIndex, item);
    }

    private long normalizeMove(Item item) {
        int sign = (int) Math.signum(item.v);
        long abs = Math.abs(item.v);

        abs = abs % (size-1);
        return sign * abs;
    }

    private long numberOfFlipsDuringMove(Item item, long moveBy) {
        long ind = map.inverse().get(item);
        long target = ind + moveBy;

        if (target < 0) {
            long plus = ((Math.abs(target) / size) + 1)*size;
            target += plus;
            ind += plus;
        }

        long d = Math.abs(ind-target);
        long ret = d / size;
        if (d % size + Math.min(ind, target) > size) {
            ret++;
        }

        return ret;

    }

    private void moveAll() {

        for (Item item : itemList) {
            move(item);
        }
    }

    private Item getItem(long value) {
        return map.values().stream().filter(i -> i.v ==0).findFirst().orElse(null);
    }

    private long getScore() {

        long inds[] = {1000, 2000, 3000};

        long ind0 = map.inverse().get(getItem(0));

        long score = 0;
        for (long ind : inds) {
            LOG.debug("coord {} is {}", ind, map.get(normalize(ind0+ind)).v);
            score += map.get(normalize(ind0+ind)).v;
        }

        return score;
    }

    @Override
    protected Object algorithmPart1() {

        moveAll();

        return getScore();
    }

    @Override
    protected Object algorithmPart2() {

        long mult = 811589153;
        map.values().forEach(i -> i.v*=mult);
        printInput();

        for (int i = 0; i < 10; i++) {
            moveAll();
            printInput();
        }

        return getScore();
    }

    private void printInput() {
        StringBuilder sb = new StringBuilder();

        for (long i = 0; i < size; i++) {
            sb.append(map.get(i).v).append(", ");
        }

        LOG.debug(sb.toString());

    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }
}
