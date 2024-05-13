package edu.bhcc.semester_project;

import java.awt.Image;

public class Asteriod extends GameObject {
    public int velocity;
    Image image;

    Asteriod(Image img){
        super();
        super.x = (int)(Math.random() * (Game.boardWidth - 32) + 1);
        super.y = 0;
        super.height = 32;
        super.width = 32;
        velocity = (int)(Math.random() * 10) + 1; // asteriod speed varies from 1 to 10
        image = img;
    }

    public boolean isOffScreen() {
        return (x + width < 0) ||
               (y + height < 0) ||
               (x > Game.boardWidth) ||
               (y > Game.boardHeight);
    }
}
