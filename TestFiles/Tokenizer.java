/* Program takes input string and uses the StringTokenizer to split it
into separate words */

import java.util.*;

public class Tokenizer {
   public static void main(String[] args) {

      System.out.print("Enter a string: ");

      Scanner sc = new Scanner(System.in);
      String str = sc.nextLine();

      StringTokenizer st = new StringTokenizer(str);
      while (st.hasMoreElements()) {
         System.out.println(st.nextElement());
      }

      sc.close();
   }
}
