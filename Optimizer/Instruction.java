package Optimizer;

public class Instruction {
    public String LHS, OP, RHS_1, RHS_2;
    public boolean isTemp;

    /* could probably use Expression class instead but don't want to mess with other code */
    public Instruction(String LHS, String OP, String RHS_1, String RHS_2, boolean isTemp){
        this.LHS = LHS;
        this.OP = OP;
        this.RHS_1 = RHS_1;
        this.RHS_2 = RHS_2;
        this.isTemp = isTemp;
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
}
