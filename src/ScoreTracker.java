import java.util.Map;
import java.util.concurrent.Callable;

public class ScoreTracker extends Actor {
    private GameEngine game;
    private int score;
    private int tickCount;
    private int idleTickCount;
    static int timeOutTime = 167;         //The time it takes to die from timeout

    ScoreTracker(GameEngine game) {
        this.game = game;
        score = 0;
        tickCount = 0;
        idleTickCount = 0;
    }
    public boolean act(Map m) {
        tickCount++;
        idleTickCount++;
//        System.out.println(idleTickCount*GameEngine.refreshRate/1000.0);
        if(idleTickCount > timeOutTime+score*(timeOutTime*0.1))
            game.kill();
        return true;
    }

    void ate() {
//        System.out.println("i ate");
        score++;
        idleTickCount = 0;
    }
    public double getFitness() {
        System.out.printf("Scoretracker getFit called score = %d, idleTickCount = %d%n", score, idleTickCount);
        double duration = idleTickCount;
        double maxDuration = timeOutTime - timeOutTime*Double.MIN_VALUE;
        return score + Math.min(duration,maxDuration)/timeOutTime;
    }
    int getScore() {
        return score;
    }
}
