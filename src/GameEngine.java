import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class GameEngine implements Drawable {

    final static int numSquares = 35;          //The size of the grid taking up the play screen

    Grid gameGrid;
    ScoreTracker scoreTracker;

    AppleStuff food;
    SnakeHead snake1;

    LinkedList<Drawable> drawables = new LinkedList<>();

    boolean gameRunning;
    boolean usePlayerInput;


    GameEngine(double startXPercent, double startYPercent, double screenSize, int height, int width, boolean upi, Brain brain)
    {
        usePlayerInput = upi;

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
        if(usePlayerInput)
            snake1 = new SnakeHead(Color.CYAN, gameGrid.numSquares/3, gameGrid.numSquares/2, gameGrid, 1,scoreTracker);
        else
            snake1 = new AISnakeHead(Color.CYAN, gameGrid.numSquares/3, gameGrid.numSquares/2, gameGrid, 1,scoreTracker, brain);

        drawables.add(gameGrid);
        drawables.add(snake1);

        gameRunning = true;
    }
    public GameEngine(){
        this(0, 0, 0, 1, 1, false, null);
    }

    void reset(){
        //Re-initializes variables to run game again
        gameGrid.reset();
        scoreTracker.reset();
        snake1.reset();
    }

    abstract void gameTick();

    abstract void kill();

    public void drawMe(Graphics g) {

        Iterator it = drawables.listIterator();
        Drawable tempDrawable;
        while (it.hasNext()){
            tempDrawable = (Drawable) it.next();
            if(tempDrawable!=null) {
                tempDrawable.drawMe(g);
            }
            else
                System.out.println(drawables);
        }
        g.setColor(Color.cyan);
        g.setFont(new Font("TimesRoman", Font.BOLD, gameGrid.size*2));
        int xOffset = gameGrid.size/2 + ( (Integer.toString(scoreTracker.getScore()).length() - 1) * gameGrid.size);
        g.drawString("" + scoreTracker.getScore(), gameGrid.getXPixels(-1)-xOffset, gameGrid.getYPixels(1));

        if(!gameRunning) {
            Color ctemp = g.getColor();
            Font ftemp =g.getFont();

            g.setColor(Color.RED);//SUBLIME
            g.setFont(new Font("TimesRoman", Font.BOLD, (gameGrid.size)*2));
            g.drawString("Thou art slain",gameGrid.getXPixels(gameGrid.numSquares/4),gameGrid.getXPixels(gameGrid.numSquares/2));

//            System.out.printf("Score is: %d, fitness is %f%n", scoreTracker.getScore(), scoreTracker.getFitness());
//            g.setFont(new Font("TimesRoman", Font.BOLD, (gameGrid.size*gameGrid.numSquares)/12));
//            g.drawString(str,gameGrid.getXPixels(gameGrid.numSquares/4),gameGrid.getXPixels(gameGrid.numSquares/2 + gameGrid.numSquares/5));

            g.setColor(ctemp);
            g.setFont(ftemp);
        }
    }

    //method which updates the snake's vision
    int[] lookAround(SnakeHead head){
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
//        System.out.printf("Looking %10s: max distance is %d%n", "North", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x][y-i] instanceof Body){
                newInputs[0] = i;
                break;
            }
        }
//        System.out.printf("   Looking %10s: distance is %d%n", "North", newInputs[0]);

        stop = Math.min(y, gameGrid.gridMat.length - x - 1);                                //Look to the NorthEast
        newInputs[1] = stop + 1;
//        System.out.printf("Looking %10s: max distance is %d%n", "NorthEast", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x+i][y-i] instanceof Body){
                newInputs[1] = i;
                break;
            }
        }
//        System.out.printf("   Looking %10s: distance is %d%n", "NorthEast", newInputs[1]);

        stop = gameGrid.gridMat.length - 1 - x;                                             //Look to the East
        newInputs[2] = stop + 1;
//        System.out.printf("Looking %10s: max distance is %d%n", "East", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x+i][y] instanceof Body){
                newInputs[2] = i;
                break;
            }
        }
//        System.out.printf("   Looking %10s: distance is %d%n", "East", newInputs[2]);

        stop = Math.min(gameGrid.gridMat.length - 1 - y, gameGrid.gridMat.length - 1 - x);      //Look to the SouthEast
        newInputs[3] = stop + 1;
//        System.out.printf("Looking %10s: max distance is %d%n", "SouthEast", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x+i][y+i] instanceof Body){
                newInputs[3] = i;
                break;
            }
        }
//        System.out.printf("   Looking %10s: distance is %d%n", "SouthEast", newInputs[3]);

        stop = gameGrid.gridMat.length - 1 - y;                                             //Look to the South
        newInputs[4] = stop + 1;
//        System.out.printf("Looking %10s: max distance is %d%n", "South", stop);
        for (int i = 1; i < stop; i++) {       //Look the the South
            if(grid[x][y+i] instanceof Body){
                newInputs[4] = i;
                break;
            }
        }
//        System.out.printf("   Looking %10s: distance is %d%n", "South", newInputs[4]);

        stop = Math.min(gameGrid.gridMat.length - 1 - y, x);                                //Look to the SouthWest
        newInputs[5] = stop + 1;
//        System.out.printf("Looking %10s: max distance is %d%n", "SouthWest", stop);
        for (int i = 1; i < stop; i++) {       //Look the the SouthWest
            if(grid[x-i][y+i] instanceof Body){
                newInputs[5] = i;
                break;
            }
        }
//        System.out.printf("   Looking %10s: distance is %d%n", "SouthWest", newInputs[5]);

        stop = x;                                                                       //Look to the West
        newInputs[6] = stop + 1;
//        System.out.printf("Looking %10s: max distance is %d%n", "West", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x-i][y] instanceof Body){
                newInputs[6] = i;
                break;
            }
        }
//        System.out.printf("   Looking %10s: distance is %d%n", "West", newInputs[6]);

        stop = Math.min(y, x);                                                          //Look to the NorthWest
        newInputs[7] = stop + 1;
//        System.out.printf("Looking %10s: max distance is %d%n", "NorthWest", stop);
        for (int i = 1; i < stop; i++) {
            if(grid[x-i][y-i] instanceof Body){
                newInputs[7] = i;
                break;
            }
        }
//        System.out.printf("   Looking %10s: distance is %d%n", "NorthWest", newInputs[7]);
        //End looking for snake body---------------------

        newInputs[8] = y;                               //North wall
        newInputs[9] = gameGrid.gridMat.length - 1 - x;     //East wall
        newInputs[10] = gameGrid.gridMat.length - 1 - y;    //South wall
        newInputs[11] = x;                              //West wall

        newInputs[12] = food.x - snake1.x;              //x vector to food, if food is to the right of snake, positive
        newInputs[13] = food.y - snake1.y;              //y vector to food, if food is below the snake, positive

//        System.out.printf("[%s]%n%n", printArr(newInputs));

        return newInputs;
    }
    private static String printArr(int[] arr){
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