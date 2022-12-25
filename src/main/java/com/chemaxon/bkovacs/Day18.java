package com.chemaxon.bkovacs;

import com.chemaxon.bkovacs.util.DaySolver;
import com.chemaxon.bkovacs.util.Point2DInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Day18 extends DaySolver {

    private static final String TEST = "2,2,2\n" +
            "1,2,2\n" +
            "3,2,2\n" +
            "2,1,2\n" +
            "2,3,2\n" +
            "2,2,1\n" +
            "2,2,3\n" +
            "2,2,4\n" +
            "2,2,6\n" +
            "1,2,5\n" +
            "3,2,5\n" +
            "2,1,5\n" +
            "2,3,5";

    private final HashSet<Point3D> cubes = new HashSet<>();

    public Day18() {
        super(18);
    }

    @Override
    protected void init(List<String> input) {
        input.forEach(l -> {

            List<Integer> coords = Arrays.stream(l.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            cubes.add(new Point3D(coords.get(0), coords.get(1), coords.get(2)));
        });

        LOG.debug("number of cubes: {}",cubes.size());
    }

    @Override
    protected Object algorithmPart1() {
        return getSurface(cubes);
    }

    private int getSurface(Set<Point3D> points) {
        int count = 0;
        for (Point3D p : points) {
            count += p.getNeighbours().stream().filter(n -> !points.contains(n)).count();
        }
        return count;
    }

    @Override
    protected Object algorithmPart2() {
        int maxX, maxY, maxZ, minX, minY, minZ;


        var sorted = new ArrayList<>(cubes);

        sorted.sort(Comparator.comparingInt(Point3D::getX));
        minX = sorted.get(0).getX();

        sorted.sort(Comparator.comparingInt(Point3D::getY));
        minY = sorted.get(0).getY();

        sorted.sort(Comparator.comparingInt(Point3D::getZ));
        minZ = sorted.get(0).getZ();

        sorted.sort(Comparator.comparingInt(Point3D::getX).reversed());
        maxX = sorted.get(0).getX();

        sorted.sort(Comparator.comparingInt(Point3D::getY).reversed());
        maxY = sorted.get(0).getY();

        sorted.sort(Comparator.comparingInt(Point3D::getZ).reversed());
        maxZ = sorted.get(0).getZ();

        LOG.debug("Max: {},{},{}, min: {}, {}, {}",maxX, maxY, maxZ, minX, minY, minZ);

        Set<Point3D> air = new HashSet<>();

        for (int x = minX-1; x <= maxX+1; x++) {
            for (int y = minY-1; y <= maxY+1; y++) {
                for (int z = minZ-1; z <= maxZ+1; z++) {
                    Point3D p = new Point3D(x, y, z);
                    if (!cubes.contains(p)) {
                        air.add(p);
                    }
                }
            }
        }

        LOG.debug("air: {}", air.size());
//        floodFrom(new Point3D(minX-1, minY-1, minZ-1), air);

        Set<Point3D> connected = new HashSet<>();
        connected.add(new Point3D(minX-1, minY-1, minZ-1));
        Set<Point3D> nextConnected = new HashSet<>(connected);

        do {
            connected.clear();
            connected.addAll(nextConnected);
            for (Point3D p : new HashSet<>(nextConnected)) {
                addAirNeighbors(p, nextConnected, air);
            }
        } while (!nextConnected.equals(connected));

        air.removeAll(nextConnected);

        return getSurface(cubes) - getSurface(air);
    }

    private void floodFrom(Point3D start, Set<Point3D> points) {
        Set<Point3D> ns = start.getNeighbours().stream().filter(points::contains).collect(Collectors.toSet());
        points.remove(start);
        ns.forEach(points::remove);
        ns.forEach(s -> floodFrom(s, points));
    }

    private void addAirNeighbors(Point3D start, Set<Point3D> connected, Set<Point3D> allPoints) {
        connected.add(start);
        connected.addAll(start.getNeighbours().stream().filter(allPoints::contains).collect(Collectors.toList()));
    }

    protected List<String> getTestInput() {
        return Arrays.asList(TEST.split("\n"));
    }
}

class Point3D {
    private final int x, y, z;

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D(int[] coords) {
        this(coords[0], coords[1], coords[2]);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Set<Point3D> getNeighbours(){
        Set<Point3D> ret = new HashSet<>();
        return Arrays.stream(Direction3D.values()).map(d -> this.add(d.getV())).collect(Collectors.toSet());
    }

    public Point3D add(Point3D p) {
        return new Point3D(this.x + p.x, this.y + p.y, this.z + p.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point3D point3D = (Point3D) o;
        return x == point3D.x && y == point3D.y && z == point3D.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}

enum Direction3D {
    X(1,0,0), Y(0,1,0), Z(0,0,1),
    _X(-1,0,0), _Y(0,-1,0), _Z(0,0,-1);

    Direction3D(int x, int y, int z) {
        this.v = new Point3D(x, y, z);
    }

    private final Point3D v;

    public Point3D getV() {
        return v;
    }
}