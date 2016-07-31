import java.util.ArrayList;
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
        StringBuilder statistics = new StringBuilder("");
        statistics.append("$ " + numEdges + " ");
        
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
        
        statistics.append("$ " + numTriangles/3 + " ");
        statistics.append("$ " + numTwoPaths + " ");
        
        //Get Degree Distribution:
        statistics.append("$ ");
        for(int v = 0; v < skeleton.length; v++){
            statistics.append(skeleton[v].neighbors.size() + " ");
        }
        System.out.print(statistics);
        
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
    public int[] topologicalOrder(List<Vertex> dag){
        int[] order = new int[dag.size()];
        boolean[] marked = new boolean[dag.size()]; //Should be all false

        List<Integer> orderList = new ArrayList<Integer>();
        
        for(int i = 0; i < dag.size(); i++){
            if(!marked[i]){
                topOrderDFS(dag, i, orderList, marked);                
            }
        }
        
        int index = 0;
        for(Integer val: orderList){
            order[index++] = val;
        }
        return order;
    }
    
    public boolean hasNeighbors(int i){
        for(Vertex v: skeleton[i].neighbors){
            if(v.getLabel() < i){
                return true;
            }
        }
        return false;
    }
    
     
    //ACYCLIC function from paper
    public void acyclic(int i, List<Vertex> dag, List<Vertex[]> dagList){
        if(i >= skeleton.length){
            
//            System.out.println("FINAL DAG");
//            for(int k = 0; k < dag.size(); k++){
//                System.out.print(dag.get(k).getLabel() + "{ ");
//                for(Vertex neighbor: dag.get(k).neighbors){
//                    System.out.print(neighbor.getLabel() + " ");
//                }
//                System.out.println("}");
//            }         
//            System.out.println();
            
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
            //e.enumerate(dag, MECDist);
            return;
            
        }
        else if(!hasNeighbors(i)){
            dag.add(new Vertex(i));
            acyclic(++i, dag, dagList);
        }
        else{
            
//            for(int k = 0; k < dag.size(); k++){
//                System.out.print(dag.get(k).getLabel() + "{ ");
//                for(Vertex neighbor: dag.get(k).neighbors){
//                    System.out.print(neighbor.getLabel() + " ");
//                }
//                System.out.println("}");
//            }            
//            System.out.println("");

            
            //Find topological ordering of dag
            int[] order = topologicalOrder(dag);
            //Reverse order to get from vertices to topological order
//            int[] vertexToOrder = new int[order.length];
//            for(int j = 0 ; j < order.length; j++){
//                vertexToOrder[order[j]] = j; 
//            }
            
//            for(int k = 0; k < order.length; k++){
//                System.out.println(k + " " + order[k]);
//            }

            
            //Create d function for neighbors of i in dag:
            StringBuilder d = new StringBuilder("");
            Set<Integer> validNeighbors = new HashSet<Integer>();
            for(Vertex neighbor: skeleton[i].neighbors){
                if(neighbor.getLabel() < i){
                    d.append('0');
                    validNeighbors.add(neighbor.getLabel());
                }
            }
            
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
                for(int j = 0; j < d.length(); j++){
                    Vertex neighbor = dag.get(neighborPosition.get(j));
                    if(d.charAt(j) == '0'){
                        //Draw edge from neighborPosition(j) to vertex i
                        neighbor.neighbors.add(vi);
                    } else{
                        //Draw edge from vertex i to neighborPosition(j)
                        vi.neighbors.add(neighbor);
                    }
                    
                }
                
//                System.out.println("NEW DAG: " + " " + i);
//                for(int k = 0; k < dag.size(); k++){
//                    System.out.print(dag.get(k).getLabel() + "{ ");
//                    for(Vertex neighbor: dag.get(k).neighbors){
//                        System.out.print(neighbor.getLabel() + " ");
//                    }
//                    System.out.println("}");
//                }            
//                System.out.println("");

                
                acyclic(i+1, dag, dagList);
                
                //clean new_dag            
                while(dag.get(dag.size()-1).getLabel() >= i)
                    dag.remove(dag.get(dag.size()-1));
                for(int j = 0; j < d.length(); j++){
                    Vertex neighbor = dag.get(neighborPosition.get(j));
                    if(d.charAt(j) == '0'){
                        //Draw edge from neighborPosition(j) to vertex i
                        neighbor.neighbors.remove(vi);
                    } else{
                        //Draw edge from vertex i to neighborPosition(j)
                        vi.neighbors.remove(neighbor);
                    }
                }
                
//                System.out.println("CLEANED" + " " + i);
//                for(int k = 0; k < dag.size(); k++){
//                    System.out.print(dag.get(k).getLabel() + "{ ");
//                    for(Vertex neighbor: dag.get(k).neighbors){
//                        System.out.print(neighbor.getLabel() + " ");
//                    }
//                    System.out.println("}");
//                }
                
                if(d.toString().matches("1+")){
                    break;
                }
                d = updateD(d, neighborPosition, dag, reverseNeighborPosition);
            }

        }
    }
    
    public boolean ancestorDFS(List<Vertex> dag, int x, StringBuilder d, boolean[] fixed, Map<Integer, Integer> reverseNeighborPosition){
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
                    //System.out.println(neighbor.getLabel() + " " + d);
                    d.setCharAt(reverseNeighborPosition.get(neighbor.getLabel()), '1');
                }
            }
        }
        return true;
    }
    
    public StringBuilder updateD(StringBuilder oldD, Map<Integer, Integer> neighborPosition, List<Vertex> dag, Map<Integer,Integer> reverseNeighborPosition){
        int base10 = Integer.parseInt(oldD.toString(), 2);
        base10++;
        String formatter = "%" + oldD.length() + "s";
        StringBuilder newD = new StringBuilder(String.format(formatter, Integer.toBinaryString(base10)).replace(' ', '0'));
        boolean[] fixed = new boolean[newD.length()];
        for(int i = 0; i < newD.length(); i++){
            if(newD.charAt(i) == '1'){
                if(!fixed[i])
                    ancestorDFS(dag, neighborPosition.get(i), newD, fixed, reverseNeighborPosition);
            }
        }
        return newD;
    }
}
