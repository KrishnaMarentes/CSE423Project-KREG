public class Instruction {
    public String LHS, OP, RHS_1, RHS_2;
    public boolean isTemp;

    public Instruction(String LHS, String OP, String RHS_1, String RHS_2, boolean isTemp){
        this.LHS = LHS;
        this.OP = OP;
        this.RHS_1 = RHS_1;
        this.RHS_2 = RHS_2;
        this.isTemp = isTemp;
    }

    /**
     * Constructor to parse a line of code for you
     * @param line One line of IR code in 3 address format
     * TODO Finish this
     */
    public Instruction(String line) {
        String[] splitEq = line.split(" = ");
        String lhs = splitEq[0];
        String rhs = splitEq[1];

        /* Use a regex that actually works to split on any possible op
         (including spaces left and right of op) */
        //rhs = rhs.split(" [+-*%^&\\\] ");

        /* If the split was successful, assign appropriate op, rhs1, and rh2
         Otherwise it might be a function call..? Have to check what
         the basic block code does for those ( e.g. i = foo(); ) */

        // I was gonna do constructor chaining here but java doesn't like it..
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
