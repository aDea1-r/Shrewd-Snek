import com.sun.istack.internal.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Game{
    static GamePanel m;
    private static JFrame j;
    private static String defaultName = "Testing";

    public static void main(String[] args) throws IOException {
        File dir = new File("Training Data/"+defaultName);
        if (!dir.exists()) {
            dir.mkdirs();
            StaticEvolutionVariables.logVars(String.format("Training Data/%s/evolutionVars.dat",defaultName));
        }

        j = new JFrame("Shrewd Snek");  //JFrame is the window; window is a depreciated class

        Image snake = ImageIO.read(new File("snake.png"));
        j.setIconImage(snake);

        m = new GamePanel(defaultName);
        j.setSize(m.getSize());
        j.add(m); //adds the panel to the frame so that the picture will be drawn
        //use setContentPane() sometimes works better then just add b/c of greater efficiency.

        j.addMouseListener(m);
        j.addKeyListener(m);
        j.setVisible(true); //allows the frame to be shown.

        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //makes the dialog box exit when you click the "x" button.
//        Brain b = new Brain();
//        b.testMe();

//        StaticEvolutionVariables.create("Testing");
        //TODO: Add reboot method, called by button in GUI, that opens dialog box asking for new Species name, and reboots GamePanel with new species name.
    }
    static void reboot(@NotNull String name) {
        j.setVisible(false);
        j.remove(m);

        if (name.length()==0)
            m = new GamePanel(defaultName);
        else
            m = new GamePanel(name);

        j.setSize(m.getSize());
        j.add(m);

        j.addMouseListener(m);
        j.addKeyListener(m);
        j.setVisible(true);

        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
