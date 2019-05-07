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
import java.util.concurrent.atomic.AtomicInteger;

public class Generation implements Drawable {

    String speciesName;
    int generationNum;

    private int numPerGeneration;

    private Queue<GameEngine> enginesWaitingToRun;        //Set of engines which are in the queue to run
//    private Set<GameEngine> enginesCurrentlyRunning;    //Set of engines currently being run
    private GameEngine[] enginesCurrentlyRunning;
//    private int enginesCurrentlyRunningLength;
    private final AtomicInteger enginesCurrentlyRunningLength = new AtomicInteger();

    SnakeSorters snekSort;
    private double percentOldToKeep;                //the percent of the previous generation we will keep and mutate

    static int maximumSimultaneousThreads = 200;
    static int maxThreadsToStartAtOnce = 20;
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

        this.numPerGeneration = numPerGeneration;

        pixelWidthProgress = (gameGrid.size*(gameGrid.numSquares-2)) / (double)numPerGeneration;

        //-----------------------------------init
        enginesWaitingToRun = new LinkedList<GameEngine>();
        GameEngineVariableTickRate.genNum = generationNum;
        snekSort = new SnakeSorters(GameEngineVariableTickRate.genNum, numPerGeneration, speciesName);
//        enginesCurrentlyRunning = new HashSet<GameEngine>(maximumSimultaneousThreads);

        enginesCurrentlyRunning = new GameEngine[numPerGeneration];
//        enginesCurrentlyRunningLength = 0;
        enginesCurrentlyRunningLength.set(0);

