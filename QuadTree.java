import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private static final int MAX_OBJECTS = 10;
    private static final int MAX_LEVELS = 5;

    private int level;
    private List<Rectangle> objects;
    private Rectangle bounds;
    private QuadTree[] nodes;

    public QuadTree(int level, Rectangle bounds) {
        this.level = level;
        this.objects = new ArrayList<>();
        this.bounds = bounds;
        this.nodes = new QuadTree[4];
    }

    public void clear() {
        this.objects.clear();
        for (int i = 0; i < this.nodes.length; i++) {
            if (this.nodes[i] != null) {
                this.nodes[i].clear();
                this.nodes[i] = null;
            }
        }
    }

    private void split() {
        int subWidth = this.bounds.width / 2;
        int subHeight = this.bounds.height / 2;
        int x = this.bounds.x;
        int y = this.bounds.y;

        this.nodes[0] = new QuadTree(this.level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        this.nodes[1] = new QuadTree(this.level + 1, new Rectangle(x, y, subWidth, subHeight));
        this.nodes[2] = new QuadTree(this.level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        this.nodes[3] = new QuadTree(this.level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    private int getIndex(Rectangle pRect) {
        int index = -1;
        double verticalMidpoint = this.bounds.x + (this.bounds.width / 2);
        double horizontalMidpoint = this.bounds.y + (this.bounds.height / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = (pRect.y < horizontalMidpoint && pRect.y + pRect.height < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (pRect.y > horizontalMidpoint);

        // Object can completely fit within the left quadrants
        if (pRect.x < verticalMidpoint && pRect.x + pRect.width < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (pRect.x > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    public void insert(Rectangle pRect) {
        if (this.nodes[0] != null) {
            int index = getIndex(pRect);

            if (index != -1) {
                this.nodes[index].insert(pRect);
                return;
            }
        }

        this.objects.add(pRect);

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

    public List<Rectangle> retrieve(List<Rectangle> returnObjects, Rectangle pRect) {
        int index = getIndex(pRect);
        if (index != -1 && this.nodes[0] != null) {
            this.nodes[index].retrieve(returnObjects, pRect);
        }

        returnObjects.addAll(this.objects);

        return returnObjects;
    }
}
