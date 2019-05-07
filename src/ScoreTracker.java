import java.util.Map;
import java.util.concurrent.Callable;

public class ScoreTracker extends Actor {
    private GameEngine game;
    private int score;
    private int tickCount;
    private int idleTickCount;
    static int timeOutTime = 267;         //The time it takes to die from timeout

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
//        System.out.printf("Scoretracker getFit called score = %d, idleTickCount = %d%n", score, idleTickCount);
        double duration = idleTickCount*2;
        double maxDuration = timeOutTime - Double.MIN_VALUE;
        double timePoints = (Math.min(duration,maxDuration)/timeOutTime);
//        double duration = tickCount*2;                 //multiplier makes each time step worth more
//        double maxDuration = timeOutTime*3;             //cap of points gained by time
//        double timePoints = (Math.min(duration,maxDuration)/timeOutTime);
        return score + timePoints;
    }
    int getScore() {
        return score;
    }
}
