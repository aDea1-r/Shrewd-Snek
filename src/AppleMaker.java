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
        other.grow(foodVal);
        this.act(null);
        System.out.printf("Snake id: %d, has eaten Apple id: %d, and grown by %d%n", other.id, this.id, foodVal);
        return foodVal;
    }

    @Override
    public boolean act(Map<Integer, Boolean> inputs) {
        //TODO: this
        return false;
    }

    @Override
    public void drawMe(Graphics g) {
        //TODO: this
    }
}
