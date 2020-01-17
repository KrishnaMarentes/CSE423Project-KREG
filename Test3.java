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
    private static String line;

    public static void main(String[] args)
    {
       Scanner in = new Scanner(System.in);
       System.out.print("Enter a filename ");
       String filename = in.nextLine();
       System.out.println("Opening " + filename);

       try
       {
           FileReader fileReader = new FileReader(filename);
           BufferedReader bufferedReader = new BufferedReader(fileReader);

           while((line = bufferedReader.readLine()) != null)
           {
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
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            System.out.println(matcher.group(0));
        }

   }

}
