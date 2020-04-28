import java.util.ArrayList;

public class FunDeclaration extends ASTNode {
    //      0        1   2      3       4                5           (6 total)
    //typeSpecifier ID LPAREN params RPAREN (compoundStmt | SEMICOLN)
    //really only care about 0, 1, 3, 5
    public ASTNode typeSpecifier;
    public String functionName;
    //        public ASTNode params; //could put params into children//or not
    public ArrayList<Pair<TypeSpecifier, String>> params;
    public ASTNode compoundStmt;
    public FunDeclaration(ASTNode typeSpecifier, ASTNode functionName, ASTNode params, ASTNode compoundStmt) {
        super("FunDeclaration");
        this.typeSpecifier = typeSpecifier;
        this.functionName = functionName.id;
        this.params = paramResolver(params);
        this.compoundStmt = compoundStmt;
    }

    public static FunDeclaration FunctionDeclarationResolver(ASTNode node) {
        ArrayList<ASTNode> n = node.children;
        if(n.get(5).id.equals("compoundStmt")) {
            return new FunDeclaration(
                    ASTNodeResolver(n.get(0)),
                    ASTNodeResolver(n.get(1)),
                    ASTNodeResolver(n.get(3)),
                    ASTNodeResolver(n.get(5))
            );
        }
        return new FunDeclaration(
                ASTNodeResolver(n.get(0)),
                ASTNodeResolver(n.get(1)),
                ASTNodeResolver(n.get(3)),
                null
        );
    }

    //hotfix implemented here, be wary of spooky things that might become of this
    public ArrayList<Pair<TypeSpecifier, String>> paramResolver(ASTNode node) {
        ArrayList<Pair<TypeSpecifier, String>> p = new ArrayList<>();
        if(node.id.equals("params")) {
            for(int i = 0; i < node.children.size(); i++) {
                if(node.children.get(i) == null || node.children.get(i).id.equals(",")) continue;
                if(node.children.get(i).id.equals("params")) {
                    p.addAll(paramResolver(node.children.get(i)));
                } else {
                    TypeSpecifier t = TypeSpecifier.TypeSpecifierResolver(node.children.get(i).children.get(0));
                    String param = node.children.get(i).children.get(1).id;
                    p.add(new Pair<>(t, param));
                }
            }
        } else { //id = parameter?
            TypeSpecifier t = TypeSpecifier.TypeSpecifierResolver(node.children.get(0));
            String param = node.children.get(1).id;
            p.add(new Pair<>(t, param));
        }
        return p;
    }

    public String printNode(int indentLevel) {
        StringBuilder sb = new StringBuilder();

        sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
        indentLevel++;
        sb.append(this.typeSpecifier.printNode(indentLevel) + EOL);
        sb.append(ASTNode.lead(indentLevel) + this.functionName + EOL);

        indentLevel++;
        for(int i = 0; i < this.params.size(); i++) {
//                String param_type = this.params.get(i).getKey().id + this.params.get(i).getKey().pointerLevel;
            String param_type = this.params.get(i).getKey().printNode(indentLevel);
            sb.append(param_type + " " + this.params.get(i).getValue() + EOL);
        }
        indentLevel--;

        if(this.compoundStmt == null) {
            sb.append(";");
        } else {
            sb.append(this.compoundStmt.printNode(indentLevel));
        }
        return sb.toString();
    }

    public String generateCode() {
        StringBuilder sb = new StringBuilder();

        sb.append("function " + this.functionName + "(");
        for(int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getKey().id + params.get(i).getKey().pointerLevel + " ");
            sb.append(params.get(i).getValue());
            if(i < params.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");

        if(compoundStmt != null) {
            sb.append(EOL);
            sb.append(compoundStmt.generateCode());
        } else {
            sb.append(";");
        }

        return sb.toString();
    }

}