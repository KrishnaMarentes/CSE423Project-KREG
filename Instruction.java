public class Instruction {
    public String LHS, OP, RHS_1, RHS_2;
    public boolean isNull;

    public Instruction(String LHS, String OP, String RHS_1, String RHS_2, boolean isNull){
        this.LHS = LHS;
        this.OP = OP;
        this.RHS_1 = RHS_1;
        this.RHS_2 = RHS_2;
        this.isNull = isNull; // Used if asked to create Ins obj for var declaration
    }

    /**
     * "Wrapper" constructor to parse a line of code for you
     * @param line One line of IR code in 3 address format
     * @return New Instruction object
     */
    public static Instruction strToInstruction(String line) {
        String[] splitEq = line.split(" = ");
        if (splitEq.length == 1) { // Variable declaration
            return new Instruction(null, null, null, null, true);
        }
        String lhs = splitEq[0];
        String rhs = splitEq[1];
        String op;
        String rhs1;
        String rhs2;

        /* Use a regex to split on any possible op
         (including spaces left and right of op) */
        String splitrhs[] = rhs.split(" ");
        if (splitrhs.length == 1) {
            op = "";
            rhs1 = splitrhs[0];
            rhs2 = "";
        } else {
            rhs1 = splitrhs[0];
            op = splitrhs[1];
            rhs2 = splitrhs[2];
        }

        /* Check back for dealing with function calls...?*/

        return new Instruction(lhs, op, rhs1, rhs2, false);
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
