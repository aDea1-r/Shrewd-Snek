import java.awt.*;
import java.util.*;

public abstract class Actor {
    Color color;
    public int x;      //in terms of grid
    int y;      //in terms of grid
    Grid grid;
    int id;
//    public int typeID;      //1 is snakehead, 2 is appleMaker
    Actor(Color c, int x, int y, Grid grid, int id){
        color = c;
        this.x = x;
        this.y = y;
        this.grid = grid;
        this.id = id;
    }
    Actor() {
        this(null,0,0,null,0);
    }
    public abstract boolean act(Map<Integer, Boolean> inputs);
}
