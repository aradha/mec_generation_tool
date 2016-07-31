import java.util.Map;


public class DAGEnumerator {

    public DAGEnumerator(){        
    }
    
    public void enumerate(Vertex[] dag, Map<String, ImmoralityCountPair> MECDist){
        String v_structs = "";
        int num_v_structs = 0;
       
        for(int v = 0; v < dag.length; v++){
            
            if(dag[v].neighbors.size() > 1){
                //Go through pairs of neighbors
                for(int n1 = 0; n1 < dag[v].neighbors.size(); n1++){
                    for(int n2 = n1+1; n2 < dag[v].neighbors.size(); n2++){
                        Vertex neighbor1 = dag[v].neighbors.get(n1);
                        Vertex neighbor2 = dag[v].neighbors.get(n2);
                        boolean foundEdge = false;
                        for(Vertex check1: neighbor1.neighbors){
                            if(check1.getLabel() == neighbor2.getLabel()){
                                foundEdge = true;
                                break;
                            }
                        }
                        for(Vertex check2: neighbor2.neighbors){
                            if(check2.getLabel() == neighbor1.getLabel()){
                                foundEdge = true;
                                break;
                            }
                        }
                        if(!foundEdge){
                            //System.out.println("GOT HERE");
                            v_structs += v + " " + neighbor1.getLabel() + " " + neighbor2.getLabel() + " ";
                            num_v_structs += 1;
                        }
                    }
                }
            }
        }
        
        //System.out.println("V STRUCT: " + v_structs);
        if(MECDist.containsKey(v_structs)){
            ImmoralityCountPair p = MECDist.get(v_structs);
            p.incrementCount();
            MECDist.put(v_structs, p);
        } else{
            ImmoralityCountPair p = new ImmoralityCountPair(num_v_structs, 1);         
            
            //ImmoralityCountPair p = new ImmoralityCountPair(0, 1); 

            MECDist.put(v_structs, p);
        }       
    }
    
}
