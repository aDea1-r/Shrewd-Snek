import javax.swing.*;

public class Game{
    static GamePanel m;
    public static void main(String[] args) {
        JFrame j = new JFrame();  //JFrame is the window; window is a depricated class
        m = new GamePanel("Testing");
        j.setSize(m.getSize());
        j.add(m); //adds the panel to the frame so that the picture will be drawn
        //use setContentPane() sometimes works better then just add b/c of greater efficiency.

        j.addMouseListener(m);
        j.addKeyListener(m);
        j.setVisible(true); //allows the frame to be shown.

        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //makes the dialog box exit when you click the "x" button.
//        Brain b = new Brain();
//        b.testMe();
        //TODO: Add reboot method, called by button in GUI, that opens dialog box asking for new Species name, and reboots GamePanel with new species name.
    }
}
