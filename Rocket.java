package edu.bhcc.semester_project;

import java.awt.Image;

public class Rocket {
    public int x = Game.boardWidth / 8;
    public int y = Game.boardWidth /2;
    public int height = 70;
    public int width = 33;
    public Image image ; 

    Rocket(Image img){
        this.image = img;
    }
}
