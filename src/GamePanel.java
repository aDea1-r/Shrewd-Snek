import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
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
                                                        // 1 - Computer Selection Menu
                                                        // 2 - Species Selection Menu

    private HiddenMenu VisibleMenu;
    private SnakeSorters selectedSorter;
    NumberSelector selectedNumberSelector;

    private GameEngine renderEngine;
    Brain brainToDraw;
    private NumberSelector tickRateSelector;

    private static int numPerGeneration = 1000;
    private Generation currentGeneration;
    private String currentSpeciesName;

    private int currentTask;        //Track what it is currently doing
                                        //0 = idle
                                        //1 = running player
                                        //2 = running single ai
                                        //3 = running generation
                                        //4 = processing generation
    private String playerName;
    private boolean spamTraining = false;

    private final int fractionOfScreenToTake = 1;

    GamePanel(String speciesName)
    {
        currentSpeciesName = speciesName;

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) screen.getWidth()/fractionOfScreenToTake;
        height = (int) (screen.getHeight()-100)/fractionOfScreenToTake;

        setSize(width, height);
        setVisible(true); //it's like calling the repaint method.

        frames = 0;
        avgFrameRate = -1.0;
        numPastFrameAverages = 0;

        currentTask = 0;

        inputs = new LinkedHashMap<>();

        updateGenCount();
        calcNumPerGen(speciesName);
