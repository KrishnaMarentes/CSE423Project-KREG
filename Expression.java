import java.util.ArrayList;

public class Expression extends ASTNode {
    public ASTNode left;
    public ASTNode right;
    public String symbol;
    public Expression(ASTNode node) {
        super(node.id);
        left = null;
        right = null;
        symbol = null;
    }

    public static class OpExpression extends Expression {
        public OpExpression(ASTNode node) {
            super(node);
            this.id = "OpExpression";
            symbol = node.children.get(1).id;
            if(symbol.equals("sumop") || symbol.equals("mulop") || symbol.equals("relop")) {
                symbol = node.children.get(1).children.get(0).id;
            }
            left = ASTNode.ASTNodeResolver(node.children.get(0));
            right = ASTNode.ASTNodeResolver(node.children.get(2));
        }

        public String generateCode() {
            StringBuilder sb = new StringBuilder();

            return sb.toString();
        }

    }

    public static class AssignmentExpression extends Expression {
        public AssignmentExpression(ASTNode node) {
            super(node);
            this.id = "AssignmentExpression";
            symbol = node.children.get(1).id;
            left = ASTNode.ASTNodeResolver(node.children.get(0));
            right = ASTNode.ASTNodeResolver(node.children.get(2));
        }
    }

    public static class UnaryExpression extends Expression {
        public UnaryExpression(ASTNode node) {
            super(node);
            if(node.children.get(0).id.equals("mutable") || node.children.get(0).id.equals("constant") ) { //found mutable at 0?
                symbol = node.children.get(1).id;
                left = Expression.ExpressionResolver(node.children.get(0));
                right = null;
            } else if(node.children.get(0).id.equals("unaryop")) {
                symbol = node.children.get(0).children.get(0).id;
                left = null;
                right = Expression.ExpressionResolver(node.children.get(1));
            } else {
                symbol = node.children.get(0).id;
                left = null;
                right = Expression.ExpressionResolver(node.children.get(1));
            }
        }

        public String printNode(int indentLevel) {
            StringBuilder sb = new StringBuilder();

            sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
            ++indentLevel;
            if(left == null) {
                sb.append(ASTNode.lead(indentLevel) + this.symbol + EOL);
                sb.append(this.right.printNode(indentLevel));
            } else {
                sb.append(this.left.printNode(indentLevel));
                sb.append(ASTNode.lead(indentLevel) + this.symbol + EOL);
            }

            return sb.toString();
        }

    }

    public static Expression ExpressionResolver(ASTNode node) {
        if(node == null) return null;
        if(node.children.size() == 3 && !node.id.equals("immutable")) {
            String op = node.children.get(1).id;
            if(op.contains("=") && !node.id.equals("relExpression")) {
                return new AssignmentExpression(node);
            } else {
                return new OpExpression(node);
            }
        } else if(node.id.equals("unaryExpression") || node.id.equals("unaryRelExpression")) {
            return new UnaryExpression(node);
        }
        String expressionType = node.id.replaceAll("Expression", "");
        switch (expressionType) {
            case "constant":
                return new Constant(node);
            case "mutable":
                return new Mutable(node);
            case "immutable":
                return new Immutable(node);
            case "call":
                return new Call(node);
            default:
                return new Expression(node);
        }
    }

    public static class Constant extends Expression {
        public Constant(ASTNode node) {
            super(node);
            this.id = node.children.get(0).id;
        }

        //            public String printNode(int indentLevel) {
//                return ASTNode.lead(indentLevel) + this.id + EOL;
//            }
        public String printNode(int indentLevel) {
            if(indentLevel == -1)
                return this.id;
            return ASTNode.lead(indentLevel) + this.id + EOL;
        }
    }

    public static class Mutable extends Expression {
        public Expression squareBracketExpression;
        public Mutable(ASTNode node) {
            super(node);
            if(node.children.size() > 1 && node.children.get(1) == null) { //line 314 kregGrammar.g4
                this.id = node.children.get(0).id;
                this.squareBracketExpression = Expression.ExpressionResolver(node.children.get(2));
            } else {
                this.id = node.children.get(0).id;
                squareBracketExpression = null;
            }
        }

        public String printNode(int indentLevel) {
            return ASTNode.lead(indentLevel) + this.id + EOL;
        }
    }

    public static class Immutable extends Expression {
        public Expression expression;
        public Immutable(ASTNode node) {
            super(node);
            expression = Expression.ExpressionResolver(node.children.get(1));
        }

        public String printNode(int indentLevel) {
            return expression.printNode(indentLevel);
        }

    }

    public static class Call extends Expression {
        public String funcName;
        public Call(ASTNode node) {
            super(node);
            funcName = node.children.get(0).id; //function name that is being called, now ID
            ArrayList<ASTNode> args = node.children.get(2).children;
//                node.children.get(2).children.clear();
            this.children.clear();
            for(int i = 0; i < args.size(); i++) {
                if(args.get(i) == null || args.get(i).id.equals(",")) continue;
                this.children.add(ASTNode.ASTNodeResolver(args.get(i)));
            }
        }

        public String printNode(int indentLevel) {
            StringBuilder sb = new StringBuilder();

            sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
            ++indentLevel;
            sb.append(ASTNode.lead(indentLevel) + funcName + EOL);
            ++indentLevel;
            for(int i = 0; i < this.children.size(); i++) {
                sb.append(this.children.get(i).printNode(indentLevel));
            }

            return sb.toString();
        }
    }


    public String printNode(int indentLevel) {
        StringBuilder sb = new StringBuilder();

        sb.append(ASTNode.lead(indentLevel) + this.symbol + EOL);
        indentLevel++;
        sb.append(this.left.printNode(indentLevel));
        sb.append(this.right.printNode(indentLevel));

        return sb.toString();
    }

    public String generateCode() {
        return "";
    }

}
