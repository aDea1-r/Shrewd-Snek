import java.awt.*;
import java.util.Map;

public class Body extends Actor {
    int id;
    public Body(Color c, int x, int y, Grid grid, int id){
        super(c, x, y, grid, id);
        id = id;
    }

    public void kill(){
        grid.gridMat[x][y] = null;
    }

    @Override
    public boolean act(Map<Integer, Boolean> inputs) {
        return false;
    }

    @Override
    public void drawMe(Graphics g) {
        grid.drawSquare(g, this);
    }
}
