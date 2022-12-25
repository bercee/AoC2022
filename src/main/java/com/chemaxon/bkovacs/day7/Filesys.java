package com.chemaxon.bkovacs.day7;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Filesys {


    class Node {
        private Node parent;
        private String name;
        private List<Node> children = new ArrayList<>();
        private int size = 0;

        public void addChild(int size, String name) {
            children.add(new Node(size, name, this));
        }

        public Node(int size, String name, Node parent) {
            this.name = name;
            this.size = size;
            this.parent = parent;
        }

        public int getSize() {
            return size;
        }
    }

    private Node root = new Node(0, "/", null);

    Node getRoot() {
        return root;
    }

    Node getChild(Node parent, String name) {
        for (Node child : parent.children) {
            if (child.name.equals(name)) {
                return child;
            }
        }
        return null;
    }

    public Node getParent(Node curr) {
        return curr.parent;
    }

    public int getSize(Node dir) {
        int size = 0;
        size += dir.size;
        for (Node child : dir.children) {
            size+=getSize(child);
        }
//        System.out.println("size of " + dir.name+" = "+size);
        return size;
    }

    public void getDirs(Node start, Predicate<Node> filter, List<Node> nodes) {

        if (start.size == 0 && filter.test(start)) {
            nodes.add(start);
        }
        for (Node child : start.children) {
            getDirs(child, filter, nodes);
        }

    }

}
