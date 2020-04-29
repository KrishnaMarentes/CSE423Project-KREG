import java.lang.reflect.Array;
import java.util.*;

public class ASM {
    /*
    SOURCE : DESTINATION
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

    //    public ArrayList<ASMFunction> functions; //global list of functions in the ir
    Map<String, List<String>> functions = new HashMap<String, List<String>>();

    public ASM(String ir) {

        String[] lines = ir.split(EOL);
        ir_lines = new ArrayList(Arrays.asList(lines));
        ir_lines.removeAll(Collections.singleton(""));
        //functions = new ArrayList<>();
    }

    public ASM(ArrayList<String> ir) {
        ir_lines = ir;
        ir_lines.removeAll(Collections.singleton(""));
        //functions = new ArrayList<>();
    }

    public String getASMString() {
        StringBuilder asmString = new StringBuilder();
        /*
        Declaration for linker
         */
        asmString.append(".section .text\n");
        asmString.append("\t.global _start\n\n");

        //not sure if this approach actually works how we're intending it to
        //we'll probably have to break up assembly generation by each function, then
        //evaluate what is inside each function and generate the appropriate assembly.
        //Having the functions in a data structure though will let us know how many
        //variables need to be placed on the stack though
        //As it stands, this loop correctly parses functions and places them into the "functions" arraylist
        for(String line : ir_lines) {
            /*
            * Setup for a structure "varStmt" to call individual parts for assembly
            *   if varstatement is true, there is an equal sign. Set of operations for this
            *   if varstatement is false, this is a var declaration, fun delcaration w/o vars, or return
            */
            /* Should be a single instance every loop time */
            //Map<String, List<String>> varStmt = new HashMap<String, List<String>>();
            String[] irStmt = line.split(" ");
            String leftside = irStmt[0];
            ArrayList<String> rightside = new ArrayList<>();
            boolean funcCall = false;
            /* checking for equal sign w/o bounds error */
            if (irStmt.length > 2) {
                for (int i = 2; i < irStmt.length; i++) {
                    rightside.add(irStmt[i]);
                    //if (irStmt[i].contains("()")) {
                    if (irStmt[i].matches(".*\\(.*\\);")) {
                        funcCall = true;
                    }
                }
            }

            /* function declaration */
            if(leftside.equals("function")) {
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
                    //functions.add(new ASMFunction(func_name, Arrays.asList(args)));
                    functions.put(func_name, Arrays.asList(args));

                } else {
                    //functions.add(new ASMFunction(func_name, null));
                    functions.put(func_name, null);
                }

                if (func_name.compareTo("_start") != 0) {
                    asmString = fun_assm(func_name, asmString);
                } else {
                    asmString = startFunction(asmString);
                }


            /* variable declaration */
            } else if(type_list.contains(leftside)) {
                //it's a var declaration
                System.out.println("VAR");
            } else if(ignore_list.contains(leftside)) {
                //ignore?
            } else if(leftside.equals("return")) {
                //ret eax
            } else if(funcCall) {
                //functions.containsKey();
                String callee = null;
                String[] args = null;
                //asmString = ASM.functionCall(callee, asmString);

                for (String i : rightside) {
                    //if (i.contains("()")) {
                    if (i.matches(".*\\(.*\\);")) {
                        //need to handle function arguments somehow
                        callee = i.replaceAll("\\(.*\\);", "");
                        args = i.replaceAll(".*\\(|\\)|;", "").split(",");
                        System.out.println(functions.get(callee));
                    }
                }
                asmString = functionCall(callee, args, asmString);

                System.out.println("FUNCALL");
            }
        }
        return asmString.toString();
    }
    public StringBuilder functionCall(String callee, String[] args, StringBuilder asmCode) {

        if (functions.get(callee) == null) {
            // NO NEED TO PUSH TO STACK, NO VARIABLES
        } else {
            // Dereference variables and pushl $(value)
            //need to check here if arg is variable, constant, or temporary variable? (KREG.#)
            //how are we storing temporary variables??
            // create space on %esp by 4s for ints, check callee space needed by values
        }
        asmCode.append("\tcall " + callee + "\n");
        return asmCode;
    }
    public StringBuilder fun_assm(String callee, StringBuilder asmCode) {

        asmCode.append(callee).append(":\n");
        /* loop through parameters to pull in. Max used in 3 */
        int register = 0;
        System.out.println(callee);
        int p_size = 0;
        if (functions.get(callee) != null) {
            p_size = functions.get(callee).size();
        }
        
        if (p_size < 4) {
            for (int i = 4; i < (p_size * 4); i = i * 4) {
                asmCode.append("\tmovl ");
                if (register == 0) {
                    asmCode.append("%eax, ");
                } else if (register == 1){
                    asmCode.append("%ebx, ");
                } else if (register == 2) {
                    asmCode.append("%ecx, ");
                } else if (register == 3) {
                    asmCode.append("%edx, ");
                }
                asmCode.append(i).append("(%esp)\n");
                register++;
            }
        }

        
        asmCode.append("\tmovl %esp, %ebp\n\tpopl %ebp\n\tret\n"); // Callee must restore ESP can EBP to their old values
        return asmCode;
    }

     public StringBuilder startFunction(StringBuilder asmCode) {
        asmCode.append("_start: \n");

        
        return asmCode;
     }
}
