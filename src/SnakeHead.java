import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class SnakeHead extends Actor implements Drawable {
    LinkedList<Body> bodyParts;
    int length;
    int direction;  //0 = north, 1 = east, ect
    private ScoreTracker scoreTracker;

    SnakeHead(Color c, int x, int y, Grid grid, int id, ScoreTracker score){
        super(c, x, y, grid, id);
        bodyParts = new LinkedList<>();
        length = 4;
        direction = 1;
//        typeID = 1;
        scoreTracker = score;
    }
    void reset(){
        x = 0;
        y = 0;
    }

    public SnakeHead() {
        this(Color.CYAN, 0, 0, new Grid(0,0,0,0,null), 0, null);
    }

    boolean move(){
//        System.out.printf("Direction = %d%n", direction);
        if(direction == 0){
            if(y > 0 && !(grid.gridMat[x][y-1] instanceof Body)){
                y = y - 1; //north
                return true;
            }
        }
        else if(direction == 1){
            if(x < grid.gridMat.length - 1 && !(grid.gridMat[x+1][y] instanceof Body)){
                x = x + 1; //east
                return true;
            }
        }
        else if(direction == 2){
            if(y < grid.gridMat.length - 1 && !(grid.gridMat[x][y+1] instanceof Body)){
                y = y + 1; //south
                return true;
            }
        }
        else if(direction == 3){
            if(x > 0 && !(grid.gridMat[x-1][y] instanceof Body)){
                x = x - 1; //west
                return true;
            }
        }
//        System.out.printf("Snake id = %d, cannot move in direction %d%n", id, direction);
        return false;
    }

    @Override
    public boolean act(Map<Integer, Boolean> inputs) {
//        System.out.println(inputs);
        if(inputs.get((int)'w') || inputs.get((int)'W')){
            if(direction != 2)
                direction = 0;
//            System.out.printf("Direction changed to %d%n", 0);
            inputs.put((int)'w', false);
            inputs.put((int)'W', false);
        }
        else if(inputs.get((int)'d') || inputs.get((int)'D')){
            if(direction != 3)
                direction = 1;
//            System.out.printf("Direction changed to %d%n", 1);
            inputs.put((int)'d', false);
            inputs.put((int)'D', false);
        }
        else if(inputs.get((int)'s') || inputs.get((int)'S')){
            if(direction != 0)
                direction = 2;
//            System.out.printf("Direction changed to %d%n", 2);
            inputs.put((int)'s', false);
            inputs.put((int)'S', false);
        }
        else if(inputs.get((int)'a') || inputs.get((int)'A')){
            if(direction != 1)
                direction = 3;
//            System.out.printf("Direction changed to %d%n", 3);
            inputs.put((int)'a', false);
            inputs.put((int)'A', false);
        }

        if(!move())
            return false;
        makeBody();
        if(bodyParts.size() > length) {
            Body temp = bodyParts.poll();
            temp.kill();
        }
        return true;
    }

    @Override
    public void drawMe(Graphics g) {
        for (Body temp : bodyParts) {
            temp.drawMe(g);
        }
    }

    void grow(int growBy){     //increases length
        length += growBy;
        scoreTracker.ate();
    }

    void makeBody(){
        Body temp = new Body(color ,x, y, grid, id);
        bodyParts.add(temp);
        if(grid.gridMat[x][y] == null) {
            grid.gridMat[x][y] = temp;
//            return true;
        } else if(grid.gridMat[x][y] instanceof AppleStuff) {
            ((AppleStuff)(grid.gridMat[x][y])).eat(this);
            grid.gridMat[x][y] = temp;
//            return true;
        }
        else{
            System.out.printf("Something went wrong at SnakeHead id = %d, makeBody()%n", id);
//            return false;
        }
    }
}
