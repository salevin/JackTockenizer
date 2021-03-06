import java.util.Hashtable;
import java.util.Objects;

/**
 * Created by sam on 2/25/16.
 */
class SymbolTable {

    private static Hashtable<String, String[]> classScope;
    private static Hashtable<String, String[]> subroutineScope;

    SymbolTable() {
        classScope = new Hashtable<>();
        subroutineScope = new Hashtable<>();
    }

    void startSubroutine() {
        subroutineScope.clear();
    }

    void Define(String name, String type, Kind kind) {
        int count = 0;
        String[] kindType = new String[3];
        kindType[0] = type;
        kindType[1] = kind.toString();
        // Var and Arg go into subroutine scope
        // Static and Field go into class scope
        if (kind == Kind.VAR || kind == Kind.ARG) {
            // Set index to the number of it's kind in it's scope
            for (String key : subroutineScope.keySet()) {
                if (Objects.equals(subroutineScope.get(key)[1], kind.toString())) {
                    count++;
                }
            }
            kindType[2] = Integer.toString(count);
            subroutineScope.put(name, kindType);
        } else if (kind == Kind.STATIC || kind == Kind.FIELD) {
            // Set index to the number of it's kind in it's scope
            for (String key : classScope.keySet()) {
                if (Objects.equals(classScope.get(key)[1], kind.toString())) {
                    count++;
                }
            }
            kindType[2] = Integer.toString(count);
            classScope.put(name, kindType);
        } else {
            System.err.println("Kind is None in Define");
            System.exit(1);
        }
    }

    Integer VarCount(Kind kind) {
        int count = 0;
        for (String key : classScope.keySet()) {
            if (Objects.equals(classScope.get(key)[1], kind.toString())) {
                count++;
            }
        }
        for (String key : subroutineScope.keySet()) {
            if (Objects.equals(subroutineScope.get(key)[1], kind.toString())) {
                count++;
            }
        }
        return count;
    }

    Kind KindOf(String name) {
        if (classScope.containsKey(name))
            return toKind(classScope.get(name)[1]);
        else
            return toKind(subroutineScope.get(name)[1]);
    }

    String TypeOf(String name) {
        if (classScope.containsKey(name))
            return classScope.get(name)[0];
        else
            return subroutineScope.get(name)[0];
    }

    int IndexOf(String name) {
        if (classScope.containsKey(name))
            return Integer.parseInt(classScope.get(name)[2]);
        else if (subroutineScope.containsKey(name))
            return Integer.parseInt(subroutineScope.get(name)[2]);
        else
            return -1;
    }

    Kind toKind(String stringType) {
        for (Kind kind : Kind.values()) {
            if (stringType.equalsIgnoreCase(kind.toString()))
                return kind;
        }
        return Kind.NONE;
    }

    enum Kind {
        STATIC, FIELD, VAR, ARG, NONE
    }

}