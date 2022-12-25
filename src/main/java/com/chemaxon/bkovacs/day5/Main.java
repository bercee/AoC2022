package com.chemaxon.bkovacs.day5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {

//        [J]         [B]     [T]
//        [M] [L]     [Q] [L] [R]
//        [G] [Q]     [W] [S] [B] [L]
//[D]     [D] [T]     [M] [G] [V] [P]
//[T]     [N] [N] [N] [D] [J] [G] [N]
//[W] [H] [H] [S] [C] [N] [R] [W] [D]
//[N] [P] [P] [W] [H] [H] [B] [N] [G]
//[L] [C] [W] [C] [P] [T] [M] [Z] [W]
// 1   2   3   4   5   6   7   8   9


    public static void main(String[] args) {
        InputStream is = com.chemaxon.bkovacs.day5.Main.class.getResourceAsStream("input.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            int size = 9;
            Stacks stacks = new Stacks(size);
            String line = br.readLine();

            while (line.charAt(1) != '1') {
//                System.out.println(line);

                for (int i = 0; i < size; i++) {
                    char c = line.charAt(1 + i*4);
                    if (c != ' ') {
                        stacks.load(String.valueOf(c), i);
//                        System.out.printf("loading %s to %d\n", String.valueOf(c), i);
                    }
                }
                line = br.readLine();
            }
            stacks.finishLoading();

            br.readLine();

            String[] data;
            while(br.ready()) {
                line = br.readLine();
                data = line.split("\\s+");
                int s = Integer.parseInt(data[1]);
                int from = Integer.parseInt(data[3]);
                int to = Integer.parseInt(data[5]);
                stacks.move(s, from-1, to-1);
            }

            System.out.println(stacks.getTops());



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
