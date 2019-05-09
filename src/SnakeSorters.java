import java.io.*;
import java.util.*;

public class SnakeSorters implements Serializable {
    PriorityQueue<SnakeSorter> pq;
    private SnakeSorter[] arr;
    int genNum;
    int genSize;
    String speciesName;
    boolean sorted;

    public SnakeSorters(int genNum, int genSize, String speciesName){
        this.genNum = genNum;
        this.genSize = genSize;
        arr = new SnakeSorter[genSize];
        this.speciesName = speciesName;

        sorted = false;
//        pq = new PriorityQueue<SnakeSorter>();
    }
    public void add(SnakeSorter s){
        arr[s.genID] = s;
//        System.out.printf("SnakeSorter ID #%d, added %n", s.genID);
    }
    private void initArr(){
//        arr = new SnakeSorter[genSize];
        pq = new PriorityQueue<SnakeSorter>();
        for (int i = 0; i < genSize; i++) {
//            System.out.printf("pq: %s%n", pq.toString());
            pq.add(arr[i]);
        }
        for (int i = 0; i < genSize; i++) {
            arr[i] = pq.poll();
        }
        pq = null;
        sorted = true;
    }
    public SnakeSorter getNth(int n){
        if(!sorted)
            initArr();
        return arr[n];
    }
    public void log() {
        if(!sorted)
            initArr();
        pq = null;
        String path = String.format("Training Data/%s/%d/", speciesName, genNum);
//        File file = new File(path);
//        file.mkdirs();
        int i = 0;
        try {
            FileOutputStream f = new FileOutputStream(path + "scores.dat");
            i++;
            f.write(34);
            i++;
            ObjectOutputStream s = new ObjectOutputStream(f);
            i++;
            s.writeObject(arr);
            i++;
            s.close();
            i++;
            System.out.println("logged scores of gen"+genNum);
            i++;
        } catch (IOException e) {
            System.out.printf("Error logging snakesorters, i is %d, path is %s %n",i,path+"scores.dat");
        }
    }

    public static SnakeSorters snakeSortersReader(int gen, String speciesName){
        String path = String.format("Training Data/%s/%d/scores.dat", speciesName, gen);
        try (FileInputStream f = new FileInputStream(path)) {
            ObjectInputStream s = new ObjectInputStream(f);
            SnakeSorters temp = (SnakeSorters) s.readObject();
            s.close();
            return temp;
        } catch (FileNotFoundException e){
            System.out.printf("FileNotFound at snakeSorters readIn, gen = %d, speciesName = %s%n", gen, speciesName);
            return null;
        } catch (Exception e){
            System.out.printf("Exception: %s, at SnakeSorters readin%n", e);
            return null;
        }
    }
}
