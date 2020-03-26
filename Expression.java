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

    /* Utility function to get the last assigned variable from a
     * string, presumably from a generateCode() call.
     */
    public static String getLastAssignedVar(String ir) {
        String[] lines = ir.split(EOL);
        String lastVar = lines[lines.length-1];
        lastVar = lastVar.split(" ")[0];
        return lastVar;
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

        public String generateCode(){
            StringBuilder sb = new StringBuilder();
            // Use correct tmp names
            String leftTmp;
            String rightTmp;

            // No recursion necessary for either operand
            if((left instanceof Constant || left instanceof Mutable) &&
                    (right instanceof Constant || right instanceof Mutable)) {
                sb.append(tmpVar + globalCounter++ + " = " + left.id + " " +
                        this.symbol + " " + right.id + ";" + EOL);
                return sb.toString();
            }

            // Otherwise, recurse in!
            else {
                if (!(left instanceof Constant || left instanceof Mutable)) {
                    leftTmp = left.generateCode();
                    sb.append(leftTmp); // record any tmpVars created by the left's IR

                    // Grab just the last tmp var assigned
                    leftTmp = getLastAssignedVar(leftTmp);
                } else {
                    leftTmp = left.id;
                }
                if(!(right instanceof Constant || right instanceof Mutable)) {
                    rightTmp = right.generateCode();
                    sb.append(rightTmp);
                    // Grab just the last tmp var assigned
                    rightTmp = getLastAssignedVar(rightTmp);
                } else {
                    rightTmp = right.id;
                }
            }

            sb.append(tmpVar + globalCounter++ + " = " +
                    leftTmp + " " + this.symbol + " " + rightTmp + ";" + EOL);

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
            String lastTmp;

            String expression = right.generateCode();

            /* Only append if generateCode() call made new variables */
//            if (expression.contains(tmpVar) || expression.contains("=")) {
            if (expression.contains("=")) { // works slightly better
                sb.append(expression);
            }

            /* Every op expression resolves the right hand side into one tmpVar.
             * The following lines assign the current LHS variable name to that tmpVar */
            lastTmp = getLastAssignedVar(expression);
            String newSymbol;
            if((newSymbol = symbol.replace("=", "")).length() == 0) {
                sb.append(left.id + " = " + lastTmp + ";" + EOL);
            } else {
                sb.append(left.id + " = " + left.id + " " + newSymbol + " " + lastTmp + ";" + EOL);
            }
//            sb.append(left.id + " = " + lastTmp + ";" + EOL);

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

        /* Author: Geoff */
        public String generateCode() {
            StringBuilder sb = new StringBuilder();

            //prefixes
            //MINUS | STAR | NOT | AND | TILDE
            //MINUSMINUS | PLUSPLUS
            if(left == null) { //constant/mutable/immutable on right side, symbol on left
                //TODO implement unary prefixes
                String right = this.right.generateCode();
                String lastVar = Expression.getLastAssignedVar(right);
                String symbol = this.symbol;
                String var = tmpVar + globalCounter++;

                if(this.symbol.equals("++") || this.symbol.equals("--")) {
                    sb.append(right + " = " + right + " " + this.symbol.substring(0,1) + " " + "1" + ";" + EOL);
                } else if(this.symbol.equals("-") || this.symbol.equals("~") || this.symbol.equals("!")) {
                    if(right.contains("=")) {
                        sb.append(right);
                    }
                    if(this.symbol.equals("!")) {
                        sb.append(var + " = " + lastVar + " == 0;" + EOL);
                    } else {
                        sb.append(var + " = " + symbol + lastVar + ";" + EOL);
                    }
                }
            } else { //symbol on right, can only be ++ or --
                String left = this.left.generateCode();
                //for postfix incrementation, there is a temporary variable that is used on the
                //same line that X++ is used on, which stores the value of the variable, before incrementation.
                //https://stackoverflow.com/questions/7031326/what-is-the-difference-between-prefix-and-postfix-operators
                String var1 = tmpVar + globalCounter++;
                String var2 = tmpVar + globalCounter++;
                sb.append(var1 + " = " + left + ";" + EOL);
                sb.append(left + " = " + left + " " + this.symbol.substring(0,1) + " " + "1" + ";" + EOL);
                sb.append(var2 + " = " + var1 + ";" + EOL);
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
            String lastTmp;
            ArrayList<String> args = new ArrayList<>();

            for(int i = 0; i < this.children.size(); i++) {
                String expr = this.children.get(i).generateCode();

                /* Only append this argument's IR to output if tmpVars were made  */
                if (expr.contains(tmpVar)) {
                    sb.append(expr);
                }

                /* Add the name of the arg to the args list */
                lastTmp = getLastAssignedVar(expr);
                args.add(lastTmp);

            }

            sb.append(tmpVar + globalCounter++ + " = ");

            sb.append(funcName + "(");
            for(int i = 0; i < args.size(); i++) {
                sb.append(args.get(i));
                if(i != args.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(");" + EOL);

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

    /* Used for loop conditionals */
    public String printExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.left.id + " ");
        sb.append(this.symbol + " ");
        sb.append(this.right.id);
        return sb.toString();
    }

}
