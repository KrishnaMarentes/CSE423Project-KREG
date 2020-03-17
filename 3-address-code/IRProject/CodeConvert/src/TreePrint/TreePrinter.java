/*
Author: Anurag Saini
        anuragsn7@gmail.com

    This class provides a mean to print tree data structure.
    To use this, convert your tree node structure to corresponding
    TextNode data-structure and make sure that text of TextNode
    contains no new line.

*/

package TreePrint;

import Elements.Node;
import java.util.ArrayList;

public class TreePrinter {
    
    public TreePrinter(){
    }
    
    public static TextNode TreeToTextTree(Node n){
        if(n == null) return null;
        
        TextNode m = new TextNode(n.token);
        m.left = TreeToTextTree(n.left);
        m.right = TreeToTextTree(n.right);
        
        if(m.left != null) m.left.parent = m;
        if(m.right != null) m.right.parent = m;
        
        return m;
    }
    
    public static String TreeString(TextNode root){
        ArrayList<String> layers = new ArrayList<>();
        ArrayList<TextNode> bottom = new ArrayList<>();
         
        FillBottom(bottom, root);  DrawEdges(root);
        
        int height = GetHeight(root);
        for(int i = 0; i < height; i++) layers.add("");
        bottom.clear(); FillBottom(bottom, root);
        
        
        int min = layers.get(0).length();
        
        for(int i = 0; i < bottom.size(); i++){
            TextNode n = bottom.get(i);
            String s = layers.get(n.depth);
            
            while(s.length() < n.x) s += " ";
            if(min > s.length()) min = s.length();
            
            if(!n.isEdge) s += "[";
            s += n.text;
            if(!n.isEdge) s += "]";
            
            layers.set(n.depth, s);
        }

        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < layers.size(); i++){
            if(i != 0) sb.append("\n");
            sb.append(layers.get(i).substring(min));
        }
        
        return sb.toString();
    }
    
    private static void DrawEdges(TextNode n){
        if(n == null) return;
        if(isLeaf(n)) return;
        
        if(n.left != null){
            int count = n.x - (n.left.x + n.left.text.length() + 2);
            ArrayList<TextNode> temp = new ArrayList<>();
            
            for(int i = 0; i < count; i++){
                TextNode x = new TextNode("/"); 
                x.isEdge = true;
                x.x = n.x - i - 1;
                x.depth = n.depth + i + 1;
                if(i > 0) temp.get(i-1).left = x;
                temp.add(x);
            }
            
            temp.get(count-1).left = n.left;
            n.left.depth = temp.get(count-1).depth+1;
            n.left = temp.get(0);
            
            DrawEdges(temp.get(count-1).left);
        }
        if(n.right != null){
            int count = n.right.x - (n.x + n.text.length() + 2);
            ArrayList<TextNode> temp = new ArrayList<>();
            
            for(int i = 0; i < count; i++){
                TextNode x = new TextNode("\\"); 
                x.isEdge = true;
                x.x = n.x + n.text.length() + 2 + i;
                x.depth = n.depth + i + 1;
                if(i > 0) temp.get(i-1).right = x;
                temp.add(x);
            }
            
            temp.get(count-1).right = n.right;
            n.right.depth = temp.get(count-1).depth+1;
            n.right = temp.get(0);  
            
            DrawEdges(temp.get(count-1).right);
        }
    }
    
    private static void FillBottom(ArrayList<TextNode> bottom, TextNode n){
        if(n == null) return;
        
        FillBottom(bottom, n.left);
        
        if(!bottom.isEmpty()){            
            int i = bottom.size()-1;
            while(bottom.get(i).isEdge) i--;
            TextNode last = bottom.get(i);
            
            if(!n.isEdge) n.x = last.x + last.text.length() + 3;
        }
        bottom.add(n);
        FillBottom(bottom, n.right);
    }
    
    private static boolean isLeaf(TextNode n){
        return (n.left == null && n.right == null);
    }
    
    private static int GetHeight(TextNode n){
        if(n == null) return 0;
        
        int l = GetHeight(n.left);
        int r = GetHeight(n.right);
        
        return Math.max(l, r) + 1;
    }
}



