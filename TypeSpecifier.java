public class TypeSpecifier extends ASTNode {
    public String pointerLevel = null;
    public TypeSpecifier(String type, String pointerLevel) {
        super(type);
        this.pointerLevel = pointerLevel;
    }

    public static TypeSpecifier TypeSpecifierResolver(ASTNode node) {
        if(node.children.size() == 1) {
            return new TypeSpecifier(node.children.get(0).id, "");
        }
        return new TypeSpecifier(node.children.get(0).id, node.children.get(1).id);
    }

    public String printNode(int indentLevel) {
        return ASTNode.lead(indentLevel) + this.id + this.pointerLevel;
    }

}