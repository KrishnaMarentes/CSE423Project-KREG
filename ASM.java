import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ASM {
    /*
    32 bit registers    16 bit
    EAX                 AX         Accumulator reg: arithmetic, logic, data transfer. One number must be AX
    EBX                 BX         Base reg: holds address of base storage where data stored
    ECX                 CX         Count reg: loop counter
    EDX                 DX         Data reg: I/O operations and large values

    ESI     Source Index: point to memory locations in data segment
    EDI     Destination Index: string operations for memory access locations

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

    public ArrayList<ASMFunction> functions; //global list of functions in the ir

    public ASM(String ir) {

        String[] lines = ir.split(EOL);
        ir_lines = new ArrayList(Arrays.asList(lines));
        ir_lines.removeAll(Collections.singleton(""));
        functions = new ArrayList<>();
    }

    public ASM(ArrayList<String> ir) {
        ir_lines = ir;
        ir_lines.removeAll(Collections.singleton(""));
        functions = new ArrayList<>();
    }

    public String getASMString() {
        StringBuilder asmString = new StringBuilder();
        /*
        Declaration for linker
         */
        asmString.append(".section .text\n");
        asmString.append("\t.global _start\n");
        asmString.append("_start:\n");

        //not sure if this approach actually works how we're intending it to
        //we'll probably have to break up assembly generation by each function, then
        //evaluate what is inside each function and generate the appropriate assembly.
        //Having the functions in a data structure though will let us know how many
        //variables need to be placed on the stack though
        //As it stands, this loop correctly parses functions and places them into the "functions" arraylist
        for(String line : ir_lines) {
            String word = line.split(" ")[0];
            if(word.equals("function")) {
                System.out.println("FUNCTION");
                String func = line.split(" ", 2)[1];
                //func_split is
                //main(int argc, char** argv)
                //becomes func_split[0] = "main"; func_split[1] = "int argc, char** argv";
                String[] func_split = func.split("\\(|\\)");
                String func_name = func_split[0];
                if(func_name.equals("main")) { //defining entry point
                    func_name = "_start";
                }
                String[] args;
                if(func_split.length > 1) {
                    args = func_split[1].split(",");
                    functions.add(new ASMFunction(func_name, Arrays.asList(args)));
                } else {
                    functions.add(new ASMFunction(func_name, null));
                }
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
