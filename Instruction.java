public class Instruction {
    final static String EOL = System.lineSeparator();
    public String LHS, OP, RHS_1, RHS_2;
    public boolean isNull; // True if this instruction does not qualify for optimization

    public Instruction(String LHS, String OP, String RHS_1, String RHS_2, boolean isNull){
        this.LHS = LHS;
        this.OP = OP;
        this.RHS_1 = RHS_1;
        this.RHS_2 = RHS_2;
        this.isNull = isNull; // Used if asked to create Ins obj for var decl, empty line, etc
    }

    /**
     * "Wrapper" constructor to parse a line of code for you
     * @param line One line of IR code in 3 address format
     * @return New Instruction object
     */
    public static Instruction strToInstruction(String line) {
        String cond;
        if (line.contains("if")) {
            return Conditional.ifToInstruction(line);
        }

        String[] splitEq = line.split(" = ");
        if (splitEq.length == 1) { // Variable declaration, empty line, or curly bracket
            // Save the line in LHS so we can still print it later
            return new Instruction(line, null, "", null, true);
        }

        String lhs = splitEq[0];
        String rhs = splitEq[1];
        String op;
        String rhs1;
        String rhs2;

        /* hacky way to identify function calls
         * Just mark them "null" (i.e. don't optimize)
         * Must identify here because splitrhs below splits on spaces, which
         * doesn't work if multiple parameters in function call*/
        if (line.contains("(")) {
            return new Instruction(lhs, "", rhs.replace(";", ""), "", true);
        }

        /* Use a regex to split on any possible op
         (including spaces left and right of op) */
        String[] splitrhs = rhs.split(" ");
        if (splitrhs.length == 1) {
            op = "";
            rhs1 = splitrhs[0];
            rhs2 = "";
            rhs1 = rhs1.replace(";", "");
        } else {
            rhs1 = splitrhs[0];
            op = splitrhs[1];
            rhs2 = splitrhs[2];
            rhs2 = rhs2.replace(";", "");
        }

        return new Instruction(lhs, op, rhs1, rhs2, false);
    }

    @Override
    public String toString(){
        if (this.isNull) {
            // Functions are null but do have an RHS to print
            if (this.RHS_1.isEmpty())
                return this.LHS + EOL;
        }
        if (OP.isEmpty()) {
            return String.format("%s = %s;%s", LHS, RHS_1, EOL);
        }
        return String.format("%s = %s %s %s;%s", LHS, RHS_1, OP, RHS_2, EOL);
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
