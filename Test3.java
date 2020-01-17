/* Program uses regular expressions to tokenize user input file. Reads in file and prints it onto the
command line window.
Currently not working:
        Exception in thread "main" java.lang.NullPointerException
*/

import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Test3
{
    private static String line = "";
    private static String source_code = "";

    public static void main(String[] args)
    {
       Scanner in = new Scanner(System.in);
       System.out.print("Enter a filename ");
       String filename = in.nextLine();
       System.out.println("Opening " + filename);

       //just finding out where to put example source file
       System.out.println(System.getProperty("user.dir"));

       try
       {
           FileReader fileReader = new FileReader(filename);
           BufferedReader bufferedReader = new BufferedReader(fileReader);

           while((line = bufferedReader.readLine()) != null)
           {
               source_code += line;
               System.out.println(line);
           }
           bufferedReader.close();
       }
       catch(FileNotFoundException ex)
       {
           System.out.println(
               "Unable to open file '" +
               filename + "'");
       }
       catch(IOException ex)
       {
           System.out.println(
               "Error reading file '"
               + filename + "'");
       }

        Pattern pattern = Pattern.compile("[\\w]+|[\\W]|.+"); // Splits by words,characters
        Matcher matcher = pattern.matcher(source_code);

        while (matcher.find()) {
            System.out.println(matcher.group(0));
        }

   }

}
