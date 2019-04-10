import java.awt.*;

public class Grid implements Drawable {
    private int startX;
    private int startY;
    private int size;       //pixel width of a single spot
    private int numSquares;
    private Color color = Color.BLACK;

    public Object[][] gridMat;  //Let -1 be empty

    public Grid(int x, int y, int s, int nS, Color c){
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

    public int getXPixels(int in){      //Gets x pixel location given grid location
        return (in * size) + startX;
    }

    public int getYPixels(int in){      //Gets y pixel location given grid location
        return (in * size) + startY;
    }

    public void drawSquare(Graphics g, Actor act){               //draws a square on grid based off of pixel location
        g.setColor(act.color);
        g.fillRect(getXPixels(act.x), getYPixels(act.y), size, size);
//        System.out.printf("Grid drawing actor id: %s%n", act.toString());
    }

    @Override
    public void drawMe(Graphics g) {
        g.setColor(color);
        g.fillRect(startX, startY, size*numSquares, size*numSquares);
    }
}
