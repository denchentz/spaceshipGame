package edu.bhcc.semester_project;

import java.awt.Image;

public class Asteriod  {
    public int x = (int)(Math.random() * Game.boardWidth + 1);
    public int y = 0;
    public static int height = 64;
    public static int width = 64;
    Image image;
    boolean passed;


    Asteriod(Image img){
        image = img;
        passed = false;
    }

    public boolean isOffScreen() {
        return (x +150 <= 0);
    }
}
