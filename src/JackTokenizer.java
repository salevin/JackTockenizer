import java.io.*;

/**
 * Created by sam on 2/2/16.
 */
public class JackTokenizer {

    private PushbackReader rd;

    private String token;

    public enum tokenType {
        KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
    }

    public enum keyWord {
        CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR, STATIC, FIELD, LET,
        DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS
    }

    public JackTokenizer(String inputFile) {
        try {
            rd = new PushbackReader(
                    new FileReader(inputFile));
        } catch (IOException x) {
            System.err.println(x);
        }
    }


    public boolean hasMoreTokens() {
        try {
            int curr_char = rd.read();
            if (curr_char != -1) {
                rd.unread(curr_char);
                return true;
            } else return false;
        } catch (IOException x) {
            System.err.println(x);
            return false;
        }
    }

    public void advance() {
        try {
            char curr_char = (char) rd.read();

            if ("{}()[].,;+-*/&|<>=~".indexOf(curr_char) != -1) {
                token = "" + curr_char;
                return;
            }

            if (Character.isDigit(curr_char)) {
                token = advanceDigit("" + curr_char);
            }


        } catch (IOException x) {
            System.err.println(x);
        }
    }

    public String advanceDigit(String num) {
        try {
            char curr_char = (char) rd.read();
            if (Character.isDigit(curr_char)) {
                return advanceDigit(num + curr_char);
            } else return num;
        } catch (IOException x) {
            System.err.println(x);
            return ("-1");
        }


    }
}
