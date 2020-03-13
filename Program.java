public class Program extends ASTNode {
    public Program(ASTNode node) {
        super(node.id);
        for(int i = 0; i < node.children.size(); i++) {
            node.children.set(i, ASTNode.ASTNodeResolver(node.children.get(i)));
        }
        this.children = node.children;
    }

    public String printNode(int indentLevel) {
        return "program" + EOL;
    }

    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < this.children.size(); i++) {
            if(this.children.get(i) == null) continue;
            sb.append(this.children.get(i).generateCode());
        }
        return sb.toString();
    }
}