import java.awt.*;
import java.util.List;

public class NumberSelector implements Drawable{
    private Button increase;
    private Button decrease;
    private Button middle;
    private int currentVal;
    private int min;
    private int max;

    NumberSelector(int x, int y, int width, int height, int low, int high) {
        int bHeight = height/3;
        this.min = low;
        this.max = high;
        currentVal = (min+max)/2;
        increase = new Button(x,y,width,bHeight,"/\\") {
            @Override
            public void action() {
                if(currentVal<max) {
                    currentVal++;
                    middle.setText(Integer.toString(currentVal));
                }
            }
        };
        decrease = new Button(x, y+2*bHeight, width, bHeight, "\\/") {
            @Override
            public void action() {
                if(currentVal>min) {
                    currentVal--;
                    middle.setText(Integer.toString(currentVal));
                }
            }
        };
        middle = new Button(x,y+bHeight,width,bHeight,Integer.toString(currentVal)) {
            @Override
            public void action() {
                selectThis();
            }
        };
    }
    private void selectThis() {
        if(Game.m.selectedNumberSelector == this) {
            Game.m.selectedNumberSelector = null;
        } else {
            Game.m.selectedNumberSelector = this;
        }
        GamePanel.numTyping = "";
    }
    void addToList(List<Button> list) {
        list.add(increase);
        list.add(decrease);
        list.add(middle);
    }
    void setCurrentVal(int x) {
        if(x>max) {
            currentVal = max;
            middle.setText(Integer.toString(currentVal));
            return;
        }
        if(x<min) {
            currentVal = min;
            middle.setText(Integer.toString(currentVal));
            return;
        }
        currentVal = x;
        middle.setText(Integer.toString(currentVal));
    }
    public void drawMe(Graphics g) {
        increase.drawMe(g);
        middle.drawMe(g);
        decrease.drawMe(g);
    }
    void boxMiddle(Graphics g) {
        Rectangle box = middle.getHitbox();
        Color temp = g.getColor();
        g.setColor(Color.RED);
        g.drawRect((int)box.getX(),(int)box.getY(),(int)box.getWidth(),(int)box.getHeight());
        g.setColor(temp);
    }
    int getCurrentValue() {
        return currentVal;
    }
    void setMax(int m) {
        max = m;
        System.out.println("setting max to "+m);
    }
}
