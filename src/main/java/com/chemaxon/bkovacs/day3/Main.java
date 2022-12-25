package com.chemaxon.bkovacs.day3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
//        char a = 'a';
//        char z = 'z';
//        char A = 'A';
//        char Z = 'Z';
//
//        System.out.println(((int) a) +" --> " + ((int) a-96));
//        System.out.println(((int) z) +" --> " + ((int) z-96));
//        System.out.println(((int) A) +" --> " + ((int) A-65+27));
//        System.out.println(((int) Z) +" --> " + ((int) Z-65+27));
//        System.out.println((int) z-96);
//        System.out.println((int) A-65+27);
//        System.out.println((int) Z-65+27);


        InputStream is = com.chemaxon.bkovacs.day3.Main.class.getResourceAsStream("input.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            int score = 0;
            while (br.ready()) {
                String l1 = br.readLine();
                String l2 = br.readLine();
                String l3 = br.readLine();
                for (char c : l1.toCharArray()) {
                    if (l2.contains(String.valueOf(c)) && l3.contains(String.valueOf(c))) {
                        if (c >= 97) {
                            score += c - 96;
                        } else {
                            score += c - 65 + 27;
                        }
                        break;
                    }
                }
            }

            System.out.println(score);

//            int n = 0;
//            int score = 0;
//            while (br.ready()) {
//                n++;
////                System.out.print(n+": ");
//                String line = br.readLine();
//                String h2 = line.substring(line.length() / 2);
//                String h1 = line.substring(0, line.length() / 2);
//                for (char c : h1.toCharArray()) {
//                    if (h2.contains(String.valueOf(c))){
////                        System.out.print(c);
//                        if (c >= 97) {
//                            score += c - 96;
//                        } else {
//                            score += c -65+27;
//                        }
//                        break;
//                    }
//                }
//            }
//            System.out.println(score);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
