import java.util.ArrayList;

public class Expression extends ASTNode {

    public static int globalCounter = 0;
    public static final String tmpVar = "KREG.";

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

            sb.append(tmpVar + globalCounter++ + " = ");
            if(left instanceof Constant || left instanceof Mutable) {
                sb.append(left.generateCode() + " " + this.symbol + " ");
            } else {
                sb.append(tmpVar + globalCounter++ + " " + this.symbol + " ");
            }

            if(right instanceof Constant || right instanceof Mutable) {
                sb.append(right.generateCode() + ";" + EOL);
            } else {
                sb.append(tmpVar + globalCounter + ";" + EOL);
            }

            if(!(left instanceof Constant || left instanceof Mutable)) {
                globalCounter--;
                sb.append(left.generateCode());
            }

            if(!(right instanceof Constant || right instanceof Mutable)) {
                sb.append(right.generateCode());
            }

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

        public String generateCode() {
            StringBuilder sb = new StringBuilder();

            String expression = right.generateCode();
            String[] generatedStrings = Expression.getModifiedExpressionString(expression);
            String[] lastVar = generatedStrings[generatedStrings.length - 1].split(" ");
            for(int i = 0; i < generatedStrings.length - 1 && lastVar.length != 1; i++) {
                sb.append(generatedStrings[i] + EOL);
            }
            sb.append(left.generateCode() + " " + this.symbol + " ");
            if(lastVar.length > 1) {
                String sub = generatedStrings[generatedStrings.length - 1].split(" = ")[1];
                sb.append(sub + EOL);
            }
            else {
                sb.append(lastVar[0] + ";" + EOL);
            }

            return sb.toString();
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

        public String generateCode() {
            return this.id;
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

        public String generateCode() {
            return this.id;
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

        public String generateCode() {
            return expression.generateCode();
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

        public String generateCode() {
            StringBuilder sb = new StringBuilder();
            ArrayList<String> args = new ArrayList<>();
            ArrayList<String> toPrint = new ArrayList<>();

            for(int i = 0; i < this.children.size(); i++) {
                String expr = this.children.get(i).generateCode();
                String[] exp_list = getModifiedExpressionString(expr);
                String[] lastVar = exp_list[exp_list.length - 1].split(" ");
                for(int j = 0; j < exp_list.length && lastVar.length != 1; j++) {
//                    sb.append(exp_list[j] + EOL);
                    toPrint.add(exp_list[j] + EOL);
                }
                args.add(lastVar[0]);
//                if(lastVar.length > 1) {
//                    //String sub = exp_list[exp_list.length - 1].split(" = ")[1];
//                    //sb.append(sub + EOL);
//                    args.add(lastVar[0]);
//                }
//                if(i != this.children.size() - 1) {
//                    sb.append(", ");
//                }
            }
            sb.append(funcName + "(");
            for(int i = 0; i < args.size(); i++) {
                sb.append(args.get(i));
                if(i != args.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")" + EOL);
            for(int i = 0; i < toPrint.size(); i++) {
                sb.append(toPrint.get(i));
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

    public static String[] getModifiedExpressionString(String expr) {
        return ASTNode.ArrayReverse(expr.split(EOL));
    }

}
