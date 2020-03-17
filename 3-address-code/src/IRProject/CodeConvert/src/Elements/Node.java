/*

Author: Anurag Saini
        anuragsn7@gmail.com

        This is used in building expression tree from given expression.
        Building tree helps handle nested expressions and precedence order
        of operators.

*/

package Elements;

public class Node {
    
    public String token;
    public Node left, right;
    public boolean isDummy, isUnit;
    
    public Node(String token){
        this.token = token;
        left = null; right = null;
        isDummy = false; isUnit = false;
    }
}
