import java.awt.*;

public class AIStartButton extends Button {

    public AIStartButton(int x, int y, int width, int height, String text) {
        this(x,y,width,height,text, Color.YELLOW, Color.BLACK);
    }
    public AIStartButton(int x, int y, int width, int height, String text, Color b, Color t) {
        super(x, y, width, height, text, b, t);
    }
    public void press(GamePanel gp) {
        gp.startAI();
    }
}
