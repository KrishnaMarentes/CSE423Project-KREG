import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ASM {
    /*
    32 bit registers    16 bit
    EAX                 AX         Accumulator reg: arithmatic, logic, data transfer. One number must be AX
    EBX                 BX         Base reg: holds address of base storage where data stored
    ECX                 CX         Count reg: loop counter
    EDX                 DX         Data reg: I/O operations and large values

    ESI     Source Index: point to memeory locations in data segment
    EDI     Destination Index: string operations for memeory access locations

    Pointer registers
    ESP     Stack pointer: top address of stack
    EBP     Base pointer: base address of stack
    IP      Intruction pointer: offset address of next instruction to be executed
    ------------------------------

     */

    static final String EOL = System.lineSeparator();
    ArrayList<String> ir_lines = null;
    private static final List<String> ignore_list = Arrays.asList("{", "}", EOL);
    private static final List<String> type_list = Arrays.asList("int");

    public ASM(String ir) {

        String[] lines = ir.split(EOL);
        ir_lines = new ArrayList(Arrays.asList(lines));
        ir_lines.removeAll(Collections.singleton(""));
    }

    public ASM(ArrayList<String> ir) {
        ir_lines = ir;
        ir_lines.removeAll(Collections.singleton(""));
    }

    public String getASMString() {
        StringBuilder asmString = new StringBuilder();
        /*
        Declaration for linker
         */
        asmString.append("section .text\n");
        asmString.append("\tglobal _start\n");
        asmString.append("_start:\n");

        for(String line : ir_lines) {
            String word = line.split(" ")[0];
            if(word.equals("function")) {
                System.out.println("FUNCTION");
            } else if(type_list.contains(word)) {
                //it's a var declaration
                System.out.println("VAR");
            } else if(ignore_list.contains(word)) {
                //ignore?
            } else if(word.equals("return")) {
                //ret eax
            }
        }
        return asmString.toString();
    }

}
