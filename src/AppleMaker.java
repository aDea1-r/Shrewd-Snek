import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AppleMaker extends Actor {
    public int foodVal = 1;     //length snake gains when eats
    private static BufferedImage image;

    public AppleMaker(Color c, Grid grid, int id){
        super(c, -1, -1, grid, id);
        typeID = 2;

        place();

        try {
            image = ImageIO.read(new File("food.png"));
        } catch (IOException e) {
            System.out.println("Image not found");
        }
        int boostedSize = (int)(grid.getSize()*1.45);
        image = createResizedCopy(image,boostedSize,boostedSize);
    }
    public int eat(SnakeHead other){       //Method will be called by snakeHead when it eats this apple
        place();
        other.grow(foodVal);
        this.act(null);
        System.out.printf("Snake id: %d, has eaten Apple id: %d, and grown by %d%n", other.id, this.id, foodVal);
        return foodVal;
    }
    private void place() { //assigns x and y positions a value
        try {
            grid.gridMat[x][y] = null;
        } catch (IndexOutOfBoundsException e) {}

        do {
            x = (int)(Math.random()*grid.numSquares);
            y = (int)(Math.random()*grid.numSquares);
        }
        while(grid.gridMat[x][y] != null);
        grid.gridMat[x][y] = this;
    }

    @Override
    public boolean act(Map<Integer, Boolean> inputs) {
        //TODO: this
        return false;
    }

    @Override
    public void drawMe(Graphics g) {
        grid.drawImage(g,this,image);
    }

    private static BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight)
    {
//        System.out.println("resizing...");
        int imageType = BufferedImage.TYPE_INT_RGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }
}
