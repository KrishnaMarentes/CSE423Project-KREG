import java.util.ArrayList;
import java.util.regex.Pattern;

public class Optimizer {

    int count = 1;
    boolean optimized;

    /* Custom class for a Block - basically Pair but for only primitive int
     * Each Block holds the start and end indices of the starting and ending
     * lines for the basic block in the IR. (Inclusive)
     * e.g. If basic blocks exist from lines 10 to 16 in the IR, it would be
     * represented as a Block with Block.start = 10 and Block.end = 16
     */
    static class Block {
        int start;
        int end;
        Block(final int s, final int e) {
            this.start = s;
            this.end = e;
        }
        public String toString() {
            return "<" + start + "," + end + ">";
        }
    }

    public static String optimizeIR(String ir) {
        ArrayList<Optimizations.Block> basicblocks = new ArrayList<Optimizations.Block>();
        basicblocks = findBasicBlocks(ir);

        /* For each basic block, perform optimizations here */

        return basicblocks.toString(); // print this in SCC to check if it's right
    }

    /* Return a list of all the basic blocks in the IR
     * **basic blocks have exactly one entry pt and one exit pt**
     * @param ir The IR in one continuous string, with lines
     * separated by EOL
     * @return ArrayList of basic blocks
     */
    static ArrayList<Optimizations.Block> findBasicBlocks(String ir) {
        final String EOL = System.lineSeparator();
        String[] lines = ir.split(EOL);

        /* to be used as regexes */
        //final Pattern entry = Pattern.compile(".*function.*|.*KREG\\d*:.*");
        //final Pattern exit = Pattern.compile(".*goto.*|.*return.*");
        final String entry = ".*function.*|<KREG.\\d*>:.*";
        final String exit = ".*goto.*|.*return.*";

        ArrayList<Optimizations.Block> blocks = new ArrayList<Optimizations.Block>();

        int start = 0;
        int end = 1;
        String line;

        /* Looking at the IR line by line, mark the first found entry point
         * with "start." From there, look for either an entry or exit point,
         * and mark it with "end" */
        while (start < (lines.length-1)) {
            line = lines[start];
            if (Pattern.matches(entry, line)) {
                start++; // Don't include function or label lines in basic block
                end = start;
                while (end < (lines.length-1)) {
                    if (Pattern.matches(entry, lines[end])||Pattern.matches(exit, lines[end])) {
                        /* Special case: "If" with no "else" is a fall through, and thus an entry point */
                        // I don't think our IR currently has this case;  whiles use fall through but there's
                        // immediately a label after loops. But good to cover our bases if we add more features -becca
                        if (Pattern.matches("if ", lines[end]) && !Pattern.matches(" else ", lines[end])) {
                            /* Record the last block, mark the fall through start, and go directly to end search */
                            blocks.add(new Optimizations.Block(start, end-1));
                            start = end+1;
                            end = end+2;
                            continue;
                        }
                        break;
                    }
                    end++;
                }
                /* Case: adjacent entry/exit points. ex. "goto <KREG.5> /n <KREG.6>" */
                if (end-1 < start) {
                    start = end+1;
                    continue;
                }
                blocks.add(new Optimizations.Block(start, end-1)); // Don't include exit lines in block
                start = end+1; // Start new search after last exit point
                continue;
            }
            start++;
        }
        return cleanBlocks(blocks, ir);
    }

    /* Remove leading, trailing, and standalone "empty" lines (e.g. "","{", etc) */
    static ArrayList<Optimizations.Block> cleanBlocks(ArrayList<Optimizations.Block> blocks, String ir) {
        final String EOL = System.lineSeparator();
        String[] lines = ir.split(EOL);
        /* For each block
         *  if a one-line block, check if it's empty and if so, remove it
         *  if a multi-line block
         *   check if first and/or last line is empty, if so remove */
        int i = 0;
        Optimizations.Block block;
        String line;
        for (i = 0; i < blocks.size()-1; i++) {
            block = blocks.get(i);
            line = lines[block.start];
            if (line.equals("") || line.equals("{") || line.equals("}")) {
                // Single line block, often need to be removed
                if (block.start == block.end) {
                    blocks.remove(i);
                } else  { // Multi-line block, might need to trim start and/or end
                    block.start++;
                    if (line.equals(block.end)) {
                        block.end--;
                    }
                }
            }
        }
        return blocks;
    }

    public Optimizer(){
    }
    // This function needs debugging - could probably use Expression class instead but don't want to mess with other code
    public static ArrayList<Instruction> Build(ArrayList<String> lines){
        ArrayList<Instruction> code = new ArrayList<>();
        System.out.println("Inside of Build Function...");
        String[] s = null;

        for(String exp : lines){
            ArrayList<Instruction> i = new ArrayList<>();
            s = exp.split(".$"); // regex that removes the last character - in this case, the ";"

            for (int w = 0; w < s.length; w++){
                System.out.println(s[w]);
            }

            //i.set(0,).LHS = s[0].trim();

            //code.addAll(i);

        }

        return code;
    }

    public boolean Optimize(ArrayList<Instruction> ins){
        optimized = false;

        ConstantFolding(ins);
        AlgebraicSimplification(ins);
        ConstantPropagation(ins);
        Delete_Identity(ins);
        RemoveDeadCode(ins);

        count++;

        return optimized;
    }

