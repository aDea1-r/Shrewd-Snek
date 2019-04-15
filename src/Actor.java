import java.awt.*;
import java.util.*;

public abstract class Actor implements Drawable {
    public Color color;
    public int x;      //in terms of grid
    public int y;      //in terms of grid
    protected Grid grid;
    public int id;
//    public int typeID;      //1 is snakehead, 2 is appleMaker
    public Actor(Color c, int x, int y, Grid grid, int id){
        color = c;
        this.x = x;
        this.y = y;
        this.grid = grid;
        this.id = id;
    }
    public abstract boolean act(Map<Integer, Boolean> inputs);
}
