import java.awt.Image;

public class Bird {
    public int x = FlappyBird.boardWidth / 8;
    public int y = FlappyBird.boardWidth /2;
    public int height = 34;
    public int width = 24;
    public Image image ; 

    Bird(Image img){
        this.image = img;
    }

}
