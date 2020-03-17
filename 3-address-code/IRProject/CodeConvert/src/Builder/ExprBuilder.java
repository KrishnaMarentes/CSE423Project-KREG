/*

Author: Anurag Saini
        anuragsn7@gmail.com

        Traverses expression tree and build 3-Address code. Works in top-down
        order.
*/


package Builder;


import Elements.*;
import java.util.ArrayList;


public class ExprBuilder {
    
    public static ArrayList<Instruction> Parse(Node n, int start){
        ArrayList<Instruction> ins = new ArrayList<>();
        Parse(ins, n, start);
        return ins;
    }
    
    public static String Parse(ArrayList<Instruction> ins, Node n, int start){
        if(n == null) return "";
        if(TreeBuilder.isLeaf(n)) return n.token;
        
        Instruction i = new Instruction("",  n.token, Parse(ins, n.left, start), Parse(ins, n.right, start), true);
        
        int index = -1;
        for(int j = ins.size()-1; j >= 0 && index == -1; j--){
            if(!ins.get(j).OP.equals(n.token)) continue;
            if(ins.get(j).RHS_1.equals(i.RHS_1) && ins.get(j).RHS_2.equals(i.RHS_2)) index = j;
            else if(ins.get(j).RHS_1.equals(i.RHS_2) && ins.get(j).RHS_2.equals(i.RHS_1)) index = j;
        }
        
        if(index < 0){
            ins.add(i);
            index = ins.size()-1;
        }
        
        ins.get(index).LHS = "t_"+(index+start);
        ins.get(index).isTemp = true;
        
        return ins.get(index).LHS;
    }
    
    public static void Print(ArrayList<Instruction> ins){
        for(Instruction i : ins){
            System.out.println(i.LHS+" = "+i.RHS_1+" "+i.OP+" "+i.RHS_2);
        }
    }
}
