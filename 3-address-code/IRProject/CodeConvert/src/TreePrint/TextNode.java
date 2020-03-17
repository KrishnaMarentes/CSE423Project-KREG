/*

Author: Anurag Saini
        anuragsn7@gmail.com

        See TreeBuilder.java

*/

package TreePrint;

public class TextNode {
    public String text;
    public TextNode parent, left, right;
    public boolean isEdge;
    public int x, depth;
    
    public TextNode(String text){
        this.text = text;
        parent = null; left = null; right = null;
        isEdge = false;
        x = 0; depth = 0;
    }
}
