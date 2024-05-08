import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpaceShipGame extends Application {

    private static final int WIDTH = 700;
    private static final int HEIGHT = 600;
    private static final int SHIP_WIDTH = 50;
    private static final int SHIP_HEIGHT = 70;
    private static final int OBSTACLE_WIDTH = 50;
    private static final int OBSTACLE_HEIGHT = 50;
    private static final int OBSTACLE_SPEED = 4;
    private static final int OBSTACLE_DECISION_INTERVAL = 1000; // in milliseconds

    private Image backgroundImage;
    private Image spaceshipImage;
    private Image obstacleImage;

    private Canvas canvas;
    private GraphicsContext gc;
    private int shipX = WIDTH / 2 - SHIP_WIDTH / 2;
    private int shipY = HEIGHT - SHIP_HEIGHT - 10;
    private List<Obstacle> verticalObstacles = new ArrayList<>();
    private List<Obstacle> horizontalObstacles = new ArrayList<>();
    private boolean gameOver = false;
    private long startTime;

    private QuadTree quadTree;

    @Override
    public void start(Stage primaryStage) {
        backgroundImage = new Image("file:background.jpg");
        spaceshipImage = new Image("file:spaceship.png");
        obstacleImage = new Image("file:obstacle.png");
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        quadTree = new QuadTree(0, new QuadTree.Rectangle(0, 0, WIDTH, HEIGHT));

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W:
                    shipY -= 10; // Move up
                    break;
                case S:
                    shipY += 10; // Move down
                    break;
                case A:
                    shipX -= 10; // Move left
                    break;
                case D:
                    shipX += 10; // Move right
                    break;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("SpaceShip Game");
        primaryStage.show();

        startGame();
    }

    private void startGame() {
        startTime = System.currentTimeMillis();
        new Thread(() -> {
            Random random = new Random();
            while (!gameOver) {
                Platform.runLater(() -> {
                    draw();
                    move();
                    checkCollisions();
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(() -> {
                showGameOverAlert();
            });
        }).start();

        new Thread(() -> {
            Random random = new Random();
            while (!gameOver) {
                Platform.runLater(() -> {
                    if (random.nextInt(100) < 50) {
                        int obstacleX = random.nextInt(WIDTH - OBSTACLE_WIDTH);
                        horizontalObstacles.add(new Obstacle(obstacleX, -OBSTACLE_HEIGHT, true));
                    } else {
                        int obstacleY = random.nextInt(HEIGHT - OBSTACLE_HEIGHT);
                        verticalObstacles.add(new Obstacle(-OBSTACLE_WIDTH, obstacleY, false));
                    }
                });
                try {
                    Thread.sleep(OBSTACLE_DECISION_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void draw() {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);

        gc.drawImage(spaceshipImage, shipX, shipY, SHIP_WIDTH, SHIP_HEIGHT);

        for (Obstacle obstacle : verticalObstacles) {
            gc.drawImage(obstacleImage, obstacle.getX(), obstacle.getY(), OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        }

        for (Obstacle obstacle : horizontalObstacles) {
            gc.drawImage(obstacleImage, obstacle.getX(), obstacle.getY(), OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        }
    }

    private void move() {
        for (Obstacle obstacle : horizontalObstacles) {
            obstacle.setY(obstacle.getY() + OBSTACLE_SPEED);
            if (obstacle.getY() > HEIGHT) {
                horizontalObstacles.remove(obstacle);
                break;
            }
        }

        for (Obstacle obstacle : verticalObstacles) {
            obstacle.setX(obstacle.getX() + OBSTACLE_SPEED);
            if (obstacle.getX() > WIDTH) {
                verticalObstacles.remove(obstacle);
                break;
            }
        }
    }

    private void checkCollisions() {
        List<Obstacle> allObstacles = new ArrayList<>(verticalObstacles);
        allObstacles.addAll(horizontalObstacles);

        quadTree.clear();

        for (Obstacle obstacle : allObstacles) {
            quadTree.insert(obstacle);
        }

        QuadTree.Rectangle shipRect = new QuadTree.Rectangle(shipX, shipY, SHIP_WIDTH, SHIP_HEIGHT);

        List<QuadTree.Comparable> collidingObstacles = quadTree.query(shipRect);

        for (QuadTree.Comparable comparable : collidingObstacles) {
            Obstacle obstacle = (Obstacle) comparable;
            if (shipX < obstacle.getX() + OBSTACLE_WIDTH &&
                    shipX + SHIP_WIDTH > obstacle.getX() &&
                    shipY < obstacle.getY() + OBSTACLE_HEIGHT &&
                    shipY + SHIP_HEIGHT > obstacle.getY()) {
                gameOver = true;
                return;
            }
        }
    }

    private void showGameOverAlert() {
        long survivalTime = (System.currentTimeMillis() - startTime) / 1000;
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over!!!");
            alert.setHeaderText(null);
            alert.setContentText("You survived for " + survivalTime + " seconds.");

            alert.setOnHidden(e -> Platform.exit());

            alert.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Obstacle implements QuadTree.Comparable {
        private int x;
        private int y;
        private boolean isHorizontal;

        public Obstacle(int x, int y, boolean isHorizontal) {
            this.x = x;
            this.y = y;
            this.isHorizontal = isHorizontal;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public boolean isHorizontal() {
            return isHorizontal;
        }

        @Override
        public QuadTree.Rectangle getBoundingBox() {
            return new QuadTree.Rectangle(x, y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        }
    }

    private static class QuadTree {
        private static final int MAX_OBJECTS = 10;
        private static final int MAX_LEVELS = 5;

        private int level;
        private List<Comparable> objects;
        private Rectangle bounds;
        private QuadTree[] nodes;

        public QuadTree(int level, Rectangle bounds) {
            this.level = level;
            this.bounds = bounds;
            objects = new ArrayList<>();
            nodes = new QuadTree[4];
        }

        public void clear() {
            objects.clear();

            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i] != null) {
                    nodes[i].clear();
                    nodes[i] = null;
                }
            }
        }

        private void split() {
            int subWidth = bounds.width / 2;
            int subHeight = bounds.height / 2;
            int x = bounds.x;
            int y = bounds.y;

            nodes[0] = new QuadTree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
            nodes[1] = new QuadTree(level + 1, new Rectangle(x, y, subWidth, subHeight));
            nodes[2] = new QuadTree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
            nodes[3] = new QuadTree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
        }

        private int getIndex(Comparable comparable) {
            Rectangle range = comparable.getBoundingBox();
            int index = -1;
            double verticalMidpoint = this.bounds.x + (this.bounds.width / 2);
            double horizontalMidpoint = this.bounds.y + (this.bounds.height / 2);

            boolean topQuadrant = (range.y < horizontalMidpoint && range.y + range.height < horizontalMidpoint);
            boolean bottomQuadrant = (range.y > horizontalMidpoint);

            if (range.x < verticalMidpoint && range.x + range.width < verticalMidpoint) {
                if (topQuadrant) {
                    index = 1;
                } else if (bottomQuadrant) {
                    index = 2;
                }
            } else if (range.x > verticalMidpoint) {
                if (topQuadrant) {
                    index = 0;
                } else if (bottomQuadrant) {
                    index = 3;
                }
            }

            return index;
        }

        public void insert(Comparable comparable) {
            if (nodes[0] != null) {
                int index = getIndex(comparable);

                if (index != -1) {
                    nodes[index].insert(comparable);

                    return;
                }
            }

            objects.add(comparable);

            if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
                if (nodes[0] == null) {
                    split();
                }

                int i = 0;
                while (i < objects.size()) {
                    int index = getIndex(objects.get(i));
                    if (index != -1) {
                        nodes[index].insert(objects.remove(i));
                    } else {
                        i++;
                    }
                }
            }
        }

        public List<Comparable> query(Rectangle range) {
            List<Comparable> result = new ArrayList<>();
            query(range, result);
            return result;
        }

        private void query(Rectangle range, List<Comparable> found) {
            int index = getIndex(range);
            if (index != -1 && nodes[0] != null) {
                nodes[index].query(range, found);
            }

            found.addAll(objects);

            if (nodes[0] != null) {
                for (QuadTree node : nodes) {
                    node.query(range, found);
                }
            }
        }

        private int getIndex(Rectangle range) {
            int index = -1;
            double verticalMidpoint = bounds.x + (bounds.width / 2);
            double horizontalMidpoint = bounds.y + (bounds.height / 2);

            boolean topQuadrant = (range.y < horizontalMidpoint && range.y + range.height < horizontalMidpoint);
            boolean bottomQuadrant = (range.y > horizontalMidpoint);

            if (range.x < verticalMidpoint && range.x + range.width < verticalMidpoint) {
                if (topQuadrant) {
                    index = 1;
                } else if (bottomQuadrant) {
                    index = 2;
                }
            } else if (range.x > verticalMidpoint) {
                if (topQuadrant) {
                    index = 0;
                } else if (bottomQuadrant) {
                    index = 3;
                }
            }

            return index;
        }

        public void remove(Comparable comparable) {
            if (nodes[0] != null) {
                int index = getIndex(comparable);
                if (index != -1) {
                    nodes[index].remove(comparable);
                    return;
                }
            }
            objects.remove(comparable);
        }

        private static class Rectangle {
            private int x, y, width, height;

            public Rectangle(int x, int y, int width, int height) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }

            public boolean intersects(Rectangle other) {
                return this.x < other.x + other.width &&
                        this.x + this.width > other.x &&
                        this.y < other.y + other.height &&
                        this.y + this.height > other.y;
            }
        }

        public interface Comparable {
            QuadTree.Rectangle getBoundingBox();
        }
    }
}
