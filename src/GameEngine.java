import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class GameEngine implements ActionListener, Drawable {
    private Timer time;
    public final static int refreshRate = 60;        //Delay in milliseconds between game ticks

    public final static int numSquares = 25;          //The size of the grid taking up the play screen

    private Grid gameGrid;
    private Map<Integer, Boolean> inputs;

    AppleMaker food;

    private LinkedList<Actor> actors = new LinkedList<Actor>();
    private LinkedList<Drawable> drawables = new LinkedList<Drawable>();


    GameEngine(Map<Integer, Boolean> inputs)
    {
        time = new Timer(refreshRate, this); //sets delay to 15 millis and calls the actionPerformed of this class.

        inputs = new LinkedHashMap<Integer, Boolean>();

        gameGrid = new Grid(0, 0, (int)(Math.sqrt(numSquares)), numSquares, Color.BLACK);

        SnakeHead snake1 = new SnakeHead(Color.CYAN, 1, 1, gameGrid, 1);
        actors.add(snake1);

        food = new AppleMaker(Color.BLACK,gameGrid,2);

        drawables.add(gameGrid);
        drawables.add(snake1);
        drawables.add(food);

        this.inputs = inputs;
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

        Iterator it = drawables.listIterator();
        Drawable tempDrawable;
        while (it.hasNext()){
            tempDrawable = (Drawable) it.next();
            tempDrawable.drawMe(g);
        }
    }

    //method which updates the snake's vision
    private int[] lookAround(SnakeHead head){
        int x = head.x;
        int y = head.y;
        Actor[][] grid = (Actor[][])gameGrid.gridMat;

        int[] newInputs = new int[14];
            //first 8 is nearest snake body: N-NE-E-SE-S-SW-W-NW
            //next 4 is distance from walls
            //last 2 is vector to food

        //Looking for snake body--------------------------------
        //Look the the East
        for (int i = x+1; i < gameGrid.numSquares; i++) {
            if(grid[y][i] instanceof Body){
                i = gameGrid.numSquares;
                newInputs[2] = i - x;
            }
        }

        return newInputs;
    }
}