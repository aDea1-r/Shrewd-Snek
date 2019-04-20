import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

class AppleMaker extends AppleStuff {
    PrintWriter log;
    AppleMaker(Color c, Grid grid, int id) {
        super(c,grid,id);
    }
    AppleMaker(Color c, Grid grid, int id, int generation, int brainID) {
        super(c,grid,id);
        try {
            log = new PrintWriter(new PrintWriter("/"+generation+"/"+brainID+"/apples.dat"),true);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open/make file");
        }
    }
    void place() { //assigns x and y positions a value
        int x1 = x;
        int y1 = y;
        try {
            grid.gridMat[x][y] = null;
        } catch (IndexOutOfBoundsException e) {
            System.out.print("");
        }

        do {
            x = (int)(Math.random()*grid.numSquares);
            y = (int)(Math.random()*grid.numSquares);
        }
        while(grid.gridMat[x][y] != null || (x1==x && y1==y));
        grid.gridMat[x][y] = this;
        if(log!=null)
            log.println(x+" "+y);
    }
}