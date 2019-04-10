import java.awt.*;
import java.util.*;

public class AppleMaker extends Actor {
    public int foodVal;     //length snake gains when eats
    public AppleMaker(Color c, Grid grid, int id, int foodVal){
        super(c, -1, -1, grid, id);
        typeID = 2;
    }
    public int eat(SnakeHead other){       //Method will be called by snakeHead when it eats this apple
        x = -1;
        y = -1;
        other.
        this.act(null);
    }
}
