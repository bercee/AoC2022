package com.chemaxon.bkovacs.util;

public enum Direction {
    S(new Point2DInt(0, 1)),
    W(new Point2DInt(-1, 0)),
    E(new Point2DInt(1, 0)),
    N(new Point2DInt(0, -1)),
    SW(new Point2DInt(-1, 1)),
    SE(new Point2DInt(1, 1)),
    NW(new Point2DInt(-1, -1)),
    NE(new Point2DInt(1, -1)),
    NULL(new Point2DInt(0, 0));

    private final Point2DInt v;

    Direction(Point2DInt v) {
        this.v = v;
    }

    public Point2DInt getV() {
        return v;
    }
}
