import javafx.util.Pair;

import java.util.ArrayList;

public class VarDeclaration extends ASTNode {
    public String scope;
    public TypeSpecifier type;
    ArrayList<Pair<String, Expression>> vars; //<ID, init_value>

    //replace String with Var Type? (to show that it can be an array too, maybe some internal boolean)
    public VarDeclaration(String scope, TypeSpecifier type, ArrayList<Pair<String, Expression>> vars) {
        super("VarDeclaration");
        this.scope = scope;
        this.type = type;
        this.vars = vars;
    }

    public static VarDeclaration VarDeclarationResolver(ASTNode node) {
        TypeSpecifier type = TypeSpecifier.TypeSpecifierResolver(node.children.get(0));
        ArrayList<ASTNode> vars = node.children;
        ArrayList<Pair<String, Expression>> newVars = new ArrayList<>();
        for(int i = 0; i < vars.size(); i++) {
            if(vars.get(i) == null) continue;
            ASTNode n = vars.get(i);
            if(n.id.equals("varDeclInitialize")) {
                ArrayList<ASTNode> c = n.children;
                if(c.size() == 3) {
                    Expression expr = Expression.ExpressionResolver(c.get(2));
                    newVars.add(new Pair<String, Expression>(c.get(0).id, expr));
                } else if(c.size() == 4) {
                    Expression expr = Expression.ExpressionResolver(c.get(2));
                    //nasty hack - surround string with [brackets] to denote array
                    newVars.add(new Pair<String, Expression>("[" + c.get(0).id + "]", expr));
                } else {
                    newVars.add(new Pair<String, Expression>(c.get(0).id, null));
                }
            }
        }
        return new VarDeclaration(null, type, newVars);
    }

    public String printNode(int indentLevel) {
        StringBuilder sb = new StringBuilder();

        sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
        ++indentLevel;
        sb.append(ASTNode.lead(indentLevel) + this.type.id + this.type.pointerLevel + EOL);
        ++indentLevel;
        for(int i = 0; i < vars.size(); i++) {
            if(vars.get(i).getValue() == null) {
                sb.append(ASTNode.lead(indentLevel) + vars.get(i).getKey() + EOL);
            } else {
                if(vars.get(i).getKey().startsWith("[")) {
                    String key = ASTNode.lead(indentLevel) + vars.get(i).getKey();
                    String val = "[" + vars.get(i).getValue().printNode(-1) + "]";
                    sb.append(key.replaceAll("(\\[|])", "") + val + EOL);
                } else {
                    sb.append(ASTNode.lead(indentLevel) + vars.get(i).getKey() + " = " + EOL);
                    indentLevel += 2;
                    sb.append(vars.get(i).getValue().printNode(indentLevel));
                    indentLevel -= 2;
                }
            }
        }

        return sb.toString();
    }

    public String generateCode() {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < vars.size(); i++) {
            sb.append(this.type.id + this.type.pointerLevel + " " + vars.get(i).getKey() + ";" + EOL);
        }

        return sb.toString();
    }

}