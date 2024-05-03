import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class PipePool  extends ObjectPool<ArrayList<Pipe>>  {

    private ArrayList<Pipe> lst;
    private Image topPipeImg;
    private Image bottomPipeImg;
    

    public PipePool(){
        lst = new ArrayList<>(2);  
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
    }

    @Override
    protected ArrayList<Pipe> create() {
        int randomPipeY = (int) (0 - Pipe.height/4 - Math.random()*(Pipe.height/2));
        int openingSpace = FlappyBird.boardHeight/4;
    
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
       
    
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y  + Pipe.height + openingSpace;
     
        lst.add(topPipe);
        lst.add(bottomPipe);
        return lst;
    }


    @Override
    public void setUpNewCordinator(ArrayList<Pipe> o) {
        int randomPipeY = (int) (0 - Pipe.height/4 - Math.random()*(Pipe.height/2));
        int openingSpace = FlappyBird.boardHeight/4;
        o.get(0).y = randomPipeY;
        o.get(0).x = FlappyBird.boardWidth;
        o.get(0).passed = false;
        o.get(1).x =  FlappyBird.boardWidth;
        o.get(1).y =  lst.get(0).y  + Pipe.height + openingSpace;
        o.get(1).passed = false;
    }

   

}
