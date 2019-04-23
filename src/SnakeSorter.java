/*
Class to help sort the snakes in the generation
So that we can delete snakes from memory
 */
public class SnakeSorter implements Comparable {
    int genNum;
    int genID;
    int score;
    public SnakeSorter(int gN, int gI, int s){
        genNum = gN;
        genID = gI;
        score = s;
    }

    @Override
    public int compareTo(Object o) {
        return this.score - ((SnakeSorter)o).score;
    }
}