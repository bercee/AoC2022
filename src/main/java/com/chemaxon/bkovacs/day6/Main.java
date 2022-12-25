package com.chemaxon.bkovacs.day6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {

    private static int LENGTH = 14;
    public static void main(String[] args) {
        InputStream is = Main.class.getResourceAsStream("input.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            for (int i = LENGTH-1; i < line.length(); i++) {
                String substr = line.substring(i-(LENGTH-1), i+1);
                char[] chars = substr.toCharArray();
                boolean foundSame = false;

                for (int k = 0; k < LENGTH; k++) {
                    for (int l = k+1; l < LENGTH; l++) {
                        if (chars[k] == chars[l]) {
                            foundSame = true;
                            break;
                        }
                    }
                    if (foundSame) {
                        break;
                    }
                }

                if (!foundSame) {
                    System.out.println(i+1);
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
