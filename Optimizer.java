import java.util.ArrayList;
import java.util.regex.Pattern;

public class Optimizer {

    int count = 1;
    boolean optimized;
    final static String EOL = System.lineSeparator();
    final static int MAX_ROUNDS = 25;

    public Optimizer(){
    }

    /**
     * Main method to call from SCC to optimize a plain string of IR
     * @param ir Long-ass string, returned from generateIR or from reading in IR
     * @return Also a long-ass string, but now the IR is optimized.
     */
    public String optimizeIR(String ir) {
        ArrayList<Block> blockIndices = Block.findBasicBlocks(ir);

        String[] lines = ir.split(EOL);
        // Each element of codeBlocks is one basic block in String format
        ArrayList<String> codeBlocks = Block.extractBasicBlocks(blockIndices, lines);

        int i;
        int k;
        String block;
        StringBuilder sb = new StringBuilder();
        /* For each basic block, perform optimizations here */
        for (i = 0; i < codeBlocks.size(); i++) {
            block = codeBlocks.get(i);
            ArrayList<Instruction> blockIns = build(block);
            optimized = true;
            k = 0;
            System.out.println("Code before optimization:\n" + ToText(blockIns));
            while(optimized && k < MAX_ROUNDS){
                optimized = false;
                optimized = optimize(blockIns);

                k++;
                System.out.println("\nCode after " + k + " optimization round:");
                System.out.println(ToText(blockIns));
            }
            sb.append(insBlkToString(blockIns));
        }




        // This is a dummy debug return. Should actually return IR code
        //return blockIndices.toString(); // print this in SCC to check if it's right

        return sb.toString();
    }

    public String insBlkToString(ArrayList<Instruction> block) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < block.size(); i++) {
            sb.append(block.get(i).toString() + EOL);
        }
        return sb.toString();
    }


    /**
     * Takes ONE basic block at a time
     * Converts ArrayList of lines of IR code to list of Instructions
     * @param lines One basic block of IR code as a String list of lines
     * @return The same basic block, but each line stored as an Instruction object
     */
    public static ArrayList<Instruction> build(String lines){
        ArrayList<Instruction> code = new ArrayList<>();
        System.out.println("Inside of Build Function...");
        String[] s = null;

        /* split */
        s = lines.split(";"); // Semicolons the only line separator

        /* for each line of code in the block */
        Instruction i;
        for (int w = 0; w < s.length; w++){
            i = Instruction.strToInstruction(s[w]);
            // Code blocks might have var decls, i.isNull will be true; ignore them
            if (!i.isNull) {
                code.add(i);
            }
        }

        return code;
    }

    public boolean optimize(ArrayList<Instruction> ins){
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

    public static String ToText(ArrayList<Instruction> ins){
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
