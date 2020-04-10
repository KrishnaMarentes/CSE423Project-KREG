import java.util.Arrays;
import java.util.List;

public class ASM {
    static final String EOL = System.lineSeparator();
    String[] ir_lines;
    private static final List<String> ignore_list = Arrays.asList("{", "}", EOL);
    private static final List<String> type_list = Arrays.asList("int");

    public ASM(String ir) {
        ir_lines = ir.split(EOL);
    }

    public String getASMString() {
        StringBuilder asmString = new StringBuilder();

        for(int i = 0; i < ir_lines.length; i++) {
            String word = ir_lines[i].split(" ")[0];
            if(word.equals("function")) {

            } else if(type_list.contains(word)) {
                //it's a var declaration
            } else if(ignore_list.contains(word)) {
                //ignore?
            } else if(word.equals("return")) {
                //ret eax
            }
        }

        return asmString.toString();
    }

}
