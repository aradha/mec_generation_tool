import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DistributionGenerator{

    
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
        Map<String, Integer> MultiMECDist = new HashMap<String, Integer>();
        
        timer.startTimer();
        int count = 0;
        int numberMECs = 0;
        while(indata.hasNext()){
            count++;
            if(count%1000 == 0 && count != 0)
                System.out.println(count);
            int n = indata.nextInt();
            int m = indata.nextInt();
            
            Vertex[] skeleton = new Vertex[n];
            for(int i = 0; i < n; i++){
                skeleton[i] = new Vertex(i);
            }
            
            Edge[] edgeSet = new Edge[m];        
            int edgeIndex = 0;
            for(int i = 0; i < m; i++){
                int v1 = indata.nextInt();
                int v2 = indata.nextInt();
                skeleton[v1-1].neighbors.add(skeleton[v2-1]);
                skeleton[v2-1].neighbors.add(skeleton[v1-1]);
                edgeSet[edgeIndex++] = new Edge(v1, v2);
            }
            
            DAGGenerator g = new DAGGenerator(skeleton, m, op.getPrintSkeletonData());
            
            List<Vertex> testDag = new ArrayList<Vertex>();
            List<Vertex[]> dagList = new ArrayList<Vertex[]>();
            g.acyclic(0, testDag, dagList);
            
            Map<String, ImmoralityCountPair> MECDist = new HashMap<String, ImmoralityCountPair>();
            DAGEnumerator e = new DAGEnumerator();
            
            for(Vertex[] dag: dagList){
                e.enumerate(dag, MECDist);
            }
            
            List<ImmoralityCountPair> value_set = new ArrayList<ImmoralityCountPair>(MECDist.values());
            Collections.sort(value_set);
            numberMECs += value_set.size();
                        
            /*VALUE HASHES FOR MULTI MEC DISTRIBUTION*/
            
            String value_hash = "" + value_set.size() + " ";
            String statistics = "";
            for(ImmoralityCountPair val: value_set){
                value_hash += "(" + 0 + "," + val.getCount() + ") ";
                statistics += "(" + val.getNumberImmoralities() + "," + val.getCount() + ") ";
            }
            if(op.getPrintSkeletonData()){
                System.out.println("$ " + value_set.size());
                System.out.println(statistics);
            }
            
            if(MultiMECDist.containsKey(value_hash)){
                MultiMECDist.put(value_hash, MultiMECDist.get(value_hash)+1);
            } else{
                MultiMECDist.put(value_hash, 1);
            }
            
        }
        timer.stopTimer();

        
        /*OUTPUT FOR MULTI MEC DISTRIBUTION*/
        if(op.getPrintMECCheck() || op.getPrintMECDistribution()){
            System.out.println("__________________________________________________________________________________________________");
            System.out.println("DATA FORMAT:");
            System.out.println("# OF MECS " + " | " + "PAIRS(#IMMORALITIES, COUNT IN EQUIVALENCE CLASS)" + " | " + " NUMBER OF GRAPHS");
            System.out.println("__________________________________________________________________________________________________");
            for(String in: MultiMECDist.keySet()){
                if(MultiMECDist.get(in) > 1 && !op.getPrintMECDistribution()){
                    System.out.println("FOUND TWO GRAPHS WITH SAME # OF MECS, SAME DISTRIBUTION OF MECS, AND SAME # OF EDGES");
                    System.out.println(in  + " " + MultiMECDist.get(in));
                }

                if(op.getPrintMECDistribution()){
                    System.out.println(in  + " " + MultiMECDist.get(in));
                }
            }           
            System.out.println("NUMBER MECS: " + numberMECs);
            System.out.println("TIME ELAPSED: " + timer.getElapsedTime() + " ms");
        }
        
        indata.close();
        
    }
    
}
