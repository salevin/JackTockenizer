import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

/**
 * Created by sam on 2/2/16.
 */
class JackTokenizer {

    private PushbackReader rd;

    private String token;

    private types curr_tokenType;

    private keys curr_keyWord;

    JackTokenizer(String inputFile) {
        try {
            rd = new PushbackReader(
                    new FileReader(inputFile));
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    boolean hasMoreTokens() {
        try {
            int curr_char = rd.read();
            if (curr_char != -1 && !Character.isWhitespace(curr_char)) {
                rd.unread(curr_char);
                return true;
            } else
                return Character.isWhitespace(curr_char) && hasMoreTokens();
        } catch (IOException x) {
            System.err.println(x);
            return false;
        }
    }

    void advance() {
        try {
            char curr_char = (char) rd.read();

            //Comment
            if (curr_char == '/') {
                curr_char = (char) rd.read();
                if (curr_char == '*') {
                    advanceBlock();
                    return;
                } else if (curr_char == '/') {
                    advanceComment();
                    return;
                } else {
                    rd.unread(curr_char);
                    curr_char = '/';
                }
            }

            // Symbol Type
            if ("{}()[].,;+-*/&|<>=~".indexOf(curr_char) != -1) {
                token = "" + curr_char;
                curr_tokenType = types.SYMBOL;
                return;
            }

            // Digit Type
            if (Character.isDigit(curr_char)) {
                token = advanceDigit("" + curr_char);
                curr_tokenType = types.INT_CONST;
                return;
            }

            // Identifier or Keyword
            if (Character.isLetter(curr_char)) {
                token = advanceLetter("" + curr_char);
                keyIdSwitch();
                return;
            }

            // String
            if (curr_char == '"') {
                token = "";
                token = advanceString(token);
                curr_tokenType = types.STRING_CONST;
            }

        } catch (IOException x) {
            System.err.println(x);
        }
    }

    types tokenType() {
        return curr_tokenType;
    }

    keys keyWord() {
        return curr_keyWord;
    }

    private String advanceDigit(String num) {
        try {
            char curr_char = (char) rd.read();
            if (Character.isDigit(curr_char)) {
                return advanceDigit(num + curr_char);
            } else {
                rd.unread(curr_char);
                return num;
            }
        } catch (IOException x) {
            System.err.println(x);
            return ("-1");
        }
    }

    private String advanceLetter(String letter) {
        try {
            char curr_char = (char) rd.read();
            if (Character.isLetterOrDigit(curr_char) || curr_char == '_') {
                return advanceLetter(letter + curr_char);
            } else {
                rd.unread(curr_char);
                return letter;
            }
        } catch (IOException x) {
            System.err.println(x);
            return ("-1");
        }
    }

    private String advanceString(String token) {
        try {
            char curr_char = (char) rd.read();
            while (curr_char != '"') {
                token = token + curr_char;
                curr_char = (char) rd.read();
            }
            return token;
        } catch (IOException x) {
            System.err.println(x);
            return ("-1");
        }
    }

    String returnToken() {
        return token;
    }

    private void keyIdSwitch() {
        curr_tokenType = types.KEYWORD;
        switch (token) {
            case "class":
                curr_keyWord = keys.CLASS;
                break;
            case "constructor":
                curr_keyWord = keys.CONSTRUCTOR;
                break;
            case "function":
                curr_keyWord = keys.FUNCTION;
                break;
            case "method":
                curr_keyWord = keys.METHOD;
                break;
            case "field":
                curr_keyWord = keys.FIELD;
                break;
            case "static":
                curr_keyWord = keys.STATIC;
                break;
            case "var":
                curr_keyWord = keys.VAR;
                break;
            case "int":
                curr_keyWord = keys.INT;
                break;
            case "char":
                curr_keyWord = keys.CHAR;
                break;
            case "boolean":
                curr_keyWord = keys.BOOLEAN;
                break;
            case "void":
                curr_keyWord = keys.VOID;
                break;
            case "true":
                curr_keyWord = keys.TRUE;
                break;
            case "false":
                curr_keyWord = keys.FALSE;
                break;
            case "null":
                curr_keyWord = keys.NULL;
                break;
            case "this":
                curr_keyWord = keys.THIS;
                break;
            case "let":
                curr_keyWord = keys.LET;
                break;
            case "do":
                curr_keyWord = keys.DO;
                break;
            case "if":
                curr_keyWord = keys.IF;
                break;
            case "else":
                curr_keyWord = keys.ELSE;
                break;
            case "while":
                curr_keyWord = keys.WHILE;
                break;
            case "return":
                curr_keyWord = keys.RETURN;
                break;
            default:
                curr_tokenType = types.IDENTIFIER;
                break;
        }

    }

    private void advanceComment() {
        try {
            curr_tokenType = types.COMMENT;
            char curr_char = (char) rd.read();
            while (curr_char != '\n') {
                curr_char = (char) rd.read();
            }


        } catch (IOException x) {
            System.err.println(x);
        }
    }

    private void advanceBlock() {
        try {
            curr_tokenType = types.COMMENT;
            char next_char = ' ';
            while (true) {
                char curr_char = next_char;
                next_char = (char) rd.read();
                if (curr_char == '*' && next_char == '/') return;
            }
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    enum types {
        KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST, COMMENT
    }


    enum keys {
        CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR, STATIC, FIELD, LET,
        DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS
    }

}
