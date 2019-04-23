import java.awt.*;

public class AISnakeHead extends SnakeHead {
    private Brain brain;

    public AISnakeHead(Color c, int x, int y, Grid grid, int id, ScoreTracker trak) {
        super(c,x,y,grid,id,trak);
        this.brain = new Brain();
    }
    public boolean act(int[] vision) {
        double[] inputs = brain.compute(vision);
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
            if(temp!=null)
            temp.kill();
        }
        return true;
    }

    @Override
    public void drawMe(Graphics g){
        super.drawMe(g);
        brain.drawMe(g, grid);
    }
}
