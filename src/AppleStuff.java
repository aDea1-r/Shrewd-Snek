import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class AppleStuff extends Actor implements Drawable {
    private final int foodVal = 1;     //length snake gains when eats
    private static BufferedImage image;
    String speciesName;

    AppleStuff(Color c, Grid grid, int id, String speciesName){
        super(c, -1, -1, grid, id);
//        typeID = 2;
        this.speciesName = speciesName;

        if(image==null) {
            try {
                image = ImageIO.read(new File("food.png"));
            } catch (IOException e) {
                System.out.println("Image not found");
            }
            int boostedSize = (int)(grid.getSize()*1.45);
            image = createResizedCopy(image,boostedSize,boostedSize);
        }
    }
    void eat(SnakeHead other){       //Method will be called by snakeHead when it eats this apple
        this.place();
        other.grow(foodVal);
//        System.out.printf("Snake id: %d, has eaten Apple id: %d, and grown by %d%n",
//                other.id,
//                this.id,
//                foodVal);
    }

    abstract void place();

    @Override
    public boolean act(Map<Integer, Boolean> inputs) {
        //TODO: this
        System.out.printf("AppleMaker id %d, act called, this is a mistake%n", id);
        return false;
    }

    @Override
    public void drawMe(Graphics g) {
        grid.drawImage(g,this,image);
    }

    static BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight)
    {
//        System.out.println("resizing...");
        int imageType = BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }
}
