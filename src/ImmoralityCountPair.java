public class ImmoralityCountPair implements Comparable<ImmoralityCountPair>{
    private int numImmoralities = 0;
    private int count = 0;
    
    public ImmoralityCountPair(int numImmoralities_, int count_){
        count = count_;
        numImmoralities = numImmoralities_;
    }
    
    public void incrementCount(){
        count += 1;
    }

    public int getCount(){
        return count;
    }
    
    public int getNumberImmoralities(){
        return numImmoralities;
    }
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof ImmoralityCountPair)){
            return false;
        } else{
            ImmoralityCountPair p = (ImmoralityCountPair) o;
            return (this.count == p.count) && (this.numImmoralities == p.numImmoralities);
        }
    }
    
    @Override
    public int hashCode() {
        Integer c = count;
        Integer n = numImmoralities;
        return n.hashCode() + c.hashCode();
    }
    
    @Override
    public int compareTo(ImmoralityCountPair p) {
        if(this.numImmoralities == p.numImmoralities){
            if(this.count == p.count){
                return 0;
            }
            else if (this.count < p.count){
                return -1;
            } else{
                return 1;
            }
        }
        else if(this.numImmoralities < p.numImmoralities){
            return -1;
        } else{
            return 1;
        }
    }
}
