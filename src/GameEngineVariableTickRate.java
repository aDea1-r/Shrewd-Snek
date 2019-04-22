public class GameEngineVariableTickRate extends GameEngine implements Runnable {
    private Thread thread;
    int genID; //ID of engine in current generation
    static int genNum; //current generation number

    GameEngineVariableTickRate(double startXPercent, double startYPercent, double screenSize, int height, int width, boolean upi) {
        super(startXPercent,startYPercent,screenSize,height,width,upi);
        thread = new Thread(this);
        thread.start();
    }
    //like action preformed
    public void run() {
        while(gameRunning)
            gameTick();
    }
    void gameTick(){
        if(!usePlayerInput && !((AISnakeHead)snake1).act(lookAround(snake1)))
            kill();
        scoreTracker.act(null);
    }
    void kill() {
        gameRunning = false;
        System.out.println("Dead is "+genID);
        ((AISnakeHead)snake1).getBrain().log(genNum,genID);
    }
}
