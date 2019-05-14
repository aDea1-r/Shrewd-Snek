import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Pattern;

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

    static void readVars(String speciesName) {
        String path = String.format("Training Data/%s/evolutionVars.dat",speciesName);
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
    static void logVars(String path) {
//        String path = String.format("Training Data/%s/evolutionVars.dat",speciesName);
        try {
            File dir = new File(path.substring(0,path.lastIndexOf('/')+1));
            if (!dir.exists())
                dir.mkdirs();
            PrintWriter logger = new PrintWriter(path);
            logger.println(timeScoreMultiplier);
            logger.println(maxTimeScore);
            logger.println(timeOutTime);
            logger.println(percentOldToKeep);
            logger.println(numTimesToRunGeneration);
            logger.println(numTimesToRunGenerationDecreaseBy);
            logger.println(thresholdForRemovingBestRun);
            logger.close();
        } catch (FileNotFoundException e) {
            System.out.printf("FileNotFoundException at StaticEvolutionVars logger: %s%n",e);
        }
    }
//    static void init(String speciesName) {
//        String path = String.format("Training Data/%s/evolutionVars.dat",speciesName);
//        File data = new File(path);
//        if(data.exists())
//            readVars(path);
//        else
//            create(path);
//    }
    static void create(String speciesName, Icon icon) {
        //TODO: Test this garbage code to see if it actually works
        String title = "Species Creation Wizard";
        String path = String.format("Training Data/%s/evolutionVars.dat",speciesName);
        JOptionPane.showMessageDialog(Game.m,"Welcome to the Species Creation Wizard",title,JOptionPane.INFORMATION_MESSAGE,icon);

        String temp; //input
        boolean isValid;
        
        //Init timeScoreMultiplier
        do {
            temp = (String) JOptionPane.showInputDialog(Game.m,
                    "Please Enter timeScoreMultiplier: \nThis is a measure of how fast points for time accrue.",
                    title, JOptionPane.QUESTION_MESSAGE, icon, null,
                    Double.toString(timeScoreMultiplier));
            isValid = isDouble(temp);
            System.out.printf("isValid = %s, temp = %s%n", isValid, temp);
            if(!isValid)
                JOptionPane.showMessageDialog(Game.m,String.format("Invalid Input! %nFound: %s%nRequired: %s",temp,((Object)timeScoreMultiplier).getClass().getName()),title,JOptionPane.ERROR_MESSAGE,icon);
        } while (!isValid);
        timeScoreMultiplier = Double.valueOf(temp);

        //Init maxTimeScore
        do {
            temp = (String) JOptionPane.showInputDialog(Game.m,
                    "Please Enter maxTimeScore: \nThis is the maximum number of points earned by surviving.",
                    title, JOptionPane.QUESTION_MESSAGE, icon, null,
                    Double.toString(maxTimeScore));
            isValid = isDouble(temp);
            if(!isValid)
                JOptionPane.showMessageDialog(Game.m,String.format("Invalid Input! %nFound: %s%nRequired: %s",temp,((Object)maxTimeScore).getClass().getName()),title,JOptionPane.ERROR_MESSAGE,icon);
        } while (!isValid);
        maxTimeScore = Double.valueOf(temp);

        //Init timeOutTime
        do {
            temp = (String) JOptionPane.showInputDialog(Game.m,
                    "Please Enter timeOutTime: \nThis is a measure of how fast points for time accrue",
                    title, JOptionPane.QUESTION_MESSAGE, icon, null,
                    Integer.toString(timeOutTime));
            isValid = isInt(temp);
            if(!isValid)
                JOptionPane.showMessageDialog(Game.m,String.format("Invalid Input! %nFound: %s%nRequired: %s",temp,((Object)timeOutTime).getClass().getName()),title,JOptionPane.ERROR_MESSAGE,icon);
        } while (!isValid);
        timeOutTime = Integer.valueOf(temp);

        //Init percentOldToKeep
        do {
            temp = (String) JOptionPane.showInputDialog(Game.m,
                    "Please Enter percentOldToKeep: \nThis is a measure of how many snakes to not kill each generation.",
                    title, JOptionPane.QUESTION_MESSAGE, icon, null,
                    Double.toString(percentOldToKeep));
            isValid = isDouble(temp);
            if(!isValid)
                JOptionPane.showMessageDialog(Game.m,String.format("Invalid Input! %nFound: %s%nRequired: %s",temp,((Object)percentOldToKeep).getClass().getName()),title,JOptionPane.ERROR_MESSAGE,icon);
        } while (!isValid);
        percentOldToKeep = Double.valueOf(temp);

        //Init numTimesToRunGeneration
        do {
            temp = (String) JOptionPane.showInputDialog(Game.m,
                    "Please Enter numTimesToRunGeneration: \nThis is a measure of how many times each snake is ran.",
                    title, JOptionPane.QUESTION_MESSAGE, icon, null,
                    Integer.toString(numTimesToRunGeneration));
            isValid = isInt(temp);
            if(!isValid)
                JOptionPane.showMessageDialog(Game.m,String.format("Invalid Input! %nFound: %s%nRequired: %s",temp,((Object)numTimesToRunGeneration).getClass().getName()),title,JOptionPane.ERROR_MESSAGE,icon);
        } while (!isValid);
        numTimesToRunGeneration = Integer.valueOf(temp);

        //Init numTimesToRunGenerationDecreaseBy
        do {
            temp = (String) JOptionPane.showInputDialog(Game.m,
                    "Please Enter numTimesToRunGenerationDecreaseBy: \nThis is a measure of how fast numTimesToRunGeneration decreases.",
                    title, JOptionPane.QUESTION_MESSAGE, icon, null,
                    Integer.toString(numTimesToRunGenerationDecreaseBy));
            isValid = isInt(temp);
            if(!isValid)
                JOptionPane.showMessageDialog(Game.m,String.format("Invalid Input! %nFound: %s%nRequired: %s",temp,((Object)numTimesToRunGenerationDecreaseBy).getClass().getName()),title,JOptionPane.ERROR_MESSAGE,icon);
        } while (!isValid);
        numTimesToRunGenerationDecreaseBy = Integer.valueOf(temp);

        //Init thresholdForRemovingBestRun
        do {
            temp = (String) JOptionPane.showInputDialog(Game.m,
                    "Please Enter thresholdForRemovingBestRun: \nThis is a threshold of numTimesToRunGeneration for removing the best run of each snake.",
                    title, JOptionPane.QUESTION_MESSAGE, icon, null,
                    Integer.toString(thresholdForRemovingBestRun));
            isValid = isInt(temp);
            if(!isValid)
                JOptionPane.showMessageDialog(Game.m,String.format("Invalid Input! %nFound: %s%nRequired: %s",temp,((Object)thresholdForRemovingBestRun).getClass().getName()),title,JOptionPane.ERROR_MESSAGE,icon);
        } while (!isValid);
        thresholdForRemovingBestRun = Integer.valueOf(temp);
        
        logVars(path);
    }
    private static boolean isDouble(String myString) {
//        final String Digits     = "(\\p{Digit}+)";
//        final String HexDigits  = "(\\p{XDigit}+)";
//        // an exponent is 'e' or 'E' followed by an optionally
//        // signed decimal integer.
//        final String Exp        = "[eE][+-]?"+Digits;
//        final String fpRegex    =
//                ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
//                        "[+-]?(" + // Optional sign character
//                        "NaN|" +           // "NaN" string
//                        "Infinity|" +      // "Infinity" string
//
//                        // A decimal floating-point string representing a finite positive
//                        // number without a leading sign has at most five basic pieces:
//                        // Digits . Digits ExponentPart FloatTypeSuffix
//                        //
//                        // Since this method allows integer-only strings as input
//                        // in addition to strings of floating-point literals, the
//                        // two sub-patterns below are simplifications of the grammar
//                        // productions from section 3.10.2 of
//                        // The Java Language Specification.
//
//                        // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
//                        "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+
//
//                        // . Digits ExponentPart_opt FloatTypeSuffix_opt
//                        "(\\.("+Digits+")("+Exp+")?)|"+
//
//                        // Hexadecimal strings
//                        "((" +
//                        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
//                        "(0[xX]" + HexDigits + "(\\.)?)|" +
//
//                        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
//                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
//
//                        ")[pP][+-]?" + Digits + "))" +
//                        "[fFdD]?))" +
//                        "[\\x00-\\x20]*");// Optional trailing "whitespace"
//        return Pattern.matches(fpRegex, myString);
        try{
            Double.parseDouble(myString);
            return true;
        }catch(NumberFormatException e){
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }
    private static boolean isInt(String s) {
        int radix = 10;
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
