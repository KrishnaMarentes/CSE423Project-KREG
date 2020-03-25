import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
            public static int whileId = 0; // For naming labels made for while blocks
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

            /* Loop conditionals are at the end of the control block to
            * avoid needing an "else" in the IR. Example:
            *   goto label1;
            *   label2:
            *       // loop body
            *   label1:
            *       // if (conditional) goto label2;
            */
            public String generateCode() {
                StringBuilder sb = new StringBuilder();

                /* First make a label for the conditional */
                String condTagName= this.children.get(0).id + this.whileId + "cond";
                LabelStatement.labels.put(condTagName, Statement.tagCreator());
                sb.append("goto " + LabelStatement.labels.get(condTagName) + ";" + EOL);

                /* Now label for the body */
                String bodyTagName = this.children.get(0).id + this.whileId++ + "body";
                LabelStatement.labels.put(bodyTagName, Statement.tagCreator());
                sb.append(LabelStatement.labels.get(bodyTagName) + ":" + EOL);

                /* Now the body code */
                sb.append(this.statement.generateCode());

                /* Finally, add "if" IR line, e.g.
                *  "if i < 10 goto label 2 */
                sb.append(LabelStatement.labels.get(condTagName) + ":" + EOL);
                String conditional = this.bool_expression.printExpression();
                sb.append("if " + conditional + " goto " + LabelStatement.labels.get(bodyTagName) + ";" + EOL);
                sb.append(EOL); // Extra space for human readability
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

            //just print variable declarations
            for(int i = 0; i < this.children.size(); i++) {
                if(this.children.get(i) == null ||
                        !(this.children.get(i) instanceof VarDeclaration)) continue;
                sb.append(this.children.get(i).generateCode());
            }

            if(sb.length() > ("{"+EOL).length()) {
                sb.append(EOL);
            }

            for(int i = 0; i < this.children.size(); i++) {
                if(this.children.get(i) == null) continue;
                //this takes care of VarDeclaration assignments
                if(this.children.get(i) instanceof VarDeclaration) {
                    VarDeclaration v = ((VarDeclaration) this.children.get(i));
                    for(int j = 0; j < v.vars.size(); j++) {
                        if(v.vars.get(j).getValue() == null) continue;
                        String expression = v.vars.get(j).getValue().generateCode();

                        /* Either we need to print all the tmpVars generated in the
                        * expression, then assign the variable name to the last tmpVar..*/
                        if (expression.contains("KREG")) { // hacky using string literal but whatever
                            sb.append(expression);
                            String lastVar = Expression.getLastAssignedVar(expression);
                            sb.append(v.vars.get(j).getKey() + " = " + lastVar + ";" + EOL);
                        } else { /* Or just print the variable and its original value */
                            sb.append(v.vars.get(j).getKey() + " = " +
                                    v.vars.get(j).getValue().id + ";" + EOL);
                        }
                    }
                } else {
                    sb.append(this.children.get(i).generateCode());
                }
            }

            sb.append("}" + EOL);
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
            String expression = this.children.get(0).generateCode();
            String lastVar;

            // if no tmpvar was made in last expr, just take the first term
            if (!expression.contains("KREG")) {
                lastVar = expression.split(" ")[0];
            } else {
                lastVar = Expression.getLastAssignedVar(expression);
                sb.append(expression);
            }
            sb.append("return " + lastVar + ";" + EOL);

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
            // Map of children of an IfStatement:
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

            public String generateCode() {
                StringBuilder sb = new StringBuilder();
                ArrayList<String> tagList = new ArrayList<>();

                tagList.add(tagCreator()); //beginning tag
                tagList.add(tagCreator()); //end tag

                for(int i = 1; i < elseIfList.size(); i++) {
                    tagList.add(i, tagCreator());
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

        public String generateCode() {
            StringBuilder sb = new StringBuilder();
            
            return sb.toString();
        }
    }

    public static class LabelStatement extends Statement {
        public String tagName;

        //<label_name, generated_id (KREG.####)>
        public static Map<String, String> labels = new HashMap<>();
        public LabelStatement(ASTNode node) {
            super(node);
            this.id = "LabelStatement";
            tagName = node.children.get(0).children.get(0).id;
            labels.put(tagName, Statement.tagCreator());
        }

        public String printNode(int indentLevel) {
            StringBuilder sb = new StringBuilder();

            sb.append(ASTNode.lead(indentLevel) + this.id + EOL);
            ++indentLevel;
            sb.append(ASTNode.lead(indentLevel) + tagName + EOL);

            return sb.toString();
        }

        /* Author: Geoff */
        public String generateCode() {
            return labels.get(tagName) + ":" + EOL;
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

        /* Author: Geoff */
        public String generateCode() {
            return "goto " + LabelStatement.labels.get(tagName) + ";" + EOL;
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

    public static String tagCreator() {
        return "<" + Expression.tmpVar + Expression.globalCounter++ + ">";
    }

}