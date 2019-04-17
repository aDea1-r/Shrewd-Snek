import java.awt.*;
import java.util.Map;

public class AISnakeHead extends SnakeHead {

    public AISnakeHead(Color c, int x, int y, Grid grid, int id) {
        super(c,x,y,grid,id);
    }
    public boolean act(double[] inputs) {
        int maxSpot = 0;
        for(int i=1; i<inputs.length; i++) {
            if(inputs[i]>inputs[maxSpot])
                maxSpot = i;
        }
        direction = maxSpot;
        if(!move())
            return false;
        makeBody();
        if(bodyParts.size() > length) {
            Body temp = bodyParts.poll();
            temp.kill();
        }
        return true;
    }
}
