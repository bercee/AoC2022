package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.StatUtils;

import java.util.List;

public class Day8 extends DaySolver {

    private RealMatrix matrix;
    //rows
    private int n;
    //columns
    private int m;

    public Day8() {
        super(8);
    }

    @Override
    protected void init(List<String> input) {
        n = input.size();
        m = input.get(0).length();
        matrix = MatrixUtils.createRealMatrix(n, m);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int e = Integer.parseInt(input.get(i).substring(j, j+1));
                matrix.setEntry(i, j, e);
            }
        }

        LOG.debug(score(3,3));
    }

    private boolean isVisible(int i, int j) {
        if (isOnEdge(i, j)) {
            return true;
        }

        double h = matrix.getEntry(i, j);

        double[] col = matrix.getColumn(j);
        double[] top = ArrayUtils.subarray(col, 0, i);
        double[] bottom = ArrayUtils.subarray(col, i+1, n);
        double[] row = matrix.getRow(i);
        double[] left = ArrayUtils.subarray(row, 0, j);
        double[] right = ArrayUtils.subarray(row, j+1, m);

        return StatUtils.max(top) < h || StatUtils.max(bottom) < h ||
                StatUtils.max(left) < h || StatUtils.max(right) < h;

    }

    private int score(int i, int j) {
        int score = 1;
        double h = matrix.getEntry(i, j);

        double[] col = matrix.getColumn(j);
        double[] top = ArrayUtils.subarray(col, 0, i);
        double[] bottom = ArrayUtils.subarray(col, i+1, n);
        double[] row = matrix.getRow(i);
        double[] left = ArrayUtils.subarray(row, 0, j);
        double[] right = ArrayUtils.subarray(row, j+1, m);

        ArrayUtils.reverse(top);
        ArrayUtils.reverse(left);

        score *= arrayScore(top, h);
        score *= arrayScore(bottom, h);
        score *= arrayScore(left, h);
        score *= arrayScore(right, h);
        return score;
    }

    private int arrayScore(double[] array, double d) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] >= d) {
                return i + 1;
            }
        }
        return array.length;
    }

    private boolean isOnEdge(int i, int j) {
        return i == 0 || j == 0 || i == n-1 || j == m-1;
    }


    @Override
    protected Object algorithmPart1() {
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (isVisible(i, j)) {
                    count ++;
                }
            }
        }
        return count;
    }

    @Override
    protected Object algorithmPart2() {
        int max = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int s = score(i, j);
                if (s > max) {
                    max = s;
                }
            }
        }
        LOG.debug(max);
        return max;
    }

}
