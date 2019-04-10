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
        hiddenWeights = new double[numHidden][sizeHidden][sizeHidden];

        outputNodes = new double[sizeOutput];
    }

    //Given an input, put through neural net and return the index of the correct output node
    public int compute(int[] inputs){

        for (int i = 0; i < inputs.length; i++) {       //Put the inputs into the neural net
            inputNodes[i] = (double)inputs[i];
        }

        for (int i = 0; i < inputNodes.length; i++) {   //Loop through input nodes-----------------------

            double nodeVal = inputNodes[i];
            double[] thisNodeWeights = inputWeights[i];

            for (int j = 0; j < thisNodeWeights.length; j++) {  //Loop through weights----------------------
                double weight = thisNodeWeights[j];
                if(weight != -999.999){
                    hiddenNodes[0][j] += (nodeVal * weight);
                }
            }                                                   //End loop through weights------------------

        }                                               //End loop through input nodes-------------------

        for (int i = 0; i < hiddenNodes.length - 1; i++) {  //Loop through each hidden layers----------------
                                                        //This code will only run if there are multiple hidden layers

            double[] layerNodes = hiddenNodes[i];
            double[][] layerWeights = hiddenWeights[i];

            for (int nodeNum = 0; nodeNum < layerNodes.length; nodeNum++) { //Loop through nodes------------

                double nodeVal = layerNodes[nodeNum];
                double[] thisNodeWeights = layerWeights[nodeNum];

                for (int weightNum = 0; weightNum < thisNodeWeights.length; weightNum++) {  //Loop through weights
                    double weight = thisNodeWeights[weightNum];
                    if(weight != -999.999){
                        hiddenNodes[i + 1][weightNum] += (nodeVal * weight);
                    }
                }

            }         //End Loop through nodes--------------------------------------------------------------

        }

        return 'N';
    }

//    private int computeHelp(){
//
//    }
}
