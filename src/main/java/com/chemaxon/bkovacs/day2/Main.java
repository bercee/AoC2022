package com.chemaxon.bkovacs.day2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {

    private enum Play {
        R(1, 2, 3), P(2, 3, 1), S(3, 1, 2);

        Play(int score, int win, int lose) {
            this.score = score;
            this.lose = lose;
            this.win = win + 6;
            this.draw = score + 3;

//            switch (this) {
//                case R:
//                    this.win = win + 2;
//                    this.draw = draw + 1;
//                    break;
//                case P:
//                    this.win = win + 3;
//                    this.draw = draw + 2;
//                    break;
//                case S:
//                    this.win = win + 1;
//                    this.draw = draw + 3;
//                    break;
//                default:
//                    this.win = 0;
//                    this.draw = 0;
//
//            }
        }

        final int score;
        final int draw;
        final int win;
        final int lose;


        int matchScore(Play other) {
            int matchScore = 0;
            if (this.score % 3 == other.score-1) {
                matchScore += 0;
            } else if (other.score % 3 == this.score -1) {
                matchScore += 6;
            } else {
                matchScore += 3;
            }
            matchScore += this.score;
            return matchScore;
        }

        static Play getByScore(int score) {
            switch (score) {
                case 1:
                    return R;
                case 2:
                    return P;
                case 3:
                    return S;
                default:
                    return null;
            }
        }

        int opponentScore(String s) {
            switch (s) {
                case "X":
                    return lose;
                case "Y":
                    return draw;
                case "Z":
                    return win;
                default:
                    return -1;
            }
        }

        static Play parse(String s) {
            if (s.equals("A") || s.equals("X")) {
                return R;
            } else if (s.equals("B") || s.equals("Y")) {
                return P;
            } else if (s.equals("C") || s.equals("Z")) {
                return S;
            } else {
                return null;
            }

        }
    }

    public static void main(String[] args) {
        InputStream is = com.chemaxon.bkovacs.day2.Main.class.getResourceAsStream("input.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))){
            String[] line;
            int score = 0;
            while (br.ready()) {
                line = br.readLine().split("\\s");
//                score+= Play.parse(line[1]).matchScore(Play.parse(line[0]));
                score += Play.parse(line[0]).opponentScore(line[1]);
            }
            System.out.println(score);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