//        calcNumSpecies();

        initializeButtons();
        hiddenMenus = new ArrayList<>();
        addReplayMenu();
        addComputerMenu();
        addChangeSpeciesMenu();

        tickRateSelector = new NumberSelector((width*17) /20, height*13/20, width/30, height/4, 1,120);
        tickRateSelector.addToList(buttonList);

        setPlayerName();
    }
    private void initializeButtons() {
        buttonList = new ArrayList<>();

        Button player = new Button((width*17) /20, height/20, width/8, height/12, "Player") {
            @Override
            public void action() {
                startPlayer();
            }
        };
        buttonList.add(player);
        Button AI = new Button((width*17) /20, height*3/20, width/8, height/12, "Start AI") {
            @Override
            public void action() {
                int indexOfComputerMenu = 1;
                if(VisibleMenu==hiddenMenus.get(indexOfComputerMenu))
                    VisibleMenu = null;
                else
                    VisibleMenu = hiddenMenus.get(indexOfComputerMenu);
            }
        };
        buttonList.add(AI);
//        Button runGeneration = new Button((width*17) /20, height*5/20, width/8, height/12, "Generation") {
//            @Override
//            public void action() {
//                startGeneration();
//            }
//        };
//        buttonList.add(runGeneration);
        Button replay = new Button((width*17) /20, height*5/20, width/8, height/12, "Replay") {
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
        Button runNextGeneration = new Button((width*17) /20, height*7/20, width/8, height/12, "Next Gen") {
            @Override
            public void action() {
                    startNextGeneration();
            }
        };
        buttonList.add(runNextGeneration);
        Button spamGenerations = new Button((width*17) /20, height*9/20, width/8, height/12, "Rapid Train: Off") {
            @Override
            public void action() {
                if (spamTraining) {
                    spamTraining = false;
                    setText("Rapid Train: Off");
                } else {
                    spamTraining = true;
                    setText("Rapid Train: On");
                }
            }
        };
        buttonList.add(spamGenerations);
        Button changeSpecies = new Button((width*17) /20, height*11/20, width/8, height/12, "Change Species") {
            @Override
            public void action() {
                int indexOfSpeciesMenu = 2;
                if(VisibleMenu==hiddenMenus.get(indexOfSpeciesMenu))
                    VisibleMenu = null;
                else
                    VisibleMenu = hiddenMenus.get(indexOfSpeciesMenu);
            }
        };
        buttonList.add(changeSpecies);
    }
    private void addReplayMenu() {
        HiddenMenu temp = new HiddenMenu();

//        updateGenCount();
        int numGenerations = getGenCount(currentSpeciesName);

        int genStart = 0;
        if(numGenerations==0)
            genStart=-1;
        NumberSelector gen = new NumberSelector((width*14) /20, height/10, width*2/40, height/5, 0, numGenerations-1,genStart);
        NumberSelector num = new NumberSelector((width*14) /20, height*4/10, width/40, height/5, 0, BufferedNumPerGen-1,genStart);
        temp.addNumberSelector(gen);
        temp.addNumberSelector(num);

        if(numGenerations>0) {
            selectedSorter = SnakeSorters.snakeSortersReader(0,currentSpeciesName);
        }

        Button refresh = new Button(width*14/20, height/40, width/10, height/20, "Refresh") {
            @Override
            public void action() {
                int numGenerations = getGenCount(currentSpeciesName);
                int numPerGeneration = getNumPerGen(currentSpeciesName);
                gen.setMax(numGenerations-1);
                num.setMax(numPerGeneration-1);
            }
        };
        temp.addButton(refresh);
        Button go = new Button((width*14) /20, height*8/10, width/10, height/20, "Go!") {
            @Override
            public void action() {
                int genID = gen.getCurrentValue();
                int brainID = num.getCurrentValue();
                startReplay(genID, brainID);
                VisibleMenu = null;
            }
        };
        temp.addButton(go);
        Button save = new Button((width*31)/40, height*2/10, width*5/80, height*3/40, "Save") {
            @Override
            public void action() {
                int genID = gen.getCurrentValue();
                int brainID = num.getCurrentValue();
                save(genID, brainID);
            }
        };
        temp.addButton(save);
        Button setBest = new Button(width*59/80, height*4/10, width*4/40, height*2/40, "Best: ") {
            @Override
            public void action() {
                num.setCurrentVal(getNumberAtEndOfText());
            }
        };
        temp.addButton(setBest);
        Button setQ3 = new Button(width*59/80, height*19/40, width*4/40, height*2/40, "Q3: ") {
            @Override
            public void action() {
                num.setCurrentVal(getNumberAtEndOfText());
            }
        };
        temp.addButton(setQ3);
        Button setMedian = new Button(width*59/80, height*22/40, width*4/40, height*2/40, "Med: ") {
            @Override
            public void action() {
                num.setCurrentVal(getNumberAtEndOfText());
            }
        };
        temp.addButton(setMedian);
        Button setQ1 = new Button(width*59/80, height*25/40, width*4/40, height*2/40, "Q1: ") {
            @Override
            public void action() {
                num.setCurrentVal(getNumberAtEndOfText());
            }
        };
        temp.addButton(setQ1);
        Button setWorst = new Button(width*59/80, height*28/40, width*4/40, height*2/40, "Worst: ") {
            @Override
            public void action() {
                num.setCurrentVal(getNumberAtEndOfText());
            }
        };
        temp.addButton(setWorst);
        Button loadGen = new Button((width*14) /20, height*3/10, width*11/80, height*3/40, "Load Statistics") {
            @Override
            public void action() {
                int genID = gen.getCurrentValue();
//                System.out.println("Loading gen");
                selectedSorter = SnakeSorters.snakeSortersReader(genID,currentSpeciesName);
                setBest.setText("Best: "+selectedSorter.getNth(0).genID);
                setQ3.setText("Q3: "+selectedSorter.getNth(selectedSorter.getGenSize()/4).genID);
                setMedian.setText("Med: "+selectedSorter.getNth(selectedSorter.getGenSize()/2).genID);
                setQ1.setText("Q1: "+selectedSorter.getNth(selectedSorter.getGenSize()*3/4).genID);
                setWorst.setText("Worst: "+selectedSorter.getNth(selectedSorter.getGenSize()-1).genID);
            }
        };
        temp.addButton(loadGen);

        hiddenMenus.add(temp);
    }

    private void addComputerMenu() {
        HiddenMenu temp = new HiddenMenu();

//        updateGenCount();
        int numGenerations = getGenCount(currentSpeciesName);

        int genStart = 0;
        if(numGenerations==0)
            genStart=-1;
        NumberSelector gen = new NumberSelector((width*14) /20, height/10, width*2/40, height/5, 0, numGenerations-1,genStart);
        NumberSelector num = new NumberSelector((width*14) /20, height*4/10, width/40, height/5, 0, BufferedNumPerGen-1,genStart);
        temp.addNumberSelector(gen);
        temp.addNumberSelector(num);

        if(numGenerations>0) {
//            System.out.println("Loading gen"+numGenerations);
            selectedSorter = SnakeSorters.snakeSortersReader(0,currentSpeciesName);
        }

        Button loadSaved = new Button(width*61/80,height/10,width*3/40, height/10, "Saved") {
            @Override
            public void action() {
                playSaved();
                VisibleMenu = null;
            }
        };
        temp.addButton(loadSaved);
        Button refresh = new Button(width*14/20, height/40, width/10, height/20, "Refresh") {
            @Override
            public void action() {
                int numGenerations = getGenCount(currentSpeciesName);
                int numPerGeneration = getNumPerGen(currentSpeciesName);
                gen.setMax(numGenerations-1);
                num.setMax(numPerGeneration-1);
            }
        };
        temp.addButton(refresh);
        Button go = new Button((width*14) /20, height*61/80, width/10, height*2/42, "Go!") {
            @Override
            public void action() {
                int genID = gen.getCurrentValue();
                int brainID = num.getCurrentValue();
                startAI(genID, brainID);
                VisibleMenu = null;
            }
        };
        temp.addButton(go);
        Button setBest = new Button(width*59/80, height*4/10, width*4/40, height*2/40, "Best: ") {
            @Override
            public void action() {
                num.setCurrentVal(getNumberAtEndOfText());
            }
        };
        temp.addButton(setBest);
        Button setQ3 = new Button(width*59/80, height*19/40, width*4/40, height*2/40, "Q3: ") {
            @Override
            public void action() {
                num.setCurrentVal(getNumberAtEndOfText());
            }
        };
        temp.addButton(setQ3);
        Button setMedian = new Button(width*59/80, height*22/40, width*4/40, height*2/40, "Med: ") {
            @Override
            public void action() {
                num.setCurrentVal(getNumberAtEndOfText());
            }
        };
        temp.addButton(setMedian);
        Button setQ1 = new Button(width*59/80, height*25/40, width*4/40, height*2/40, "Q1: ") {
            @Override
            public void action() {
                num.setCurrentVal(getNumberAtEndOfText());
            }
        };
        temp.addButton(setQ1);
        Button setWorst = new Button(width*59/80, height*28/40, width*4/40, height*2/40, "Worst: ") {
            @Override
            public void action() {
                num.setCurrentVal(getNumberAtEndOfText());
            }
        };
        temp.addButton(setWorst);
        Button loadGen = new Button((width*14) /20, height*3/10, width*11/80, height*3/40, "Load Statistics") {
            @Override
            public void action() {
                int genID = gen.getCurrentValue();
                selectedSorter = SnakeSorters.snakeSortersReader(genID,currentSpeciesName);
                setBest.setText("Best: "+selectedSorter.getNth(0).genID);
                setQ3.setText("Q3: "+selectedSorter.getNth(selectedSorter.getGenSize()/4).genID);
                setMedian.setText("Med: "+selectedSorter.getNth(selectedSorter.getGenSize()/2).genID);
                setQ1.setText("Q1: "+selectedSorter.getNth(selectedSorter.getGenSize()*3/4).genID);
                setWorst.setText("Worst: "+selectedSorter.getNth(selectedSorter.getGenSize()-1).genID);
            }
        };
        temp.addButton(loadGen);
        Button rand = new Button((width*14) /20, height*66/80, width/10, height*2/42, "Random") {
            @Override
            public void action() {
                startAI();
                VisibleMenu = null;
            }
        };
        temp.addButton(rand);

        hiddenMenus.add(temp);
    }

    private void addChangeSpeciesMenu() {
        HiddenMenu temp = new HiddenMenu();

        Button load = new Button((width*14) /20, height*3/10, width*11/80, height*3/40, "Load Existing Species") {
            @Override
            public void action() {
                startRebootWithExistingSpecies();
            }
        };
        temp.addButton(load);
        Button create = new Button((width*14) /20, height*4/10, width*11/80, height*3/40, "Create New Species") {
            @Override
            public void action() {
                startRebootWithNewSpecies();
            }
        };
        temp.addButton(create);

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
        {
            renderEngine.drawMe(g);
            if(brainToDraw!=null)
                brainToDraw.drawMe(g,renderEngine.gameGrid);
        }


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

            if(!spamTraining)
                startReplay(best.genNum, best.genID);
            else
                currentTask = 0;
        }

        if(selectedNumberSelector!=null) {
            selectedNumberSelector.boxMiddle(g);
        }
        int fontSize = width/50;
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        g.setColor(Color.yellow);
        g.drawString(titleCard, width/4 - (titleCard.length()*fontSize/4), fontSize);
        g.drawString(numTyping,getWidth()-60, getHeight()-20);
        //-------------------------------------------------------------------------------------------------------------
        stupidG.drawImage(buff,0,0,null);   //Double buffer stuff

        frames++;

        if(spamTraining && currentTask == 0) {
            startNextGeneration();
        }

        repaint();
    }

    public void mouseClicked(MouseEvent e)
    {
        if(renderEngine!=null && !renderEngine.gameRunning)
            brainToDraw = null;
        e = SwingUtilities.convertMouseEvent(e.getComponent(),e,this);
        for (Button b: buttonList) {
            if(b.isPressed(e.getX(), e.getY())) {
                b.press();
                return;
            }
        }
        if(VisibleMenu!=null) {
            VisibleMenu.tryPress(e.getX(),e.getY());
            return;
        }
        selectedNumberSelector = null;
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
        if(e.getKeyChar()>='0' && e.getKeyChar()<='9' && numTyping.length()<9) {
            numTyping+=Character.toString(e.getKeyChar());
            return;
        }
        if(e.getKeyChar()=='\n' && selectedNumberSelector!=null) {
            if (numTyping.length()>0)
                selectedNumberSelector.setCurrentVal(Integer.parseInt(numTyping));
            numTyping = "";
            selectedNumberSelector = null;
            return;
        }
        if (e.getKeyChar()=='\n') {
            numTyping = "";
            return;
        }
        if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE && numTyping.length()>0) {
            numTyping = numTyping.substring(0,numTyping.length()-1);
            return;
        }
        if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            numTyping = "";
            selectedNumberSelector = null;
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
    private void startAI(int genID, int brainID) {
        GameEngineFixedTickRate.refreshRate = tickRateSelector.getCurrentValue();
        Brain brain = Brain.brainReader(genID,brainID,currentSpeciesName);
        renderEngine = new GameEngineFixedTickRate(startXPercent, startYPercent, screenSize, height, width, inputs,false, brain, "Wut");
        currentTask = 2;
    }
