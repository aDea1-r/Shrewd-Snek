public class StaticBrainVariables {

    //neural net constants
    static final int sizeInputs = 14;
    static final int sizeHidden = 10;
    static final int numHidden = 2;
    static final int sizeOutput = 4;

    //for the very first mutate, when constructing a new brain
    static final double initialWeight = 0.0;
    static final double initialBias = 1;
    static final double initialMutateMean = 0;
    static final double initialMutateStanDev = 1.0;
    static final double initialBiasMutateMean = 0;
    static final double initialBiasMutateStanDev = 5;

    //For mutating an existing brain
    static double mutateMean = 0;
    static double mutateStanDev = 0.05;
    static double mutateBiasMean = 0;
    static double mutateBiasStanDev = 0.05;

    //For mutating an existing brain
    static double bigMutateChance = 0.05;
    static double mutateBigMean = 0;
    static double mutateBigStanDev = 1.5;
    static double mutateBigBiasMean = 0;
    static double mutateBigBiasStanDev = 1.5;
}
