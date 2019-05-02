import java.awt.*;

public class GameEngineVariableTickRate extends GameEngine implements Runnable {
    private Thread thread;
    int genID; //ID of engine in current generation
    static int genNum; //current generation number

    String speciesName;

    GameEngineVariableTickRate(double startXPercent, double startYPercent, double screenSize, int height, int width, boolean upi, int genID, Brain brain, String speciesName) {
        super(startXPercent,startYPercent,screenSize,height,width,upi, brain);
        thread = new Thread(this,"Brain "+genNum);
        this.genID = genID;

        food = new AppleMaker(Color.BLACK,gameGrid,2,genNum,this.genID, speciesName);
        drawables.add(food);

        this.speciesName = speciesName;
    }
    //like action preformed
    public void run() {
        while(gameRunning)
            gameTick();
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
        gameRunning = false;
//        System.out.println("Dead is "+genID);
//        System.out.printf("Score is: %d, fitness is %f%n", scoreTracker.getScore(), scoreTracker.getFitness());
        ((AISnakeHead)snake1).getBrain().log(genNum,genID, speciesName);
        Game.m.killAnEngine(this);
    }
}
