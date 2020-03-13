import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;

public class TreeUtils {

    /** Platform dependent end-of-line marker */
    public static final String Eol = System.lineSeparator();
    /** The literal indent char(s) used for pretty-printing */
    public static final String Indents = "  ";
    private static int level;


    //private static final List<String> iffy_ignores = Arrays.asList("varDeclList", "argList", "expressionStmt", "varDeclInitialize", "expressionList");
    //got rid of 'params' and 'compoundStmt' in ignore_list
    private static final List<String> ignore_list = Arrays.asList("declarationList", "paramId", "expressionList", "argList", "varDeclId", "varDeclList", "scopedVarDeclaration", "unInitVarDeclList", "expressionStmt", "declaration", "statementList", "statement", "(", ")", "{", "}", "[", "]", ";", "<EOF>");
    private static final List<String> expression_list = Arrays.asList("params", "expression", "simpleExpression", "andExpression", "unaryRelExpression", "relExpression", "sumExpression", "mulExpression", "unaryExpression", "factor", "immutable");
    private static final List<String> non_collapse = Arrays.asList("returnStmt");

    /* Call to convert Antlr parse tree into our own AST */
    public static ASTNode generateAST(final Tree t, final List<String> ruleNames) {
        ASTNode an = toCustomNodeTree(t, ruleNames);
//        return toAST(an);
        an = toAST(an);
        an = ASTNode.ASTNodeResolver(an);
        return an;
    }

    public static ASTNode toAST(ASTNode an) {
        if(an == null) return null;
        int count = an.children.size();
        //remove all nodes that have id as ""
        for(int i = 0; i < count; i++) {
            ArrayList<ASTNode> list = extractEmptyNodeChildren(an.children.get(i));
            if(list != null) {
                an.children.remove(i); //remove current child
                an.children.addAll(i, list); //add list to child list at last index
                i -= 1; //check the last child again since it already had a match
                count = an.children.size(); //change size for loop purposes
            }
        }
        //an.children = extractNullChildren(an);
        for(int i = 0; i < an.children.size(); i++) {
            an.children.set(i, toAST(an.children.get(i)));
        }
        return an;
    }

    public static ArrayList<ASTNode> extractEmptyNodeChildren(ASTNode an) {
        if(an != null && an.id.equals("")) {
            return an.children;
        }
        return null;
    }

    public static ArrayList<ASTNode> extractNullChildren(ASTNode n) {
        for(int i = 0; i < n.children.size(); i++) {
            if(n.children.get(i) == null) {
                n.children.remove(i);            }
        }
        return n.children;
    }

    /* Recursively convert Antlr tree to AST by creating an AST node at each Antlr node */
    public static ASTNode toCustomNodeTree(final Tree t, final List<String> ruleNames) {

        String rule = Utils.escapeWhitespace(Trees.getNodeText(t, ruleNames), false);

        /* If the node is a terminal, check if it's in the ignore list. If not,
         * return a new AST node with the node rule as its name. */
        if (t.getChildCount() == 0) {
            //String checkString = Utils.escapeWhitespace(Trees.getNodeText(t, ruleNames), false);
            if(ignore_list.contains(rule)) {
                return null;
            }
            return new ASTNode(rule);
        }

        ASTNode an = new ASTNode(rule);

        if(ignore_list.contains(rule)) {
            /* If rule is in ignore list, make the node name an empty String,
             * to be yoinked out later */
            an = new ASTNode("");

        } else if(t.getChildCount() == 1 && expression_list.contains(rule)) {
            /* Mark nodes to yank later that are useless one-child expression
            * nodes, e.g. sumexpression->mulexpression->unaryexpression->...*/
            an = new ASTNode("");
        }

        for (int i = 0; i < t.getChildCount(); i++) {
            an.add(toCustomNodeTree(t.getChild(i), ruleNames));
        }
        return an;
    }

    public static String toPrettyTree(final Tree t, final List<String> ruleNames) {
        level = 0;
        return process(t, ruleNames).replaceAll("(?m)^\\s+$", "").replaceAll("\\r?\\n\\r?\\n", Eol);
    }

    private static String process(final Tree t, final List<String> ruleNames) {

        if (t.getChildCount() == 0) {
            return Utils.escapeWhitespace(Trees.getNodeText(t, ruleNames), false);
        }

        StringBuilder sb = new StringBuilder();

        sb.append(lead(level));
        level++;
        String s = Utils.escapeWhitespace(Trees.getNodeText(t, ruleNames), false);
//        System.out.println(t.getChildCount() + " : " + s);
        sb.append(s + ' ');
        for (int i = 0; i < t.getChildCount(); i++) {
            sb.append(process(t.getChild(i), ruleNames));
        }
        level--;
        sb.append(lead(level));
        return sb.toString();
    }

    private static String lead(int level) {
        StringBuilder sb = new StringBuilder();
        if (level > 0) {
            sb.append(Eol);
            for (int cnt = 0; cnt < level; cnt++) {
                sb.append(Indents);
            }
        }
        return sb.toString();
    }
}