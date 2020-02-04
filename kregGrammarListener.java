// Generated from C:/Users/ryan1/Desktop/programming_stuff/compiler/src\kregGrammar.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link kregGrammarParser}.
 */
public interface kregGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link kregGrammarParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(kregGrammarParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link kregGrammarParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(kregGrammarParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link kregGrammarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(kregGrammarParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link kregGrammarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(kregGrammarParser.ExpressionContext ctx);
}