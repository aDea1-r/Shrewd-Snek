import java.awt.*;
import java.awt.image.BufferedImage;

public class Grid implements Drawable {
    int startX;
    int startY;
    int size;       //pixel width of a single spot
    int numSquares;
    private Color color = Color.BLACK;

    Object[][] gridMat;  //Let -1 be empty   //is x, y

    Grid(int x, int y, int s, int nS, Color c){
        startX = x;
        startY = y;
        size = s;
        numSquares = nS;
        color = c;
        gridMat = new Object[numSquares][numSquares];
        for (int r = 0; r < gridMat.length; r++) {
            for (int col = 0; col < gridMat[r].length; col++) {
                gridMat[r][col] = null;
            }
        }
    }

    public int getXGrid(int in){       //Gets x position given a pixel location
        return (in - startX) / size;
    }

    public int getYGrid(int in){       //Gets y position given a pixel location
        return (in - startY) / size;
    }

    int getXPixels(int in){      //Gets x pixel location given grid location
        return (in * size) + startX;
    }

    int getYPixels(int in){      //Gets y pixel location given grid location
        return (in * size) + startY;
    }

    void drawSquare(Graphics g, Actor act){               //draws a square on grid based off of pixel location
        g.setColor(act.color);
        g.fillRect(getXPixels(act.x), getYPixels(act.y), size, size);
//        System.out.printf("Grid drawing actor id: %s%n", act.toString());
    }

    void drawImage(Graphics g, Actor act, BufferedImage image) {
//       finds center of square, then backtracks to find corner of where the image should go
        int xCenter = getXPixels(act.x) + size/2;
        int yCenter = getYPixels(act.y) + size/2;
        int xCorner = xCenter - image.getWidth()/2;
        int yCorner = yCenter - image.getHeight()/2;
        g.drawImage(image,xCorner,yCorner,null);
//        g.drawImage(image,1200,100,null);
    }

    int getSize() {
        return size;
    }

    @Override
    public void drawMe(Graphics g) {
        g.setColor(color);
        g.fillRect(startX, startY, size*numSquares, size*numSquares);
    }
}
