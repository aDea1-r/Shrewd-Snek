import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class AppleReader extends AppleStuff{
    private BufferedReader buff;

    AppleReader(Color c, Grid grid, int id, int generation, int brainID, String speciesName) {
        super(c,grid,id, speciesName);
        try {
            String path = String.format("Training Data/%s/%d/%d/apples.dat", speciesName, generation, brainID);
//            buff = new BufferedReader(new FileReader("Training Data/" +generation + "/" + brainID + "/apple.dat"));
            buff = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            System.out.printf("Training Data/%s/%d/%d/apples.dat not found%n", speciesName, generation, brainID);
        }
        this.place();
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
