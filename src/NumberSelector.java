import java.util.List;

public class NumberSelector implements Drawable{
    private Button increase;
    private Button decrease;
    private int currentVal;
    private int min;
    private int max;

    NumberSelector(int x, int y, int width, int height, int min, int max) {
        int bHeight = height/3;
        increase = new Button(x,y,width,bHeight,"^") {
            @Override
            public void press() {
                currentVal++;
            }
        };
        decrease = new Button(x, y+2*bHeight, width, bHeight, "v") {
            @Override
            public void press() {
                currentVal--;
            }
        };
        this.min = min;
        this.max = max;
        currentVal = (min+max)/2;
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

}
