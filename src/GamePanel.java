import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.Graphics;
import java.awt.Color;
import java.util.*;
import javax.swing.SwingUtilities;

public class GamePanel extends JPanel implements MouseListener, KeyListener {
    private BufferedImage buff;
    private int width = 2000;
    private int height = 1000;

    private int frames;             //Used to calculate frame rate
    private double avgFrameRate;
    private int numPastFrameAverages;

    private final double startXPercent = 0.05;          //% of the total screen which the play screen will start at
    private final double startYPercent = 0.05;          //% of the total screen which the play screen will start at
    private final double screenSize = 0.85;      //% of the total screen which the play screen will take up

    private Map<Integer, Boolean> inputs;
    private List<Button> buttonList;

    private GameEngine[] engines;

    GamePanel()
    {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) screen.getWidth();
        height = (int) (screen.getHeight()-100);

        setSize(width, height);
        setVisible(true); //it's like calling the repaint method.

        frames = 0;
        avgFrameRate = -1.0;
        numPastFrameAverages = 0;

        inputs = new LinkedHashMap<Integer, Boolean>();

        int startX = (int)(width * startXPercent);
        int startY = (int)(height * startYPercent);

        int gridSize = Math.max(Math.min((int)(height * screenSize), (int)(width * screenSize))/GameEngine.numSquares, 1);

        buttonList = new ArrayList<Button>();

        Button player = new PlayerStartButton((getWidth()*17) /20, getHeight()/10, 200, 80, "Player");
        buttonList.add(player);
        Button AI = new AIStartButton((getWidth()*17) /20, getHeight()*2/10, 200, 80, "Computer");
        buttonList.add(AI);

        inputs.put((int)'P', false);
        inputs.put((int)'p', false);

        engines = new GameEngine[1];
    }

    public void paintComponent(Graphics stupidG)
    {
        if(buff == null)
            buff = (BufferedImage)(createImage(getWidth(),getHeight()));
        Graphics g = buff.createGraphics();

        g.setColor(Color.MAGENTA);
        g.fillRect(0,0,2000,1500);

        if(engines[0]!=null) {
            engines[0].drawMe(g);

//        System.out.println(engines[0].scoreTracker.getScore());
            g.drawString("" + engines[0].scoreTracker.getScore(), 10, 60);
        }

        for (Button b: buttonList) {
            b.drawMe(g);
        }

        stupidG.drawImage(buff,0,0,null);

        frames++;
        repaint();
    }

    public void mouseClicked(MouseEvent e)
    {
        e = SwingUtilities.convertMouseEvent(e.getComponent(),e,this);
        for (Button b: buttonList) {
            if(b.isPressed(e.getX(), e.getY()))
                b.press(this);
        }
    }
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}


    public void keyPressed(KeyEvent e)
    {
        int code = e.getKeyCode();
//        System.out.printf("Code %d (%s) pressed %n", code, (char)code + "");

        inputs.put(code, true);
    }

    public void keyReleased(KeyEvent e)	{}
    public void keyTyped(KeyEvent e){}

    private void frameRateTest(){
        int tempFrameRate = frames * (1000/GameEngine.refreshRate);
        if(frames != 0) {
            if (avgFrameRate == -1.0) {
                avgFrameRate = tempFrameRate;
            } else {
                double tempDouble = 1.0/((double)numPastFrameAverages);
                avgFrameRate = (avgFrameRate*(1-tempDouble) + tempFrameRate*(tempDouble));
            }
            numPastFrameAverages++;
        }
        System.out.printf("Framerate is %d%n", tempFrameRate);
        frames = 0;
        System.out.printf("Average framerate is %.2f%n", avgFrameRate);
    }
    void startPlayer() {
        engines[0] = new GameEngine(startXPercent, startYPercent, screenSize, height, width, inputs);
    }
    void startAI() {
        engines[0] = new GameEngine(startXPercent, startYPercent, screenSize, height, width, inputs);
    }
}