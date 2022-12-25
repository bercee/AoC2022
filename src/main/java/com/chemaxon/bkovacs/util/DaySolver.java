package com.chemaxon.bkovacs.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

public abstract class DaySolver {

    protected static final Logger LOG = LogManager.getLogger(DaySolver.class);

    private final int year;
    private final int day;

    private final List<String> input;

    public DaySolver(int day) {
        this.day = day;
        this.year = 2022;
        try {
            this.input = Collections.unmodifiableList(PageConnector.downloadInputFile(year, day));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected List<String> getTestInput() {
        LOG.error("Test input is missing.");
        return Collections.emptyList();
    }

    protected abstract void init(List<String> input);
    protected abstract Object algorithmPart1();
    protected abstract Object algorithmPart2();

    public void solvePart1() {
        init(input);
        LOG.info("Part 1 solution: {}",algorithmPart1());
    }

    public void solvePart2() {
        init(input);
        LOG.info("Part 2 solution: {}", algorithmPart2());
    }

    public void solvePart1Test() {
        init(getTestInput());
        LOG.info("Part 1 test solution: {}", algorithmPart1());
    }

    public void solvePart2Test() {
        init(getTestInput());
        LOG.info("Part 2 test solution: {}", algorithmPart2());
    }

    public void solveAndSubmitPart1() {
        try {
            init(input);
            Object solution = algorithmPart1();
            if (solution != null) {
                PageConnector.submitSolution(year, day, 1, solution);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void solveAndSubmitPart2() {
        try {
            init(input);
            Object solution = algorithmPart2();
            if (solution != null) {
                PageConnector.submitSolution(year, day, 2, solution);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}
