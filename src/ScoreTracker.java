import java.util.Map;

public class ScoreTracker extends Actor {
    private GameEngine game;
    private int score;
    private int tickCount;
    private int idleTickCount;

    public ScoreTracker(GameEngine game) {
        this.game = game;
        score = 0;
        tickCount = 0;
        idleTickCount = 0;
    }
    public boolean act(Map m) {
        tickCount++;
        idleTickCount++;
        System.out.println(idleTickCount*GameEngine.refreshRate/1000.0);
        if(idleTickCount*GameEngine.refreshRate/1000.0 > 10+score*.1)
            game.kill();
        return true;
    }
    public void ate() {
//        System.out.println("i ate");
        score++;
        idleTickCount = 0;
    }
    public double getFitness() {
        double duration = tickCount*GameEngine.refreshRate;
        double maxDuration = 10 - 10*Double.MIN_VALUE;
        return score + Math.min(duration,maxDuration)/10;
    }
    public int getScore() {
        return score;
    }
}
