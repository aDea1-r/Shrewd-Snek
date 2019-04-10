import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AppleMaker extends Actor {
    public int foodVal;     //length snake gains when eats
    private static BufferedImage image;

    public AppleMaker(Color c, Grid grid, int id, int foodVal){
        super(c, -1, -1, grid, id);
        typeID = 2;

        try {
            image = ImageIO.read(new File("food.png"));
        } catch (IOException e) {
            System.out.println("Image not found");
        }
        image = createResizedCopy(image,grid.getSize(),grid.getSize());
    }
    public int eat(SnakeHead other){       //Method will be called by snakeHead when it eats this apple
        x = -1;
        y = -1;
        other.grow(foodVal);
        this.act(null);
        System.out.printf("Snake id: %d, has eaten Apple id: %d, and grown by %d%n", other.id, this.id, foodVal);
        return foodVal;
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

    public static BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight)
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
