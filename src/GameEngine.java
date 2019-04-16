import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class GameEngine implements ActionListener, Drawable {
    private Timer time;
    private final int refreshRate = 60;        //Delay in milliseconds between game ticks

    private final int numSquares = 25;          //The size of the grid taking up the play screen

    private Grid gameGrid;
    private Map<Integer, Boolean> inputs;
    //    boolean initInputs = false;
    AppleMaker food;

    private LinkedList<Actor> actors = new LinkedList<Actor>();
    private LinkedList<Drawable> drawables = new LinkedList<Drawable>();


    GameEngine()
    {
        time = new Timer(refreshRate, this); //sets delay to 15 millis and calls the actionPerformed of this class.

        inputs = new LinkedHashMap<Integer, Boolean>();

        gameGrid = new Grid(0, 0, (int)(Math.sqrt(numSquares)), numSquares, null);

        SnakeHead snake1 = new SnakeHead(Color.CYAN, 1, 1, gameGrid, 1);
        actors.add(snake1);

        food = new AppleMaker(Color.BLACK,gameGrid,2);

        //------------------------------- Snake 1 Code
        inputs.put((int)'w', false);
        inputs.put((int)'d', false);
        inputs.put((int)'s', false);
        inputs.put((int)'a', false);
        inputs.put((int)'W', false);
        inputs.put((int)'D', false);
        inputs.put((int)'S', false);
        inputs.put((int)'A', false);

        time.start();
    }

    public void gameTick(){
        Iterator<Actor> it = actors.listIterator();
        while(it.hasNext()){
            Actor tempActor = it.next();
            tempActor.act(inputs);
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        gameTick();
    }

    public void keyPressed(KeyEvent e)
    {
        int code = e.getKeyCode();
        System.out.printf("Code %d (%s) pressed %n", code, (char)code + "");

        inputs.put(code, true);
    }

    public void drawMe(Graphics g) {

        g.setColor(Color.MAGENTA);
        g.fillRect(0,0,2000,1500);

        Iterator it = drawables.listIterator();
        Drawable tempDrawable;
        while (it.hasNext()){
            tempDrawable = (Drawable) it.next();
            tempDrawable.drawMe(g);
        }
    }
}