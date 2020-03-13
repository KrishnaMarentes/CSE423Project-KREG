import javafx.util.Pair;

import java.util.ArrayList;

public class Statement extends ASTNode {
    public Statement(ASTNode node) {
        super(node.id);
        this.children = node.children;
    }

    public static class IterationStatement extends Statement {
        public IterationStatement(ASTNode node) {
            super(node);
        }

        public static class WhileStatement extends IterationStatement {
            public Expression bool_expression;
            public Statement statement;
            public WhileStatement(ASTNode node) {
                super(node);
                this.id = "WhileStatement";
                bool_expression = Expression.ExpressionResolver(node.children.get(2));
                statement = Statement.StatementResolver(node.children.get(4));
            }

            public String printNode(int indentLevel) {
                StringBuilder sb = new StringBuilder();

                sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
                ++indentLevel;
                sb.append(bool_expression.printNode(indentLevel));
                sb.append(statement.printNode(indentLevel));

                return sb.toString();
            }
        }
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

        public String generateCode() {
            StringBuilder sb = new StringBuilder();
            sb.append("{" + EOL);

            for(int i = 0; i < this.children.size(); i++) {
                if(this.children.get(i) == null) continue;
                sb.append(this.children.get(i).generateCode());
            }

            sb.append("}");
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

        public String generateCode() {
            StringBuilder sb = new StringBuilder();
            sb.append("return ");
            sb.append(";" + EOL);
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

    public static class BreakStatement extends Statement {
        public BreakStatement(ASTNode node) {
            super(node);
            this.id = "BreakStatement";
        }

        public String printNode(int indentLevel) {
            return ASTNode.lead(indentLevel) + this.id + EOL;
        }
    }

    public static class LabelStatement extends Statement {
        public String tagName;
        public LabelStatement(ASTNode node) {
            super(node);
            this.id = "LabelStatement";
            tagName = node.children.get(0).children.get(0).id;
        }

        public String printNode(int indentLevel) {
            StringBuilder sb = new StringBuilder();

            sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
            ++indentLevel;
            sb.append(ASTNode.lead(indentLevel) + tagName + EOL);

            return sb.toString();
        }
    }

    public static class GotoStatement extends Statement {
        public String tagName;
        public GotoStatement(ASTNode node) {
            super(node);
            this.id = "GotoStatement";
            tagName = node.children.get(1).children.get(0).id;
        }

        public String printNode(int indentLevel) {
            StringBuilder sb = new StringBuilder();

            sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
            ++indentLevel;
            sb.append(ASTNode.lead(indentLevel) + tagName + EOL);

            return sb.toString();
        }
    }

    public static Statement StatementResolver(ASTNode node) {
        if(node == null) return null;
        String statementType = node.id.replaceAll("Stmt", "");
        switch (statementType) {
            case "iteration":
                if(node.children.get(0).id.equals("while")) {
                    return new IterationStatement.WhileStatement(node);
                }
            case "selection":
                if(node.children.get(0).id.equals("if")) {
                    return new SelectionStatement.IfStatement(node);
                } else if(node.children.get(0).id.equals("switch")) {
                    return new SelectionStatement.SwitchStatement(node);
                }
            case "label":
                return new LabelStatement(node);
            case "goto":
                return new GotoStatement(node);
            case "break":
                return new BreakStatement(node);
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