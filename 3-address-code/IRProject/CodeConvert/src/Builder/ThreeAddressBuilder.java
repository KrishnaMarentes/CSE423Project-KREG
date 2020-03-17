/*

Author: Anurag Saini
        anuragsn7@gmail.com


        Each line is assumed to be one expression of form. Like:
        exp:    LHS = exp + (exp / exp - (exp)) ...

        each of these expression is converted to its corresponding
        3-Address form. These forms are finally merged to give whole 
        source file as 3-Address form.

        Problem arises because each expression is parsed separately which 
        means temparory variables of same name may appear in 3-Address form
        of multiple expression. Hence we rename temp-var names before merging

        See the following case:
         1. x = (p + q) - 1
              => 3-Address form as
                    t_1 = p + q
                    x   = t_1 - 1

         2. y = z - (p + q)
              => 3-Address form as
                    t_1 = p + q
                    y   = z - t_1
                    
         these two will be merged to give following
            t_1 = p + q
            x   = t_1 - 1
            t_2 = p + q
            y   = z 0 t_2

        Note that we could use t_1 itself instead of defining t_2 but it may 
        happen that another statement is between above 2 expression as:
            x = (p + q) - 1
            p = 4 + 3
            y = z - (p + q)
        As it is clear, value of one of operands may change between two 
        expression. Keep on using t_1 in last statement will give wrong result.
        
        Thus redundancy is added to avoid complex statement inspection. This
        redundancy is removed by Optimizer.

        `normalize` is basically a hack. for identity statements: x = 9
        Instruction construction fails, to fix we replace it with: x = 9 + 0
*/

package Builder;

import Elements.*;
import java.util.ArrayList;

public class ThreeAddressBuilder {
    
    
    public static ArrayList<Instruction> Build(ArrayList<String> lines){
        ArrayList<Instruction> code = new ArrayList<>();
        
        int start = 1;
        TreeBuilder tb = new TreeBuilder();
        
        for(String exp : lines){
            ArrayList<Instruction> i = new ArrayList<>();
            String[] s = exp.split("=");
            
            Node n = tb.GenerateTree(s[1].trim());
            n = normalize(n);
            
            ExprBuilder.Parse(i, n, start);
            
            i.get(i.size()-1).LHS = s[0].trim();
            i.get(i.size()-1).isTemp = false;
            
            code.addAll(i);
            start = LastNumbered(code) + 1;
        }
        
        return code;
    }
    
    public static Node normalize(Node n){
        if(n.left == null && n.right == null){
            Node t = new Node("+");
            t.left = n;
            t.right = new Node("0");
            return t;
        }
        return n;
    }
    
    public static int LastNumbered(ArrayList<Instruction> ins){
        for(int i = ins.size()-1; i >= 0; i--){
            if(!ins.get(i).LHS.startsWith("t_")) continue;
            return Integer.parseInt(ins.get(i).LHS.substring(2));
        }
        return 0;
    }
}
