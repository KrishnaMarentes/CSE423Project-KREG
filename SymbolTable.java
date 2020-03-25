import java.util.ArrayList;

/**
 * EX:
 *  : will always be root type: global
 *      SymbolTable Global
 *      String Name: Global
 *      String Return Type: Null (or void?) ( used for forloops as well)
 *      SuperClass parent : Null
 *      List of superclass elements (Table)
 *      : inside:
 *          List of superclass elements (Symbol Table or Entry)
 *              SymbolTable (example) : Main
 *                  String Name: Main
 *                  String Type: INT # check symboltableentry.java for setup for types -> SuperClass
 *                  SuperClass parent : Global
 *
 *                  List of superclass elements
 *
 *
 *              SymbolTable ...
 *                  : Other Functions that can be globally seen :
 *              Entry ...
 *                  : Global variables :
 *
 */

abstract class SymbolTableSuper {
    enum SymbolType {
        INT, CHAR, FLOAT, DOUBLE, LONG, UNSIGNED, SIGNED, SHORT, VOID, PNTR;
    }

    private SymbolType type;
    private String name;

    abstract String getEntryType();

    String getType() { return this.type.name(); }
    String getName() { return this.name; }

    void setName(String name) {
        this.name = name;
    }

    void setType(String type) {
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
}

/**
 * Symbol Table Class
 */
public class SymbolTable extends SymbolTableSuper {

    private SymbolTable parent = null;
    private ArrayList<SymbolTableSuper> entries = new ArrayList<>();

    String getEntryType() { return "Table"; }

    public SymbolTable() {
    }

    public SymbolTable(String name, String type) {
        this.setName(name);
        this.setType(type);
    }


    /**
     * Adds table to a tables entries
     * @param type Type of instance, see parent class for types
     * @param name Name of instance
     */
    void addTable(String type, String name) {

        SymbolTable entry = new SymbolTable();

        entry.parent = this;
        entry.setName(name);
        entry.setType(type);

        this.entries.add(entry);
    }

    /**
     * Adds simple entry to a tables entries
     * @param type Type of instance, see parent class for types
     * @param name Name of the instance
     */
    void addEntry(String type, String name) {

        SymbolEntry entry = new SymbolEntry();

        entry.setName(name);
        entry.setType(type);

        this.entries.add(entry);
    }

    /**
     * Check if a symbol with the given name is in scope. Returns true if found, false otherwise.
     * @param name The name of the entry
     * @return true or false
     */
    public boolean lookupEntry(String name) {

        SymbolTable check = new SymbolTable();
        check = this;

        while (check != null) {
            for (SymbolTableSuper entry : check.entries) {
                if (name.equals(entry.getName())) {
                    return true;
                }
            }
            check = this.parent;
        }
        return false;
    }

}

/**
 * Simple entry in a Symbol Table, variable declaration.
 */
class SymbolEntry extends SymbolTableSuper {
    String getEntryType() { return "Entry"; }
}
