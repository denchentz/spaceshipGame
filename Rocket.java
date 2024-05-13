package edu.bhcc.semester_project;

import java.awt.Image;

public class Rocket extends GameObject{
    Image image ; 

    Rocket(Image img) {
        super();
        super.x = 160;
        super.y = 500;
        super.height = 70;
        super.width = 33;
        this.image = img;
    }
}