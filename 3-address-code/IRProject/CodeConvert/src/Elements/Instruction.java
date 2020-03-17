/*

Author: Anurag Saini
        anuragsn7@gmail.com

        This class is defined to contain 3-Address instructions but can be used
        for unary operations too. 

        Function `matches` is faulty in its logic. For operators such as 
        + and *, order of operands does not matter. For operators such as
        - and /, order matters. I have intentionally left is as is and you can
        provide logic to see if order of operator matters

*/


package Elements;

import java.util.Objects;

public class Instruction {
    
    public String LHS, OP, RHS_1, RHS_2;
    public boolean isTemp, isDummy;
    
    public Instruction(String LHS, String OP, String RHS_1, String RHS_2, boolean isTemp){
        this.LHS = LHS;
        this.OP = OP;
        this.RHS_1 = RHS_1;
        this.RHS_2 = RHS_2;
        
        this.isTemp = isTemp;
        isDummy = false;
    }
    
    @Override
    public String toString(){
        return String.format("%s = %s %s %s", LHS, RHS_1, OP, RHS_2);
    }
    
    @Override
    public int hashCode(){
        return String.format("%s %s %s %s", LHS, RHS_1, OP, RHS_2).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instruction other = (Instruction) obj;
        if (!LHS.equals(other.LHS)) {
            return false;
        }
        if (!OP.equals(other.OP)) {
            return false;
        }
        if (!RHS_1.equals(other.RHS_1)) {
            return false;
        }
        if (!RHS_2.equals(other.RHS_2)) {
            return false;
        }
        return true;
    }
    
    public boolean Matches(Instruction i){
        if(!this.OP.equals(i.OP)) return false;
        
        if(this.RHS_1.equals(i.RHS_1) && this.RHS_2.equals(i.RHS_2)) 
            return true;
        if(this.RHS_2.equals(i.RHS_1) && this.RHS_1.equals(i.RHS_2)) 
            return true;
        
        return false;
    }
}
