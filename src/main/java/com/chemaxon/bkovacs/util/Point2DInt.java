package com.chemaxon.bkovacs.util;

import java.util.Objects;

public class Point2DInt {
    private final int i;
    private final int j;

    public Point2DInt(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public Point2DInt add(Point2DInt p) {
        return new Point2DInt(this.i + p.i, this.j + p.j);
    }

    @Override
    public String toString() {
        return String.format("%d; %d", i, j);
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point2DInt that = (Point2DInt) o;
        return i == that.i && j == that.j;
    }

    public Point2DInt step(Direction d) {
        return this.add(d.getV());
    }


}
