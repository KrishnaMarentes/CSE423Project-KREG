import java.util.ArrayList;

public class ASTNode {
    public String id = null;
    public ArrayList<ASTNode> children = new ArrayList<ASTNode>();

    public ASTNode(String id) {
        this.id = id;
    }

    public void add(ASTNode an) {
        this.children.add(an);
    }

    public static ASTNode ASTNodeCreator(String id) {
        /* Will expect more cases here to resolve what
        * "kind" of node it is, e.g. program, varDeclaration, etc. */
        switch(id) {
            case "program":
                break;
            default:
                return null;
        }
        return new ASTNode("id");
    }

    /* Subclasses to handle rule-specific features
    * ******************************************** */
    public static class VarDeclaration extends ASTNode {
        public VarDeclaration(TypeSpecifier ts, String id) {
            super(id);
        }
    }

    public static class TypeSpecifier extends ASTNode {
        String pointer = null;
        public TypeSpecifier(String type) {
            super(type);
        }

        public TypeSpecifier(String type, String pointer) {
            super(type);
            this.pointer = pointer;
        }
    }

    public static class FunDeclaration extends ASTNode {
        //      0        1   2      3       4                5           (6 total)
        //typeSpecifier ID LPAREN params RPAREN (compoundStmt | SEMICOLN)
        //really only care about 0, 1, 3, 5
        public FunDeclaration(ASTNode typeSpecifier, String id, ASTNode params, ASTNode compoundStmt) {
            super(id);
            children.add(typeSpecifier);
            children.add(params);
            children.add(compoundStmt);
        }

        public FunDeclaration(ASTNode typeSpecifier, String id, ASTNode params) {
            super(id);
            children.add(typeSpecifier);
            children.add(params);
        }
    }

    public static class Expression extends ASTNode {
        public Expression(String id, ASTNode left, ASTNode right) {
            super(id);
            children.add(left);
            children.add(right);
        }
    }

    public static class FunctionCall extends ASTNode {
        public FunctionCall(String id, ASTNode argList) {
            super(id);

        }
    }
}


