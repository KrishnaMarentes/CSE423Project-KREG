import java.util.ArrayList;

public class SymbolTable {

    /* Numerical measurement for scope level. Global table is 0. Main would be 1, etc. */
    private int scope;
    /* Would like to have a name associated with it, e.g. the function name, but
     * I haven't figured out how we would name nameless blocks */
    //private String name;
    private ArrayList<SymbolTableEntry> table;

    public SymbolTable(int scope) {
        this.table = new ArrayList<SymbolTableEntry>();
        this.scope = scope;
    }

    /* Add entry with given name and type to table */
    public void addEntry(String name, String type) {
        SymbolTableEntry entry = new SymbolTableEntry(name, type);
        this.table.add(entry);
    }

    /* Check if a symbol with the given name is already in
     * the symbol table. Searches the table linearly.
     * Returns true if already in table, false otherwise.
     */
    public boolean lookupSymbol(String name) {
        int i = 0;
        SymbolTableEntry entry = table.get(i++);
        while (entry != null) {
            if (name.compareTo(entry.getName()) == 0) {
                return true;
            }
            entry = table.get(i++);
        }
        return false;
    }
}
