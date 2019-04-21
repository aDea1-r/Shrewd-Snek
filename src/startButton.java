import java.awt.*;

public class startButton extends Button {

    public startButton(int x, int y, int width, int height, String text) {
        super(x,y,width,height,text, Color.yellow, Color.black);
    }
    public startButton(int x, int y, int width, int height, String text, Color b, Color t) {
        super(x, y, width, height, text, b, t);
    }
    public void press(GamePanel gp) {
        gp.start();
    }
}
