import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class StaticEvolutionVariables {

    //time score stuff
    static double timeScoreMultiplier = 2;     //How fast points for time accrue,
                                            // high number makes a snake get more points in a shorter amount of time, no effect on max points
    static double maxTimeScore = 1;  //Maximum score possible for time, Ex: 1 makes max score slightly less than 1 apple
    static int timeOutTime = 200;           //Time in ticks it takes a snake to die from not eating

    //other stuff
    static double percentOldToKeep = 1/80.0;    //Percent of old generation to keep and mutate
    static int numTimesToRunGeneration = 2;     //Number of times each snake in each generation will run, averages fitness together
    static int numTimesToRunGenerationDecreaseBy = 0;   //How much above variable decreases per gen,
                            // Ex: 1 means each generation it will run one less time until it reaches once per generation
    static int thresholdForRemovingBestRun = 4; //If a snake is run greater than or equal to times as this, that snakes best run will be removed when calculating its average fitness. Used to control for extreme luck.
                                                // set to a value larger than numTimesToRunGeneration to disable

    private static void readVars(String path) {
//        String path = String.format("Training Data/%s/evolutionVars.dat",speciesName);
        try {
            Scanner read = new Scanner(new File(path));
            timeScoreMultiplier = read.nextDouble();
            maxTimeScore = read.nextDouble();
            timeOutTime = read.nextInt();
            percentOldToKeep = read.nextDouble();
            numTimesToRunGeneration = read.nextInt();
            numTimesToRunGenerationDecreaseBy = read.nextInt();
            thresholdForRemovingBestRun = read.nextInt();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException at StaticEvolutionVars readVars");
        }
    }
    private static void logVars(String path) {
//        String path = String.format("Training Data/%s/evolutionVars.dat",speciesName);
        try {
            PrintWriter logger = new PrintWriter(path);
            logger.println(timeScoreMultiplier);
            logger.println(maxTimeScore);
            logger.println(timeOutTime);
            logger.println(percentOldToKeep);
            logger.println(numTimesToRunGeneration);
            logger.println(numTimesToRunGenerationDecreaseBy);
            logger.println(thresholdForRemovingBestRun);
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException at StaticEvolutionVars logger");
        }
    }
    static void init(String speciesName) {
        String path = String.format("Training Data/%s/evolutionVars.dat",speciesName);
        File data = new File(path);
        if(data.exists())
            readVars(path);
        else
            create(path);
    }
    static void create(String path) {
        JOptionPane.showMessageDialog(Game.m,"Welcome to the Species Creation Wizard");

    }
}
