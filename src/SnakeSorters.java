import java.io.Serializable;
import java.util.*;

public class SnakeSorters implements Serializable {
    PriorityQueue<SnakeSorter> pq;
    SnakeSorter[] arr;
    int genNum;
    int genSize;
    public SnakeSorters(int genNum, int genSize){
        this.genNum = genNum;
        this.genSize = genSize;
        pq = new PriorityQueue<SnakeSorter>();
    }
    public void add(){

    }
}
