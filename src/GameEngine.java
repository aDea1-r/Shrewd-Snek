import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class GameEngine implements ActionListener {
    private Timer time;
    private final int refreshRate = 60;        //Delay in milliseconds between game ticks

    private final int numSquares = 25;          //The size of the grid taking up the play screen

    private Grid gameGrid;
    private Map<Integer, Boolean> inputs;
    //    boolean initInputs = false;
    AppleMaker food;

    private LinkedList<Actor> actors = new LinkedList<Actor>();

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
        for (int i = 1; i < y; i++) {       //Look the the North
            if(grid[y-i][x] instanceof Body){
                newInputs[0] = i;
                break;
            }
        }
        for (int i = 1; i < y; i++) {       //Look the the NorthEast
            if(grid[y-i][x+1] instanceof Body){
                newInputs[1] = i;
                break;
            }
        }
        for (int i = 1; i < y; i++) {       //Look the the East
            if(grid[y][x+1] instanceof Body){
                newInputs[1] = i;
                break;
            }
        }


        return newInputs;
    }
}