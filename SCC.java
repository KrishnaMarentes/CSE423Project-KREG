import org.antlr.v4.runtime.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class SCC {
    public static void main(String[] args) {
        String filename = null;
        CharStream src_code = null;
        char opt;
        int i = 0;
        boolean print_tks = false;
        boolean print_pt = false;
        boolean print_ast = false;
        boolean save_output = false;

        if(args.length > 0) {
            try {
                /*
                Command line arguments for file name, parse tree, or token list
                 */
                if (args[0].charAt(0) == '-') {
                    for (i = 1; i < args[0].length(); i++) {
                        opt = args[0].charAt(i);
                        switch (opt) {
                            case 'a':
                                print_ast = true;
                                break;
                            case 't':
                                print_tks = true;
                                break;
                            case 'p':
                                print_pt = true;
                                break;
                            case 's':
                                save_output = true;
                                break;
                            default:
                                System.out.println("Entered a unrecognized option.");
                                usage();
                                System.exit(1);
                                break;
                        }
                    }
                }

                filename = args[args.length-1];
                src_code = CharStreams.fromFileName(filename);
            } catch (IOException e) {
                e.printStackTrace();
                usage();
                System.exit(1);
            }
        } else {
            usage();
            System.exit(1);
        }

        kregGrammarLexer lexer = new kregGrammarLexer(src_code);
        kregGrammarParser parser = new kregGrammarParser(new CommonTokenStream(lexer));
        parser.setBuildParseTree(true);

        RuleContext tree = parser.program();

        if (save_output) {
            // Destroy output file if it already exists
            File out = new File(filename + ".out");
            if (out.exists())
                out.delete();
            // Create the output file so that output can be appended to a clean file
            try {
                out.createNewFile();
            } catch (IOException e) {
                System.out.println("Error in creating file " + filename + ".out" +
                        "\nThe output will not be saved.");
                save_output = false;
            }
        }


        //TODO: Allow to just save to file without having to also print to terminal
        if (print_tks) {
            System.out.println("printing tokens...");
            if (save_output)
                printTokens(parser.getTokenStream(), lexer.getRuleNames(), filename);
            else
                printTokens(parser.getTokenStream(), lexer.getRuleNames(), null);
        }
        if (print_pt) {
            System.out.println("printing parse tree...");
            if (save_output)
                printParseTree(tree, parser.getRuleNames(), filename);
            else
                printParseTree(tree, parser.getRuleNames(), null);
        }
        if (print_ast) {
            //TODO: Print AST from the AST structure itself, not from rulenames list
            System.out.println("printing abstract syntax tree...");
            if (save_output)
                printAST(tree, parser.getRuleNames(), filename);
            else
                printAST(tree, parser.getRuleNames(), null);
        }

        System.out.println("done!");

        /* List of symbol tables, one for each scope. The first is the global table */
        SymbolTable global = new SymbolTable("global", null);

        // TODO: Make a class/function that walks an AST to populate/add symbol tables
        // TODO: Add error messages when invalid declarations are made
    }

    private static void printAST(RuleContext rc, String[] ruleNames, String filename) {
        List<String> ruleNamesList = Arrays.asList(ruleNames);
        ASTNode an = TreeUtils.generateAST(rc, ruleNamesList);

        System.out.println(generateIR(an)); /// REMOOOOOVE

        String prettyAST = ASTNode.toPrettyASTString(an);
        System.out.println(prettyAST);
        if (filename != null) {
            try {
                FileWriter f = new FileWriter(filename + ".out", true);
                BufferedWriter b = new BufferedWriter(f);
                b.write(prettyAST + "\n\n");
                b.close();
                f.close();
            } catch (IOException e) {
                System.out.println("An error occurred when attempting to save the output to a file");
            }
        }
    }

    private static String generateIR(ASTNode node) {
        return node.generateCode();
    }

    private static void printParseTree(RuleContext rc, String[] ruleNames, String filename) {
        List<String> ruleNamesList = Arrays.asList(ruleNames);
        String prettyTree = TreeUtils.toPrettyTree(rc, ruleNamesList);
        System.out.println(prettyTree);
        if (filename != null) {
            try {
                FileWriter f = new FileWriter(filename + ".out", true);
                BufferedWriter b = new BufferedWriter(f);
                b.write(prettyTree.toString() + "\n\n");
                b.close();
                f.close();
            } catch (IOException e) {
                System.out.println("An error occurred when attempting to save the output to a file");
            }
        }
    }

    private static void printTokens(TokenStream ts, String[] ruleNames, String filename) {
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < ts.size() - 1; i++) {
            String t = ts.get(i).getText();
            int line = ts.get(i).getLine(); //get line that token is on
            String type = ruleNames[ts.get(i).getType() - 1]; //type of token, integer only
            output.append("< ").append(t).append(" , ").append(type).append(" >");
        }
        System.out.println(output);
        if (filename != null) {
            try {
                FileWriter f = new FileWriter(filename + ".out", true);
                BufferedWriter b = new BufferedWriter(f);
                b.write(output.toString() + "\n\n");
                b.close();
                f.close();
            } catch (IOException e) {
                System.out.println("An error occurred when attempting to save the output to a file");
            }
        }
    }

    private static void usage() {
        System.out.println("usage: java [OPTS] FILENAME");
        System.out.println("OPTS: [t, p, a, s]");
        System.out.println("t: Print the tokens");
        System.out.println("p: Print the parse tree");
        System.out.println("a: Print the abstract syntax tree");
        System.out.println("s: Save all printing to a file named 'FILENAME.out'");
        System.out.println("FILENAME: file path");
    }
}
