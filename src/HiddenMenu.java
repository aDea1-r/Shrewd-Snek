import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class HiddenMenu implements Drawable {
    private List<Button> btns;
    private boolean vissible = false;

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
        if(vissible)
            for (Button b: btns)
                b.drawMe(g);
    }
    void toggleVisibility() {
        vissible = !vissible;
    }
}
