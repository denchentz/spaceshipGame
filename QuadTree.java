package edu.bhcc.semester_project;

import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private static final int MAX_OBJECTS = 4;
    private static final int MAX_LEVELS = 5;

    private int level;
    private List<GameObject> objects;
    private Rectangle bounds;
    private QuadTree[] nodes;

    /**
     * Constructor
     */
    public QuadTree(int level, Rectangle bounds) {
        this.level = level;
        this.objects = new ArrayList<>();
        this.bounds = bounds;
        this.nodes = new QuadTree[4];
    }

    /**
     * Clears the quadtree 
     */
    public void clear() {
        this.objects.clear();
        for (int i = 0; i < this.nodes.length; i++) {
            if (this.nodes[i] != null) {
                this.nodes[i].clear();
                this.nodes[i] = null;
            }
        }
    }

    /** 
     * Splits the node into 4 subnodes 
     */
    private void split() {
        int subWidth = (int)(bounds.getWidth() / 2);
        int subHeight = (int)(bounds.getHeight() / 2);
        int x = (int)bounds.getX();
        int y = (int)bounds.getY();

        nodes[0] = new QuadTree(level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new QuadTree(level+1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new QuadTree(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new QuadTree(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    /**
     * Determine which node the object belongs to. -1 means 
     * object cannot completely fit within a child node and is part 
     * of the parent node 
     */
    public int getIndex(GameObject object) {
        int index = -1;
        double verticalMidpoint = this.bounds.x + (this.bounds.width / 2);
        double horizontalMidpoint = this.bounds.y + (this.bounds.height / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = (object.y < horizontalMidpoint && object.y + object.height < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (object.y > horizontalMidpoint);

        // Object can completely fit within the left quadrants
        if (object.x < verticalMidpoint && object.x + object.width < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants 
        else if (object.x > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    /**
     * Insert the object into the quadtree. If the node 
     * exceeds the capacity, it will split and add all 
     * objects to their corresponding nodes. 
     */
    public void insert(GameObject object) {
        if (this.nodes[0] != null) {
            int index = getIndex(object);

            if (index != -1) {
                this.nodes[index].insert(object);
                return;
            }
        }

        this.objects.add(object);

        if (this.objects.size() > MAX_OBJECTS && this.level < MAX_LEVELS) {
            if (this.nodes[0] == null) {
                this.split();
            }

            int i = 0;
            while (i < this.objects.size()) {
                int index = getIndex(this.objects.get(i));
                if (index != -1) {
                    this.nodes[index].insert(this.objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    /** 
     * Return all objects that could collide with the given object 
     */
    public List<GameObject> retrieve(List<GameObject> returnObjects, GameObject object) {
        int index = getIndex(object);
        if (index != -1 && this.nodes[0] != null) {
            this.nodes[index].retrieve(returnObjects, object);
        }

        returnObjects.addAll(this.objects);
        return returnObjects;
    }
}