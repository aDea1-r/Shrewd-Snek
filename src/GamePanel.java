import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileFilter;
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
    private List<HiddenMenu> hiddenMenus;               // 0 - Replay Selection Menu
    private HiddenMenu VisibleMenu;
    NumberSelector selectedNumberSelector;

    private GameEngine renderEngine;
    private NumberSelector tickRateSelector;

    private static int numPerGeneration = 1000;
    private Generation currentGeneration;
    private String currentSpeciesName = "Testing";
    
    private int currentTask;        //Track what it is currently doing
                                        //0 = idle
                                        //1 = running player
                                        //2 = running single ai
                                        //3 = running generation
                                        //4 = processing generation
    private String playerName;

    private final int fractionOfScreenToTake = 2;

    GamePanel()
    {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) screen.getWidth()/fractionOfScreenToTake;
        height = (int) (screen.getHeight()-100)/fractionOfScreenToTake;

        setSize(width, height);
        setVisible(true); //it's like calling the repaint method.

        frames = 0;
        avgFrameRate = -1.0;
        numPastFrameAverages = 0;

        currentTask = 0;

        inputs = new LinkedHashMap<Integer, Boolean>();

        initializeButtons();

        tickRateSelector = new NumberSelector((width*17) /20, height*6/10, width/40, height/4, 1,120);
        tickRateSelector.addToList(buttonList);

        inputs.put((int)'P', false);
        inputs.put((int)'p', false);

        setPlayerName();
    }
    private void initializeButtons() {
        buttonList = new ArrayList<Button>();

        Button player = new Button((width*17) /20, height/10, width/10, height/12, "Player") {
            @Override
            public void action() {
                startPlayer();
            }
        };
        buttonList.add(player);
        Button AI = new Button((width*17) /20, height*2/10, width/10, height/12, "Computer") {
            @Override
            public void action() {
                startAI();
            }
        };
        buttonList.add(AI);
        Button runGeneration = new Button((width*17) /20, height*3/10, width/10, height/12, "Generation") {
            @Override
            public void action() {
                startGeneration();
            }
        };
        buttonList.add(runGeneration);
        Button replay = new Button((width*17) /20, height*4/10, width/10, height/12, "Replay") {
            @Override
            public void action() {
                int indexOfReplayMenu = 0;
                if(VisibleMenu==hiddenMenus.get(indexOfReplayMenu))
                    VisibleMenu = null;
                else
                    VisibleMenu = hiddenMenus.get(indexOfReplayMenu);
            }
        };
        buttonList.add(replay);
        Button runNextGeneration = new Button((width*17) /20, height*5/10, width/10, height/12, "Next Gen") {
            @Override
            public void action() {
                if(currentGeneration != null)
                    startNextGeneration();
                else
                    System.out.printf("No Next Generation To Run%n");
            }
        };
        buttonList.add(runNextGeneration);

        hiddenMenus = new ArrayList<>();
        addReplayMenu();
    }
    private void addReplayMenu() {
        HiddenMenu temp = new HiddenMenu();

        int numGenerations = getGenCount(currentSpeciesName);
        int numPerGeneration = getNumPerGen(currentSpeciesName);
        NumberSelector gen = new NumberSelector((width*14) /20, height*1/10, width/40, height/5, 0, numGenerations-1);
        NumberSelector num = new NumberSelector((width*14) /20, height*4/10, width/40, height/5, 0, numPerGeneration-1);
        temp.addNumberSelector(gen);
        temp.addNumberSelector(num);

        Button go = new Button((width*14) /20, height*7/10, width/20, height/40, "Go!") {
            @Override
            public void action() {
                int genID = gen.getCurrentValue();
                int brainID = num.getCurrentValue();
                startReplay(genID, brainID);
                VisibleMenu = null;
            }
        };
        temp.addButton(go);

        hiddenMenus.add(temp);
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

        if(VisibleMenu!=null) {
            VisibleMenu.drawMe(g);
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
        else if(currentTask == 4){
            titleCard = "Processing generation";
            SnakeSorters snekSort = currentGeneration.snekSort;

            SnakeSorter best = snekSort.getNth(0);
            snekSort.log();
            System.out.printf("Best snake of generation #%d is%n  Snake with gen ID #%d%n", currentGeneration.generationNum, best.genID);
            startReplay(best.genNum, best.genID);
        }

        if(selectedNumberSelector!=null) {
            selectedNumberSelector.boxMiddle(g);
        }
        int fontSize = width/50;
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        g.setColor(Color.yellow);
        g.drawString(titleCard, width/4 - (titleCard.length()*fontSize/4), 0+fontSize);
        g.drawString(numTyping,getWidth()-60, getHeight()-20);
        //-------------------------------------------------------------------------------------------------------------
        stupidG.drawImage(buff,0,0,null);   //Double buffer stuff

        frames++;
        repaint();
    }

    public void mouseClicked(MouseEvent e)
    {
        e = SwingUtilities.convertMouseEvent(e.getComponent(),e,this);
        for (Button b: buttonList) {
            if(b.isPressed(e.getX(), e.getY())) {
                b.press();
                return;
            }
        }
        if(VisibleMenu!=null)
            VisibleMenu.tryPress(e.getX(),e.getY());
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

    static String numTyping = "";
    public void keyTyped(KeyEvent e){
//        System.out.printf("You typed. KeyChar is %s and keyCode is %d%n",e.getKeyChar(),e.getKeyCode());
        if(e.getKeyChar()>='0' && e.getKeyChar()<='9') {
            numTyping+=Character.toString(e.getKeyChar());
            return;
        }
        if(e.getKeyChar()=='\n' && selectedNumberSelector!=null) {
            selectedNumberSelector.setCurrentVal(Integer.parseInt(numTyping));
            numTyping = "";
            selectedNumberSelector = null;
            return;
        }
        if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE && numTyping.length()>0) {
            numTyping = numTyping.substring(0,numTyping.length()-1);
            return;
        }
        if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            numTyping = "";
            selectedNumberSelector = null;
            return;
        }

    }

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
        renderEngine = new GameEngineFixedTickRate(startXPercent, startYPercent, screenSize, height, width, inputs,true, null, "Wut");
    }
    private void startAI() {
        GameEngineFixedTickRate.refreshRate = tickRateSelector.getCurrentValue();
        renderEngine = new GameEngineFixedTickRate(startXPercent, startYPercent, screenSize, height, width, inputs,false, null, "Wut");
        currentTask = 2;
    }
    private void startGeneration() {
        currentTask = 3;
        currentGeneration = new Generation(startXPercent, startYPercent, screenSize, height, width, currentSpeciesName, 0, numPerGeneration);
    }
    private void startNextGeneration() {
        //TODO: make method to call this, probably make a new button for this

        GameEngineFixedTickRate.refreshRate = tickRateSelector.getCurrentValue();
        Generation nextGeneration = new Generation(startXPercent, startYPercent, screenSize, height, width, currentSpeciesName, currentGeneration.generationNum+1, numPerGeneration);
        nextGeneration.evolve(currentGeneration.snekSort, StaticEvolutionVariables.percentOldToKeep);
        currentGeneration = nextGeneration;
        currentTask = 3;
    }
    private void startReplay(int genID, int brainID) {
        GameEngineFixedTickRate.refreshRate = tickRateSelector.getCurrentValue();

        Brain b = Brain.brainReader(genID,brainID,currentSpeciesName);
        renderEngine = new GameEngineFixedTickRate(startXPercent, startYPercent, screenSize, height, width, b, genID, brainID, currentSpeciesName);
        currentTask = 2;
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
    private int getGenCount(String name) {
        File dir = new File("Training Data/"+name);
        if(!dir.exists())
            return -1;
        File[] subDirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        return subDirs.length;
    }
    private int getNumPerGen(String name) {
        File dir = new File("Training Data/"+name+"/0");
        if (!dir.exists())
            return -1;
        File[] subDirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        return subDirs.length;
    }
}