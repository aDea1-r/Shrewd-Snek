import java.awt.*;

public class GameEngineVariableTickRate extends GameEngine implements Runnable {
    private Thread thread;
    int genID; //ID of engine in current generation
    static int genNum; //current generation number

    String speciesName;

    double[] fitnessScores;     //Tracks fitness scores of previous runs
    int numTimesRun;            //Tracks number of times it has run already
    int targetNumTimesRun;

    GameEngineVariableTickRate(double startXPercent, double startYPercent, double screenSize, int height, int width, boolean upi, int genID, Brain brain, String speciesName, int targetNumTimesRun) {
        super(startXPercent,startYPercent,screenSize,height,width,upi, brain);
        thread = new Thread(this,"Brain "+genNum);
        this.genID = genID;

        food = new AppleMaker(Color.BLACK,gameGrid,2,genNum,this.genID, speciesName);
        drawables.add(food);

        this.speciesName = speciesName;

        this.targetNumTimesRun = targetNumTimesRun;
        numTimesRun = 0;
        fitnessScores = new double[targetNumTimesRun];
    }
    //like action preformed
    public void run() {
        while(gameRunning) {
//            try {
//                thread.sleep(10000);
//            }
//            catch (Exception e){
//                System.out.printf("%s at GameEngineVariableTickRate%n", e.toString());
//            }
            gameTick();
        }
    }
    void start() {
        thread.start();
    }
    void gameTick(){
        if(!usePlayerInput && !((AISnakeHead)snake1).act(lookAround(snake1)))
            kill();
        scoreTracker.act(null);
    }
    void kill() {
        if(numTimesRun >= targetNumTimesRun) {
            gameRunning = false;
//        System.out.println("Dead is "+genID);
//        System.out.printf("Score is: %d, fitness is %f%n", scoreTracker.getScore(), scoreTracker.getFitness());
            ((AISnakeHead) snake1).getBrain().log(genNum, genID, speciesName);
            Game.m.killAnEngine(this);
        }
        else{
//            System.out.printf("Run %d finished, proceeding%n", numTimesRun);
            fitnessScores[numTimesRun] = scoreTracker.getFitness();
            reset();
            numTimesRun++;
        }
    }
    double getFitness(){
        double sum = 0;
        double max = 0;
//        System.out.printf("getFitness of engine ID #%d called%n", genID);
        for (int i = 0; i < fitnessScores.length; i++) {
//            System.out.printf(" %d-ith fitness was %f%n", i, fitnessScores[i]);
            sum += fitnessScores[i];
            if(fitnessScores[i]>max)
                max = fitnessScores[i];
        }
        sum -= max;
        return sum/(fitnessScores.length-1);
    }
}
