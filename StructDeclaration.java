import java.util.ArrayList;

public class StructDeclaration extends ASTNode {
    //          0     1            3-n               n+2
    //STATIC? STRUCT ID LCURLY unInitVarDecl* RCURLY ID? SEMICOLN
    public String scope = null;
    public String struct_id = null;
    public String struct_init_id = null;

    public StructDeclaration(String scope, String id, ArrayList<ASTNode> varDecls) {
        super("StructDeclaration");

    }

    public static StructDeclaration StructDeclarationResolver(ASTNode node) {
        ArrayList<ASTNode> nodes = node.children;
        int size = nodes.size();
        if(nodes.get(0).id.equals("static")) {

        } else {

        }
        return null;
    }

    //public static ArrayList<> StructVars

}