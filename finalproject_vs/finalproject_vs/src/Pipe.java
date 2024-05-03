import java.awt.Image;

public class Pipe  {
    public int x = FlappyBird.boardWidth;
    public int y = 0;
    public static int height = 512;
    public static int width = 64;
    Image image;
    boolean passed;


    Pipe(Image img){
        image = img;
        passed = false;
    }

    public boolean isOffScreen() {
        return (x +150 <= 0);
    }

    
}
