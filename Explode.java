package edu.bhcc.semester_project;

import java.awt.Image;

public class Explode {
    public int x;
    public int y;
    public int height = 64;
    public int width = 64;
    Image image;

    Explode(int x, int y, Image img){
        this.x = x;
        this.y = y;
        image = img;
    }
}
