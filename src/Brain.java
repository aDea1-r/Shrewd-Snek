import javax.management.monitor.GaugeMonitor;
import java.awt.*;
import java.util.*;
import java.io.*;

public class Brain implements Drawable, Serializable {
    private int sizeInput;          //Num nodes in input layer
    private int sizeHidden;         //Num nodes in hidden layer
    private int numHidden;          //Number of hidden layers
    private int sizeOutput;         //Num nodes in output layer

    private double[] inputNodes;        //Array that holds the values of all input nodes
    private double[][] inputWeights;    //Matrix which holds the weights of all input nodes: r = input node; c = hidden node

    private double[][] hiddenNodes;         //Array that holds the values of all hidden nodes r = 0 for first layer
    private double[][] hiddenBias;          //Holds the biases(initial values) for all hidden layers
    private double[][][] hiddenWeights;     //Matrix which holds the weights of all hidden nodes: first index = layer #; r = input node; c = hidden node
                                            //Sentinel value is -999.999 for unused weights

    private double[] outputNodes;           //Array that holds the values of all output nodes

    private Brain(int sI, int sH, int nH, int sO, double initialWeight, double initialBias, double initialMutateMean, double initialMutateStanDev, double initialBiasMutateMean, double initialBiasMutateStanDev){
        sizeInput = sI;
        sizeHidden = sH;
        numHidden = nH;
        sizeOutput = sO;

        inputNodes = new double[sizeInput];
        inputWeights = new double[sizeInput][sizeHidden];
        for (int r = 0; r < inputWeights.length; r++) {
            for (int c = 0; c < inputWeights[r].length; c++) {
                inputWeights[r][c] = initialWeight;
            }
        }

        hiddenNodes = new double[numHidden][sizeHidden];
        hiddenWeights = new double[numHidden][sizeHidden][sizeHidden];
        for (int i = 0; i < numHidden; i++) {                           //init weights
            for (int r = 0; r < hiddenWeights[i].length; r++) {
                for (int c = 0; c < hiddenWeights[i][r].length; c++) {
                    hiddenWeights[i][r][c] = initialWeight;
                }
            }
        }
        hiddenBias = new double[numHidden][sizeHidden];
        for (int i = 0; i < numHidden; i++) {
            for (int j = 0; j < hiddenBias[i].length; j++) {
                hiddenBias[i][j] = initialBias;
            }
        }

        outputNodes = new double[sizeOutput];

        if(!(initialMutateMean == 0 && initialMutateStanDev == 0 && initialBiasMutateMean == 0 && initialBiasMutateStanDev == 0)) {
            //if statement to prevent mutation when cloning brains
            this.mutate(initialMutateMean, initialMutateStanDev, initialBiasMutateMean, initialBiasMutateStanDev);
        }
    }
    Brain() {
        this(14,10,2,4, 0.0, 1, 0, 0.2, 0, 2);
    }

    public static Brain brainReader(int gen, int num) {
        String path = "/"+gen+"/"+num+"/brain.dat";
        try (FileInputStream f = new FileInputStream(path)) {
            ObjectInputStream s = new ObjectInputStream(f);
            Brain temp = (Brain) s.readObject();
            s.close();
            return temp;
        } catch (Exception e) {
            return new Brain();
        }
    }
    //used to create a copy version of existing Brain
    public Brain(Brain parent) {
        this(parent.sizeInput,parent.sizeHidden,parent.numHidden,parent.sizeOutput, 0, 0, 0, 0, 0, 0);
        System.arraycopy(parent.inputNodes,0,inputNodes,0,inputNodes.length);
        System.arraycopy(parent.inputWeights,0,inputWeights,0,inputWeights.length);
        System.arraycopy(parent.hiddenNodes,0,hiddenNodes,0,hiddenNodes.length);
        System.arraycopy(parent.hiddenWeights,0,hiddenWeights,0,hiddenWeights.length);
        System.arraycopy(parent.outputNodes,0,outputNodes,0,outputNodes.length);
    }

    //Given an input, put through neural net and return the index of the correct output node
    double[] compute(int[] inputs){

        for (int i = 0; i < inputs.length; i++) {       //Put the inputs into the neural net
            inputNodes[i] = (double)inputs[i];              //TODO: insert bias here
        }
        
        transferBetweenLayers(inputNodes, inputWeights, sizeHidden, hiddenNodes[0], null);      //Transfer info from inputs to 1st hidden layer
        removeNegatives(hiddenNodes[0]);

        
        for (int i = 0; i < hiddenNodes.length - 1; i++) {  //Loop through each hidden layers----------------
                                                            //This code will only run if there are multiple hidden layers

            double[] layerNodes = hiddenNodes[i];
            double[][] layerWeights = hiddenWeights[i];

            transferBetweenLayers(layerNodes, layerWeights, sizeHidden, hiddenNodes[i + 1], hiddenBias[i + 1]);
            removeNegatives(hiddenNodes[i + 1]);

        }
        
        
        transferBetweenLayers(hiddenNodes[numHidden - 1], hiddenWeights[numHidden - 1], sizeOutput, outputNodes, null);    //Transfer info in last hidden layer to output layer
        removeNegatives(outputNodes);
        
        
//        int maxIndex = 0;                                   //Find the max of the output nodes
//        for (int i = 1; i < outputNodes.length; i++) {
//            if(outputNodes[i] > outputNodes[maxIndex])
//                maxIndex = i;
//        }
//
//        return maxIndex;

        return outputNodes;
    }

