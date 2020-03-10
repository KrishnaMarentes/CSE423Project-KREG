import com.sun.corba.se.impl.protocol.INSServerRequestDispatcher;
import com.sun.corba.se.spi.activation.EndpointInfoListHelper;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    //just override this
    public String printNode(int indentLevel)
    {
        return "";
    }

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

    /* Subclasses to handle rule-specific features
    * ******************************************** */
    public static class Program extends ASTNode {
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
    }

    public static class VarDeclaration extends ASTNode {
        public String scope;
        public TypeSpecifier type;
        ArrayList<Pair<String, Expression>> vars; //<ID, init_value>

        //put vars in children?
        public VarDeclaration(String scope, TypeSpecifier type, ArrayList<Pair<String, Expression>> vars) {
            super("VarDeclaration");
            this.scope = scope;
            this.type = type;
            this.vars = vars;
        }

        public static VarDeclaration VarDeclarationResolver(ASTNode node) {
            TypeSpecifier type = TypeSpecifier.TypeSpecifierResolver(node.children.get(0));
            ArrayList<ASTNode> vars = node.children;
            ArrayList<Pair<String, Expression>> newVars = new ArrayList<>();
            for(int i = 0; i < vars.size(); i++) {
                if(vars.get(i) == null) continue;
                ASTNode n = vars.get(i);
                if(n.id.equals("varDeclInitialize")) {
                    ArrayList<ASTNode> c = n.children;
                    if(c.size() == 3) {
                        Expression expr = Expression.ExpressionResolver(c.get(2));
                        newVars.add(new Pair<String, Expression>(c.get(0).id, expr));
                    } else {
                        newVars.add(new Pair<String, Expression>(c.get(0).id, null));
                    }
                }
            }
            return new VarDeclaration(null, type, newVars);
        }

        public String printNode(int indentLevel) {
            StringBuilder sb = new StringBuilder();

            sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
            ++indentLevel;
            sb.append(ASTNode.lead(indentLevel) + this.type.id + this.type.pointerLevel + EOL);
            ++indentLevel;
            for(int i = 0; i < vars.size(); i++) {
                if(vars.get(i).getValue() == null) {
                    sb.append(ASTNode.lead(indentLevel) + vars.get(i).getKey() + EOL);
                } else {
                    sb.append(ASTNode.lead(indentLevel) + vars.get(i).getKey() + " = " + EOL);
                    indentLevel+=2;
                    sb.append(vars.get(i).getValue().printNode(indentLevel));
                    indentLevel-=2;
                }
            }

            return sb.toString();
        }

    }

    public static class FunDeclaration extends ASTNode {
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

        public ArrayList<Pair<TypeSpecifier, String>> paramResolver(ASTNode node) {
            ArrayList<Pair<TypeSpecifier, String>> p = new ArrayList<>();
            for(int i = 0; i < node.children.size(); i++) {
                if(node.children.get(i) == null || node.children.get(i).id.equals(",")) continue;
                TypeSpecifier t = TypeSpecifier.TypeSpecifierResolver(node.children.get(i).children.get(0));
                String param = node.children.get(i).children.get(1).id;
                p.add(new Pair<TypeSpecifier, String>(t, param));
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

    }

    public static class StructDeclaration extends ASTNode {
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

    public static class TypeSpecifier extends ASTNode {
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

    public static class Statement extends ASTNode {
        public Statement(ASTNode node) {
            super(node.id);
            this.children = node.children;
        }

        public static class CompoundStatement extends Statement {
            public CompoundStatement(ASTNode node) {
                super(node);
                this.id = "Body"; //also CompoundStatement
                this.children = node.children;
                for(int i = 0; i < this.children.size(); i++) {
                    if(this.children.get(i) == null) continue;
                    this.children.set(i, ASTNode.ASTNodeResolver(this.children.get(i)));
                }
            }

            public String printNode(int indentLevel) {
                StringBuilder sb = new StringBuilder();
                sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
                indentLevel++;
                for(int i = 0; i < this.children.size(); i++) {
                    if(this.children.get(i) == null) continue;
                    sb.append(this.children.get(i).printNode(indentLevel));
                }
                return sb.toString();
            }

        }

        public static class ReturnStatement extends Statement {
            //ignore first child of return statement
            public ReturnStatement(ASTNode node) {
                super(node);
                this.id = "ReturnStatement";
                if(node.children.size() > 1) {
                    this.children.set(0, Expression.ExpressionResolver(this.children.get(1)));
                }
                this.children.set(1, null);
            }

            public String printNode(int indentLevel) {
                StringBuilder sb = new StringBuilder();
                sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
                indentLevel++;
                for(int i = 0; i < this.children.size(); i++) {
                    if(this.children.get(i) == null) continue;
                    sb.append(this.children.get(i).printNode(indentLevel));
                }

                return sb.toString();
            }

        }

        public static class SelectionStatement extends Statement {
            public Expression bool_expression;
            public Statement body;
            public SelectionStatement(ASTNode node) {
                super(node);
                bool_expression = null;
            }

            public static class IfStatement extends SelectionStatement {
                //0     1       2        3        4        5        6      7
                //IF LPAREN expression RPAREN statement elsifList ELSE statement
                public ArrayList<Pair<Expression, Statement>> elseIfList;
                public Statement elseBody;
                public IfStatement(ASTNode node) {
                    super(node);
                    this.id = "IfStatement";
                    bool_expression = Expression.ExpressionResolver(node.children.get(2));
                    body = Statement.StatementResolver(node.children.get(4));
                    elseIfList = ElseIfToArray(node.children.get(5)); //pass whole node, to find all children with id "elsifList"
                    if(node.children.size() >= 8) {
                        elseBody = Statement.StatementResolver(node.children.get(7));
                    } else {
                        elseBody = null;
                    }
                    this.children.clear();
                    //first 'if' has 6 children, next has 8 (from grammar)
                }

                public ArrayList<Pair<Expression, Statement>> ElseIfToArray(ASTNode node) {
                    if(node == null) return null;
                    ArrayList<Pair<Expression, Statement>> elseIfStatements = new ArrayList<>();
                    ASTNode current_node = node;
                    while(current_node.children.size() != 0) {
                        Expression expression = Expression.ExpressionResolver(current_node.children.get(4));
                        Statement statement = Statement.StatementResolver(current_node.children.get(6));
                        elseIfStatements.add(new Pair<>(expression, statement));
                        current_node = current_node.children.get(0);
                    }

                    //reverse array
                    for(int i = 0; i < elseIfStatements.size(); i+=2) {
                        Pair<Expression, Statement> tmp1 = elseIfStatements.get(i);
                        Pair<Expression, Statement> tmp2 = elseIfStatements.get(elseIfStatements.size() - 1 - i);
                        elseIfStatements.set(i, tmp2);
                        elseIfStatements.set(elseIfStatements.size() - 1 - i, tmp1);

                    }

                    return elseIfStatements;
                }

                public String printNode(int indentLevel) {
                    StringBuilder sb = new StringBuilder();

                    sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
                    ++indentLevel;
                    sb.append(this.bool_expression.printNode(indentLevel));
                    sb.append(this.body.printNode(indentLevel--));
                    if(elseIfList != null && elseIfList.size() > 0) {
                        for(int i = 0; i < elseIfList.size(); i++) {
                            sb.append(ASTNode.lead(indentLevel) + "else if" + EOL);
                            ++indentLevel;
                            sb.append(elseIfList.get(i).getKey().printNode(indentLevel));
                            sb.append(elseIfList.get(i).getValue().printNode(indentLevel));
                            --indentLevel;
                        }
                    }
                    if(elseBody != null) {
                        sb.append(ASTNode.lead(indentLevel) + "else" + EOL);
                        ++indentLevel;
                        sb.append(elseBody.printNode(indentLevel));
                    }

                    return sb.toString();
                }

            }

            public static class SwitchStatement extends SelectionStatement {
                public SwitchStatement(ASTNode node) {
                    super(node);
                }

                public String printNode(int indentLevel) {
                    return "";
                }
            }


        }
        public static class ExpressionStatement extends Statement {
            Expression expression;
            public ExpressionStatement(ASTNode node) {
                super(node);
                expression = Expression.ExpressionResolver(node);
            }

            public String printNode(int indentLevel) {
                return expression.printNode(indentLevel);
            }
        }

        public static Statement StatementResolver(ASTNode node) {
            if(node == null) return null;
            String statementType = node.id.replaceAll("Stmt", "");
            switch (statementType) {
                case "selection":
                    if(node.children.get(0).id.equals("if")) {
                        return new SelectionStatement.IfStatement(node);
                    } else if(node.children.get(0).id.equals("switch")) {
                        return new SelectionStatement.SwitchStatement(node);
                    }
                case "expression":
                    return new ExpressionStatement(node);
                case "return":
                    return new ReturnStatement(node);
                case "compound":
                    return new CompoundStatement(node);
            }
            if(node.id.contains("Expression") || node.id.contains("expression")) {
                return new ExpressionStatement(node);
            }
            return new Statement(node);
        }

    }

    public static class Expression extends ASTNode {
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
            //sb.append(getASTString(node.children.get(i), ++indentLevel));
            sb.append(node.children.get(i).printNode(indentLevel));
        }

        return sb.toString();
    }

    private static String lead(int level) {
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


