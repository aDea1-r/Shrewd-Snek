import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.sound.sampled.*; // allows you to use the sound classes

public class GameEngine extends JPanel implements ActionListener, MouseListener, KeyListener {
    private Timer time;
    private BufferedImage buff;
    private final int width = 2000;
    private final int height = 1000;

    private int testerX;

    private final int refreshRate = 60;        //Delay in milliseconds between game ticks

    private int frames;             //Used to calculate frame rate
    private double avgFrameRate;
    private int numPastFrameAverages;

    private final double startXPercent = 0.05;          //% of the total screen which the play screen will start at
    //    public int startX;
    private final double startYPercent = 0.05;          //% of the total screen which the play screen will start at
    //    public int startY;
    private final double screenSize = 0.85;      //% of the total screen which the play screen will take up
    private final int numSquares = 25;          //The size of the grid taking up the play screen
//    public int gridSize;                                //the size in pixels of each grid piece

    private Grid gameGrid;
    private Map<Integer, Boolean> inputs;
    //    boolean initInputs = false;
    AppleMaker food;

    private LinkedList<Drawable> drawables = new LinkedList<Drawable>();
    private LinkedList<Actor> actors = new LinkedList<Actor>();

    GameEngine()
    {
        time = new Timer(refreshRate, this); //sets delay to 15 millis and calls the actionPerformed of this class.
        setSize(width, height);
        setVisible(true); //it's like calling the repaint method.

        frames = 0;
        avgFrameRate = -1.0;
        numPastFrameAverages = 0;

        inputs = new LinkedHashMap<Integer, Boolean>();

        testerX = 5;

        int startX = (int)(width * startXPercent);
        int startY = (int)(height * startYPercent);

        int gridSize = Math.max(Math.min((int)(height * screenSize), (int)(width * screenSize))/numSquares, 1);

        gameGrid = new Grid(startX, startY, gridSize, numSquares, Color.BLACK);
        drawables.add(gameGrid);

//                Body testBody = new Body(Color.CYAN, 1, 1, gameGrid);
//                drawables.add(testBody);
//                System.out.printf("TestBody id: %s%n", testBody.toString());

        SnakeHead snake1 = new SnakeHead(Color.CYAN, 1, 1, gameGrid, 1);
        drawables.add(snake1);
        actors.add(snake1);

        food = new AppleMaker(Color.BLACK,gameGrid,2);
        drawables.add(food);
//        actors.add(food);

        //------------------------------- Snake 1 Code
        inputs.put((int)'w', false);
        inputs.put((int)'d', false);
        inputs.put((int)'s', false);
        inputs.put((int)'a', false);
        inputs.put((int)'W', false);
        inputs.put((int)'D', false);
        inputs.put((int)'S', false);
        inputs.put((int)'A', false);

        time.start();

//        try{
//            file = new File("test.au");//File must be .WAV, .AU, or .AIFF
//            stream = AudioSystem.getAudioInputStream(file);
//            music = AudioSystem.getClip();
//            music.open(stream);
//        }catch(Exception e){}
//        musicPlaying = false;
    }

    public void gameTick(){
        Iterator<Actor> it = actors.listIterator();
        while(it.hasNext()){
            Actor tempActor = it.next();
            tempActor.act(inputs);
        }
    }

    public void paintComponent(Graphics stupidG)
    {
        if(buff == null)
            buff = (BufferedImage)(createImage(getWidth(),getHeight()));
        Graphics g = buff.createGraphics();

        g.setColor(Color.MAGENTA);
        g.fillRect(0,0,2000,1500);

        Iterator it = drawables.listIterator();
        Drawable tempDrawable;
        while (it.hasNext()){
            tempDrawable = (Drawable) it.next();
            tempDrawable.drawMe(g);
        }

//                //Testing
//                g.setColor(Color.CYAN);
//                g.fillRect(testerX, 50, 50, 50);

//        System.out.println("Reeee");
//        g.setFont(new Font ("Arial", Font.BOLD, 25));
//        g.drawString("Click the mouse on the screen to spawn a ball", 100,500);
//        g.drawString("Press 1,2, or 3 to change background color", 100,600);
//        g.drawString("Click B to toggle ball type, Currently: " + ballType, 100,700);
//        g.drawString("Click G to toggle gravity, Currently: "+ gravity,100,800);
//        g.drawString("Click M to toggle Music" ,100,900);
//
//        drawBob(g);

        //music.start(); //Start the music
        stupidG.drawImage( buff, 0, 0, null);

//        try {
//            time.wait(1000);
//        }catch (Exception e){
//            System.out.println(e);
//        }

        frames++;
        repaint();
    }
    public void actionPerformed(ActionEvent e)
    {
        testerX += 5;
//        repaint();

//        frameRateTest();

        gameTick();
    }
    public void mouseClicked(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        //System.out.println("Ball added at " + x + " " + y);
    }
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}


    public void keyPressed(KeyEvent e)
    {
        int code = e.getKeyCode();
        System.out.printf("Code %d (%s) pressed %n", code, (char)code + "");

        inputs.put(code, true);
//        switch (code)
//        {
//            case '1': change = 1; break;
//            case '2': change = 2; break;
//            case '3': change = 3; break;
//            case 'g': case 'G' :
//            gravity++;
//            if(gravity > 3)
//                gravity = 0;
//            break;
//            case 'b': case 'B':
//            ballType++;
//            if(ballType>1)
//                ballType = 0;
//            break;
//            case 'm': case 'M':
//            if(!musicPlaying)
//            {
//                musicPlaying = true;
//                music.start();
//            }
//            else
//            {
//                music.stop();
//                musicPlaying = false;
//            }
//            break;
//        }
    }

    public void keyReleased(KeyEvent e)	{}
    public void keyTyped(KeyEvent e){}

    private void frameRateTest(){
        int tempFrameRate = frames * (1000/refreshRate);
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
}