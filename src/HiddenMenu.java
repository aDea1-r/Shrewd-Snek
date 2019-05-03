import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HiddenMenu implements Drawable {
    private List<Button> btns;

    HiddenMenu(int btnCount) {
        btns = new ArrayList<>(btnCount);
    }
    HiddenMenu() {
        btns = new ArrayList<>();
    }
    void addButton(Button b) {
        btns.add(b);
    }
    public void drawMe(Graphics g) {
        for (Button b: btns)
            b.drawMe(g);
    }
    void addNumberSelector(NumberSelector ns) {
        ns.addToList(btns);
    }
    void tryPress(int x, int y) {
        for (Button b :
                btns) {
            if (b.isPressed(x,y)) {
                b.press();
                return;
            }
        }
    }
}
