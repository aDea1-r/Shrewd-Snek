import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

class AppleMaker extends AppleStuff {
    private PrintWriter log;

    AppleMaker(Color c, Grid grid, int id, String speciesName) {
        super(c,grid,id, speciesName);
        this.place();
    }
    AppleMaker(Color c, Grid grid, int id, int generation, int brainID, String speciesName) {
        super(c,grid,id, speciesName);
        String path = String.format("Training Data/%s/%d/%d/", speciesName, generation, brainID);
//        File file = new File("Training Data/"+ generation + "/" + brainID + "/");
        File file = new File(path);
        file.mkdirs();
        try {
            log = new PrintWriter(new PrintWriter(path + "apples.dat"),true);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open/make file");
        }
        this.place();
    }
    void place() { //assigns x and y positions a value
        int x1 = x;
        int y1 = y;
        try {
            grid.gridMat[x][y] = null;
        } catch (IndexOutOfBoundsException e) {
            System.out.print(e);
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