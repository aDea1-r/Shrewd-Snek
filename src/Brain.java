import javax.management.monitor.GaugeMonitor;
import java.util.*;
import java.io.*;

public class Brain {
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

    public Brain(int sI, int sH, int nH, int sO){
        sizeInput = sI;
        sizeHidden = sH;
        numHidden = nH;
        sizeOutput = sO;

        inputNodes = new double[sizeInput];
        inputWeights = new double[sizeInput][sizeHidden];

        hiddenNodes = new double[numHidden][sizeHidden];
        hiddenBias = new double[numHidden][sizeHidden];
        hiddenWeights = new double[numHidden][sizeHidden][sizeHidden];

        outputNodes = new double[sizeOutput];
    }
    //used to create a mutated version of existing Brain
    public Brain(Brain parent) {
        this(parent.sizeInput,parent.sizeHidden,parent.numHidden,parent.sizeOutput);
        System.arraycopy(parent.inputNodes,0,inputNodes,0,inputNodes.length);
        System.arraycopy(parent.inputWeights,0,inputWeights,0,inputWeights.length);
        System.arraycopy(parent.hiddenNodes,0,hiddenNodes,0,hiddenNodes.length);
        System.arraycopy(parent.hiddenWeights,0,hiddenWeights,0,hiddenWeights.length);
        System.arraycopy(parent.outputNodes,0,outputNodes,0,outputNodes.length);
    }

    //Given an input, put through neural net and return the index of the correct output node
    public double[] compute(int[] inputs){

        for (int i = 0; i < inputs.length; i++) {       //Put the inputs into the neural net
            inputNodes[i] = (double)inputs[i];              //TODO: insert bias here
        }
        
        transferBetweenLayers(inputNodes, inputWeights, hiddenNodes[0], hiddenBias[0]);      //Transfer info from inputs to 1st hidden layer
        removeNegatives(hiddenNodes[0]);

        
        for (int i = 0; i < hiddenNodes.length - 1; i++) {  //Loop through each hidden layers----------------
                                                            //This code will only run if there are multiple hidden layers

            double[] layerNodes = hiddenNodes[i];
            double[][] layerWeights = hiddenWeights[i];

            transferBetweenLayers(layerNodes, layerWeights, hiddenNodes[i + 1], hiddenBias[i + 1]);
            removeNegatives(hiddenNodes[i + 1]);

        }
        
        
        transferBetweenLayers(hiddenNodes[numHidden - 1], hiddenWeights[numHidden - 1], outputNodes, null);    //Transfer info in last hidden layer to output layer
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

    private void transferBetweenLayers(double[] thisLayerNodes, double[][] thisLayerWeights, double[] nextLayerNodes, double[] nextLayerBias){
        /*
        Given the nodes of a layer, thisLayerNodes
        the weights of a layer, thisLayerWeights
        and the nodes of the next layer, nextLayerNodes
        this method updates all the nods of the nextLayer

        In addition: when it is on the first node, it will reinitialize the values of every node in nextLayerNodes to bias
         */
        
        for (int nodeNum = 0; nodeNum < thisLayerNodes.length; nodeNum++) { //Loop through nodes------------

            double nodeVal = thisLayerNodes[nodeNum];
            double[] thisNodeWeights = thisLayerWeights[nodeNum];

            for (int weightNum = 0; weightNum < thisNodeWeights.length; weightNum++) {  //Loop through weights
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

    private void mutate(double mean, double stanDeviation) {
        //TODO
        //should mutate current brain object
        Random ran = new Random();
        for (int i = 0; i < inputWeights.length; i++) {             //input weights
            for (int j = 0; j < inputWeights[i].length; j++) {
                if(inputWeights[i][j] != -999.999)
                    inputWeights[i][j] += (ran.nextGaussian()*stanDeviation)+mean;
            }
        }

        for (int hiddenLayerNum = 0; hiddenLayerNum < hiddenBias.length; hiddenLayerNum++) {    //hidden biases
            for (int i = 0; i < hiddenBias[hiddenLayerNum].length; i++) {
                if (hiddenBias[hiddenLayerNum][i] != -999.999)
                    hiddenBias[hiddenLayerNum][i] += (ran.nextGaussian()*stanDeviation)+mean;
            }
        }

        for (int hiddenLayerNum = 0; hiddenLayerNum < hiddenWeights.length; hiddenLayerNum++) {     //hidden weights
            for (int i = 0; i < hiddenWeights[hiddenLayerNum].length; i++) {
                for (int j = 0; j < hiddenWeights[hiddenLayerNum][i].length; j++) {
                    if(hiddenWeights[hiddenLayerNum][i][j] != -999.999)
                        hiddenWeights[hiddenLayerNum][i][j] += (ran.nextGaussian()*stanDeviation)+mean;
                }
            }
        }
    }
}
