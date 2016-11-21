import java.util.ArrayList;
import java.util.List;

public class Vertex{
    private int label;
    List<Vertex> neighbors = new ArrayList<Vertex>();
    
    public Vertex(int label_){
        this.label = label_;
    }
    
    public int getLabel() {
        return label;
    }
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Vertex)){
            return false;
        } else{
            Vertex v = (Vertex) o;
            return this.label == v.label;
        }
    }
    
    @Override
    public int hashCode() {
        Integer l = label;
        return l.hashCode();
    }
    
    
}
