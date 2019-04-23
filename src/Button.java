import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public abstract class Button implements Drawable {
    private String text;
    private Rectangle hitbox;
    private Color buttonColor;
    private Color textColor;

    Button(int x, int y, int width, int height, String text) {
        this(x,y,width,height,text,Color.yellow, Color.black);
    }
    Button(int x, int y, int width, int height, String text, Color b, Color t) {
        this.text = text;
        hitbox = new Rectangle(x,y,width,height);
        buttonColor = b;
        textColor = t;
    }
    public void drawMe(Graphics stupidG) {
        Graphics g = stupidG.create();

        g.setColor(buttonColor);
        g.fillRect((int)hitbox.getX(),(int)hitbox.getY(),(int)hitbox.getWidth(),(int)hitbox.getHeight());
        g.setColor(textColor);
        g.setFont(new Font("Comic Sans MS",Font.PLAIN,50));

        Rectangle rect = new Rectangle((int)(hitbox.getX()+hitbox.width*0.1),
                (int)(hitbox.getY()+hitbox.getHeight()*.1),
                (int)(hitbox.getWidth()*.8),
                (int)(hitbox.getHeight()*.8));
        FontMetrics fm = g.getFontMetrics();
        FontRenderContext frc = ((Graphics2D)g).getFontRenderContext();
        TextLayout tl = new TextLayout(text, g.getFont(), frc);
        AffineTransform transform = new AffineTransform();
        transform.setToTranslation(rect.getX(), rect.getY()+rect.getHeight());
        double scaleY =
                rect.getHeight() / (double) (tl.getOutline(null).getBounds().getMaxY()
                        - tl.getOutline(null).getBounds().getMinY());
        transform.scale(rect.getWidth() / (double) fm.stringWidth(text), scaleY);
        Shape shape = tl.getOutline(transform);
        g.setClip(shape);
        ((Graphics2D)g).fill(shape.getBounds());

        g.dispose();
    }
    boolean isPressed(int x, int y) {
        return hitbox.contains(new Point(x,y));
    }
    void setText(String s) {
        text = s;
    }
    public abstract void press();
}
