public class SymbolTableEntry {

    /* No "function" type; entries that are functions will have their return
    * type stored in the "type" member. */
    enum SymbolType {
        INT, CHAR, FLOAT, DOUBLE, LONG, UNSIGNED, SIGNED, SHORT, VOID;
    }

    private String name; // Name of symbol. e.g. for "int a", this is "a"

    /* MUST Use very careful type checking on this member to avoid runtime errors */
    /* Can be of type SymbolType (see above enum) or SymbolTable (if entry is a function) */
    private SymbolType type;

    public SymbolTableEntry(String n, String t) {
        this.name = n;
        /* Use the setter since it has the try catch block */
        setSymbolType(t);
        //this.type = SymbolType.valueOf(t);
    }

    public SymbolTableEntry() {
        this.name = null;
        this.type = null;
    }

    public void setEntryName(String name) {
        this.name = name;
    }

    public void setSymbolType(String type) {
        try {
            this.type = SymbolType.valueOf(type);
        } catch(IllegalArgumentException e) {
            // This should probably be more formal or print somewhere else but here it is.
            System.out.println(e.getMessage());
            System.out.println("'" + type + "' is not a valid type");
        } catch(NullPointerException e) {
            System.out.println(e.getMessage());
        }

    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type.name();
    }

}
