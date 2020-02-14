import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import static javafx.application.Platform.exit;

public class SCC {
    public static void main(String[] args) {
        CharStream cs = null;
        if(args.length > 0) {
            try {
                cs = CharStreams.fromFileName(args[0]);
            } catch (IOException e) {
                e.printStackTrace();
                usage();
                exit();
            }
        } else {
            usage();
            exit();
        }

        kregGrammarLexer lexer = new kregGrammarLexer(cs);
        kregGrammarParser parser = new kregGrammarParser(new CommonTokenStream(lexer));
        parser.setBuildParseTree(true);

        RuleContext tree = parser.program();


        //printParseTree(tree, parser.getRuleNames());
        //printTokens(parser.getTokenStream(), lexer.getRuleNames());

        System.out.println("done!");
    }

    private static void printParseTree(RuleContext rc, String[] ruleNames) {
        List<String> ruleNamesList = Arrays.asList(ruleNames);
        String prettyTree = TreeUtils.toPrettyTree(rc, ruleNamesList);

        System.out.println(prettyTree);
    }

    private static void printTokens(TokenStream ts, String[] ruleNames) {
        for(int i = 0; i < ts.size() - 1; i++) {
            String t = ts.get(i).getText();
            int line = ts.get(i).getLine(); //get line that token is on
            String type = ruleNames[ts.get(i).getType() - 1]; //type of token, integer only

            System.out.println("< " + t + " , " + type + " >");
        }
    }

    //TODO: Finish this
    private static void usage() {
        System.out.println("usage: java .... ");
    }
}
