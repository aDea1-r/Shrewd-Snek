import java.awt.*;
import java.util.List;

public class NumberSelector implements Drawable{
    private Button increase;
    private Button decrease;
    private Button middle;
    private int currentVal;
    private int min;
    private int max;

    NumberSelector(int x, int y, int width, int height, int min, int max) {
        int bHeight = height/3;
        this.min = min;
        this.max = max;
        currentVal = (min+max)/2;
        increase = new Button(x,y,width,bHeight,"^") {
            @Override
            public void press() {
                currentVal++;
                middle.setText(Integer.toString(currentVal));
            }
        };
        decrease = new Button(x, y+2*bHeight, width, bHeight, "v") {
            @Override
            public void press() {
                currentVal--;
                middle.setText(Integer.toString(currentVal));
            }
        };
        middle = new Button(x,y+bHeight,width,bHeight,Integer.toString(currentVal)) {
            @Override
            public void press() { }
        };

    }
    void addtoList(List<Button> list) {
        list.add(increase);
        list.add(decrease);
    }
    void setCurrentVal(int x) {
        if(x>max) {
            currentVal = max;
            return;
        }
        if(x<min) {
            currentVal = min;
            return;
        }
        currentVal = x;
    }
    public void drawMe(Graphics g) {
        increase.drawMe(g);
        middle.drawMe(g);
        decrease.drawMe(g);
    }

}
