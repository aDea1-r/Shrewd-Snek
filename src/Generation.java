/*
README: This will be the class for a single generation of snakes
Some of this code used to be in GamePanel, removed and put her because of organization and the need for many global variable
In addition, will sync up with repaint() calls instead of long for loops so that screen doesn't freeze
Splits game engines into two sets to facilitate max simultaneous thread numbers
Can be drawn:
    Progress Bar
 */

import java.util.*;
import java.awt.*;

public class Generation implements Drawable {

    private String speciesName;
    private int generationNum;

    private static int numPerGeneration = 1000;

    private Queue<GameEngine> enginesWaitingToRun;        //Set of engines which are in the queue to run
    private Set<GameEngine> enginesCurrentlyRunning;    //Set of engines currently being run

    private SnakeSorters snekSort;
    private double percentOldToKeep;                //the percent of the previous generation we will keep and mutate

    static int maximumSimultaneousThreads = 50;
    static int maxThreadsToStartAtOnce = 5;
        //TODO: glitch where one or two gameEngines never finish
        //TODO: seems to occur more when this variable is bigger, possibly due to simultaneous running?
        //TODO: Glitch seems fixed? NVM its still broken


    //Drawing stuff--------------------------------------------
    private double pixelWidthProgress;                 //Stores the pixel width of each progress bar

    private int width;
    private int height;
    private double startXPercent;          //% of the total screen which the play screen will start at
    private double startYPercent;          //% of the total screen which the play screen will start at
    private double screenSize;      //% of the total screen which the play screen will take up
    private Grid gameGrid;

    public Generation(double startXPercent, double startYPercent, double screenSize, int height, int width, String speciesName, int generationNum, int numPerGeneration){

        this.startXPercent = startXPercent;
        this.startYPercent = startYPercent;
        this.screenSize = screenSize;
        this.height = height;
        this.width = width;

        //Grid----------------------------
        int startX = (int)(width * startXPercent);
        int startY = (int)(height * startYPercent);

        int gridSize = Math.max(Math.min((int)(height * screenSize), (int)(width * screenSize))/GameEngine.numSquares, 1);

        gameGrid = new Grid(startX, startY, gridSize, GameEngine.numSquares, Color.BLACK);
        //---------------

        this.speciesName = speciesName;
        this.generationNum = generationNum;

        Generation.numPerGeneration = numPerGeneration;

        pixelWidthProgress = (gameGrid.size*(gameGrid.numSquares-2)) / (double)numPerGeneration;

        //-----------------------------------init
        enginesWaitingToRun = new LinkedList<GameEngine>();
        GameEngineVariableTickRate.genNum = generationNum;
        snekSort = new SnakeSorters(GameEngineVariableTickRate.genNum, numPerGeneration);
        enginesCurrentlyRunning = new HashSet<GameEngine>(maximumSimultaneousThreads);

        if(generationNum == 0){
            for (int i = 0; i < numPerGeneration; i++) {
                GameEngineVariableTickRate temp = new GameEngineVariableTickRate(startXPercent, startYPercent, screenSize, height, width, false, i, null);
                enginesWaitingToRun.add(temp);
            }
        }
    }

    public void removeEngine(GameEngineVariableTickRate gm){
//        System.out.printf("Removing GameEngine: %s%n", gm.genID);
        enginesCurrentlyRunning.remove(gm);
        snekSort.add(new SnakeSorter(gm));
    }

    private void startEngine(){
        GameEngineVariableTickRate temp = (GameEngineVariableTickRate) enginesWaitingToRun.poll();
        enginesCurrentlyRunning.add(temp);
        temp.start();
    }

    public boolean isDone(){
        return enginesCurrentlyRunning.isEmpty() && enginesWaitingToRun.isEmpty();
    }

    @Override
    public void drawMe(Graphics g) {

        //Act---------------------------------
//        for (int i = 0; i < maxThreadsToStartAtOnce; i++) {
        //TODO: for loop commented out because of weird glitch
            if (!enginesWaitingToRun.isEmpty() && enginesCurrentlyRunning.size() < maximumSimultaneousThreads) {
                //IMPORTANT: cannot call method here because it causes glitch noted above
//                startEngine();
                GameEngineVariableTickRate temp = (GameEngineVariableTickRate) enginesWaitingToRun.poll();
                enginesCurrentlyRunning.add(temp);
                temp.start();
            }
//        }

        //TODO: While loop seems to have fixed it, needs more testing tho
        while (!enginesWaitingToRun.isEmpty() && enginesCurrentlyRunning.size() < maximumSimultaneousThreads){
            startEngine();
        }
        //------------------------------------
        System.out.printf("Species name: %s, enginesCurrentlyRunning = %d, active thread count = %s%n", speciesName, enginesCurrentlyRunning.size(),Thread.activeCount());
        if(enginesCurrentlyRunning.size() == 1 || enginesCurrentlyRunning.size() == 2 || enginesCurrentlyRunning.size() == 3) {
            Iterator itr = enginesCurrentlyRunning.iterator();
            if(itr.hasNext())
                System.out.println(itr.next().toString());
            else
                System.out.println(enginesCurrentlyRunning);
        }

        gameGrid.drawMe(g);

        g.setColor(Color.yellow);
        int fontSize = gameGrid.size;
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        String title = String.format("Species: %s", speciesName);
        g.drawString(title, gameGrid.getXPixels(gameGrid.numSquares/2) - (title.length()*gameGrid.size/4), gameGrid.getYPixels(2) + gameGrid.size/2);

        g.setColor(Color.CYAN);
        int startX = gameGrid.getXPixels(1);
        int startY = gameGrid.getYPixels(3);
        //Draws border of bounding box
        g.drawRect(startX - 1, startY - 1, gameGrid.size*(gameGrid.numSquares-2) + 2, gameGrid.size*2 + 2);

        //Draws progress bar
        int width = gameGrid.size*(gameGrid.numSquares-2) - (int)(pixelWidthProgress*(enginesWaitingToRun.size() + enginesCurrentlyRunning.size()));
        g.fillRect(startX, startY, width, gameGrid.size*2);
    }
}
