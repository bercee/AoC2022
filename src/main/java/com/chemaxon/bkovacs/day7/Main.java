package com.chemaxon.bkovacs.day7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        InputStream is = Main.class.getResourceAsStream("input.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            Filesys filesys = new Filesys();
            Filesys.Node curr = filesys.getRoot();
            while (br.ready()) {
                line = br.readLine();
                String[] dat = line.split("\\s");
                if (dat[0].equals("$")) {
                    if (dat[1].equals("cd")) {
                        if (dat[2].equals("/")) {
                            curr = filesys.getRoot();
                        } else if (dat[2].equals("..")) {
                            curr = filesys.getParent(curr);
                        } else {
//                            System.out.println("entering "+dat[2]);
                            curr = filesys.getChild(curr, dat[2]);
                        }
                    } else if (dat[1].equals("ls")) {
                        continue;
                    }
                } else if (dat[0].equals("dir")) {
//                    System.out.println("creating dir "+dat[1]);
                    curr.addChild(0, dat[1]);
                } else if (dat[0].matches("\\d+")){
//                    System.out.println("creating file "+dat[1]);
                    curr.addChild(Integer.parseInt(dat[0]), dat[1]);
                }
            }

//            List<Filesys.Node> nodes = new ArrayList<>();
//            filesys.getDirs(filesys.getRoot(), d -> d.getSize() == 0 && filesys.getSize(d) <= 100000, nodes);
//            System.out.println(nodes.size());
//            System.out.println(nodes.stream().flatMapToInt(d -> IntStream.of(filesys.getSize(d))).sum());

            System.out.println();
            int minDel = 30000000-(70000000-filesys.getSize(filesys.getRoot()));
            System.out.println(minDel);
            List<Filesys.Node> nodes = new ArrayList<>();
            filesys.getDirs(filesys.getRoot(), d -> filesys.getSize(d) >= minDel, nodes);
            int[] ints = nodes.stream().flatMapToInt(d -> IntStream.of(filesys.getSize(d))).toArray();
            Arrays.sort(ints);
            System.out.println(ints[0]);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
