import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.fastinfoset.sax.SystemIdResolver;
import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static java.lang.System.*;

/**
 * Created by sam on 2/2/16.
 */
public class CompilationEngine {

    private JackTokenizer jToke;
    private BufferedWriter writer;
    private String savedToken;

    public CompilationEngine(String inputPath, String outputPath) {
        jToke = new JackTokenizer(inputPath);
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileClass() {
        try {
            writer.write("<class>\n");
            JackTokenizer.keys key;

            realAdvance();
            key = jToke.keyWord();

            if (key == JackTokenizer.keys.CLASS) {
                writeCurrToke();
            } else {
                err.println("No class dec!");
                exit(0);
            }

            realAdvance();

            if (jToke.tokenType() == JackTokenizer.types.IDENTIFIER) {
                writeCurrToke();
            } else {
                err.println("No class dec!");
                exit(0);
            }

            realAdvance();

            if (currToke().equals("{")) {
                writeCurrToke();
            } else {
                err.println("incorrect format!");
                exit(0);
            }

            realAdvance();
            key = jToke.keyWord();


            while (key == JackTokenizer.keys.STATIC
                    || key == JackTokenizer.keys.FIELD) {

                compileClassVarDec();

                realAdvance();
                key = jToke.keyWord();
            }

            while (key == JackTokenizer.keys.CONSTRUCTOR
                    || key == JackTokenizer.keys.FUNCTION
                    || key == JackTokenizer.keys.METHOD) {

                compileSubroutineDec();

                key = jToke.keyWord();
            }

            if (currToke().equals("}")) {
                writeCurrToke();
                writer.write("</class>\n");

            } else {
                err.println("incorrect format! compileClass");
                exit(0);
            }
            writer.close();
        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileClassVarDec() {
        try {
            writer.write("<classVarDec>\n");
            writeCurrToke();
            realAdvance();

            if (jToke.keyWord() != JackTokenizer.keys.INT
                    && jToke.keyWord() != JackTokenizer.keys.CHAR
                    && jToke.keyWord() != JackTokenizer.keys.BOOLEAN
                    && jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {

                err.println("incorrect format! compileClassVarDec");
                exit(0);
            }

            writeCurrToke();
            realAdvance();

            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                err.println("incorrect format! compileClassVarDec");
                exit(0);
            }

            writeCurrToke();
            realAdvance();

            writeLoop();

            writeCurrToke();


            writer.write("</classVarDec>\n");
        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileSubroutineDec() {
        try {
            writer.write("<subroutineDec>\n");
            writeCurrToke();
            realAdvance();

            if (jToke.keyWord() != JackTokenizer.keys.INT
                    && jToke.keyWord() != JackTokenizer.keys.CHAR
                    && jToke.keyWord() != JackTokenizer.keys.BOOLEAN
                    && jToke.tokenType() != JackTokenizer.types.IDENTIFIER
                    && jToke.keyWord() != JackTokenizer.keys.VOID) {

                err.println("incorrect format! compileSubroutineDec 1");
                return;
            }

            writeCurrToke();
            realAdvance();

            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                err.println("incorrect format! compileSubroutineDec 2");
                exit(0);
            }

            writeCurrToke();
            realAdvance();

            if (!currToke().equals("(")) {
                err.println("incorrect format! compileSubroutineDec 3");
                exit(0);
            }

            writeCurrToke();
            realAdvance();

            compileParameterList();

            writeCurrToke();
            realAdvance();

            if (!currToke().equals("{")) {
                err.println("incorrect format! compileSubroutineDec 4");
                exit(0);
            }

            compileSubroutineBody();


            writer.write("</subroutineDec>\n");
        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileSubroutineBody() {
        try {
            writer.write("<subroutineBody>\n");
            writeCurrToke();
            realAdvance();

            while (jToke.tokenType() == JackTokenizer.types.KEYWORD
                    && jToke.keyWord() == JackTokenizer.keys.VAR) {
                compileVarDec();
            }


            while (jToke.tokenType() == JackTokenizer.types.KEYWORD &&
                    (jToke.keyWord() == JackTokenizer.keys.IF
                            || jToke.keyWord() == JackTokenizer.keys.DO
                            || jToke.keyWord() == JackTokenizer.keys.ELSE
                            || jToke.keyWord() == JackTokenizer.keys.WHILE
                            || jToke.keyWord() == JackTokenizer.keys.RETURN
                            || jToke.keyWord() == JackTokenizer.keys.LET)) {
                compileStatements();
            }

            if (!currToke().equals("}")) {
                err.println("incorrect format! compileSubroutineBody");
                exit(0);
            }

            writeCurrToke();
            realAdvance();

            writer.write("</subroutineBody>\n");
        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileParameterList() {
        try {
            writer.write("<parameterList>\n");
            if (!currToke().equals(")")) {
                if (jToke.keyWord() != JackTokenizer.keys.INT
                        && jToke.keyWord() != JackTokenizer.keys.CHAR
                        && jToke.keyWord() != JackTokenizer.keys.BOOLEAN
                        && jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                    err.println("incorrect format! compileParameterList 1");
                    exit(0);
                }
                writeCurrToke();
                realAdvance();
                if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                    err.println("incorrect format! compileParameterList 2");
                    exit(0);
                }
                writeCurrToke();
                realAdvance();
                while (!currToke().equals(")")) {
                    if (!currToke().equals(",")) {
                        err.println("incorrect format! compileParameterList 3");
                        exit(0);
                    }
                    writeCurrToke();
                    realAdvance();
                    if (jToke.keyWord() != JackTokenizer.keys.INT
                            && jToke.keyWord() != JackTokenizer.keys.CHAR
                            && jToke.keyWord() != JackTokenizer.keys.BOOLEAN
                            && jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                        err.println("incorrect format! compileParameterList 4");
                        exit(0);
                    }
                    writeCurrToke();
                    realAdvance();
                    if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                        err.println("incorrect format! compileParameterList 5");
                        exit(0);
                    }
                    writeCurrToke();
                    realAdvance();
                }

            }

            writer.write("</parameterList>\n");

        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileVarDec() {
        try {
            writer.write("<varDec>\n");
            writeCurrToke();
            realAdvance();
            writeCurrToke();
            realAdvance();
            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                err.println("incorrect format! compileVarDec");
                exit(0);
            }
            writeCurrToke();
            realAdvance();
            writeLoop();
            writeCurrToke();
            realAdvance();
            writer.write("</varDec>\n");
        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileStatements() {
        try {
            writer.write("<statements>\n");
            while (!currToke().equals("}")) {
                switch (currToke()) {
                    case "let":
                        compileLet();
                        realAdvance();
                        break;
                    case "if":
                        compileIf();
                        break;
                    case "while":
                        compileWhile();
                        realAdvance();
                        break;
                    case "do":
                        compileDo();
                        realAdvance();
                        break;
                    case "return":
                        compileReturn();
                        realAdvance();
                        break;
                    default:
                        System.err.println("compileStatements problemo");
                        exit(0);
                }
            }
            writer.write("</statements>\n");

        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileDo() {
        try {
            writer.write("<doStatement>\n");
            writeCurrToke();
            realAdvance();

            compileSubroutineCall(false);

            if (!currToke().equals(";")) {
                err.println("incorrect format! in compileDo()");
                exit(0);
            }
            writeCurrToke();

            writer.write("</doStatement>\n");

        } catch (IOException x) {
            err.println(x);
        }


    }

    public void compileLet() {
        try {
            writer.write("<letStatement>\n");

            writeCurrToke();
            realAdvance();

            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                err.println("incorrect format! in compileLet()");
                exit(0);
            }
            writeCurrToke();
            realAdvance();

            if (currToke().equals("[")) {
                writeCurrToke();
                compileExpression();
                writeCurrToke();
                realAdvance();
            } else if (currToke().equals("=")) {
                writeCurrToke();
                realAdvance();
            } else {
                err.println("incorrect format! in compileLet()");
                exit(0);
            }
            compileExpression();
            if (currToke().equals(";")) {
                writeCurrToke();
            } else {
                err.println("incorrect format! in compileLet()");
                exit(0);
            }

            writer.write("</letStatement>\n");

        } catch (IOException x) {
            err.println(x);
        }

    }

    public void compileWhile() {
        try {
            writer.write("<whileStatement>\n");
            writeCurrToke();
            realAdvance();

            if (!currToke().equals("(")) {
                err.println("incorrect format! in compileWhile() 1");
                exit(0);
            }
            writeCurrToke();
            realAdvance();
            compileExpression();
            if (!currToke().equals(")")) {
                err.println("incorrect format! in compileWhile() 2");
                exit(0);
            }
            writeCurrToke();
            realAdvance();
            if (!currToke().equals("{")) {
                err.println("incorrect format! in compileWhile() 3");
                exit(0);
            }
            writeCurrToke();

            realAdvance();

            compileStatements();

            writeCurrToke();
            writer.write("</whileStatement>\n");


        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileReturn() {
        try {
            writer.write("<returnStatement>\n");
            writeCurrToke();
            realAdvance();

            if (!currToke().equals(";")) {
                compileExpression();
            }

            writeCurrToke();
            writer.write("</returnStatement>\n");
        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileIf() {
        try {
            writer.write("<ifStatement>\n");
            writeCurrToke();
            realAdvance();
            if (!currToke().equals("(")) {
                err.println("incorrect format! in compileIf() 1");
                exit(0);
            }
            writeCurrToke();
            realAdvance();
            compileExpression();
            if (!currToke().equals(")")) {
                err.println("incorrect format! in compileIf() 2");
                exit(0);
            }
            writeCurrToke();
            realAdvance();
            if (!currToke().equals("{")) {
                err.println("incorrect format! in compileIf() 3");
                exit(0);
            }
            writeCurrToke();
            realAdvance();


            compileStatements();

            writeCurrToke();
            realAdvance();

            if (jToke.tokenType() == JackTokenizer.types.KEYWORD
                    && jToke.keyWord() == JackTokenizer.keys.ELSE) {
                writeCurrToke();
                realAdvance();

                if (!currToke().equals("{")) {
                    err.println("incorrect format! in compileIf() 3");
                    exit(0);
                }
                writeCurrToke();
                realAdvance();


                compileStatements();

                writeCurrToke();
            }

            writer.write("</ifStatement>\n");

        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileExpression() {
        try {
            writer.write("<expression>\n");

            compileTerm();

            while ("s+-*/&|<>=".contains(jToke.returnToken())) {
                writeCurrToke();
                realAdvance();
                compileTerm();
            }


            writer.write("</expression>\n");


        } catch (IOException x) {
            err.println(x);
        }
    }

    public void compileTerm() {
        try {
            writer.write("<term>\n");
            switch (currToke()) {
                case "(":
                    writeCurrToke();
                    realAdvance();
                    compileExpression();
                    if (!currToke().equals(")")) {
                        err.println("incorrect format! in compileTerm()");
                        exit(0);
                    }
                    writeCurrToke();
                    realAdvance();
                    break;
                case "-":
                case "~":
                    writeCurrToke();
                    realAdvance();
                    compileTerm();
                    break;
                default:
                    switch (jToke.tokenType()) {
                        case KEYWORD:
                            if (jToke.keyWord() != JackTokenizer.keys.TRUE
                                    && jToke.keyWord() != JackTokenizer.keys.FALSE
                                    && jToke.keyWord() != JackTokenizer.keys.NULL
                                    && jToke.keyWord() != JackTokenizer.keys.THIS) {
                                err.println("incorrect format! in compileTerm()");
                                exit(0);
                            }
                            writeCurrToke();
                            realAdvance();
                            break;
                        case INT_CONST:
                            writeCurrToke();
                            realAdvance();
                            break;
                        case STRING_CONST:
                            writeCurrToke();
                            realAdvance();
                            break;
                        case IDENTIFIER:
                            saveCurrToke();
                            realAdvance();
                            // Remember that you have to save the token
                            if (currToke().equals("[")) {
                                writeSavedToke();
                                writeCurrToke();
                                compileExpression();
                                if (!currToke().equals("]")) {
                                    err.println("incorrect format! in compileTerm()");
                                    exit(0);
                                }
                                realAdvance();
                            } else if (currToke().equals("(")
                                    || currToke().equals(".")) {
                                compileSubroutineCall(true);
                            } else {
                                writeSavedToke();
                            }

                    }
                    break;
            }
            writer.write("</term>\n");
        } catch (IOException e) {
            err.println(e);
        }
    }

    // Boolean saved should only be true if
    // it is being called from inside compile term
    private void compileSubroutineCall(Boolean saved) {

        if (saved) {
            writeSavedToke();
        } else {
            writeCurrToke();
            realAdvance();
        }

        switch (currToke()) {
            case "(":
                writeCurrToke();
                realAdvance();
                compileExpressionList();
                if (!currToke().equals(")")) {
                    err.println("incorrect format! in compileSubroutineCall()");
                    exit(0);
                }
                writeCurrToke();
                realAdvance();
                break;
            case ".":
                writeCurrToke();
                realAdvance();
                if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                    err.println("incorrect format! in compileSubroutineCall()");
                    exit(0);
                }
                writeCurrToke();
                realAdvance();
                if (!currToke().equals("(")) {
                    err.println("incorrect format! in compileSubroutineCall()");
                    exit(0);
                }
                writeCurrToke();
                realAdvance();
                compileExpressionList();
                if (!currToke().equals(")")) {
                    err.println("incorrect format! in compileSubroutineCall()");
                    exit(0);
                }
                writeCurrToke();
                realAdvance();
                break;
            default:
                err.println("incorrect format! in compileDo()");
                exit(0);
        }


    }

    public void compileExpressionList() {
        try {
            writer.write("<expressionList>\n");
            if (!currToke().equals(")")) {
                compileExpression();
                while (!currToke().equals(")")) {
                    if (!currToke().equals(",")) {
                        err.println("incorrect format!");
                        exit(0);
                    }
                    writeCurrToke();
                    realAdvance();
                    compileExpression();
                }

            }

            writer.write("</expressionList>\n");

        } catch (IOException x) {
            err.println(x);
        }
    }

    private void writeCurrToke() {
        try {
            String tokenType = currTokeType();

            String line = "<" + tokenType + "> " + currToke() + " </" + tokenType + ">\n";
            writer.write(line);
        } catch (IOException e) {
            err.println(e);
        }
    }

    private void saveCurrToke() {
        String tokenType = currTokeType();
        savedToken = "<" + tokenType + "> " + currToke() + " </" + tokenType + ">\n";
    }

    private void writeSavedToke() {
        try {
            writer.write(savedToken);
        } catch (IOException e) {
            err.println(e);
        }
    }

    private String currTokeType() {
        String tokenType = jToke.tokenType().toString().toLowerCase();

        if (tokenType.equals("int_const")) {
            tokenType = "integerConstant";
        }
        if (tokenType.equals("string_const")) {
            tokenType = "stringConstant";
        }
        return tokenType;
    }

    private String currToke() {
        String token = jToke.returnToken();

        token = token.replace("&", "&amp;");
        token = token.replace("<", "&lt;");
        token = token.replace(">", "&gt;");

        return token;
    }

    private void writeLoop() {
        while (!currToke().equals(";")) {
            if (!currToke().equals(",")) {
                err.println("incorrect format!");
                exit(0);
            }
            writeCurrToke();
            realAdvance();
            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                err.println("incorrect format!");
                exit(0);
            }
            writeCurrToke();
            realAdvance();
        }
    }

    private void realAdvance() {
        if (jToke.hasMoreTokens()) {
            jToke.advance();
            if (jToke.tokenType() == JackTokenizer.types.COMMENT)
                realAdvance();
        } else {
            err.println("incorrect format!");
            exit(0);
        }

    }


}
