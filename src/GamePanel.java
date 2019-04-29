import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
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

    private GameEngine renderEngine;
    private NumberSelector tickRateSelector;

    private static int numPerGeneration = 1000;
    Generation currentGeneration;

    private double percentOldToKeep;                //the percent of the previous generation we will keep and mutate

    private int currentTask;        //Track what it is currently doing
                                        //0 = idle
                                        //1 = running player
                                        //2 = running single ai
                                        //3 = running generation
                                        //4 = processing generation
    String playerName;

    GamePanel()
    {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) screen.getWidth()/1;
        height = (int) (screen.getHeight()-100)/1;

        setSize(width, height);
        setVisible(true); //it's like calling the repaint method.

        frames = 0;
        avgFrameRate = -1.0;
        numPastFrameAverages = 0;

        currentTask = 0;

        inputs = new LinkedHashMap<Integer, Boolean>();

        buttonList = new ArrayList<Button>();

        Button player = new Button((width*17) /20, height/10, width/10, height/12, "Player") {
            @Override
            public void press() {
                startPlayer();
            }
        };
        buttonList.add(player);
        Button AI = new Button((width*17) /20, height*2/10, width/10, height/12, "Computer") {
            @Override
            public void press() {
                startAI();
            }
        };
        buttonList.add(AI);
        Button runGeneration = new Button((width*17) /20, height*3/10, width/10, height/12, "Generation") {
            @Override
            public void press() {
                startGeneration();
            }
        };
        buttonList.add(runGeneration);

        percentOldToKeep = 1/30.0;

        tickRateSelector = new NumberSelector((width*17) /20, height*4/10, width/40, height/4, 1,120);
        tickRateSelector.addToList(buttonList);

        inputs.put((int)'P', false);
        inputs.put((int)'p', false);

        setPlayerName();
    }

    private void setPlayerName() {
        String str = System.getProperty("user.name");
        str = str.substring(0,1).toUpperCase() + str.substring(1); //capitalizes first letter of name
        if (str.contains("_"))
            str = str.substring(0,str.indexOf("_"));
        for (int i = 0; i < str.length()-1; i++) {
            if (str.charAt(i)==' ')
                str = str.substring(0,i+1) + str.substring(i+1,i+2).toUpperCase() + str.substring(i+2);
        }
        if(str.charAt(str.length()-1)==' ')
            str = str.substring(0,str.length()-1);
        playerName = str;
    }

    public void paintComponent(Graphics stupidG)
    {
        if(buff == null)
            buff = (BufferedImage)(createImage(getWidth(),getHeight()));
        Graphics g = buff.createGraphics();

        g.setColor(Color.MAGENTA);                      //Background
        g.fillRect(0,0,2000,1500);

        if(renderEngine!=null)                          //Draw last
            renderEngine.drawMe(g);

        for (Button b: buttonList) {                    //Draw buttons
            b.drawMe(g);
        }

        //--------------------------------------------------------------------------------------------------------------
        String titleCard = "Default Title";

        if(currentTask == 0){                               //Idle
            titleCard = "Welcome! Choose an activity";
        }
        else if(currentTask == 1){                          //Player
            titleCard = "Welcome "+playerName+"! WASD to control the snake";
        }
        else if(currentTask == 2){                          //Single AI
            titleCard = "AI loaded, playing";
        }
        else if(currentTask == 3){                          //Generation
            titleCard = "Running generation";
            currentGeneration.drawMe(g);
            if(currentGeneration.isDone()){
                System.out.printf("Generation #%d has finished running%n", GameEngineVariableTickRate.genNum);
                currentTask = 4;
            }
        }
        else if(currentTask == 4){                          //Generation Processing TODO
            titleCard = "Processing generation";
            int numToKeep = (int)(numPerGeneration*percentOldToKeep);
//            snekSort.getNth(0);
//            System.out.printf("Sorted Array of gen #%d is now: %s%n", GameEngineVariableTickRate.genNum, Arrays.toString(snekSort.arr));
        }

        int fontSize = width/50;
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        g.setColor(Color.yellow);
        g.drawString(titleCard, width/4 - (titleCard.length()*fontSize/4), 0+fontSize);
        //--------------------------------------------------------------------------------------------------------------

        stupidG.drawImage(buff,0,0,null);   //Double buffer stuff

        frames++;
        repaint();
    }

    public void mouseClicked(MouseEvent e)
    {
        e = SwingUtilities.convertMouseEvent(e.getComponent(),e,this);
        for (Button b: buttonList) {
            if(b.isPressed(e.getX(), e.getY()))
                b.press();
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
        int tempFrameRate = frames * (1000/GameEngineFixedTickRate.refreshRate);
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
    private void startPlayer() {
        GameEngineFixedTickRate.refreshRate = tickRateSelector.getCurrentValue();
        currentTask = 1;
        renderEngine = new GameEngineFixedTickRate(startXPercent, startYPercent, screenSize, height, width, inputs,true, null);
    }
    private void startAI() {
        GameEngineFixedTickRate.refreshRate = tickRateSelector.getCurrentValue();
        renderEngine = new GameEngineFixedTickRate(startXPercent, startYPercent, screenSize, height, width, inputs,false, null);
        currentTask = 2;
    }
    private void startGeneration() {
//        engines = new HashSet<GameEngine>(numPerGeneration);
//        GameEngineVariableTickRate.genNum = 0;
//        snekSort = new SnakeSorters(GameEngineVariableTickRate.genNum, numPerGeneration);
//        currentTask = 3;
//        for (int i = 0; i < numPerGeneration; i++) {
//            GameEngineVariableTickRate temp = new GameEngineVariableTickRate(startXPercent, startYPercent, screenSize, height, width, false, i, null);
//            engines.add(temp);
//            temp.start();
//        }
//        Iterator itr = engines.iterator();
//        renderEngine = (GameEngine) itr.next();
        currentTask = 3;
        currentGeneration = new Generation(startXPercent, startYPercent, screenSize, height, width, "Testing", 0, numPerGeneration);
    }
    void killAnEngine(GameEngine gm){
        if(currentTask == 1 || currentTask == 2){
            currentTask = 0;
        }
        else if(currentTask == 3){
            currentGeneration.removeEngine((GameEngineVariableTickRate)gm);
        }
        else if(currentTask == 4){
            System.out.printf("Wait wtf happened, killAnEngine in gamePanel%n");
        }
    }
}