    private void transferBetweenLayers(double[] thisLayerNodes, double[][] thisLayerWeights, int thisLayerNodeWeightsLength, double[] nextLayerNodes, double[] nextLayerBias){
        /*
        Given the nodes of a layer, thisLayerNodes
        the weights of a layer, thisLayerWeights
        and the nodes of the next layer, nextLayerNodes
        this method updates all the nods of the nextLayer

        Added: thisLayerNodeWeightsLength

        In addition: when it is on the first node, it will reinitialize the values of every node in nextLayerNodes to bias
         */
        
        for (int nodeNum = 0; nodeNum < thisLayerNodes.length; nodeNum++) { //Loop through nodes------------

            double nodeVal = thisLayerNodes[nodeNum];
            double[] thisNodeWeights = thisLayerWeights[nodeNum];

            for (int weightNum = 0; weightNum < thisLayerNodeWeightsLength; weightNum++) {  //Loop through weights
                double weight = thisNodeWeights[weightNum];
                if(weight != -999.999){
                    if(nextLayerBias != null && nodeNum == 0){          //Biases
                        nextLayerNodes[weightNum] = (nodeVal * weight) + nextLayerBias[weightNum];
                    }
                    else {
                        nextLayerNodes[weightNum] += (nodeVal * weight);
                    }
                }
            }

        }         //End Loop through nodes--------------------------------------------------------------
        
    }

    private void removeNegatives(double[] layerNodes){
        /*
        Loops through a single layer's nodes, layerNodes
        and changes any negative values to 0
         */

        for (int i = 0; i < layerNodes.length; i++) {
            layerNodes[i] = Math.max(0, layerNodes[i]);
        }

    }

    void mutate(double mean, double stanDeviation, double biasMean, double biasStanDeviation) {
        //TODO
        //should mutate current brain object
        Random ran = new Random();
        for (int inputLayerNodeNum = 0; inputLayerNodeNum < inputWeights.length; inputLayerNodeNum++){      //Input weights
            for (int weightNum = 0; weightNum < inputWeights[inputLayerNodeNum].length; weightNum++) {
                inputWeights[inputLayerNodeNum][weightNum] += (ran.nextGaussian()*stanDeviation)+mean;
            }
        }

        for (int hiddenLayerNum = 0; hiddenLayerNum < hiddenBias.length; hiddenLayerNum++) {                //Hidden Layer
            //Biases
            for (int hiddenBiasNumber = 0; hiddenBiasNumber < hiddenBias[hiddenLayerNum].length; hiddenBiasNumber++) {
                hiddenBias[hiddenLayerNum][hiddenBiasNumber] += (ran.nextGaussian()*biasStanDeviation)+biasMean;
            }

            //Weights
            for (int hiddenLayerNodeNum = 0; hiddenLayerNodeNum < hiddenWeights[hiddenLayerNum].length; hiddenLayerNodeNum++) {
                for (int weightNum = 0; weightNum < hiddenWeights[hiddenLayerNum][hiddenLayerNodeNum].length; weightNum++) {
                    hiddenWeights[hiddenLayerNum][hiddenLayerNodeNum][weightNum] += (ran.nextGaussian()*stanDeviation)+mean;
                }
            }
        }

        for (int hiddenLayerNum = 0; hiddenLayerNum < hiddenWeights.length; hiddenLayerNum++) {

        }
    }

    @Override
    public void drawMe(Graphics g) {
        System.out.println("Dummy Brain draw");
    }

    public void drawMe(Graphics g, Grid grid){
        g.setColor(Color.cyan);

        int totalGridSize = grid.numSquares*grid.size;
//        System.out.println(totalGridSize);

        int fontSizeTitles = totalGridSize/18;
        g.setFont(new Font("TimesRoman", Font.BOLD, fontSizeTitles));
        int startXPixels = grid.getXPixels((int)(grid.numSquares * 1.25));
        int startYPixels = grid.getYPixels(0);
        g.drawString(String.format("Inputs%8sOutputs", ""),startXPixels, startYPixels);

        int fontSizeInputs = totalGridSize/(6+inputNodes.length);
        int spaceBetweenInputs = totalGridSize/inputNodes.length;
        for (int i = 0; i < inputNodes.length; i++) {
            g.drawString(inputNodes[i]+"", startXPixels, startYPixels + (spaceBetweenInputs*(i+1)));
        }

        int fontSizeOutputs = totalGridSize/(6+outputNodes.length);
        int spaceBetweenOutputs = totalGridSize/outputNodes.length;
        for (int i = 0; i < outputNodes.length; i++) {
//            g.drawString(""+outputNodes[i], startXPixels + fontSizeTitles*5, startYPixels + (spaceBetweenOutputs*(i+1)) - totalGridSize/50);
            g.drawString(String.format("% .3f", outputNodes[i]), startXPixels + fontSizeTitles*5, startYPixels + (spaceBetweenOutputs*(i+1)) - spaceBetweenOutputs/2);
        }
    }
    void log(int generation, int brainID) {
        File file = new File(generation + "/" + brainID + "/brain.dat");
        file.mkdirs();
        try {
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(this);
            s.close();
        } catch (IOException e) {
            System.out.println("bad error");
        }
    }
}
