import java.util.Map;

public class ScoreTracker extends Actor {
    private GameEngine game;
    private double score;
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
        if(idleTickCount*GameEngine.refreshRate > 10+score*.1)
            game.kill();
        return true;
    }
    public void ate() {
        score++;
        idleTickCount = 0;
    }
    public double getScore() {
        double duration = tickCount*GameEngine.refreshRate;
        double maxDuration = 10 - 10*Double.MIN_VALUE;
        return score + Math.min(duration,maxDuration)/10;
    }
}
