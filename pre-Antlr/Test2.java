/* Program uses regular expressions to tokenize user input file. */
/* Currently not working:
        error: incompatible types: Scanner cannot be converted to CharSequence
        Matcher matcher = pattern.matcher(scan);
*/

import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Test2 {
    public static void main(String[] args) throws IOException {

        Scanner user = new Scanner(System.in);
        String inputFileName;

        System.out.print("Enter Input File Name - add file extension: ");
        inputFileName = user.nextLine().trim();
        File input = new File(inputFileName);

        Scanner scan = new Scanner(input);

        Pattern pattern = Pattern.compile("[\\w]+|[\\W]|.+"); // Splits by words,characters
        Matcher matcher = pattern.matcher(scan);

        while (matcher.find()) {
            System.out.println(matcher.group(0));
        }
    }
}
