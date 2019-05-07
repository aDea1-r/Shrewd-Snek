import javax.swing.*;

public class Game{
    static GamePanel m;
    public static void main(String[] args) {
        JFrame j = new JFrame();  //JFrame is the window; window is a depricated class
        m = new GamePanel();
        j.setSize(m.getSize());
        j.add(m); //adds the panel to the frame so that the picture will be drawn
        //use setContentPane() sometimes works better then just add b/c of greater efficiency.

        j.addMouseListener(m);
        j.addKeyListener(m);
        j.setVisible(true); //allows the frame to be shown.

        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //makes the dialog box exit when you click the "x" button.
//        Brain b = new Brain();
//        b.testMe();
    }
}
