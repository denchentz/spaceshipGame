package edu.bhcc.semester_project;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Game extends JPanel implements ActionListener, KeyListener {
    static int boardWidth = 480;
    static int boardHeight = 640;

    //images
    Image backgroundImg;
    Image rocketImg;
    Image asteriodImg;

    //game logic
    Rocket rocket;
    int velocityX = -4; //move pipes to the left speed (simulates bird moving right)
    int velocityY = 0; //move bird up/down speed.

    ArrayList<Asteriod> asteriods;

    Timer gameLoop;
    Timer spawnAsteriodTimer;
    boolean gameOver = false;
    double score = 0;


    Game() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./bg.jpg")).getImage();
        rocketImg = new ImageIcon(getClass().getResource("./rocket.png")).getImage();
        asteriodImg = new ImageIcon(getClass().getResource("./asteriod.png")).getImage();


        // game object
        rocket = new Rocket(rocketImg);
        asteriods = new ArrayList<Asteriod>();

        // timer to spawn asteriods every 2 seconds
        int count = 3;
        spawnAsteriodTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                for ()
                Asteriod asteriod = new Asteriod(asteriodImg);
                
                count += 2;
            }
        });
        spawnAsteriodTimer.start();
        
		//game timer
		gameLoop = new Timer(1000/60, this); //how long it takes to start timer, milliseconds gone between frames 
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
        // for (int i = 0; i < pipes.size(); i++) {
        //     Pipe pipe = pipes.get(i);
        //     pipe.x += velocityX;

        //     if (!pipe.passed && bird.x > pipe.x + Pipe.width) {
        //         score += 0.5; //0.5 because there are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
        //         pipe.passed = true;
                
        //     }

        //     if (collision(bird, pipe)) {
        //         gameOver = true;
        //     }

        // }

        // check rocket hit border
        if (rocket.x < 0) {
            rocket.x = 0;
        }
        if (rocket.y < 0) {
            rocket.y = 0;
        }
        if (rocket.x > boardWidth) {
            rocket.x = boardWidth;
        }
        if (rocket.y > boardHeight) {
            rocket.y = boardHeight;
        }


        //check offscreen
        // for(int i = 0; i < pipes.size();i++){
        //     Pipe pipe = pipes.get(i);
        //     if (pipe.isOffScreen()) {
        //         pipepool.checkIn(pipes);
        //         break;
        //     }
        // }

      
    }

    // boolean collision(Bird a, Pipe b) {
    //     return a.x < b.x + Pipe.width &&   //a's top left corner doesn't reach b's top right corner
    //            a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
    //            a.y < b.y + Pipe.height &&  //a's top left corner doesn't reach b's bottom left corner
    //            a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    // }

    //called every x milliseconds by gameLoop timer
    @Override
    public void actionPerformed(ActionEvent e) { 
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
            // up
            if (e.getKeyCode() == KeyEvent.VK_W) {
                rocket.y -= 20;
            }
            // left
            if (e.getKeyCode() == KeyEvent.VK_A) {
                rocket.x -= 20;
            }
            // down
            if (e.getKeyCode() == KeyEvent.VK_S) {
                rocket.y += 20;
            }
            // right
            if (e.getKeyCode() == KeyEvent.VK_D) {
                rocket.x += 20;
            }
        }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
