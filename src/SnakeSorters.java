import java.io.*;
import java.util.*;

public class SnakeSorters implements Serializable {
    PriorityQueue<SnakeSorter> pq;
    private SnakeSorter[] arr;
    int genNum;
    private int genSize;
    String speciesName;
    private boolean sorted;

    SnakeSorters(int genNum, int genSize, String speciesName){
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
    SnakeSorter getNth(int n){
        if(!sorted)
            initArr();
        return arr[n];
    }
    int getGenSize() {
        return genSize;
    }
    void log() {
        if(!sorted)
            initArr();
        pq = null;
        String path = String.format("Training Data/%s/%d/", speciesName, genNum);
//        File file = new File(path);
//        file.mkdirs();
//        int i = 0;
        try {
            FileOutputStream f = new FileOutputStream(path + "scores.dat");
//            i++;
//            f.write(34);
//            i++;
            ObjectOutputStream s = new ObjectOutputStream(f);
//            i++;
            s.writeObject(this);
//            i++;
            s.close();
//            i++;
            System.out.println("logged scores of gen"+genNum);
//            i++;
        } catch (IOException e) {
            System.out.printf("Error logging snakesorters, path is %s %n",path+"scores.dat");
        }
    }

    static SnakeSorters snakeSortersReader(int gen, String speciesName){
        String path = String.format("Training Data/%s/%d/scores.dat", speciesName, gen);
//        int i = 0;
        try (FileInputStream f = new FileInputStream(path)) {
//            i++;
//            System.out.println(f.available());
//            i++;
            ObjectInputStream s = new ObjectInputStream(f);
//            i++;
            SnakeSorters temp = (SnakeSorters) s.readObject();
//            i++;
            s.close();
//            i++;
            return temp;
        } catch (FileNotFoundException e){
            System.out.printf("FileNotFound at snakeSorters readIn, gen = %d, speciesName = %s%n", gen, speciesName);
        } catch (IOException e){
            System.out.printf("IOException, at SnakeSorters readin%n");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException at snakeSorters readin");
        }
        return null;
    }
}
