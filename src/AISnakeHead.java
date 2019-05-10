import java.awt.*;

public class AISnakeHead extends SnakeHead {
    private Brain brain;

    AISnakeHead(Color c, int x, int y, Grid grid, int id, ScoreTracker trak, Brain brain) {
        super(c,x,y,grid,id,trak);
        if(brain == null)
            this.brain = new Brain();
        else
            this.brain = brain;
        Game.m.brainToDraw = this.brain;
    }
    boolean act(int[] vision) {
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
//        brain.drawMe(g, grid);
    }

    public Brain getBrain() {
        return brain;
    }
}
