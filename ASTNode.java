import java.util.ArrayList;

public class ASTNode {
    public String id = null;
    public ArrayList<ASTNode> children = new ArrayList<ASTNode>();

    public ASTNode(String id) {
        this.id = id;
    }
}
