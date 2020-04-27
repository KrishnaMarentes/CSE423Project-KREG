package Optimizer;

import java.util.ArrayList;

public class Optimizer {

    int count = 1;
    boolean optimized;

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
