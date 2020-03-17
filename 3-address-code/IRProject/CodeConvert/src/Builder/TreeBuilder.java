/*

Author: Anurag Saini
        anuragsn7@gmail.com

        This class reads list of tokens and gives expression tree which is 
        very easy to convert to 3-Address code.

        It is assumed that the expression is correct. Some examples:

        If root = 3 and op = * then
            *
           /
       root



        If root precedence < current token precedence
            root
                \
                 current token
       


        If root precedence > current token precedence 
            current token
           /
       root
        
        Note that for nested expressions, an expression inside brackets is
        treated as one unit for all operators outside that bracket. ie it is
        like one single var/num and root node operator cannot claim precedence
        over its local root (last applied operator within those brackets)
        
        This means that some previous examples states above may not work in
        presense of brackets. ie root = * and current = + 

                *       // p * (4 + 3)
                 \
                  +     // (last applied operator within a bracket)
                 / \
                4   3

        Finally some functions are also allowed such as sin, cos, floor, ceil
        etc. For full list see JExel help page. These are treated as numbers
        and are left for optimizer phase to handle. eg
            
                +
               / \
      ceil(4.3)   tan(3.3)

*/

package Builder;

import Elements.Operator;
import Elements.*;
import TreePrint.*;
import TreePrint.TreePrinter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreeBuilder {

    public ArrayList<Operator> oprs = null;
    private enum Type { OP, OPEN, CLOSE, ID, NUM };
    
    public TreeBuilder(){
        DefaultOperators();
    }
    
    private ArrayList<String> GetTokens(String s){
        StringBuilder sb = new StringBuilder();
        sb.append(Pattern.quote(oprs.get(0).symbol));
        
        for(int i = 1; i < oprs.size(); i++){
            sb.append("|");
            sb.append(Pattern.quote(oprs.get(i).symbol));
        }
        sb.append("|[a-zA-Z_]+[a-zA-Z0-9_]*\\([^()]*\\)|[0-9]+|[a-zA-Z_][a-zA-Z0-9_]*|\\Q(\\E|\\Q)\\E");
        
        ArrayList<String> tokens = new ArrayList<>();
        Pattern p = Pattern.compile(sb.toString());
        Matcher m = p.matcher(s);
        
        while(m.find()) tokens.add(m.group());
        return tokens;        
    }
    
    public Node GenerateTree(String input){
        ArrayList<String> in = GetTokens(input);
        return GenerateTree(in);
    }
    
    private Node GenerateTree(ArrayList<String> in){
        FormatInput(in);
        if(in == null || in.isEmpty()) return null;
        
        Node root = new Node("");
        
        for(int i = 0; i < in.size(); i++){
            String tk = in.get(i);
            Type ty = GetType(tk);
            
            if(ty == Type.OP){
                Operator curr = GetOperator(tk);
                Operator prev = GetOperator(root.token);
                Node n = new Node(tk);
                
                if(prev == null || root.isUnit || 
                        curr.precedence < prev.precedence || (curr.precedence == prev.precedence && curr.operands != 1)){
                    if(curr.operands == 2){
                        n.left = root; n.right = new Node("");
                    }
                    else if(curr.operands == 1){
                        if(curr.isLeft){
                            n.right = root; n.left = new Node(""); n.left.isDummy = true;
                        }
                        else{ 
                            n.left = root; n.right = new Node(""); n.right.isDummy = true;
                        }
                    }
                    root = n;
                }
                else if(curr.precedence > prev.precedence || (curr.precedence == prev.precedence && curr.operands == 1)){
                    Node t = FindOperatorLessThan(root, curr.precedence);                    
                    if(curr.operands == 2){                    
                        n.left = t.right; t.right = n;
                        n.right = new Node("");
                    }
                    else if(curr.operands == 1){
                        if(curr.isLeft){
                            n.right = t.right; t.right = n;
                            n.left = new Node(""); n.left.isDummy = true;
                        }
                        else{
                            n.left = t.right; t.right = n;
                            n.right = new Node(""); n.right.isDummy = true;
                        }
                    }
                }
            }
            else if(ty == Type.OPEN){
                int count = 1, j;
                for(j = i+1; j < in.size() && count != 0; j++){
                    if(in.get(j).equals("(")) count++;
                    else if(in.get(j).equals(")")) count--;
                }
                
                ArrayList<String> subIn = new ArrayList<>();
                for(int k = i; k < j; k++) subIn.add(in.get(k));
                
                Node p = FindOperandPosition(root);
                Node t = GenerateTree(subIn);
                

                    p.isUnit = true;
                    p.token = t.token; p.isDummy = false;
                    p.left = t.left; p.right = t.right;
                i = j-1;
            }
            else if(ty == Type.NUM || ty == Type.ID){
                Node n = FindOperandPosition(root);
                n.token = tk; n.isDummy = false;
            }
        }
        return root;
    }
    
    private Node FindOperatorLessThan(Node n, int Prec){        
        if(n == null) return null;
        if(n.right.token.length() == 0 && !n.right.isDummy) return n;
        if(GetOperator(n.right.token) == null) return n;
        if(n.right.isUnit) return n;
        
        int p1 = GetOperator(n.token).precedence;
        int p2 = GetOperator(n.right.token).precedence;
        
        if(p1 < Prec && p2 >= Prec) return n;
        
        return FindOperatorLessThan(n.right, Prec);
    }
    
    private Node FindOperandPosition(Node n){
        if(n == null) return null;
        if(n.token.length() == 0 && !n.isDummy) return n;
        //if(n.token.length() == 0 && !n.isDummy) return n;
        
        if(n.left != null){
            Node t = FindOperandPosition(n.left);
            if(t != null && !t.isDummy) return t;
        }
        
        return FindOperandPosition(n.right);
    }
    
    private Operator GetOperator(String op){
        for(int i = 0; i < oprs.size(); i++)
            if(oprs.get(i).symbol.equals(op))
                return oprs.get(i);
        return null;
    }
    
    private void FormatInput(ArrayList<String> in){
        while(EnclosedInBrackets(in)){
            in.remove(0); in.remove(in.size()-1);
        }
    }
    
    private boolean EnclosedInBrackets(ArrayList<String> in){
        if(in == null || !in.get(0).equals("(")) return false;
        
        int count = 1, i;
        for(i = 1; i < in.size() && count != 0; i++){
            if(in.get(i).equals("(")) count++;
            else if(in.get(i).equals(")")) count--;
        }   
        i--;
        
        return (i == in.size()-1);
    }
    
    private Type GetType(String token){
        for (Operator opr : oprs) {
            if (opr.symbol.equals(token)) { 
                return Type.OP;
            }
        }
        
        if(token.matches("[0-9]+")) return Type.NUM;
        if(token.matches("[a-zA-Z_]+[a-zA-Z0-9_]*\\([^()]*\\)")) return Type.NUM;
        if(token.equals("(")) return Type.OPEN;
        if(token.equals(")")) return Type.CLOSE;
        
        return Type.ID;
    }
    
    private void DefaultOperators(){
        if(oprs != null) return;
        
        oprs = new ArrayList<>();
        
        oprs.add(new Operator("+", "a+b", 2, 2, true));
        oprs.add(new Operator("-", "a-b", 2, 2, true));
        oprs.add(new Operator("*", "a*b", 3, 2, true));
        oprs.add(new Operator("/", "a/b", 3, 2, true));
    }
    
    public static boolean isLeaf(Node n){
        return (n.left == null && n.right == null);
    }
    
    public static void Print(Node n){
        TextNode tn = TreePrinter.TreeToTextTree(n);
        System.out.println(TreePrinter.TreeString(tn));
    }
    
    public void Traverse(Node n){
        if(n == null) return;
        
        if(!n.isDummy && n.left != null && n.right != null)
            System.out.print(n.token);
        else return;
        
        if(n.left != null && !n.left.isDummy){ System.out.print(" Left: " +n.left.token); }
        if(n.right != null && !n.right.isDummy){ System.out.print(" Right: " +n.right.token); }
        System.out.println("");
        
        if(n.left != null) Traverse(n.left);
        if(n.right != null) Traverse(n.right);
    }
}



