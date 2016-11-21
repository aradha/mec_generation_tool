
public class OptionsParser {
    private String[] args;
    private boolean printSkeletonData; //Print Skeleton data along with skeleton distribution
    private boolean printMECDistribution; //Print distribution map
    private boolean printMECCheck; //Check whether two skeletons output same distribution
    private boolean errorFlag;
    public OptionsParser(String[] _args){
        args = _args;
    }

    public void parse(){
        if(args.length > 4){
            this.errorFlag = true;
        } else{
            int hasValidFlag = 1;

            for(String s: args){
                if(s.equals("-s")){
                    hasValidFlag++;
                    printSkeletonData = true;
                }
                if(s.equals("-d")){
                    hasValidFlag++;
                    printMECDistribution = true;
                }
                if(s.equals("-c")){
                    hasValidFlag++;
                    printMECCheck = true;
                }
            }
            if(hasValidFlag != args.length || hasValidFlag == 0){
                this.errorFlag = true;
            }            
        }
    }

    public boolean getPrintSkeletonData() {
        return printSkeletonData;
    }

    public boolean getPrintMECDistribution() {
        return printMECDistribution;
    }

    public boolean getPrintMECCheck() {
        return printMECCheck;
    }

    public boolean getErrorFlag() {
        return errorFlag;
    }

}
