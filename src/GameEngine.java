import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class GameEngine implements ActionListener, Drawable {
    private Timer time;
    final static int refreshRate = 220;        //Delay in milliseconds between game ticks

    final static int numSquares = 25;          //The size of the grid taking up the play screen

    private Grid gameGrid;
    ScoreTracker scoreTracker;
    private Map<Integer, Boolean> inputs;

    private AppleMaker food;
    private SnakeHead snake1;

    private LinkedList<Drawable> drawables = new LinkedList<Drawable>();

    boolean gameRunning;


    GameEngine(double startXPercent, double startYPercent, double screenSize, int height, int width, Map<Integer, Boolean> inputs)
    {
        time = new Timer(refreshRate, this); //sets delay to 15 millis and calls the actionPerformed of this class.

//        Grid setup----------------------------------------------------------------------------------------
//        double startXPercent = 0.05;          //% of the total screen which the play screen will start at
//        double startYPercent = 0.05;          //% of the total screen which the play screen will start at
//        double screenSize = 0.85;      //% of the total screen which the play screen will take up

        int startX = (int)(width * startXPercent);
        int startY = (int)(height * startYPercent);

        int gridSize = Math.max(Math.min((int)(height * screenSize), (int)(width * screenSize))/numSquares, 1);

        gameGrid = new Grid(startX, startY, gridSize, numSquares, Color.BLACK);
//      end grid setup -----------------------------------------------------------------------------------

        scoreTracker = new ScoreTracker(this);
        snake1 = new AISnakeHead(Color.CYAN, gameGrid.numSquares/2, gameGrid.numSquares/2, gameGrid, 1,scoreTracker);

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
        gameRunning = true;
    }
    public GameEngine(Map<Integer, Boolean> inputs){
        this(0, 0, 0, 1, 1, inputs);
    }

    private void gameTick(){
        if(!((AISnakeHead)snake1).act(lookAround(snake1)))
            kill();
        scoreTracker.act(inputs);
    }

    void kill() {
        time.stop();
        gameRunning = false;
    }

    public void actionPerformed(ActionEvent e)
    {
        gameTick();
    }

    public void keyPressed(KeyEvent e)
    {
        int code = e.getKeyCode();
//        System.out.printf("Code %d (%s) pressed %n", code, (char)code + "");

        inputs.put(code, true);
    }

    public void drawMe(Graphics g) {

        Iterator it = drawables.listIterator();
        Drawable tempDrawable;
        while (it.hasNext()){
            tempDrawable = (Drawable) it.next();
            tempDrawable.drawMe(g);
        }
        if(!gameRunning) {
            g.setColor(Color.RED);//SUBLIME
            g.setFont(new Font("TimesRoman", Font.BOLD, (gameGrid.size*gameGrid.numSquares)/12));
            g.drawString("Thou art slain",gameGrid.getXPixels(gameGrid.numSquares/4),gameGrid.getXPixels(gameGrid.numSquares/2));
        }
    }

    //method which updates the snake's vision
    private int[] lookAround(SnakeHead head){
        int x = head.x;
        int y = head.y;
        Object[][] grid = gameGrid.gridMat;

        int[] newInputs = new int[14];
            //first 8 is nearest snake body: N-NE-E-SE-S-SW-W-NW
            //next 4 is distance from walls: N-E-S-W
            //last 2 is vector to food: x-y

        //Looking for snake body--------------------------------
        int stop;   //Helper variable for loop stop
        stop = y;                                                                       //Look the the North
        newInputs[0] = stop + 1;
        System.out.printf("Looking %10s: max distance is %d%n", "North", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x][y-i] instanceof Body){
                newInputs[0] = i;
                break;
            }
        }
        System.out.printf("   Looking %10s: distance is %d%n", "North", newInputs[0]);

        stop = Math.min(y, gameGrid.gridMat.length - x - 1);                                //Look to the NorthEast
        newInputs[1] = stop + 1;
        System.out.printf("Looking %10s: max distance is %d%n", "NorthEast", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x+i][y-i] instanceof Body){
                newInputs[1] = i;
                break;
            }
        }
        System.out.printf("   Looking %10s: distance is %d%n", "NorthEast", newInputs[1]);

        stop = gameGrid.gridMat.length - 1 - x;                                             //Look to the East
        newInputs[2] = stop + 1;
        System.out.printf("Looking %10s: max distance is %d%n", "East", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x+i][y] instanceof Body){
                newInputs[2] = i;
                break;
            }
        }
        System.out.printf("   Looking %10s: distance is %d%n", "East", newInputs[2]);

        stop = Math.min(gameGrid.gridMat.length - 1 - y, gameGrid.gridMat.length - 1 - x);      //Look to the SouthEast
        newInputs[3] = stop + 1;
        System.out.printf("Looking %10s: max distance is %d%n", "SouthEast", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x+i][y+i] instanceof Body){
                newInputs[3] = i;
                break;
            }
        }
        System.out.printf("   Looking %10s: distance is %d%n", "SouthEast", newInputs[3]);

        stop = gameGrid.gridMat.length - 1 - y;                                             //Look to the South
        newInputs[4] = stop + 1;
        System.out.printf("Looking %10s: max distance is %d%n", "South", stop);
        for (int i = 1; i < stop; i++) {       //Look the the South
            if(grid[x][y+i] instanceof Body){
                newInputs[4] = i;
                break;
            }
        }
        System.out.printf("   Looking %10s: distance is %d%n", "South", newInputs[4]);

        stop = Math.min(gameGrid.gridMat.length - 1 - y, x);                                //Look to the SouthWest
        newInputs[5] = stop + 1;
        System.out.printf("Looking %10s: max distance is %d%n", "SouthWest", stop);
        for (int i = 1; i < stop; i++) {       //Look the the SouthWest
            if(grid[x-i][y+i] instanceof Body){
                newInputs[5] = i;
                break;
            }
        }
        System.out.printf("   Looking %10s: distance is %d%n", "SouthWest", newInputs[5]);

        stop = x;                                                                       //Look to the West
        newInputs[6] = stop + 1;
        System.out.printf("Looking %10s: max distance is %d%n", "West", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x-i][y] instanceof Body){
                newInputs[6] = i;
                break;
            }
        }
        System.out.printf("   Looking %10s: distance is %d%n", "West", newInputs[6]);

        stop = Math.min(y, x);                                                          //Look to the NorthWest
        newInputs[7] = stop + 1;
        System.out.printf("Looking %10s: max distance is %d%n", "NorthWest", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x-i][y-i] instanceof Body){
                newInputs[7] = i;
                break;
            }
        }
        System.out.printf("   Looking %10s: distance is %d%n", "NorthWest", newInputs[7]);
        //End looking for snake body---------------------

        newInputs[8] = y;                               //North wall
        newInputs[9] = gameGrid.gridMat.length - 1 - x;     //East wall
        newInputs[10] = gameGrid.gridMat.length - 1 - y;    //South wall
        newInputs[11] = x;                              //West wall

        newInputs[12] = food.x - snake1.x;              //x vector to food, if food is to the right of snake, positive
        newInputs[13] = food.y - snake1.y;              //y vector to food, if food is below the snake, positive

        System.out.printf("[%s]%n%n", printArr(newInputs));

        return newInputs;
    }
    public static String printArr(int[] arr){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            str.append(arr[i] + " ");
        }
        return str.toString();
    }
    public static String printArr(Object[][] arr){
        StringBuilder str = new StringBuilder();
        for(int c = 0; c < arr.length; c++){
            for(int r = 0; r < arr[c].length; r++){
                str.append(String.format("%18s,", arr[r][c] + " "));
            }
            str.append("\n");
        }
        return str.toString();
    }
}