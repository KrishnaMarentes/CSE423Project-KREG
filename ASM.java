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

    Map<String, List<String>> functions = new HashMap<String, List<String>>();

    String current_function = ""; //store the current function being evaluated for contextual use
    Map<String, Integer> current_vars = new HashMap<>(); //holds var_name and memory offset
    Map<String, Integer> passed_args = new HashMap<>(); //holds current function's arguments that were passed to it
    boolean is_returned = false; //has the current function returned?


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
        asmString.append(".section .text\n");
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
                    if (irStmt[i].matches(".*\\(.*\\);")) {
                        funcCall = true;
                    }
                }
            }

            /* function declaration */
            if(leftside.equals("function")) {
                asmString = resetTrackers(asmString); //new function, reset tracking variables
                System.out.println("FUNCTION");
                String func = line.split(" ", 2)[1];
                //func_split is
                //main(int argc, char** argv)
                //becomes func_split[0] = "main"; func_split[1] = "int argc, char** argv";
                String[] func_split = func.split("\\(|\\)");
                String func_name = func_split[0];
                current_function = func_name;
//                if(func_name.equals("main")) { //defining entry point
//                    func_name = "_start"; //apparently using 'main' is preferable with cdecl x86
//                }
                String[] args;
                if(func_split.length > 1) {
                    args = func_split[1].split(",");
                    functions.put(func_name, Arrays.asList(args));

                } else {
                    functions.put(func_name, null);
                }

                //if (func_name.compareTo("_start") != 0) {
                if (func_name.compareTo("main") != 0) { //should be main
                    asmString = fun_assm(func_name, asmString);
                } else {
                    asmString = startFunction(asmString);
                }


            /* variable declaration */
            } else if(type_list.contains(leftside)) {
                String var = irStmt[1].replace(";", "");
                current_vars.put(var, (current_vars.size() * -4) - 4);
            } else if(ignore_list.contains(leftside)) {
                //ignore?
            } else if(leftside.equals("return")) {
                is_returned = true;
                String ret_val = irStmt[1].replace(";", "");
                asmString.append("\tmov ");
                if(current_vars.containsKey(ret_val)) {//local variable
                    asmString.append(current_vars.get(ret_val)).append("(%ebp)");
                } else if(passed_args.containsKey(ret_val)) { //return passed args...if you want
                    asmString.append(passed_args.get(ret_val)).append("(%ebp)");
                } else { //probably a number
                    asmString.append("$").append(ret_val);
                }
                asmString.append(", ").append("%eax").append(EOL);
                asmString.append("\tleave").append(EOL).append("\tret").append(EOL);
            } else if(funcCall) {
                String callee = null;
                String[] args = null;

                for (String i : rightside) {
                    if (i.matches(".*\\(.*\\);")) {
                        //need to handle function arguments somehow
                        callee = i.replaceAll("\\(.*\\);", "");
                        args = i.replaceAll(".*\\(|\\)|;", "").split(",");
                        System.out.println(functions.get(callee));
                    }
                }
                asmString = functionCall(callee, args, asmString);

                System.out.println("FUNCALL");
            } else if(leftside.equals("if")) { //this handles 'while' as well
                //since 'if' lines are ALWAYS 9 long when split by spaces
                //AND, since 'while' lines are ALWAYS 6 long when split by spaces
                //just grab the values you need based on the indices
                //EXAMPLES
                //if b == 40 goto <KREG.12> else goto <KREG.13>;    //formatted this way for IF blocks
                //if i < 3 goto <KREG.4>;                           //formatted this way for WHILE blocks
                String left = irStmt[1];
                String op = irStmt[2];
                String right = irStmt[3];
                if(irStmt.length == 9) {

                } else if(irStmt.length == 6) {

                }
            } else if(leftside.matches("<KREG\\..*>:")) { //just print it?
                String tag = leftside.replaceAll("[<>]", "");
                asmString.append("\t").append(tag).append(EOL);
            } else if(leftside.equals("goto")) {
                String tag = irStmt[1].replaceAll("[<>;]", ""); //goto this tag
                asmString.append("\tjmp ").append(tag).append(EOL);
            }
        }
        asmString = resetTrackers(asmString);
        return asmString.toString();
    }
    public StringBuilder functionCall(String callee, String[] args, StringBuilder asmCode) {

        if (functions.get(callee) != null) {
            for(int i = args.length - 1; i >= 0; i--) { //backwards, as is tradition
                if(current_vars.containsKey(args[i])) {
                    asmCode.append("\tpush ").append(current_vars.get(args[i])).append("(%ebp)").append(EOL);
                } else {
                    asmCode.append("\tpush $").append(args[i]).append(EOL);
                }
            }
        }
        asmCode.append("\tcall " + callee).append(EOL);
        return asmCode;
    }
    public StringBuilder fun_assm(String callee, StringBuilder asmCode) {

        asmCode.append(".global ").append(callee).append(EOL);
        asmCode.append(callee).append(":").append(EOL);
        /* loop through parameters to pull in. Max used is SO MANY */
        int register = 0;
        System.out.println(callee);
        int p_size = 0;
        List<String> params = null;
        if (functions.get(callee) != null) {
            p_size = functions.get(callee).size();
            params = functions.get(callee);
            for(int i = 0; i < params.size(); i++) {
                String param_i = params.get(i).replaceAll("int ", "");
                passed_args.put(param_i, ((i + 1) * 4) + 4);
            }
        }

        asmCode.append("\tpush %ebp").append(EOL);
        asmCode.append("\tmov %esp, %ebp").append(EOL);
        asmCode.append("\tsub %NUMVARS%, %esp").append(EOL); //replace in string later, the number of variables declared in function

        //using the cdecl approach, we can just grab everything from the stack
//        if (p_size < 4) {
//            for (int i = 4; i < (p_size * 4); i = i * 4) {
//                asmCode.append("\tmovl ");
//                if (register == 0) {
//                    asmCode.append("%eax, ");
//                } else if (register == 1){
//                    asmCode.append("%ebx, ");
//                } else if (register == 2) {
//                    asmCode.append("%ecx, ");
//                } else if (register == 3) {
//                    asmCode.append("%edx, ");
//                }
//                asmCode.append(i).append("(%esp)\n");
//                register++;
//            }
//        }

        
        //asmCode.append("\tmovl %esp, %ebp\n\tpopl %ebp\n\tret\n"); // Callee must restore ESP can EBP to their old values
        //'leave' restores old values
        return asmCode;
    }

     public StringBuilder startFunction(StringBuilder asmCode) {
        asmCode.append(".global main").append(EOL);
        asmCode.append("main:").append(EOL);
        asmCode.append("\tpush %ebp").append(EOL);
        asmCode.append("\tmov %esp, %ebp").append(EOL);
        asmCode.append("\tsub %NUMVARS%, %esp").append(EOL);


        return asmCode;
     }

     //these structures just hold all the data that I am tracking per each function being built.
     //resetting them between each function and doing some small string operations is all this does
     public StringBuilder resetTrackers(StringBuilder sb) {
         String var_size = "$" + Integer.toString(current_vars.size() * 4);
         sb = new StringBuilder(sb.toString().replace("%NUMVARS%", var_size));
         if(!is_returned && !current_function.equals("")) {
             sb.append("\tleave").append(EOL).append("\tret").append(EOL);
         }
         current_function = "";
         current_vars.clear();
         passed_args.clear();
         is_returned = false;
         return sb;
     }
}
