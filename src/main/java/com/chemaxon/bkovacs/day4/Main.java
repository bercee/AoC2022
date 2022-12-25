package com.chemaxon.bkovacs.day4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        InputStream is = com.chemaxon.bkovacs.day4.Main.class.getResourceAsStream("input.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int c = 0;
            while (br.ready()) {
                line = br.readLine();
                String[] rs = line.split("[,-]");
                int[] ints = Arrays.stream(rs).mapToInt(Integer::parseInt).toArray();
//                if (ints[0] >= ints[2] && ints[1] <= ints[3])
//                    c++;
//                else if (ints[0] <= ints[2] && ints[1] >= ints[3])
//                    c++;
                HashSet<Integer> set = new HashSet<>();
                for (int i = ints[0]; i <= ints[1]; i++) {
                    set.add(i);
                }
                for (int i = ints[2]; i <= ints[3]; i++) {
                    if (set.contains(i)) {
                        c++;
                        break;
                    }
                }

                }
            System.out.println(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
