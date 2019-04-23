import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AppleReader extends AppleStuff{
    private BufferedReader buff;

    AppleReader(Color c, Grid grid, int id, int generation, int brainID) {
        super(c,grid,id);
        try {
            buff = new BufferedReader(new FileReader("/" + generation + "/" + brainID + "/apple.dat"));
        } catch (FileNotFoundException e) {
            System.out.println("Apple log not found");
        }
    }
    void place() {
        try {
            grid.gridMat[x][y] = null;
        } catch (IndexOutOfBoundsException e) {
            System.out.print("");
        }
        try {
            String[] line = buff.readLine().split(" ");
            x = Integer.parseInt(line[0]);
            y = Integer.parseInt(line[1]);
        } catch (IOException e) {
            System.out.println("IO Exception. This is very very bad");
        }
        grid.gridMat[x][y] = this;
    }
}
