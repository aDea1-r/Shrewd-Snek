import java.awt.Graphics;
import java.awt.Rectangle;

public class Button implements Drawable {
    int x;
    int y;
    int width;
    int height;
    String text;
    Rectangle hitbox;

    public Button(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        hitbox = new Rectangle(x,y,width,height);
    }
    public void drawMe(Graphics g) {

    }
}
