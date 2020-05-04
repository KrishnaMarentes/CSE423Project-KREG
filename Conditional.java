/* Special case for lines that are if statements
 * The conditional has opportunity to be optimized but the
 * structure is fundamentally different from a normal instruction */
public class Conditional extends Instruction {
    String ifPrefix = "if ";
    String then; // the text after the if conditional. e.g. goto <L1>
    public Conditional(String OP, String RHS_1, String RHS_2, String then) {
        super("", OP, RHS_1, RHS_2, false);
        this.then = then;
    }

    public static Conditional ifToInstruction(String line) {
        String op;
        String rhs1;
        String rhs2;
        String then;
        int thenStart = line.indexOf("goto");
        // "if " is in indices 0-2; conditional starts at 3
        String cond = line.substring(3, thenStart);
        String[] parts = cond.split(" < | > | <= | >= | == ");
        rhs1 = parts[0];
        rhs2 = parts[1];
        if (cond.matches(".*<=.*"))
            op = "<=";
        else if (cond.matches(".*>=.*"))
            op = ">=";
        else if (cond.matches(".*==.*"))
            op = "==";
        else if (cond.matches(".*>.*"))
            op = ">";
        else if (cond.matches(".*<.*"))
            op = "<";
        else {
            System.out.println("invalid conditional in if statement: " + line);
            return null;
        }
        return new Conditional(op, rhs1, rhs2, line.substring(thenStart));
    }

    @Override
    public String toString() {
        return String.format("%s%s %s %s%s%s", ifPrefix, RHS_1, OP, RHS_2, then, EOL);
    }
}
