import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//Vertices are 0 indexed
public class DAGGenerator {
    private Vertex[] skeleton;
    private int numEdges;
    
    public DAGGenerator(Vertex[] skeleton, int _numEdges, boolean printFlag){
        this.skeleton = skeleton;
        this.numEdges = _numEdges;
        if(printFlag)
            this.printStatistics();
    }
    
    public void printStatistics(){
        //Want to print out # Edges, # triangles, # 2-paths, Degree Distribution, #MECs
        System.out.print("$ " + numEdges + " ");
        
        int numTriangles = 0;
        int numTwoPaths = 0;
        
        for(int v = 0; v < skeleton.length; v++){
            
            //Search pairs of neighbors:
            for(int v1 = 0; v1 < skeleton[v].neighbors.size(); v1++){
                for(int v2 = v1+1; v2 < skeleton[v].neighbors.size(); v2++){
                    Vertex neighbor1 = skeleton[v].neighbors.get(v1);
                    Vertex neighbor2 = skeleton[v].neighbors.get(v2);
                    
                    boolean isTriangle = false;
                    for(Vertex v1Neighbor: neighbor1.neighbors){
                        if(v1Neighbor.getLabel() == neighbor2.getLabel()){
                            numTriangles++;
                            isTriangle = true;
                            break;
                        }
                    }
                    if(!isTriangle){
                        numTwoPaths++;
                    }
                }
            }
        }
        
        System.out.print("$ " + numTriangles/3 + " ");
        System.out.print("$ " + numTwoPaths + " $ ");

        //Get Degree Distribution:
        for(int v = 0; v < skeleton.length; v++){
            System.out.print(skeleton[v].neighbors.size() + " ");
        }
    }
    
    public void topOrderDFS(List<Vertex> dag, int i, List<Integer> order, boolean[] marked){
        if(!marked[i]){
            for(Vertex neighbor: dag.get(i).neighbors){
                topOrderDFS(dag, neighbor.getLabel(), order, marked);
            }
            marked[i] = true;
            order.add(0, i);
        }        
    }
        
