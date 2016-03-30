import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static java.lang.System.err;
import static java.lang.System.exit;

/**
 * Created by sam on 2/2/16.
 */
class CompilationEngine {

    private JackTokenizer jToke;
    private BufferedWriter writer;
    private String savedToken;
    private SymbolTable sTable;
    private String[] prevTwo;
    private String className;
    private String savedTokeType;
    private String funcName;
    private String vmName;
    private int vmArgs;
    private boolean prevWritten;
    private int whileIndex;
    private int ifIndex;
    private VMwriter VMwrite;

    CompilationEngine(String inputPath, String outputPath) {
        jToke = new JackTokenizer(inputPath);
        sTable = new SymbolTable();
        prevTwo = new String[]{"", ""};
        className = funcName = "";
        vmArgs = whileIndex = ifIndex = 0;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
        } catch (IOException x) {
            err.println(x);
        }
        VMwrite = new VMwriter(outputPath.substring(0, outputPath.length() - 4));
    }

    void compileClass() {
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
                className = currToke();
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
            VMwrite.close();
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

    private void compileSubroutineDec() {
        try {
            sTable.startSubroutine();
            whileIndex = ifIndex = 0;
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

    private void compileSubroutineBody() {
        try {
            writer.write("<subroutineBody>\n");
            writeCurrToke();
            realAdvance();

            while (jToke.tokenType() == JackTokenizer.types.KEYWORD
                    && jToke.keyWord() == JackTokenizer.keys.VAR) {
                compileVarDec();
            }

            VMwrite.writeFunction(funcName, sTable.VarCount(sTable.toKind("VAR")));


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

    private void compileParameterList() {
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

    private void compileVarDec() {
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

    private void compileStatements() {
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
                        err.println("compileStatements problemo");
                        exit(0);
                }
            }
            writer.write("</statements>\n");

        } catch (IOException x) {
            err.println(x);
        }
    }

    private void compileDo() {
        try {
            writer.write("<doStatement>\n");
            writeCurrToke();
            realAdvance();

            compileSubroutineCall(false);

            if (!currToke().equals(";")) {
                err.println("incorrect format! in compileDo()");
                exit(0);
            }
            VMwrite.writeCall(vmName, vmArgs);
            writeCurrToke();

            VMwrite.writePop(VMwriter.Segment.TEMP, 0);

            writer.write("</doStatement>\n");

        } catch (IOException x) {
            err.println(x);
        }


    }

    private void compileLet() {
        try {
            writer.write("<letStatement>\n");

            writeCurrToke();
            realAdvance();

            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                err.println("incorrect format! in compileLet() 1");
                exit(0);
            }
            writeCurrToke();
            String pop = currToke();
            realAdvance();

            if (currToke().equals("[")) {
                VMwriter.Segment seg =
                        VMwrite.toSegment(sTable.KindOf(pop).toString());
                int index = sTable.IndexOf(pop);
                writeCurrToke();
                realAdvance();
                compileExpression();
                VMwrite.writePush(seg, index);
                VMwrite.writeArithmetic(VMwriter.Command.ADD);
                writeCurrToke();
                realAdvance();
            }

            if (currToke().equals("=")) {
                writeCurrToke();
                realAdvance();
            } else {
                err.println("incorrect format! in compileLet() 2");
                exit(0);
            }
            compileExpression();
            if (currToke().equals(";")) {
                writeCurrToke();
            } else {
                err.println("incorrect format! in compileLet() 3");
                writer.close();
                exit(0);
            }

            VMwrite.writePop(VMwrite.toSegment(sTable.KindOf(pop).toString()), sTable.IndexOf(pop));

            writer.write("</letStatement>\n");

        } catch (IOException x) {
            err.println(x);
        }

    }

    private void compileWhile() {
        try {
            writer.write("<whileStatement>\n");

            int currWhileIndex = whileIndex;
            whileIndex++;

            writeCurrToke();
            realAdvance();

            VMwrite.writeLabel("WHILE_EXP" + currWhileIndex);

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

            VMwrite.writeArithmetic(VMwriter.Command.NOT);
            VMwrite.writeIf("WHILE_END" + currWhileIndex);

            if (!currToke().equals("{")) {
                err.println("incorrect format! in compileWhile() 3");
                exit(0);
            }
            writeCurrToke();

            realAdvance();

            compileStatements();

            writeCurrToke();

            VMwrite.writeGoto("WHILE_EXP" + currWhileIndex);
            VMwrite.writeLabel("WHILE_END" + currWhileIndex);

            writer.write("</whileStatement>\n");


        } catch (IOException x) {
            err.println(x);
        }
    }

    private void compileReturn() {
        try {
            writer.write("<returnStatement>\n");
            writeCurrToke();
            realAdvance();

            if (!currToke().equals(";")) {
                compileExpression();
                VMwrite.writeReturn(false);
            } else VMwrite.writeReturn(true);

            writeCurrToke();
            writer.write("</returnStatement>\n");
        } catch (IOException x) {
            err.println(x);
        }
    }

    private void compileIf() {
        try {
            writer.write("<ifStatement>\n");
            int currIndex = ifIndex;
            ifIndex++;
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
            VMwrite.writeIf("IF_TRUE" + currIndex);
            VMwrite.writeGoto("IF_FALSE" + currIndex);
            VMwrite.writeLabel("IF_TRUE" + currIndex);
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

                VMwrite.writeGoto("IF_END" + currIndex);
                VMwrite.writeLabel("IF_FALSE" + currIndex);

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
                VMwrite.writeLabel("IF_END" + currIndex);
            } else VMwrite.writeLabel("IF_FALSE" + currIndex);

            writer.write("</ifStatement>\n");

        } catch (IOException x) {
            err.println(x);
        }
    }

    private void compileExpression() {
        try {
            writer.write("<expression>\n");

            compileTerm();

            while ("s+-*/&|<>=".contains(jToke.returnToken())) {
                String toke = jToke.returnToken();

                writeCurrToke();
                realAdvance();
                compileTerm();
                switch (toke) {
                    case "*":
                        VMwrite.writeCall("Math.multiply", 2);
                        break;
                    case "/":
                        VMwrite.writeCall("Math.divide", 2);
                        break;
                    default:
                        VMwrite.writeArithmetic(VMwrite.toCommand(toke));
                        break;
                }
            }


            writer.write("</expression>\n");


        } catch (IOException x) {
            err.println(x);
        }
    }

    private void compileTerm() {

        String name;
        int args;
        String orignialName;

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
                    String arith = currToke();
                    writeCurrToke();
                    realAdvance();
                    compileTerm();
                    VMwrite.writeArithmetic(VMwrite.toCommand(arith));
                    break;
                default:
                    switch (jToke.tokenType()) {
                        case KEYWORD:
                            switch (jToke.keyWord()) {
                                case TRUE:
                                    VMwrite.writePush(VMwriter.Segment.CONSTANT, 0);
                                    VMwrite.writeArithmetic(VMwriter.Command.NOT);
                                    break;
                                case FALSE:
                                case NULL:
                                    VMwrite.writePush(VMwriter.Segment.CONSTANT, 0);
                                    break;
                                case THIS:
                                    VMwrite.writePush(VMwriter.Segment.POINTER, 0);
                                    break;
                                default:
                                    err.println("incorrect format! in compileTerm()");
                                    exit(0);
                            }
                            writeCurrToke();
                            realAdvance();
                            break;
                        case INT_CONST:
                            VMwrite.writePush(VMwriter.Segment.CONSTANT, Integer.valueOf(currToke()));
                            writeCurrToke();
                            realAdvance();
                            break;
                        case STRING_CONST:
                            writeCurrToke();
                            VMwrite.writePush(VMwriter.Segment.CONSTANT, currToke().length());
                            VMwrite.writeCall("String.new", 1);
                            for (int i = 0; i < currToke().length(); i++) {
                                VMwrite.writePush(VMwriter.Segment.CONSTANT, (int) currToke().charAt(i));
                                VMwrite.writeCall("String.appendChar", 2);
                            }

                            realAdvance();
                            break;
                        case IDENTIFIER:
                            saveCurrToke();
                            orignialName = name = currToke();
                            args = 0;
                            realAdvance();
                            // Remember that you have to save the token
                            if (currToke().equals("[")) {
                                writeSavedToke();
                                VMwriter.Segment seg =
                                        VMwrite.toSegment(sTable.KindOf(name).toString());
                                int index = sTable.IndexOf(name);
                                writeCurrToke();
                                realAdvance();
                                compileExpression();
                                VMwrite.writePush(seg, index);
                                VMwrite.writeArithmetic(VMwriter.Command.ADD);
                                if (!currToke().equals("]")) {
                                    err.println("incorrect format! in compileTerm()");
                                    exit(0);
                                }
                                writeCurrToke();
                                realAdvance();
                            } else if (currToke().equals("(")
                                    || currToke().equals(".")) {
                                vmArgs = args;
                                vmName = name;
                                compileSubroutineCall(true);
                                int index = sTable.IndexOf(orignialName);
                                if (index != -1) {
                                    VMwrite.writePush(VMwrite.toSegment(sTable.KindOf(orignialName).toString()), index);
                                }
                                VMwrite.writeCall(vmName, vmArgs);
                            } else {
                                writeSavedToke();
                                VMwriter.Segment seg =
                                        VMwrite.toSegment(sTable.KindOf(name).toString());
                                int index = sTable.IndexOf(name);
                                VMwrite.writePush(seg, index);
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
            vmName = currToke();
            vmArgs = 0;
            realAdvance();
        }

        switch (currToke()) {
            case "(":
                writeCurrToke();
                VMwrite.writePush(VMwriter.Segment.POINTER, 0);
                vmName = className + "." + vmName;
                vmArgs++;
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
                if (sTable.IndexOf(vmName) != -1) {
                    VMwrite.writePush(VMwrite.toSegment(sTable.KindOf(vmName).toString()), sTable.IndexOf(vmName));
                    vmName = sTable.TypeOf(vmName);
                    vmArgs++;
                }
                vmName += currToke();
                realAdvance();
                if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                    err.println("incorrect format! in compileSubroutineCall()");
                    exit(0);
                }
                writeCurrToke();
                vmName += currToke();
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

    private void compileExpressionList() {
        try {
            writer.write("<expressionList>\n");
            if (!currToke().equals(")")) {
                vmArgs += 1;
                compileExpression();
                while (!currToke().equals(")")) {
                    if (!currToke().equals(",")) {
                        err.println("incorrect format!");
                        exit(0);
                    }
                    writeCurrToke();
                    vmArgs += 1;
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

            VMwrite.setPrevToken(currToke());

            if (tokenType.equals("identifier") && sTable.IndexOf(currToke()) != -1) {
                writeTable(false, currToke());
                prevWritten = false;
            } else if (tokenType.equals("identifier") && !"{;".contains(prevTwo[1])) {
                checkTable();
            } else if (!prevWritten || !currToke().equals(",")) {
                prevWritten = false;
                prevTwo[1] = prevTwo[0];
                prevTwo[0] = currToke();
            }

            String line = "<" + tokenType + "> " + currToke() + " </" + tokenType + ">\n";
            writer.write(line);
        } catch (IOException e) {
            err.println(e);
        }
    }

    private void saveCurrToke() {
        savedTokeType = currTokeType();
        savedToken = currToke();
    }

    private void writeSavedToke() {
        try {

            VMwrite.setPrevToken(currToke());

            if (savedTokeType.equals("identifier") && sTable.IndexOf(savedToken) != -1) {
                writeTable(false, savedToken);
                prevWritten = false;
            } else if (savedTokeType.equals("identifier") && !"{;".contains(prevTwo[1])) {
                checkTable();
            } else if (!prevWritten || !savedTokeType.equals(",")) {
                prevTwo[1] = prevTwo[0];
                prevTwo[0] = savedToken;
                prevWritten = false;
            }

            String tokenType = savedTokeType;

            writer.write("<" + tokenType + "> " + savedToken + " </" + tokenType + ">\n");
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

    private void checkTable() {
        String name = currToke();
        SymbolTable.Kind idKind;
        if (!prevTwo[1].isEmpty()) {
            if (jToke.tokenType() == JackTokenizer.types.IDENTIFIER) {
                if (sTable.IndexOf(name) == -1) {
                    switch (prevTwo[1]) {
                        case "(":
                        case ",":
                            sTable.Define(name, prevTwo[0], SymbolTable.Kind.ARG);
                            break;
                        case "constructor":
                            VMwrite.setConstructor(sTable.VarCount(sTable.toKind("FIELD")));
                            funcName = name;
                            return;
                        case "method":
                        case "function":
                            funcName = name;
                            name = "this";
                            sTable.Define(name, className, SymbolTable.Kind.ARG);
                            break;
                        default:
                            idKind = sTable.toKind(prevTwo[1]);
                            if (idKind != SymbolTable.Kind.NONE)
                                sTable.Define(name, prevTwo[0], idKind);
                            else {
                                prevTwo[1] = prevTwo[0];
                                prevTwo[0] = name;
                                return;
                            }
                            prevWritten = true;
                            break;
                    }
                    writeTable(true, name);
                }
            }
        }
    }

    private void writeTable(Boolean defined, String name) {
        try {
            if (defined)
                writer.write("Defined: " + name + " Type: " + sTable.TypeOf(name) + " Kind: " + sTable.KindOf(name)
                        + " Index: " + sTable.IndexOf(name) + "\n");
            else
                writer.write("Called: " + name + " Type: " + sTable.TypeOf(name) + " Kind: " + sTable.KindOf(name)
                        + " Index: " + sTable.IndexOf(name) + "\n");
        } catch (IOException e) {
            err.println(e);
        }
    }


}
