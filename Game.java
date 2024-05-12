package edu.bhcc.semester_project;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Game extends JPanel implements ActionListener, KeyListener {
    static int boardWidth = 360;
    static int boardHeight = 640;

    //images
    Image backgroundImg;
    Image rocketImg;
    Image asteriodImg;
    Image explodeImg;

    //game objects
    Rocket rocket;
    ArrayList<Asteriod> asteriods;
    Explode explode;

    Timer gameLoop;
    Timer spawnAsteriodTimer;
    boolean gameOver = false;
    double score = 0;

    QuadTree quadTree = new QuadTree(0, new Rectangle(0, 0, boardWidth, boardHeight));

    Game() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./bg.jpg")).getImage();
        rocketImg = new ImageIcon(getClass().getResource("./rocket.png")).getImage();
        asteriodImg = new ImageIcon(getClass().getResource("./asteriod.png")).getImage();
        explodeImg = new ImageIcon(getClass().getResource("./explode.png")).getImage();
        
        // game object
        rocket = new Rocket(rocketImg);
        asteriods = new ArrayList<Asteriod>();

        // timer to spawn asteriods every 2 seconds
        spawnAsteriodTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // spawn asteriods from 0 to 8
                int spawnQuantity = (int)(Math.random() * (8 + 1));
                for (int i = 0; i < spawnQuantity; i++) {
                    Asteriod asteriod = new Asteriod(asteriodImg);
                    asteriods.add(asteriod);
                }
            }
        });
        spawnAsteriodTimer.start();
        
		//game timer
		gameLoop = new Timer(1000/30, this); //how long it takes to start timer, milliseconds gone between frames 
        gameLoop.start();
	}
    
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //rocket
        g.drawImage(rocketImg, rocket.x, rocket.y, rocket.width, rocket.height, null);

        //asteriods
        for (int i = 0; i < asteriods.size(); i++) {
            Asteriod asteriod = asteriods.get(i);
            g.drawImage(asteriodImg, asteriod.x, asteriod.y, asteriod.width, asteriod.height, null);
        }

        //explode
        if (gameOver) {
            g.drawImage(explodeImg, explode.x, explode.y, explode.width, explode.height, null);
        }

        //score
        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
        
	}

    public void move() {
        //asteriods
        for (int i = 0; i < asteriods.size(); i++) {
            Asteriod asteriod = asteriods.get(i);
            asteriod.y += asteriod.velocity;

            // if (!pipe.passed && bird.x > pipe.x + Pipe.width) {
            //      score += 0.5; //0.5 because there are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
            //      pipe.passed = true;
                
            // }

            if (collision(rocket, asteriod)) {
                int x = (rocket.x + asteriod.x) / 2;
                int y = (rocket.y + asteriod.y) / 2;
                explode = new Explode(x, y, asteriodImg);
                gameOver = true;
            }

        }

        //check offscreen
        for(int i = 0; i < asteriods.size();i++){
            Asteriod asteriod = asteriods.get(i);
            if (asteriod.isOffScreen()) {
                asteriods.remove(asteriod);
            }
        }      
    }

    boolean collision(Rocket a, Asteriod b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    //called every x milliseconds by gameLoop timer
    @Override
    public void actionPerformed(ActionEvent e) {
        quadTree.clear();
        quadTree.insert(rocket);
        for (Asteriod asteriod : asteriods) {
            quadTree.insert(asteriod);
        }
        move();
        repaint();
        if (gameOver) {
            spawnAsteriodTimer.stop();
            gameLoop.stop();
        }
    }  

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            // Press W to go up
            if (e.getKeyCode() == KeyEvent.VK_W) {
                rocket.y -= 20;
                if (rocket.y < 0) {
                    rocket.y = 0;
                }
            }
            // Press A to go left
            if (e.getKeyCode() == KeyEvent.VK_A) {
                rocket.x -= 20;
                if (rocket.x < 0) {
                    rocket.x = 0;
                }
            }
            // Press S to go down
            if (e.getKeyCode() == KeyEvent.VK_S) {
                rocket.y += 20;
                if (rocket.y + rocket.height > boardHeight) {
                    rocket.y = boardHeight - rocket.height;
                }
            }
            // Press D to go right
            if (e.getKeyCode() == KeyEvent.VK_D) {
                rocket.x += 20;
                if (rocket.x + rocket.width > boardWidth) {
                    rocket.x = boardWidth - rocket.width;
                }
            }
        }
        else {
            // Press R to restart
            if (e.getKeyCode() == KeyEvent.VK_R) {
                //restart game by resetting conditions
                rocket.x = (180 - 33);
                rocket.y = 500;
                asteriods.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                spawnAsteriodTimer.start();
            }
        }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}