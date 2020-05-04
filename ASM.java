import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    StringBuilder fullAsmString = new StringBuilder();
    Map<String, List<String>> functions = new HashMap<String, List<String>>();
    Map<String, String> comparisons = populateComparisonOperators();
    Map<String, String> operators = populateArithmeticOperators();
    Map<String, Boolean> registers = populateRegisters();

    String current_function = ""; //store the current function being evaluated for contextual use
    Map<String, Integer> current_vars = new HashMap<>(); //holds var_name and memory offset
    Map<String, Integer> passed_args = new HashMap<>(); //holds current function's arguments that were passed to it
    boolean is_returned = false; //has the current function returned?
    int vars_removed = 0;

    public ASM(String ir) {

        String[] lines = ir.split(EOL);
        ir_lines = new ArrayList<String>(Arrays.asList(lines));
        ir_lines.removeAll(Collections.singleton(""));
    }

    public ASM(ArrayList<String> ir) {
        ir_lines = ir;
        ir_lines.removeAll(Collections.singleton(""));
    }

    private static Map<String, String> populateArithmeticOperators() {
        Map<String, String> retMap;
        retMap = Stream.of(new String[][]{
                {"+", "\tadd "},
                {"-", "\tsub "},
                {"*", "\timul "},
                {"/", "\tdiv "},
                {"%", "\tdiv "},
                {"<<", "\tshl "},
                {">>", "\tshr "},
                {"&", "\tand "},
                {"|", "\tor "},
                {"^", "\txor "}
        }).collect(Collectors.collectingAndThen(
                Collectors.toMap(data -> data[0], data -> data[1]), Collections::unmodifiableMap));

        return retMap;
    }

    private static Map<String, String> populateComparisonOperators() {
        Map<String, String> retMap;
        retMap = Stream.of(new String[][]{
                {"<", "\tjl "},
                {">", "\tjg "},
                {"==", "\tje "},
                {"!=", "\tjne "},
                {">=", "\tjge "},
                {"<=", "\tjle "}
        }).collect(Collectors.collectingAndThen(
                Collectors.toMap(data -> data[0], data -> data[1]), Collections::unmodifiableMap));

        return retMap;
    }

    private static Map<String, Boolean> populateRegisters() {
        Map<String, Boolean> retMap = new HashMap<>();
        retMap.put("%eax", true);
        retMap.put("%ebx", true);
        retMap.put("%ecx", true);
        retMap.put("%edx", true);
        retMap.put("%esi", true);
        retMap.put("%edi", true);
        return retMap;
    }

    public String getASMString() {
        StringBuilder asmString = new StringBuilder();

        fullAsmString.append(".section .text").append(EOL);
        for(String line : ir_lines) {
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
                asmString = resetTrackers(asmString); //new function found, reset tracking variables
                String func = line.split(" ", 2)[1];
                String[] func_split = func.split("[()]");
                String func_name = func_split[0];
                current_function = func_name;
                String[] args;
                if(func_split.length > 1) {
                    args = func_split[1].split(",");
                    functions.put(func_name, Arrays.asList(args));

                } else {
                    functions.put(func_name, null);
                }

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
                asmString.append("\tmov ").append(getValue(ret_val));
                asmString.append(", ").append("%eax").append(EOL);
                useRegister("%eax");
                asmString.append("\tleave").append(EOL).append("\tret").append(EOL);
            } else if(leftside.equals("return;")) {
                asmString.append("\tnop").append(EOL); //this is how gcc did it
                asmString.append("\tleave").append(EOL).append("\tret").append(EOL);
            } else if(funcCall) {
                String callee = null;
                String[] args = null;

                for (String i : rightside) {
                    if (i.matches(".*\\(.*\\);")) {
                        //need to handle function arguments somehow
                        callee = i.replaceAll("\\(.*\\);", "");
                        args = i.replaceAll(".*\\(|\\)|;", "").split(",");
                        //System.out.println(functions.get(callee));
                    }
                }
                asmString = functionCall(callee, args, asmString);
                asmString.append("\tmov %eax, ").append(getValue(leftside)).append(EOL);
                freeRegister("%eax");

                //System.out.println("FUNCALL");
            } else if(leftside.equals("if")) { //this handles 'while' as well
                String left = irStmt[1];
                String op = irStmt[2];
                String right = irStmt[3];
                String label1 = irStmt[5].replaceAll("[<>;]", "");

                String firstRegister = getFirstAvailableRegister();
                String secondRegister = getFirstAvailableRegister();
                asmString.append("\tmov ").append(getValue(left)).append(", ").append(firstRegister).append(EOL);
                asmString.append("\tmov ").append(getValue(right)).append(", ").append(secondRegister).append(EOL);
                asmString.append("\tcmp ").append(firstRegister).append(", ").append(secondRegister).append(EOL);
                freeRegister(firstRegister);
                freeRegister(secondRegister);

                asmString.append(comparisons.get(op)).append(label1).append(EOL);
                if(irStmt.length == 9) {
                    String label2 = irStmt[8].replaceAll("[<>;]", "");
                    asmString.append("\tjmp ").append(label2).append(EOL);
                }
            } else if(leftside.matches("<KREG\\..*>:")) { //just print it?
                String tag = leftside.replaceAll("[<>]", "");
                asmString.append(tag).append(EOL);
            } else if(leftside.equals("goto")) {
                String tag = irStmt[1].replaceAll("[<>;]", ""); //goto this tag
                asmString.append("\tjmp ").append(tag).append(EOL);
            } else if(irStmt.length > 1 && irStmt[1].equals("=")) {
                if(irStmt.length == 3) { //set var to something
                    String right = irStmt[2].replace(";", "");
                    String right_check = irStmt[2].replaceAll("[-~;]", "");
                    String left = irStmt[0];

                    if(right.startsWith("-") && variableExists(right_check)) {
                        right = getValue(right_check);
                        asmString.append("\tmov ").append(right).append(", ").append("%eax").append(EOL);
                        asmString.append("\tneg %eax").append(EOL);
                        asmString.append("\tmov ").append("%eax").append(", ").append(right).append(EOL);
                    } else if(right.startsWith("~") && variableExists(right_check)) {
                        right = getValue(right_check);
                        asmString.append("\tmov ").append(right).append(", ").append("%eax").append(EOL);
                        asmString.append("\txor 0xFFFFFFFF, %eax").append(EOL);
                        asmString.append("\tmov ").append("%eax").append(", ").append(right).append(EOL);
                    } else {
                        right = getValue(right);
                    }
                    left = getValue(left);


                    if(isMem(right) && isMem(left)) {
                        String reg = getFirstAvailableRegister();
                        asmString.append("\tmov ").append(right).append(", ").append(reg).append(EOL);
                        asmString.append("\tmov ").append(reg).append(", ").append(left).append(EOL);
                        freeRegister(reg);
                    } else {
                        //asmString.append("\tmov ").append(right);
                        asmString.append("\tmov").append(isNum(right) ? "l " + right : " " + right);
                        asmString.append(", ").append(left).append(EOL);
                    }
                } else if(irStmt.length == 5) { //operation performed
                    String receiver = irStmt[0];
                    String left = irStmt[2];
                    String op = irStmt[3];
                    String right = irStmt[4].replace(";", "");

                    receiver = getValue(receiver);
                    left = getValue(left);
                    if(operators.containsKey(op)) {
                        op = operators.get(op);
                    } else {
                        System.out.println("missing operator");
                        System.exit(1);
                    }
                    right = getValue(right);

                    if(op.contains("div")) { //div covers / and %
                        //left is dividend
                        //right is divisor
                        //quotient is stored in eax
                        //remainder stored in edx, edx must be xor'd before dividing or else FP error happens
                        asmString.append("\tmov ").append(left).append(", ").append("%eax").append(EOL);
                        asmString.append("\tmov ").append(right).append(", ").append("%ecx").append(EOL);
                        asmString.append("\txor %edx, %edx").append(EOL);
                        asmString.append(op).append("%ecx").append(EOL);
                        asmString.append("\tmov ").append(irStmt[3].equals("/") ? "%eax, " : "%edx, ").append(receiver).append(EOL);
                    } else {
                        asmString.append("\tmov ").append(left).append(", ").append("%ecx").append(EOL);
                        asmString.append("\tmov ").append(right).append(", ").append("%edx").append(EOL);
                        asmString.append(op).append("%ecx").append(", ").append("%edx").append(EOL);
                        asmString.append("\tmov ").append("%edx, ").append(receiver).append(EOL);
                    }
                }
            }
        }
        asmString = resetTrackers(asmString);
        //asmString = optimizeMovs(asmString);
        System.out.println("Assembly generated successfully");
        return fullAsmString.toString();
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

    //generate generic assembly function
    public StringBuilder fun_assm(String callee, StringBuilder asmCode) {

        asmCode.append(".global ").append(callee).append(EOL);
        asmCode.append(callee).append(":").append(EOL);
        /* loop through parameters to pull in. Max used is SO MANY */
        int register = 0;
//        System.out.println(callee);
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

        return asmCode;
    }

    //it attempts to anyway
    private StringBuilder optimizeMovs(StringBuilder in)
    {
        StringBuilder ret = new StringBuilder();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(in.toString().split(EOL)));
        int maxIndex = lines.size() - 1;

        for(int i = 0; i <= maxIndex; ++i) {
            if(i + 1 <= maxIndex && lines.get(i).startsWith("\tmov") && lines.get(i + 1).startsWith("\tmov")) {
                String[] line1 = lines.get(i).replace(",", "").split(" ");
                String[] line2 = lines.get(i + 1).replace(",", "").split(" ");
                String src1 = line1[1];
                String dest1 = line1[2];
                String src2 = line2[1];
                String dest2 = line2[2];

                if(dest1.equals(src2) && isMem(dest1) && isMem(src2) && isTemp(dest1) && isTemp(src2)) {
                    String newString = String.format("%s %s, %s", line1[0], src1, dest2);
                    lines.set(i, newString);
                    lines.remove(i + 1);
                    --i;
                    --maxIndex;
                    //++vars_removed;
                } else if(dest1.equals(src2) && isReg(dest1) && isReg(src2) && !(isMem(src1) && isMem(dest2))) {
                    String newString = String.format("%s %s, %s", line1[0], src1, dest2);
                    lines.set(i, newString);
                    lines.remove(i + 1);
                    --i;
                    --maxIndex;
                } else if(dest1.equals(src2) && src1.equals(dest2)) { //weird that this happens
                    lines.remove(i + 1);
                    --i;
                    --maxIndex;
                }
            }
        }

        for(String line : lines) {
            ret.append(line).append(EOL);
        }

        return ret;
    }

    //generate entry function
    public StringBuilder startFunction(StringBuilder asmCode) {
        asmCode.append(".global main").append(EOL);
        asmCode.append("main:").append(EOL);
        asmCode.append("\tpush %ebp").append(EOL);
        asmCode.append("\tmov %esp, %ebp").append(EOL);
        asmCode.append("\tsub %NUMVARS%, %esp").append(EOL);


        return asmCode;
    }

    //return string formatted to memory based on passed arguments, local variables, or a number
    public String getValue(String in) {
        String out = "";
        if(current_vars.containsKey(in)) {
            out = current_vars.get(in) + "(%ebp)";
        } else if(passed_args.containsKey(in)) {
            out = passed_args.get(in) + "(%ebp)";
        } else {
            out = "$" + in;
        }
        return out;
    }

    //these structures just hold all the data that I am tracking per each function being built.
    //resetting them between each function and doing some small string operations is all this does
    private StringBuilder resetTrackers(StringBuilder sb) {
        sb = optimizeMovs(sb);
        String var_size = "$" + Integer.toString((current_vars.size() - vars_removed) * 4);
        if(!is_returned && !current_function.equals("")) {
            sb.append("\tleave").append(EOL).append("\tret").append(EOL);
        }
        vars_removed = 0;
        current_function = "";
        current_vars.clear();
        passed_args.clear();
        is_returned = false;
        fullAsmString.append(sb.toString().replace("%NUMVARS%", var_size));
        sb = new StringBuilder();
        return sb;
    }

    //checks in general if a variable is in scope
    private boolean variableExists(String in)
    {
        return current_vars.containsKey(in) || passed_args.containsKey(in);
    }

    //checks if the local variable has the name of KREG.#
    private boolean isTemp(String in) {
        Integer mem_offset = Integer.parseInt(in.replaceAll("\\(.*\\)", ""));
        for(Map.Entry<String, Integer> var : current_vars.entrySet()) {
            if(var.getValue().equals(mem_offset) && var.getKey().matches("KREG\\..*")) {
                return true;
            }
        }
        return false;
    }

    private boolean isMem(String in) {
        return in.contains("%ebp");
    }

    private boolean isReg(String in) {
        return registers.containsKey(in);
    }

    private boolean isNum(String in) {
        return !(isMem(in) || isReg(in));
    }

    private void useRegister(String reg) {
        registers.put(reg, false);
    }

    private void freeRegister(String reg) {
        registers.put(reg, true);
    }

    private String getFirstAvailableRegister() {
        for(Map.Entry<String, Boolean> m : registers.entrySet()) {
            if(m.getValue()) {
                String reg = m.getKey();
                useRegister(reg);
                return reg;
            }
        }
        return null;
    }

}