        if(generationNum == 0){
            for (int i = 0; i < numPerGeneration; i++) {
                GameEngineVariableTickRate temp = new GameEngineVariableTickRate(startXPercent, startYPercent, screenSize, height, width, false, i, null, speciesName);
                enginesWaitingToRun.add(temp);
            }
        }
    }

    public boolean evolve(SnakeSorters snekSort, double percentOldToKeep){
        /*
        Method which is called by GamePanel, clones, mutates, etc a previous gen
         */
        int numToKeep = (int)(percentOldToKeep*numPerGeneration);
        System.out.printf("Beginning of evolve: numToKeep = %d%n", numToKeep);

        int[] numberOfNewGenToDistributeTo = new int[numToKeep];    //indexed based on sorted SnakeSorters

        //proportional distribution
        int totalSpotsLeft = numPerGeneration-numToKeep;            //allocate space for clones
        int proportionalDistribute = totalSpotsLeft/(numToKeep * numToKeep / 2);
        int totalAllocated = 0;
        for (int i = 0; i < numToKeep; i++) {
            numberOfNewGenToDistributeTo[i] = (numToKeep - i)*proportionalDistribute;
            totalAllocated += (numToKeep - i)*proportionalDistribute;
        }
        System.out.printf("Mutate proportional distrib: totalSpotsLeft = %d, propDistrib = %d%n" +
                "arr: [%s]%n", totalSpotsLeft, proportionalDistribute, Arrays.toString(numberOfNewGenToDistributeTo));

        //randomly distribute remained
        int numSpotsLeft = totalSpotsLeft - totalAllocated;
        int[] prefixSums = new int[numToKeep];                                  //Used to get the probability
        prefixSums[0] = numberOfNewGenToDistributeTo[0];
        for (int i = 1; i < numToKeep; i++) {                                   //Compute prefix sums
            prefixSums[i] = prefixSums[i-1] + numberOfNewGenToDistributeTo[i];
        }
        for (int i = 0; i < numSpotsLeft; i++) {
            int ran = (int)(Math.random()*prefixSums[prefixSums.length-1]);
            int correctID = Math.abs( binarySearch(prefixSums, ran, 0, prefixSums.length-1) );
            System.out.printf("Ran is %d, correctID is %d%n", ran, correctID);
            numberOfNewGenToDistributeTo[correctID]++;
            totalAllocated++;
        }
        System.out.printf("Mutate random distrib: numSpotsLeft = %d, propDistrib = %d%n" +
                "prefixSums: [%s]%n" +
                "arr: [%s]%n", numSpotsLeft, proportionalDistribute, Arrays.toString(prefixSums), Arrays.toString(numberOfNewGenToDistributeTo));


        int index = 0;
        //Clone existing
        for (int i = 0; i < numToKeep; i++) {
            SnakeSorter temp = snekSort.getNth(i);

            Brain b = Brain.brainReader( temp.genNum, temp.genID, speciesName);
            GameEngineVariableTickRate engineToAdd = new GameEngineVariableTickRate(startXPercent, startYPercent, screenSize, height, width, false, index, b, speciesName);
            enginesWaitingToRun.add(engineToAdd);

            index++;
        }

        //Mutate based on array
        for (int i = 0; i < numberOfNewGenToDistributeTo.length; i++) {

            SnakeSorter temp = snekSort.getNth(i);

            for (int numNewBrain = 0; numNewBrain < numberOfNewGenToDistributeTo[i]; numNewBrain++) {
                Brain b = Brain.brainReader( temp.genNum, temp.genID, speciesName);
                b.mutate();
                GameEngineVariableTickRate engineToAdd = new GameEngineVariableTickRate(startXPercent, startYPercent, screenSize, height, width, false, index, b, speciesName);
                enginesWaitingToRun.add(engineToAdd);

                index++;

                if (index >= numPerGeneration) {
                    i = numberOfNewGenToDistributeTo.length;
                    break;
                }
            }
        }
        System.out.printf("End of mutate: index = %d%n", index);

        return true;
    }

    public void removeEngine(GameEngineVariableTickRate gm){
//        System.out.printf("Removing GameEngine: %s%n", gm.genID);
        enginesCurrentlyRunning[gm.genID] = null;
        enginesCurrentlyRunningLength.decrementAndGet();
        snekSort.add(new SnakeSorter(gm));
    }

    private void startEngine(){
        GameEngineVariableTickRate temp = (GameEngineVariableTickRate) enginesWaitingToRun.poll();
        enginesCurrentlyRunning[temp.genID] = temp;
        enginesCurrentlyRunningLength.incrementAndGet();
        temp.start();
    }

    public boolean isDone(){
        return enginesCurrentlyRunningLength.get() == 0 && enginesWaitingToRun.isEmpty();
    }

    @Override
    public void drawMe(Graphics g) {

        //Act---------------------------------
        for (int i = 0; i < maxThreadsToStartAtOnce; i++) {
            if (!enginesWaitingToRun.isEmpty() && enginesCurrentlyRunningLength.get() < maximumSimultaneousThreads) {
                startEngine();
            }
        }
        //------------------------------------
        System.out.printf("Species name: %s, enginesCurrentlyRunning = %d%n", speciesName, enginesCurrentlyRunningLength.get());
        
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
        int width = gameGrid.size*(gameGrid.numSquares-2) - (int)(pixelWidthProgress*(enginesWaitingToRun.size() + enginesCurrentlyRunningLength.get()));
        g.fillRect(startX, startY, width, gameGrid.size*2 + 1);
    }

    /*------------------------------------------------------------------------------------------------------------------
    Will search the increasing array, arr for val
    Will return the index of val, or the negative index of where val should be placed if it doesn't exist
    Will return -1 if the index was not found and should be placed at the beginning, since -0 is still 0;
     */
    private int binarySearch(int[] arr, int val, int startIn, int endIn){
        int mid;
        int start = startIn;
        int end = endIn;

        if(val < arr[startIn]){
            return 0;
        }

        while(start <= end){

            mid = (start + end)/2;                          //Initialize mid

            int dirTest = val - (arr[mid]);

            if(dirTest < 0){                                //if val is smaller
                end = mid - 1;                                  //change range, go to the left

                if(mid - 1 >= startIn && val - (arr[mid-1]) > 0){     //if val is in between mid and mid - 1
                    return -mid;
                }
                else if(mid == startIn){                            //if val is smaller, but we are at beginning of the array
                   if(mid != 0)
                        return -mid;
                    else                                                //Edge case since -0 is 0
                        return -1;
                }
            }
            else if(dirTest > 0){                           //if val is bigger
                start = mid + 1;                                //change range, go to the right

                if(mid + 1 < endIn && val - (arr[mid+1]) < 0){        //if val is in between mid and mid + 1
                    return -mid - 1;
                }
                else if(mid == endIn - 1){                          //if val is bigger, but we are at end of the array
                    return -mid - 1;
                }
            }
            else if(dirTest == 0){
                return mid;
            }
        }
        return Integer.MIN_VALUE;
    }
    //------------------------------------------------------------------------------------------------------------------
}
