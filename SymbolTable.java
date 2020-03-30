import javafx.util.Pair;
import org.antlr.v4.runtime.RuleContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class SymbolTableSuper {
    /*
    enum SymbolType {
        INT, CHAR, FLOAT, DOUBLE, LONG, UNSIGNED, SIGNED, SHORT, VOID, PNTR;
    }
     */
    private ArrayList<String> symbolType = new ArrayList<String>( Arrays.asList(
            "int",
            "char",
            "float",
            "double",
            "long",
            "unsigned",
            "signed",
            "short",
            "void"
    ));

    private String type;
    private String name;

    abstract String getEntryType();

    String getType() { return this.type; }
    String getName() { return this.name; }

    void setName(String name) {
        this.name = name;
    }

    void setType(String type) {

        if (symbolType.contains(type)) {
            this.type = type;
        } else if (type == null) {
            System.out.println("what?");
            this.type = type;
        } else {
            System.out.println("'" + type + "' is not a valid type");
            System.exit(0);
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

    /**
     * Generic constructor for SymbolTable
     */
    public SymbolTable() {
    }

    /**
     * Constructor for SymbolTable
     * @param name Name of symbol table
     * @param type Return type of symbol table
     */
    public SymbolTable(String name, String type) {
        this.setName(name);
        this.setType(type);
    }

    /**
     * Populates the entire Symbol Table
     * @param rc Rule context to build AST to reference
     * @param ruleNames Rule names to reference
     * @return Symbol table that is populated
     */
    public static SymbolTable populate(RuleContext rc, String[] ruleNames) {

        /* List of symbol tables, one for each scope. The first is the global table */
        SymbolTable global = new SymbolTable("global", null);
        List<String> ruleNamesList = Arrays.asList(ruleNames);
        ASTNode root = TreeUtils.generateAST(rc, ruleNamesList);

        populateTable(root, global);

        return global;
    }

    /**
     * Populates a single symbol table
     * @param current The current root of the AST being picked for entries
     * @param st The current symbol table being populated
     */
    public static void populateTable(ASTNode current, SymbolTable st) {

        ASTNode child = null;

        for(int i = 0; i < current.children.size(); i++) {

            if(current.children.get(i) == null) continue;
            child = current.children.get(i);

            if(child instanceof FunDeclaration) {
                st.addTable(((FunDeclaration) child).typeSpecifier.id, ((FunDeclaration) child).functionName);

                for (Pair<TypeSpecifier, String> tmp : ((FunDeclaration) child).params) {
                    st.addEntry(tmp.getKey().id, tmp.getValue());
                }
                populateTable(((FunDeclaration) child).compoundStmt, st);

            } else if(child instanceof VarDeclaration) {
                for (Pair<String, Expression> tmp : ((VarDeclaration) child).vars) {
                    st.addEntry(((VarDeclaration) child).type.id, tmp.getKey());
                }
            }

        }

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

    /**
     * Prints the entire symbol table, tab spaced by depth
     * @param global the root table
     */
    public static void printSymbolTable(SymbolTable global) {
        printTable(global, 0);
    }

    /**
     * Pads a print statement
     * @param num tab size
     * @return string of spaces
     */
    private static String padding(int num) {
        String space = "";
        for(int i = 0; i < num; i++){
            space += " ";
        }
        return space;
    }

    /**
     * Prints the current table passed in
     * @param table current symbol table
     * @param depth depth which the table resides in relation to global
     */
    private static void printTable(SymbolTable table, int depth) {
        System.out.println(padding(depth * 4) + "Table: " + table.getName());
        System.out.println(padding(depth * 4) + "---------------------");
        for (SymbolTableSuper entry : table.entries) {
            if (entry instanceof SymbolTable) {
                System.out.println("Function: " + entry.getType() + " " + entry.getName());
                printTable((SymbolTable) entry, ++depth);
            } else if(entry instanceof SymbolEntry) {
                System.out.println(padding(depth * 4) + entry.getType() + " " + entry.getName());
            }
        }

    }

}

/**
 * Simple entry in a Symbol Table, variable declaration.
 */
class SymbolEntry extends SymbolTableSuper {
    String getEntryType() { return "Entry"; }
}
