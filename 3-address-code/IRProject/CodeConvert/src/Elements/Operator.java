/*

Author: Anurag Saini
        anuragsn7@gmail.com
        
        `semantics` is not implemented and is of no use.

        If you wish, see Optimize.Optimizer class and modify eval function.
        This function can be extended to handle custom functions based on 
        semantics given here. 

        semantics is given as a string which defines operator in terms of 
        primitive operators. `a` = RHS_1 and `b` = RHS_2

        eg we define power to 2 as operator #, then
        
        symbol    : #
        semantics : a*a
        precedence: 4 (must be higher than * and /)
        operands  : 1
        isLeft    : false (is right associative)
*/

package Elements;

public class Operator {
    
    public String symbol, semantics;
    public int precedence, operands;
    public boolean isLeft;
    
    public Operator(String Symb, String Semantics, int Prec, int ops, boolean IsLeft){
        symbol = Symb;
        semantics = Semantics;
        
        if(ops >= 1) operands = ops;
        else operands = 2;
        
        if(Prec > 0) precedence = Prec;
        else precedence = 0;
        
        isLeft = IsLeft;
    }
}
