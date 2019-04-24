import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class GameEngineFixedTickRate extends GameEngine implements ActionListener {
    private Timer time;
    static int refreshRate = 60;        //Delay in milliseconds between game ticks

    private Map<Integer, Boolean> inputs;

    GameEngineFixedTickRate(double startXPercent, double startYPercent, double screenSize, int height, int width, Map<Integer, Boolean> inputs, boolean upi) {
        super(startXPercent,startYPercent,screenSize,height,width,upi);

        super.food = new AppleMaker(Color.BLACK,gameGrid,2);
        drawables.add(food);

        time = new Timer(refreshRate, this); //sets delay to 15 millis and calls the actionPerformed of this class.

        this.inputs = inputs;
        //------------------------------- Snake 1 Code
        inputs.put((int)'w', false);
        inputs.put((int)'d', false);
        inputs.put((int)'s', false);
        inputs.put((int)'a', false);
        inputs.put((int)'W', false);
        inputs.put((int)'D', false);
        inputs.put((int)'S', false);
        inputs.put((int)'A', false);

        time.start();
    }
    void gameTick(){
        if(!usePlayerInput && !((AISnakeHead)snake1).act(lookAround(snake1)))
            kill();
        else if (usePlayerInput && !snake1.act(inputs))
            kill();
        scoreTracker.act(inputs);
    }
    void kill() {
        time.stop();
        System.out.printf("Score is: %d, fitness is %f%n", scoreTracker.getScore(), scoreTracker.getFitness());
        gameRunning = false;
    }
    public void actionPerformed(ActionEvent e)
    {
        gameTick();
    }
}
