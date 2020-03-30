import org.antlr.v4.runtime.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

public class SCC {
    public static void main(String[] args) {
        String filename = null;
        String write_filename = null;
        String read_filename = null;

        CharStream src_code = null;
        char opt;
        int i = 0;
        boolean print_tks = false;
        boolean print_pt = false;
        boolean print_ast = false;
        boolean print_ir = false;
        boolean writefile = false;
        boolean readfile = false;
        boolean print_st = false;
        boolean save_output = false;

        Scanner in = new Scanner(System.in);

        if(args.length > 0) {
            try {
                /* Command line arguments for file name */
                if (args[0].charAt(0) == '-') {
                    for (i = 1; i < args[0].length(); i++) {
                        opt = args[0].charAt(i);
                        switch (opt) {
                            case 'a': /* print ast */
                                print_ast = true;
                                break;
                            case 't': /* print tokens */
                                print_tks = true;
                                break;
                            case 'p': /* print parse tree */
                                print_pt = true;
                                break;
                            case 'i': /* print intermediate representation */
                                print_ir = true;
                                break;
                            case 'w': /* write out IR to a file with a specified name */
                                /* output filename will look: write_filename.out */
                                writefile = true;
                                System.out.println("Enter the name of the file to write out to: ");
                                write_filename = in.nextLine();
                                print_ir = true;
                                break;
                            case 'r': /* read in an IR specified instead of a source file */
                                /* will read in any type of file and then output it to the terminal for now */
                                /* ex of read_filename: src/tests/ex9.c or src/tests/ir_example.ir or src/tests/ir_example2.ast */
                                readfile = true; /* might need for later */
                                System.out.println("Enter the name of the IR file to read in: ");
                                read_filename = in.nextLine();
                                try {
                                    File input = new File(read_filename);
                                    Scanner readF = new Scanner(input);
                                    while (readF.hasNextLine()) {
                                        String IRdata = readF.nextLine();
                                        System.out.println(IRdata);
                                    }
                                    readF.close();
                                } catch (FileNotFoundException e) {
                                    System.out.println("Error: file " + read_filename + " not found.");
                                }
                                break;
                            case 's': /* print symbol table */
                                print_st = true;
                                break;
                            case 'o': /* replacing 's' with 'o' */
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
        SymbolTable st = SymbolTable.populate(tree, parser.getRuleNames());

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
            System.out.println("printing abstract syntax tree...");
            if (save_output)
                printAST(tree, parser.getRuleNames(), filename);
            else
                printAST(tree, parser.getRuleNames(), null);
        }
        if (print_ir) {
            System.out.println("printing IR...");
            if (writefile)
                printIR(tree, parser.getRuleNames(), write_filename);
            else
                printIR(tree, parser.getRuleNames(), null);
        }
        if (print_st) {
            System.out.println("printing Symbol Table...");
            SymbolTable.printSymbolTable(st);
        }

        System.out.println("done!");
        // TODO: Add error messages when invalid declarations are made
    }

    private static void printIR(RuleContext rc, String[] ruleNames, String write_filename) {
        List<String> ruleNamesList = Arrays.asList(ruleNames);
        ASTNode an = TreeUtils.generateAST(rc, ruleNamesList);

        String ir = generateIR(an);
        String write_file = "src/tests/" + write_filename; /* redirecting to appropriate folder */

        if (write_filename != null) {
            try {
                FileWriter f = new FileWriter(write_file + ".out");
                BufferedWriter b = new BufferedWriter(f);
                b.write(ir + "\n\n");
                b.close();
                f.close();
            } catch (IOException e) {
                System.out.println("An error occurred when attempting to save the output with the specified filename");
            }
        } else
            System.out.println(ir);
    }

    private static void printAST(RuleContext rc, String[] ruleNames, String filename) {
        List<String> ruleNamesList = Arrays.asList(ruleNames);
        ASTNode an = TreeUtils.generateAST(rc, ruleNamesList);

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
        System.out.println("OPTS: [t, p, a, i, w, r, s, o]");
        System.out.println("t: Print the tokens");
        System.out.println("p: Print the parse tree");
        System.out.println("a: Print the abstract syntax tree");
        System.out.println("i: Print the intermediate representation");
        System.out.println("w: Write IR out to a file with a specified name");
        System.out.println("r: Read in an IR specified instead of a source file");
        System.out.println("s: Print the symbol table");
        System.out.println("o: Save all printing to a file named 'FILENAME.out'");
        System.out.println("FILENAME: file path");
    }
}
