import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Created by sam on 2/2/16.
 */
public class JackTokenizer {

    private Scanner scanner;

    public enum tokenType {
        KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
    }

    public enum keyWord {
        CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR, STATIC, FIELD, LET,
        DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS
    }

    public JackTokenizer(String inputFile) {
        try {
            InputStream in = new FileInputStream(inputFile);
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in));
            scanner = new Scanner(reader);
        } catch (IOException x) {
            System.err.println(x);
        }
    }


    public boolean hasMoreTokens() {
        String line = scanner.nextLine();
        return false;
    }

    public void advance() {
        scanner.next();
    }


}
