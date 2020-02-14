import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import static javafx.application.Platform.exit;

public class SCC {
    public static void main(String[] args) {
        CharStream filename = null;
        char opt;
        int i = 0;
        boolean print_tks = false;
        boolean print_pt = false;

        if(args.length > 0) {
            try {
                /*
                Command line arguments for file name, parse tree, or token list
                 */
                if (args[0].charAt(0) == '-') {
                    for (i = 1; i < args[0].length(); i++) {
                        opt = args[0].charAt(i);
                        switch (opt) {
                            case 't':
                                print_tks = true;
                                break;
                            case 'p':
                                print_pt = true;
                                break;
                            default:
                                System.out.println("Entered a unrecognized option.");
                                usage();
                                System.exit(1);
                                break;
                        }
                    }
                }

                filename = CharStreams.fromFileName(args[args.length - 1]);
            } catch (IOException e) {
                e.printStackTrace();
                usage();
                System.exit(1);
            }
        } else {
            usage();
            System.exit(1);
        }

        kregGrammarLexer lexer = new kregGrammarLexer(filename);
        kregGrammarParser parser = new kregGrammarParser(new CommonTokenStream(lexer));
        parser.setBuildParseTree(true);

        RuleContext tree = parser.program();

        if (print_tks) {
            System.out.println("printing tokens...");
            printTokens(parser.getTokenStream(), lexer.getRuleNames());
        }
        if (print_pt) {
            System.out.println("printing parse tree...");
            printParseTree(tree, parser.getRuleNames());
        }


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
        System.out.println("usage: java [OPTS] FILENAME");
        System.out.println("OPTS: [t, p]");
        System.out.println("t: Print the tokens");
        System.out.println("p: Print the parse tree");
        System.out.println("FILENAME: file path");
    }
}
