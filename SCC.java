import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import static javafx.application.Platform.exit;

public class SCC {
    public static void main(String[] args) {
        //kregGrammarLexer lexer = new kregGrammarLexer(CharStreams.fromString("1+2+500"));

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

        RuleContext tree = parser.start();

        //print token text only with this code
        TokenStream ts = parser.getTokenStream();

//        for(int i = 0; i < ts.size(); i++) {
//            String t = ts.get(i).getText();
//            int line = ts.get(i).getLine(); //get line that token is on
//            int type = ts.get(i).getType(); //type of token, integer only
//            //might be better to print out token type how TreeUtils class does it
//            //OR, we make our own array of strings that is mapped by the 'type' integer
//            //First option probably better, but it means we have to make our .g4 file a certain way
//            System.out.println(type + " " + t + " " + line);
//        }


        List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
        String prettyTree = TreeUtils.toPrettyTree(tree, ruleNamesList);

        System.out.println(prettyTree);

        System.out.println("done!");
    }

    //TODO: Finish this
    private static void usage() {
        System.out.println("usage: java .... ");
    }
}
