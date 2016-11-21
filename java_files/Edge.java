
public class Edge{
    private int vertex1;
    private int vertex2;
    
    
    public Edge(int vertex1_, int vertex2_){
        vertex1 = vertex1_;
        vertex2 = vertex2_;
    }
    
    public int getVertex1(){
        return vertex1;
    }
    
    public int getVertex2(){
        return vertex2;
    }
    
    @Override
    public String toString(){
        return vertex1 + " " + vertex2;
    }
}