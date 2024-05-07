import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpaceShipGame extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;
    private static final int LANE_HEIGHT = 200;
    private static final int SHIP_WIDTH = 50;
    private static final int SHIP_HEIGHT = 70;
    private static final int OBSTACLE_WIDTH = 50;
    private static final int OBSTACLE_HEIGHT = 50;
    private static final int OBSTACLE_SPEED = 4;
    private static final int OBSTACLE_DECISION_INTERVAL = 1000; // in milliseconds
    private static final int NUM_LANES = 10; // Number of lanes

    private Image backgroundImage;
    private Image spaceshipImage;
    private Image obstacleImage;

    private Canvas canvas;
    private GraphicsContext gc;
    private int shipX = WIDTH / 2 - SHIP_WIDTH / 2;
    private int shipY = HEIGHT - SHIP_HEIGHT - 10;
    private int lane = 1; // 0: A, 1: B, 2: C
    private List<Obstacle> verticalObstacles = new ArrayList<>();
    private List<Obstacle> horizontalObstacles = new ArrayList<>();
    private boolean gameOver = false;

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

        quadTree = new QuadTree(0, new Rectangle(0, 0, WIDTH, HEIGHT));

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

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> {
            restartGame();
            restartButton.setVisible(false);
        });

        root.getChildren().add(restartButton);
        restartButton.setTranslateY(HEIGHT / 2);
        restartButton.setVisible(false);

        primaryStage.setScene(scene);
        primaryStage.setTitle("SpaceShip Game");
        primaryStage.show();

        startGame();
    }

    private void startGame() {
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

        for (Obstacle obstacle : allObstacles) {
            if (shipX < obstacle.getX() + OBSTACLE_WIDTH &&
                    shipX + SHIP_WIDTH > obstacle.getX() &&
                    shipY < obstacle.getY() + OBSTACLE_HEIGHT &&
                    shipY + SHIP_HEIGHT > obstacle.getY()) {
                gameOver = true;
                return;
            }
        }
    }

    private void restartGame() {
        shipX = WIDTH / 2 - SHIP_WIDTH / 2;
        shipY = HEIGHT - SHIP_HEIGHT - 10;
        lane = 1;
        verticalObstacles.clear();
        horizontalObstacles.clear();
        gameOver = false;
    }

    private void showGameOverAlert() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("You crashed! Game over.");

            Button restartButton = new Button("Restart");
            restartButton.setOnAction(e -> {
                restartGame();
                alert.close();
            });

            alert.getDialogPane().getButtonTypes().clear();
            alert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            alert.getDialogPane().setExpandableContent(restartButton);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                restartGame();
                alert.close();
            });

            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Obstacle {
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
    }

}