    //Return topological order as array from order number to vertex
    //We will need the reverse map as well to determine order number given vertex
    public Integer[] topologicalOrder(List<Vertex> dag){
        boolean[] marked = new boolean[dag.size()];  // Should be all false

        List<Integer> orderList = new ArrayList<Integer>();
        
        for(int i = 0; i < dag.size(); i++){
            topOrderDFS(dag, i, orderList, marked);
        }
        
        return orderList.toArray(new Integer[dag.size()]);
    }

    
    public boolean hasSmallerNeighbors(int i){
        for(Vertex v: this.skeleton[i].neighbors){
            if(v.getLabel() < i){
                return true;
            }
        }
        return false;
    }
    
     
    //ACYCLIC function from paper
    public void acyclic(int i, List<Vertex> dag, List<Vertex[]> dagList){
        if(i >= skeleton.length){
            
            Vertex[] reverseDag = new Vertex[dag.size()];
            for(int k = 0; k < reverseDag.length; k++){
                reverseDag[k] = new Vertex(k);
            }
            for(int k = 0; k < dag.size(); k++){
                for(Vertex neighbor: dag.get(k).neighbors){
                    reverseDag[neighbor.getLabel()].neighbors.add(reverseDag[k]);
                }
            }
            
            dagList.add(reverseDag);
            return;
            
        }
        else if(!hasSmallerNeighbors(i)){
            dag.add(new Vertex(i));
            acyclic(++i, dag, dagList);
        }
        else{
            
            //Find topological ordering of dag
            Integer[] order = topologicalOrder(dag);
            
            //Create d function for neighbors of i in dag:
            Set<Integer> validNeighbors = new HashSet<Integer>();
            Integer d_size = 0;
            for(Vertex neighbor: skeleton[i].neighbors){
                if(neighbor.getLabel() < i){
                    d_size += 1;
                    validNeighbors.add(neighbor.getLabel());
                }
            }
            
            BitSet d = new BitSet(d_size);

            
            //Map from neighbor to position in d function string
            Map<Integer, Integer> neighborPosition = new HashMap<Integer, Integer>();
            Map<Integer, Integer> reverseNeighborPosition = new HashMap<Integer, Integer>();

            int index = 0;
            for(int j = 0; j < order.length; j++){
                if(validNeighbors.contains(order[j])){
                    reverseNeighborPosition.put(order[j], index);
                    neighborPosition.put(index++, order[j]);
                }
            }
            
            boolean last = false;
            //While not done:
            while(!last){
                Vertex vi = new Vertex(i);
                dag.add(vi);
                //Create new DAG:
                for(int j = 0; j < d_size; j++){
                    Vertex neighbor = dag.get(neighborPosition.get(j));
                    if(!d.get(j)){
                        //Draw edge from neighborPosition(j) to vertex i
                        neighbor.neighbors.add(vi);
                    } else{
                        //Draw edge from vertex i to neighborPosition(j)
                        vi.neighbors.add(neighbor);
                    }
                    
                }
                
                acyclic(i+1, dag, dagList);
                
                //clean new_dag            
                while(dag.get(dag.size()-1).getLabel() >= i)
                    dag.remove(dag.get(dag.size()-1));
                
                for(int j = 0; j < d_size; j++){
                    Vertex neighbor = dag.get(neighborPosition.get(j));
                    if(!d.get(j)){
                        //Draw edge from neighborPosition(j) to vertex i
                        neighbor.neighbors.remove(vi);
                    } else{
                        //Draw edge from vertex i to neighborPosition(j)
                        vi.neighbors.remove(neighbor);
                    }
                }
               
                if(d.cardinality() == d_size){
                    break;
                }
                d = updateD(d, neighborPosition, dag, reverseNeighborPosition, d_size);
            }

        }
    }
    
    public boolean ancestorDFS(List<Vertex> dag, int x, BitSet d, boolean[] fixed, Map<Integer, Integer> reverseNeighborPosition){
        List<Vertex> agenda = new ArrayList<Vertex>();
        agenda.add(dag.get(x));
        while(!agenda.isEmpty()){
            Vertex first = agenda.get(0);
            agenda.remove(0);
            if(reverseNeighborPosition.containsKey(first.getLabel())){
                fixed[reverseNeighborPosition.get(first.getLabel())] = true;
            }
            for(Vertex neighbor: first.neighbors){
                agenda.add(0, neighbor);
                if(reverseNeighborPosition.containsKey(neighbor.getLabel())){
                    d.set(reverseNeighborPosition.get(neighbor.getLabel()), true);
                }
            }
        }
        return true;
    }
    
    public Integer incrementBits(BitSet d, Integer d_size){
        int lastPosition = d_size - 1; 

        // Looping from right to left
        for (int i = lastPosition; i >= 0; i--) {
            if (!d.get(i)) {
                d.flip(i); // If current digit is 0 I change it to 1
                break;
            }
            d.set(i, false);
            if(i == 0){
                BitSet newD = new BitSet(d_size + 1);
                newD.get(1, d_size).xor(d);
                newD.set(0, true);
                return d_size + 1;
            }
        }
        return d_size;
    }
    
    public BitSet updateD(BitSet oldD, Map<Integer, 
                                 Integer> neighborPosition, 
                                 List<Vertex> dag, 
                                 Map<Integer,Integer> reverseNeighborPosition,
                                 Integer d_size){
        
        d_size = incrementBits(oldD, d_size);
        boolean[] fixed = new boolean[d_size];
        for(int i = 0; i < d_size; i++){
            if(oldD.get(i)){
                if(!fixed[i])
                    ancestorDFS(dag, neighborPosition.get(i), 
                            oldD, fixed, reverseNeighborPosition);
            }
        }
        return oldD;
    }
}
