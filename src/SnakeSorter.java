import java.io.Serializable;

/*
Class to help sort the snakes in the generation
So that we can delete snakes from memory
 */
public class SnakeSorter implements Comparable, Serializable {
    int genNum;
    int genID;
    private double score;
    public SnakeSorter(int gN, int gI, double s){
        genNum = gN;
        genID = gI;
        score = s;
    }

    SnakeSorter(GameEngineVariableTickRate in){
        genNum = GameEngineVariableTickRate.genNum;
        genID = in.genID;
        score = in.getFitness();
    }

    @Override
    public int compareTo(Object o) {
        if(this.score < ((SnakeSorter)o).score)
            return 1;
        else if(this.score > ((SnakeSorter)o).score)
            return -1;
        else
            return 0;
    }

    public String toString(){
        return String.format("Snek #%d: score = %f", genID, score);
    }
}