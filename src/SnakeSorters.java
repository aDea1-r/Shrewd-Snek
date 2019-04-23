import java.io.*;
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
    public void add(SnakeSorter s){
        pq.add(s);
    }
    private void initArr(){
        arr = new SnakeSorter[genSize];
        for (int i = 0; i < genSize; i++) {
            arr[i] = pq.poll();
        }
        pq = null;
    }
    public SnakeSorter getNth(int n){
        if(arr == null)
            initArr();
        return arr[n];
    }
    public void log() {
        pq = null;
        File file = new File(genNum + "/");
        file.mkdirs();
        try {
            FileOutputStream f = new FileOutputStream(genNum + "/scores.dat");
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(this);
            s.close();
            System.out.println("logged scores of gen"+genNum);
        } catch (IOException e) {
            System.out.println("bad error");
        }
    }
}
