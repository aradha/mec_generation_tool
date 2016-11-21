import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;


public class DAGEnumerator {
    private BitSet bitMask;
    private Map<Long, Integer> bitLocations;

    public DAGEnumerator(Vertex[] skeleton){
        bitLocations = new HashMap<Long, Integer>();
        bitMask = generateMask(skeleton, bitLocations);  // Generates immorality bit mask
    }
    
    
    private BitSet generateMask(Vertex[] skeleton, Map<Long, Integer> bitLocations){
        Integer bitIndex = 0;
        
        for(int v = 0; v < skeleton.length; v++){
            if(skeleton[v].neighbors.size() > 1){
                //Go through pairs of neighbors
                for(int n1 = 0; n1 < skeleton[v].neighbors.size(); n1++){
                    for(int n2 = n1 + 1; n2 < skeleton[v].neighbors.size(); n2++){
                        Vertex neighbor1 = skeleton[v].neighbors.get(n1);
                        Vertex neighbor2 = skeleton[v].neighbors.get(n2);
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
                            long v_hash = this.hash(v, neighbor1.getLabel(), neighbor2.getLabel());
                            bitLocations.put(v_hash, bitIndex++);
                        }
                    }
                }
            }
        }
        
        BitSet bitMask = new BitSet(bitIndex);
        
        return bitMask;
    }
    
    private long hash(int v1, int v2, int v3) {        
        return cantorPairing(cantorPairing(v1, v2), v3);
      }
    
    private long cantorPairing(long k1, long k2){
        return ((k1 + k2) * (k1 + k2 + 1)) / 2 + k2;
    }
    
    public void enumerate(Vertex[] dag, Map<BitSet, Integer> MECDist){

        bitMask.clear();

        for(int v = 0; v < dag.length; v++){
            
            if(dag[v].neighbors.size() > 1){
                //Go through pairs of neighbors
                for(int n1 = 0; n1 < dag[v].neighbors.size(); n1++){
                    for(int n2 = n1 + 1; n2 < dag[v].neighbors.size(); n2++){
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
                            long v_hash = this.hash(v, neighbor1.getLabel(), neighbor2.getLabel());
                            bitMask.flip(bitLocations.get(v_hash));
                        }
                    }
                }
            }
        }
        
        if(MECDist.containsKey(bitMask)){
            MECDist.put(bitMask, MECDist.get(bitMask) + 1);
        } else{
            MECDist.put(bitMask, 1);
        }       
    }
    
}
