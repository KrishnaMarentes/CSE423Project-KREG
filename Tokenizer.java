/*
    Program takes in a file to tokenize as a command line argument.
    Prints the tokenized file to standard out.

    The Tokenizer class can also be used statically.

    TODO: Finish usage function
    TODO: take in command line arguments
    TODO: Maybe output to file?
    TODO(arg 1): Output sequence of tokens and labels
    TODO(arg 2): Output the parse tree
    TODO: Maybe don't put the main function here, consider packaging compiler classes
*/


import java.io.*;
import java.util.*;
import java.util.regex.*;

import static javafx.application.Platform.exit;

public class Tokenizer
{

    public static void main(String[] args) {
        //just finding out where to put example source file
        System.out.println(System.getProperty("user.dir"));

        String filename = "";

        if(args.length > 0) {
            filename = args[0];
        } else {
            usage();
            exit();
        }

        ArrayList<String> tokens = TokenizeFile(filename);

        for(String s : tokens) {
            System.out.println(s);
        }

    }

    //tokenize the file
    public static ArrayList<String> TokenizeFile(String file)
    {
        ArrayList<String> tokens = new ArrayList<>();
        String line;
        String source = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            //probably the least efficient way of doing this.
            //should work as long as the file is less than 2GB ¯\_(ツ)_/¯
            while((line = br.readLine()) != null) {
                source += line + '\n';
            }
            source = remove_comments(source);
        } catch (FileNotFoundException e) {
            System.out.println(
            "Unable to open file '" +
            file + "'");
        } catch (IOException e) {
            System.out.println(
            "Error reading file '" +
            file + "'");
        }

        Pattern pattern = Pattern.compile("#.+>|>>=?|<<=?|\\|\\||&&|[|\\^&%<>!\\-+*\\/=]?=|--|\\+\\+|\".+\"|[\\w]+|[\\W]|.+");
        Matcher matcher = pattern.matcher(source);

        while (matcher.find()) {
            String match = matcher.group(0);
            //This conditional might be necessary to remove later. Might generate bugs.
            match = match.replaceAll("\\s|\n|\t", "");
            if(match.length() > 0) {
                tokens.add(matcher.group(0));
            }
        }

        return tokens;
    }

    /*
        Removes comments from file and places uncommented source code into a String.
    */
    private static String remove_comments(String source)
    {
        return(source.replaceAll("\\/\\/.+\\n|(?s)\\/\\*.*?\\*\\/", ""));
    }

    /*
        Prints usage information
    */
    private static void usage()
    {
        System.out.println("usage: java " + Tokenizer.class.getName() + " [filename]");
        System.out.println("    -t    print tokens and labels");
    }

}
