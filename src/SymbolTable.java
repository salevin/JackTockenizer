import com.sun.xml.internal.bind.v2.TODO;

/**
 * Created by sam on 2/25/16.
 */
public class SymbolTable {

    public enum Type {
        SATIC, FIELD, VAR, ARG, NONE
    }

    public SymbolTable() {
        //TODO creates new empty string table
    }

    private void startSubroutine() {
        // TODO Starts a new subroutine scope (i.e., resets the subroutine’s symbol table).
    }

    private void Define(String name, String type, Type kind) {
//        TODO Defines a new identifier of a
//        given name, type, and kind
//        and assigns it a running index.
//        STATIC and FIELD identifiers
//        have a class scope, while ARG
//        and VAR identifiers have a
//        subroutine scope.
    }

    private Integer VarCount(Type kind) {
//        TODO  Returns the number of
//        variables of the given kind
//        already defined in the current
//        scope.
        return null;
    }

    private Type KindOf(String name) {
//        TODO Returns the kind of the named
//        identifier in the current scope.
//        If the identifier is unknown in
//        the current scope, returns
//        NONE.
        return null;
    }

    private String TypeOf(String nam) {
        return null;
    }

}