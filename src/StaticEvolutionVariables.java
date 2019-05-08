public class StaticEvolutionVariables {

    //time score stuff
    static int timeScoreMultiplier = 2;     //How fast points for time accrue,
                                            // high number makes a snake get more points in a shorter amount of time, no effect on max points
    static double maxTimeScoreMultiplier = 2;  //Maximum score possible for time, Ex: 1 makes max score slightly less than 1 apple
    static int timeOutTime = 100;           //Time in ticks it takes a snake to die from not eating

    //other stuff
    static double percentOldToKeep = 3/100.0;    //Percent of old generation to keep and mutate
    static int numTimesToRunGeneration = 12;     //Number of times each snake in each generation will run, averages fitness together
    static int numTimesToRunGenerationDecreaseBy = 1;   //How much above variable decreases per gen,
                            // Ex: 1 means each generation it will run one less time until it reaches once per generation


}
