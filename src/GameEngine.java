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


    public GameEngine(double startXPercent, double startYPercent, double screenSize, int height, int width)
    {
        time = new Timer(refreshRate, this); //sets delay to 15 millis and calls the actionPerformed of this class.

        inputs = new LinkedHashMap<Integer, Boolean>();

        //Grid setup----------------------------------------------------------------------------------------
//        double startXPercent = 0.05;          //% of the total screen which the play screen will start at
//        double startYPercent = 0.05;          //% of the total screen which the play screen will start at
//        double screenSize = 0.85;      //% of the total screen which the play screen will take up

        int startX = (int)(width * startXPercent);
        int startY = (int)(height * startYPercent);

        int gridSize = Math.max(Math.min((int)(height * screenSize), (int)(width * screenSize))/numSquares, 1);

        gameGrid = new Grid(startX, startY, gridSize, numSquares, Color.BLACK);
        //end grid setup -----------------------------------------------------------------------------------

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
    public GameEngine(){
        this(0, 0, 0, 1, 1);
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
        int stop;   //Helper variable for loop stop
        stop = y;                                                                       //Look the the North
        newInputs[0] = stop;
        for (int i = 1; i < stop; i++) {
            if(grid[y-i][x] instanceof Body){
                newInputs[0] = i;
                break;
            }
        }
        stop = Math.min(y, gameGrid.gridMat.length - x);                                //Look to the NorthEast
        newInputs[1] = stop;
        for (int i = 1; i < stop; i++) {
            if(grid[y-i][x+i] instanceof Body){
                newInputs[1] = i;
                break;
            }
        }
        stop = gameGrid.gridMat.length - x;                                             //Look to the East
        newInputs[2] = stop;
        for (int i = 1; i < stop; i++) {
            if(grid[y][x+i] instanceof Body){
                newInputs[2] = i;
                break;
            }
        }
        stop = Math.min(gameGrid.gridMat.length - y, gameGrid.gridMat.length - x);      //Look to the SouthEast
        newInputs[3] = stop;
        for (int i = 1; i < stop; i++) {
            if(grid[y+i][x+i] instanceof Body){
                newInputs[3] = i;
                break;
            }
        }
        stop = gameGrid.gridMat.length - y;                                             //Look to the South
        newInputs[4] = stop;
        for (int i = 1; i < stop; i++) {       //Look the the South
            if(grid[y+i][x] instanceof Body){
                newInputs[4] = i;
                break;
            }
        }
        stop = Math.min(gameGrid.gridMat.length - y, x);                                //Look to the SouthWest
        newInputs[5] = stop;
        for (int i = 1; i < stop; i++) {       //Look the the SouthWest
            if(grid[y+i][x-i] instanceof Body){
                newInputs[5] = i;
                break;
            }
        }
        stop = x;                                                                       //Look to the West
        newInputs[6] = stop;
        for (int i = 1; i < stop; i++) {
            if(grid[y][x-i] instanceof Body){
                newInputs[6] = i;
                break;
            }
        }
        stop = Math.min(y, x);                                //Look to the NorthWest
        newInputs[7] = stop;
        for (int i = 1; i < stop; i++) {
            if(grid[y-i][x-i] instanceof Body){
                newInputs[7] = i;
                break;
            }
        }


        return newInputs;
    }
}