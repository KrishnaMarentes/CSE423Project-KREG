/*

Author: Anurag Saini
        anuragsn7@gmail.com

        Performs optimization on 3-address intermediate code

*/

package Optimize;

import Elements.*;
import com.gadberry.utility.expression.Expression;
import com.gadberry.utility.expression.InvalidExpressionException;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class Optimizer {
    
    int count = 1;
    boolean optimized;
    
    public Optimizer(){
    }
    
    // Used to save intermediate results of optimization in files
    private void SaveToFile(String file, String s){
        try{
            FileWriter wr = new FileWriter(new File(file));
            wr.write(s, 0, s.length());
            wr.flush();
            wr.close();
        }
        catch(Exception ex){}
    }
    
    
    // Phases should be self-explanatory
    public boolean Optimize(ArrayList<Instruction> ins){
        //File dir = new File("./Outputs");
        //dir.mkdirs();
        optimized = false;
        
        ConstantFolding(ins);
        AlgebricSimplification(ins);
        CopyPropagation(ins);
        Delete_Identity(ins);
        RemoveDeadCode(ins);
        //SaveToFile("./Outputs/Optimization_"+count+".txt", ToText(ins, 1));
        count++;
        
        return optimized;
    }
    
    // Uses JExel to evaluate simplified primitive expression such as: 3 * 4, sin(2.3) 
    public int eval(String exp){
        Expression expr = new Expression(exp);
        try {
            return expr.evaluate().toInteger();
        } 
        catch (InvalidExpressionException ex) { 
            System.out.println(String.format("Failed to evaluate expression [%s], default set to %d", exp, 0));
            return 0; 
        }
    }
    
    
    // Remove temp-var such as t_3, t_9 which are not used as RHS in any following 3-Address code
    public void RemoveDeadCode(ArrayList<Instruction> ins){
        for(int i = 0; i < ins.size(); i++){
            String l = ins.get(i).LHS;
            if(l.matches("t_[1-9][0-9]*")){
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
    
    public void AlgebricSimplification(ArrayList<Instruction> ins){
        for(int i = 0; i < ins.size(); i++){
            String OP = ins.get(i).OP;
            String r1 = ins.get(i).RHS_1;
            String r2 = ins.get(i).RHS_2;
            
            if((OP.equals("*") && (r1.equals("0") || r2.equals("0"))) || (OP.equals("-") && r1.equals(r2))){
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
    
    // Uses eval to handle predefined functions (floor, ceil, tan etc) and simple expressios (+, -, * etc)
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
    
    
    // If we have identity expression such as t_1 = x
    // then seeks occurences of t_1 in all following codes and replaces with x 
    public void CopyPropagation(ArrayList<Instruction> ins){
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
    
    
    // Simplify identity expression. eg: x = y - 0 => x = y
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
    
    public static String ToText(ArrayList<Instruction> inst, int startIndex){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < inst.size(); i++){
            sb.append(inst.get(i).LHS);
            sb.append(" = ");
            sb.append(inst.get(i).RHS_1);
            sb.append(" ");
            sb.append(inst.get(i).OP);
            sb.append(" ");
            sb.append(inst.get(i).RHS_2);
            if(i != inst.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }
}
