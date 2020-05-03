import java.util.ArrayList;

public class ASTNode {
    public static final String EOL = System.lineSeparator();
    public static final String Indents = "  ";


    public String id = null;
    public ArrayList<ASTNode> children = new ArrayList<ASTNode>();

    public ASTNode(String id) {
        this.id = id;
    }

    public void add(ASTNode an) {
        this.children.add(an);
    }

    //just override these
    public String printNode(int indentLevel)
    {
        return "";
    }
    public String generateCode() { return ""; }

    public static ASTNode ASTNodeResolver(ASTNode node) {
        /* Will expect more cases here to resolve what
        * "kind" of node it is, e.g. program, varDeclaration, etc. */
        if(node == null) return null;
        switch(node.id) {
            case "program":
                return new Program(node);
            case "typeSpecifier":
                return TypeSpecifier.TypeSpecifierResolver(node);
            case "varDeclaration":
                return VarDeclaration.VarDeclarationResolver(node);
            case "funDeclaration":
                return FunDeclaration.FunctionDeclarationResolver(node);
            case "structDeclaration":
                return StructDeclaration.StructDeclarationResolver(node);
            case "expression":
            case "simpleExpression":
            case "andExpression":
            case "unaryRelExpression":
            case "relExpression":
            case "mulExpression":
            case "sumExpression":
            case "unaryExpression":
            case "constant":
            case "mutable":
            case "immutable":
            case "call":
                return Expression.ExpressionResolver(node);
            case "expressionStmt":
            case "compoundStmt":
            case "selectionStmt":
            case "iterationStmt":
            case "returnStmt":
            case "breakStmt":
            case "gotoStmt":
            case "labelStmt":
                return Statement.StatementResolver(node);
            //case "enumDeclaration":
            default:
                return node;
        }
    }

    public static String toPrettyASTString(ASTNode node) {
        return getASTString(node, 0);
    }

    public static String getASTString(ASTNode node, int indentLevel) {
        StringBuilder sb = new StringBuilder();

        String s = node.printNode(indentLevel);
        sb.append(s);

        ++indentLevel;

        for(int i = 0; i < node.children.size(); i++) {
            if(node.children.get(i) == null) continue;
            sb.append(node.children.get(i).printNode(indentLevel));
        }

        return sb.toString();
    }

    protected static String lead(int level) {
        StringBuilder sb = new StringBuilder();
        if (level > 0) {
            //sb.append(EOL);
            for (int cnt = 0; cnt < level; cnt++) {
                sb.append(Indents);
            }
        }
        return sb.toString();
    }

}


