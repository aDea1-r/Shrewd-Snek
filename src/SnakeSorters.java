import java.io.*;
import java.util.*;

public class SnakeSorters implements Serializable {
    PriorityQueue<SnakeSorter> pq;
    SnakeSorter[] arr;
    int genNum;
    int genSize;
    String speciesName;
    public SnakeSorters(int genNum, int genSize, String speciesName){
        this.genNum = genNum;
        this.genSize = genSize;
        arr = new SnakeSorter[genSize];
//        pq = new PriorityQueue<SnakeSorter>();
    }
    public void add(SnakeSorter s){
        arr[s.genID] = s;
    }
    private void initArr(){
//        arr = new SnakeSorter[genSize];
        pq = new PriorityQueue<SnakeSorter>();
        for (int i = 0; i < genSize; i++) {
            pq.add(arr[i]);
        }
        for (int i = 0; i < genSize; i++) {
            arr[i] = pq.poll();
        }
        pq = null;
    }
    public SnakeSorter getNth(int n){
        if(pq == null)
            initArr();
        return arr[n];
    }
    public void log() {
        pq = null;
        String path = String.format("Training Data/%s/%d/", speciesName, genNum);
        File file = new File(path);
        file.mkdirs();
        try {
            FileOutputStream f = new FileOutputStream(path + "/scores.dat");
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(this);
            s.close();
            System.out.println("logged scores of gen"+genNum);
        } catch (IOException e) {
            System.out.println("bad error");
        }
    }
}