//    private void startGeneration() {
//        currentGeneration = new Generation(startXPercent, startYPercent, screenSize, height, width, currentSpeciesName, 0, numPerGeneration);
//        currentTask = 3;
//    }
    private void startNextGeneration() {
        //TODO: make method to call this, probably make a new button for this
        GameEngineFixedTickRate.refreshRate = tickRateSelector.getCurrentValue();
        if(currentGeneration==null && getGenCount(currentSpeciesName)==0) {
            System.out.println("Gen count is "+getGenCount(currentSpeciesName));
            currentGeneration = new Generation(startXPercent, startYPercent, screenSize, height, width, currentSpeciesName, 0, numPerGeneration);
        } else if(currentGeneration!=null) {
            Generation nextGeneration = new Generation(startXPercent, startYPercent, screenSize, height, width, currentSpeciesName, currentGeneration.generationNum+1, numPerGeneration);
            nextGeneration.evolve(currentGeneration.snekSort, StaticEvolutionVariables.percentOldToKeep);
            currentGeneration = nextGeneration;
        } else {
            SnakeSorters prevData = SnakeSorters.snakeSortersReader(getGenCount(currentSpeciesName)-1,currentSpeciesName);
            if (prevData==null) {
                String path = String.format("Training Data/%s/%d",currentSpeciesName,bufferedGenCount-1);
                deleteDirectory(new File(path));
                bufferedGenCount--;
                startNextGeneration();
                return;
            }
            currentGeneration = new Generation(startXPercent, startYPercent, screenSize, height, width, currentSpeciesName, prevData.genNum+1, numPerGeneration);
            currentGeneration.evolve(prevData, StaticEvolutionVariables.percentOldToKeep);
        }
        currentTask = 3;
        bufferedGenCount++;
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

    //TODO: BufferedGenCount needs additional testing
    private int bufferedGenCount;
    private void updateGenCount() {
        File dir = new File("Training Data/"+currentSpeciesName);
        if(!dir.exists()) {
            bufferedGenCount = 0;
            return;
        }
        File[] subDirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        bufferedGenCount = subDirs.length;
    }
    private int getGenCount(String name) {
        return bufferedGenCount;
    }

    private int BufferedNumPerGen;
    private void calcNumPerGen(String name) {
        File dir = new File("Training Data/"+name+"/0");
        if (!dir.exists()) {
            BufferedNumPerGen = 0;
            return;
        }
        File[] subDirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        BufferedNumPerGen = subDirs.length;
    }
    private int getNumPerGen(String name) {
        return numPerGeneration;
    }
//    private int BufferedNumSpecies;
    private String[] calcNumSpecies() {
        File dir = new File("Training Data/");
        if (!dir.exists()) {
//            BufferedNumSpecies = 0;
            return new String[0];
        }
        String[] subDirs = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File pathname, String temp) {
                return !temp.contains(".");
            }
        });
