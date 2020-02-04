import org.antlr.v4.runtime.*;
import java.util.List;
import java.util.Arrays;

public class SCC {
    public static void main(String[] args) {
        kregGrammarLexer lexer = new kregGrammarLexer(CharStreams.fromString("1+2+500"));
        kregGrammarParser parser = new kregGrammarParser(new CommonTokenStream(lexer));
        parser.setBuildParseTree(true);

        RuleContext tree = parser.start();

        //print tokens with this code
//        TokenStream ts = parser.getTokenStream();
//
//        for(int i = 0; i < ts.size(); i++) {
//            String t = ts.get(i).getText();
//            System.out.println(t);
//        }

        List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
        String prettyTree = TreeUtils.toPrettyTree(tree, ruleNamesList);

        System.out.println(prettyTree);

        System.out.println("done!");
    }
}
