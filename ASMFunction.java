import java.util.List;

public class ASMFunction {

    private String name;
    private List<String> args;

    //takes in the function name, as well as the arguments as a list
    //for example, a list of args right now for the example of "function main(int argc, char** argv)"
    //would look like
    //args[0] = "int argc";
    //args[1] = "char** argv";
    public ASMFunction(String name, List<String> args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }


    public List<String> getArgs() {
        return args;
    }

}
