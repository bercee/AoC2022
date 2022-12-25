package com.chemaxon.bkovacs.day5;

import java.util.Collections;
import java.util.Stack;

public class Stacks {
    private final Stack<String>[] stacks;

    public Stacks(int num) {
        stacks = new Stack[num];
        for (int i = 0; i < num; i++) {
            stacks[i] = new Stack<>();
        }
    }

    public void load(String str, int stack) {
        stacks[stack].add(str);
    }

    public void finishLoading() {
        for (int i = 0; i < stacks.length; i++) {
            Collections.reverse(stacks[i]);
        }
    }

    public void move(int num, int from, int to) {
        Stack<String> tmp = new Stack<>();

        for (int i = 0; i < num; i++) {
            tmp.push(stacks[from].pop());
        }
        while (!tmp.isEmpty()) {
            stacks[to].push(tmp.pop());
        }
    }

    public String getTops() {
        String res = "";
        for (int i = 0; i < stacks.length; i++) {
            res += stacks[i].peek();
        }
        return res;
    }

    public void printStacks() {
        for (int i = 0; i < stacks.length; i++) {
            System.out.println(stacks[i]);
        }
    }
}
