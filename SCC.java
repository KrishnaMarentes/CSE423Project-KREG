import org.antlr.v4.runtime.*;

import java.io.*;
import java.util.*;

public class SCC {


    public static final String EOL = System.lineSeparator();
    public static SymbolTable symbolTable;
    public static StringBuilder input_ir = new StringBuilder();
    public static String asm_name = null;

    public static void main(String[] args) {
        String filename = null; // Input, usually C source code
        String write_filename = null;
        String read_filename = null;
        String output = null;

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
        boolean print_asm = false;
        boolean optimize = false;

        ArrayList<String> IRdata = new ArrayList<>();

        /* Three ways to use arguments (filenames are just examples)
        * 1. ./SCC -[opts] source.c
        *       Does whatever opts say to source.c -S will save to source.asm
        * 2. ./SCC -[ops w] source.c output.ir
        *       Does whatever opts say to source.c, save IR to output.ir
        *       If output.ir not given, save to source.ir
        * 3. ./SCC -[opts r] output.ir
        *       Does whatever applicable opts to given IR. -S saves to output.asm
        * This means that the file to read from will always be args[1]. If w is
        * used, then that will be in args[2].
        * */
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
                            case 'w': /* write out IR to a file */
                                writefile = true;
                                // File name not specified
                                if (args.length < 3) {
                                    write_filename = args[1];
                                    write_filename = write_filename.replace(".c", ".ir");
                                } else {
                                    write_filename = args[2];
                                }
                                break;
                            case 'r': /* read in an IR specified instead of a source file */
                                readfile = true;
                                if (args.length < 2) {
                                    System.out.println("Option -r used but no source file given.");
                                    usage();
                                    System.exit(1);
                                }
                                read_filename = args[1];
                                /* Adding input IR into an array list */
                                try (BufferedReader br = new BufferedReader(new FileReader(read_filename))) {
                                    while (br.ready()){
                                        IRdata.add(br.readLine());
                                    }
                                }
                                break;
                            case 's': /* print symbol table */
                                print_st = true;
                                break;
                            case 'S': /* Generate assembly, save in given filename*/
                                print_asm = true;
                                save_output = true;
                                //readfile = true;
                                break;
                            case 'O': /* carry out all optimizations */
                                optimize = true;
                                break;
                            default:
                                System.out.println("Entered a unrecognized option.");
                                usage();
                                System.exit(1);
                                break;
                        }
                    }
                }
                if (args.length < 2) {
                    System.out.println("No source file given.");
                    usage();
                    System.exit(1);
                }
                filename = args[1];
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

        /* Check for bad option combinations */
        if (readfile) {
            if (print_tks || print_pt || print_ast || print_st) {
                System.out.println("Bad option combination. Cannot perform requested" +
                        " functions without C source code.");
                print_tks = false;
                print_pt = false;
                print_ast = false;
                print_st = false;
            }
        }

        if (save_output) {
            if (readfile) {
                output = read_filename.replace(".ir", ".s");
            } else {
                output = filename.replace(".c", ".s");
            }
        }

        kregGrammarLexer lexer = null;
        kregGrammarParser parser = null;
        RuleContext tree = null;
        List<String> ruleNamesList;
        ASTNode an;
        String ir;
        if (!readfile) {
            lexer = new kregGrammarLexer(src_code);
            parser = new kregGrammarParser(new CommonTokenStream(lexer));
            parser.setBuildParseTree(true);

            tree = parser.program();
            symbolTable = SymbolTable.populate(tree, parser.getRuleNames());

            ruleNamesList = Arrays.asList(parser.getRuleNames());
            an = TreeUtils.generateAST(tree, ruleNamesList);
            ir = generateIR(an);
        } else {
            ir = IRdata.toString();
        }

        input_ir = new StringBuilder(ir);

        if (optimize) {
             Optimizer op = new Optimizer();
             ir = op.optimizeIR(ir);
             System.out.println("Optimized IR:");
             System.out.println(ir);
        }

        /* should never be true with a null lexer*/
        if (print_tks) {
            System.out.println("printing tokens...");
            printTokens(parser.getTokenStream(), lexer.getRuleNames());
        }

        /* should never be true with a null parser */
        if (print_pt) {
            System.out.println("printing parse tree...");
            printParseTree(tree, parser.getRuleNames());
        }

        /* should never be true with a null tree*/
        if (print_ast) {
            System.out.println("printing abstract syntax tree...");
            printAST(tree, parser.getRuleNames());
        }

        /* should never be true with a null symbolTable */
        if (print_st) {
            System.out.println("printing Symbol Table...");
            SymbolTable.printSymbolTable(symbolTable);
        }

        if (print_ir) {
            System.out.println("printing IR...");
            if (writefile)
                printIR(ir, write_filename);
            else
                printIR(ir,null);
        }

        if(print_asm) {
            printASM(ir, output);
        }

        System.out.println("done!");
    }

    private static void printASM(String ir, String filename) {

        //ASM asmCode = new ASM(ir);
        ASM asmCode = new ASM(input_ir.toString());

        try {
            //File input = new File(filename);
            filename = filename.replace(".ir", ".s");
            FileWriter f = new FileWriter(filename);
            BufferedWriter b = new BufferedWriter(f);
            b.write(asmCode.getASMString());
            b.close();
            f.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error: file " + filename + " not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filename);
            e.printStackTrace();
        }
    }

    private static void printIR(String ir, String write_filename) {
        String write_file = "src/tests/" + write_filename; /* redirecting to appropriate folder */

        if (write_filename != null) {
            try {
                FileWriter f = new FileWriter(write_file);
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

    private static void printAST(RuleContext rc, String[] ruleNames) {
        List<String> ruleNamesList = Arrays.asList(ruleNames);
        ASTNode an = TreeUtils.generateAST(rc, ruleNamesList);

        String prettyAST = ASTNode.toPrettyASTString(an);
        System.out.println(prettyAST);
    }

    private static String generateIR(ASTNode node) {
        return node.generateCode();
    }

    private static void printParseTree(RuleContext rc, String[] ruleNames) {
        List<String> ruleNamesList = Arrays.asList(ruleNames);
        String prettyTree = TreeUtils.toPrettyTree(rc, ruleNamesList);
        System.out.println(prettyTree);
    }

    private static void printTokens(TokenStream ts, String[] ruleNames) {
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < ts.size() - 1; i++) {
            String t = ts.get(i).getText();
            int line = ts.get(i).getLine(); //get line that token is on
            String type = ruleNames[ts.get(i).getType() - 1]; //type of token, integer only
            output.append("< ").append(t).append(" , ").append(type).append(" >");
        }
        System.out.println(output);
    }

    private static void usage() {
        System.out.println("usage: java [OPTS] FILENAME");
        System.out.println("OPTS: [t, p, a, i, w, r, s, o, O, S]");
        System.out.println("t: Print the tokens");
        System.out.println("p: Print the parse tree");
        System.out.println("a: Print the abstract syntax tree");
        System.out.println("i: Print the intermediate representation");
        System.out.println("w: Write IR out to a file with a specified name: -w specified_name FILENAME");
        System.out.println("r: Read in an IR specified instead of a source file");
        System.out.println("s: Print the symbol table");
        System.out.println("o: Save all printing to a file named 'FILENAME.out'");
        System.out.println("O: Perform optimizations");
        System.out.println("S: Generate assembly code. MUST be used with -rS (Needs input IR file)");
        System.out.println("FILENAME: file path");
    }
}
