import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DistributionGenerator{

    public static Long hash(List<Integer> values) {
        Long h = 1125899906842597L; // prime
        int len = values.size();
        for (int i = 0; i < len; i++) {
          h = h * 31 + values.get(i);
        }
        return h;
      }
    
    public static void main(String[] args) throws FileNotFoundException{
        
        //Read skeleton from file and make all DAGs
        File f = new File(args[0]);               
        Scanner indata = new Scanner(f);
        
        OptionsParser op = new OptionsParser(args);
        op.parse();
        if(op.getErrorFlag()){
            System.out.println("Invalid Arguments");
            indata.close();
            return;
        }
        
        StopWatch timer = new StopWatch();
        Map<Long, Integer> MultiMECDist = new HashMap<Long, Integer>();
        
        timer.startTimer();
        int count = 0;
        int numberMECs = 0;
        while(indata.hasNext()){
            count++;
            if(count%1000 == 0 && count != 0 && !op.getPrintSkeletonData())
                System.out.println(count);
            int n = indata.nextInt();
            int m = indata.nextInt();
            
            Vertex[] skeleton = new Vertex[n];
            for(int i = 0; i < n; i++){
                skeleton[i] = new Vertex(i);
            }
            
            for(int i = 0; i < m; i++){
                int v1 = indata.nextInt();
                int v2 = indata.nextInt();
                skeleton[v1-1].neighbors.add(skeleton[v2-1]);
                skeleton[v2-1].neighbors.add(skeleton[v1-1]);
            }
            
            DAGGenerator g = new DAGGenerator(skeleton, m, op.getPrintSkeletonData());
            
            List<Vertex> testDag = new ArrayList<Vertex>();
            List<Vertex[]> dagList = new ArrayList<Vertex[]>();
            g.acyclic(0, testDag, dagList);
            
            
            Map<BitSet, Integer> MECDist = new HashMap<BitSet, Integer>();
            DAGEnumerator e = new DAGEnumerator(skeleton);
                        
            for(Vertex[] dag: dagList){
                e.enumerate(dag, MECDist);
            }
                        
            List<Integer> value_set = new ArrayList<Integer>(MECDist.values());
            Collections.sort(value_set);
            numberMECs += value_set.size();
            /*VALUE HASHES FOR MULTI MEC DISTRIBUTION*/
            if(op.getPrintSkeletonData()){
                System.out.println("$ " + value_set.size() + " $ " + dagList.size());
                for(Integer val: value_set){
                    System.out.print(val + " ");
                }
                System.out.println();
            }
                       
            long statistics_hash = hash(value_set);
            if(MultiMECDist.containsKey(statistics_hash)){
                MultiMECDist.put(statistics_hash, MultiMECDist.get(statistics_hash)+1);
            } else{
                MultiMECDist.put(statistics_hash, 1);
            }            
        }
        timer.stopTimer();

        
        /*OUTPUT FOR MULTI MEC DISTRIBUTION*/
        if(op.getPrintMECCheck() || op.getPrintMECDistribution()){
            System.out.println("__________________________________________________________________________________________________");
            System.out.println("DATA FORMAT:");
            System.out.println("PAIRS(#IMMORALITIES, COUNT IN EQUIVALENCE CLASS)" + " | " + " NUMBER OF GRAPHS");
            System.out.println("__________________________________________________________________________________________________");
            for(Long in: MultiMECDist.keySet()){
                if(MultiMECDist.get(in) > 1 && !op.getPrintMECDistribution()){
                    System.out.println("FOUND TWO GRAPHS WITH SAME # OF MECS AND SAME DISTRIBUTION OF MECS");
                    System.out.println(in  + " : " + MultiMECDist.get(in));
                }

                if(op.getPrintMECDistribution()){
                    System.out.println(in  + " : " + MultiMECDist.get(in));
                }
            }           
        }
        System.out.println("NUMBER MECS: " + numberMECs);
        System.out.println("TIME ELAPSED: " + timer.getElapsedTime() + " ms");
        
        indata.close();
        
    }
    
}
