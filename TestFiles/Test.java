/* Program uses regular expressions to tokenize input string. */

import java.util.*;
import java.util.regex.*;

public class Test {
    public static void main(String...args) {

        System.out.print("Enter a string: ");

        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();

        Pattern pattern = Pattern.compile("[\\w]+|[\\W]|.+"); // Splits by words,characters
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            System.out.println(matcher.group(0));
        }
    }
}