//        BufferedNumSpecies = subDirs.length;
        return subDirs;
    }
    private void startRebootWithExistingSpecies() {
        Icon darwin = null;
        try {
            darwin = new ImageIcon(AppleStuff.createResizedCopy(ImageIO.read(new File("darwin.png")),50,50));
        } catch (IOException e) {
            System.out.println("Cannot find darwin icon");
        }
        String next = (String) JOptionPane.showInputDialog(this,
                                                        "Please Select Species to Load: ",
                                                        "Species Selection Wizard",
                                                        JOptionPane.QUESTION_MESSAGE,
                                                        darwin,
                                                        calcNumSpecies(),
                                                        currentSpeciesName);
        if (next==null)
            return;
        Game.reboot(next);
        StaticEvolutionVariables.readVars(next);
    }
    private void startRebootWithNewSpecies() {
        Icon darwin = null;
        try {
            darwin = new ImageIcon(AppleStuff.createResizedCopy(ImageIO.read(new File("darwin.png")),50,50));
        } catch (IOException e) {
            System.out.println("Cannot find darwin icon");
        }
        String title = "Create New Species Wizard";
        String next = (String) JOptionPane.showInputDialog(this,
                                                            "Please Input New Species Name: ",
                                                            title,
                                                            JOptionPane.QUESTION_MESSAGE,
                                                            darwin,
                                                            null,
                                                            currentSpeciesName);
        if (next==null)
            return;
        String[] existingSpecies = calcNumSpecies();
        boolean spotTaken = contains(existingSpecies,next);
        while(spotTaken) {
            JOptionPane.showMessageDialog(this,"Species Already Exists!",title,JOptionPane.ERROR_MESSAGE,darwin);
            next = (String) JOptionPane.showInputDialog(this,
                                                        "Please Input New Species Name: ",
                                                        title,
                                                        JOptionPane.QUESTION_MESSAGE,
                                                        darwin,
                                                        null,
                                                        next);
            if (next==null)
                return;
            spotTaken = contains(existingSpecies,next);
        }
        Game.reboot(next);
        StaticEvolutionVariables.create(next,darwin);
    }
    private static boolean contains(String[] arr, String str) {
        for (String s : arr) {
            if (s.equals(str))
                return true;
        }
        return false;
    }
    private void save(int genID, int brainID) {
        Icon icon = null;
        try {
            BufferedImage temp = ImageIO.read(new File("darwin.png"));
            temp = AppleStuff.createResizedCopy(temp,50,50);
            icon = new ImageIcon(temp);
        } catch (IOException e) {
            System.out.printf("IOException save, cannot find darwin.png%n");
        }
        boolean isValid = false;
        String newName = null;
        File newDir = null;
        while (!isValid) {
            newName = null;
            while(newName==null || newName.length()<=0) {
                Object temp = JOptionPane.showInputDialog(this,"Please Name This Snake: ", "Snake Saving", JOptionPane.QUESTION_MESSAGE, icon, null, null);
                newName = ((String)temp).trim();
            }

            newDir = new File(String.format("Saved Data/%s/",newName));
            if (!newDir.exists())
                isValid = true;
            else
                JOptionPane.showMessageDialog(this,String.format("Snake With Name %s Already Exists!",newName),"Snake Saving",JOptionPane.ERROR_MESSAGE,icon);
        }
        newDir.mkdirs();

        File oldBrain = new File(String.format("Training Data/%s/%d/%d/brain.dat",currentSpeciesName,genID,brainID));
        File newBrain = new File(String.format("Saved Data/%s/brain.dat",newName));
        try {
            Files.copy(oldBrain.toPath(),newBrain.toPath());
        } catch (IOException e) {
            System.out.printf("failed to save brain%n");
        }
    }
    private void playSaved() {
        GameEngineFixedTickRate.refreshRate = tickRateSelector.getCurrentValue();
        String[] savedList = getSavedList();
        Icon icon = null;
        try {
            BufferedImage temp = ImageIO.read(new File("darwin.png"));
            temp = AppleStuff.createResizedCopy(temp,50,50);
            icon = new ImageIcon(temp);
        } catch (IOException e) {
            System.out.printf("IOException at replaySaved, cannot find darwin.png%n");
        }
        Object temp = JOptionPane.showInputDialog(this,"Please Select Saved Snake to Run", "Snake Selection", JOptionPane.QUESTION_MESSAGE, icon, savedList, null);
        String snakeToRun = (String)temp;
        Brain brain = Brain.brainReader(snakeToRun);

        renderEngine = new GameEngineFixedTickRate(startXPercent, startYPercent, screenSize, height, width, inputs,false, brain, "Wut");
        currentTask = 2;
    }
    private String[] getSavedList() {
        File dir = new File("Saved Data/");
        if (!dir.exists()) {
            return new String[0];
        }
        String[] subDirs = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File pathname, String temp) {
                return !temp.contains(".");
            }
        });
        return subDirs;
    }
    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}