    /* expecting expressions to already be in three-address-code format */
    public int eval(String exp){
        int result = 0;

        String[] tokens = exp.split(" ");

        switch (tokens[1].charAt(0)) {
            case '+':
                result = Integer.parseInt(tokens[0])
                        + Integer.parseInt(tokens[2]);
                break;
            case '-':
                result = Integer.parseInt(tokens[0])
                        - Integer.parseInt(tokens[2]);
                break;
            case '*':
                result = Integer.parseInt(tokens[0])
                        * Integer.parseInt(tokens[2]);
                break;
            case '/':
                if (Integer.parseInt(tokens[2]) == 0) {
                    System.out.println("Error: Cannot divide by 0.");
                    System.exit(1);
                } else {
                    result = Integer.parseInt(tokens[0])
                            / Integer.parseInt(tokens[2]);
                }
                break;
        }

    return result;

    }

    public void RemoveDeadCode(ArrayList<Instruction> ins){
        for(int i = 0; i < ins.size(); i++){
            String l = ins.get(i).LHS;
            //<KREG.\d*>:.* example of other regex option
            if(l.matches("KREG.[1-9][0-9]*")){
                boolean found = false;

                for(int j = i + 1; j < ins.size() && !found; j++)
                    if(ins.get(j).RHS_1.equals(l) || ins.get(j).RHS_2.equals(l)) found = true;
                if(!found){
                    ins.remove(ins.get(i));
                    optimized = true;
                }
            }
        }
    }

    public void AlgebraicSimplification(ArrayList<Instruction> ins){

        for (int i = 0; i < ins.size(); i++){
            String OP = ins.get(i).OP;
            String r1 = ins.get(i).RHS_1;
            String r2 = ins.get(i).RHS_2;

            if ((OP.equals("*") && (r1.equals("0") || r2.equals("0"))) || (OP.equals("-") && r1.equals(r2))){
                ins.get(i).RHS_1 = "0";
                ins.get(i).OP = "";
                ins.get(i).RHS_2 = "";
                optimized = true;
            }
            if(OP.equals("/") && r1.equals(r2)){
                ins.get(i).RHS_1 = "1";
                ins.get(i).OP = "";
                ins.get(i).RHS_2 = "";
                optimized = true;
            }
        }
    }

    public void ConstantFolding(ArrayList<Instruction> ins){

        for(int i = 0; i < ins.size(); i++){
            try{
                int r1 = eval(ins.get(i).RHS_1);
                int r2 = eval(ins.get(i).RHS_2);
                ins.get(i).RHS_1 = "" + eval(r1 + ins.get(i).OP + r2);
                ins.get(i).OP = "";
                ins.get(i).RHS_2 = "";
                optimized = true;
            }
            catch(Exception ex){}
        }
    }

    public void ConstantPropagation(ArrayList<Instruction> ins){

        for(int i = 0; i < ins.size(); i++){
            String LHS = ins.get(i).LHS;
            String r1 = ins.get(i).RHS_1;

            if(ins.get(i).OP.length() == 0){
                int lastIndex;
                boolean found = false;

                for(lastIndex = i + 1; lastIndex < ins.size() && !found; lastIndex++){
                    if(ins.get(lastIndex).LHS.equals(LHS)) found = true;
                }
                if(!found) lastIndex = ins.size();

                for(int j = i + 1; j < lastIndex; j++){
                    if(ins.get(j).RHS_1.equals(LHS)){ ins.get(j).RHS_1 = r1; optimized = true; }
                    if(ins.get(j).RHS_2.equals(LHS)){ ins.get(j).RHS_2 = r1; optimized = true; }
                }
            }
        }
    }

    public void Delete_Identity(ArrayList<Instruction> ins){
        for(int i = 0; i < ins.size(); i++){
            String OP = ins.get(i).OP;
            String r1 = ins.get(i).RHS_1;
            String r2 = ins.get(i).RHS_2;

            if(OP.equals("+") || OP.equals("-")){
                if(r1.equals("0")){
                    ins.get(i).RHS_1 = r2;
                    ins.get(i).OP = "";
                    ins.get(i).RHS_2 = "";
                    optimized = true;
                }
                else if(r2.equals("0")){
                    ins.get(i).OP = "";
                    ins.get(i).RHS_2 = "";
                    optimized = true;
                }
            }
            else if(OP.equals("*") || OP.equals("/")){
                if(r1.equals("1")){
                    ins.get(i).RHS_1 = r2;
                    ins.get(i).OP = "";
                    ins.get(i).RHS_2 = "";
                    optimized = true;
                }
                else if(r2.equals("1")){
                    ins.get(i).OP = "";
                    ins.get(i).RHS_2 = "";
                    optimized = true;
                }
            }
        }

        for(int i = 0; i < ins.size(); i++){
            if(ins.get(i).OP.length() == 0){
                if(ins.get(i).LHS.equals(ins.get(i).RHS_1)){
                    ins.remove(ins.get(i));
                    optimized = true;
                }
            }
        }
    }

    public static String ToText(ArrayList<Instruction> ins, int startIndex){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < ins.size(); i++){
            sb.append(ins.get(i).LHS);
            sb.append(" = ");
            sb.append(ins.get(i).RHS_1);
            sb.append(" ");
            sb.append(ins.get(i).OP);
            sb.append(" ");
            sb.append(ins.get(i).RHS_2);
            if(i != ins.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }
}
