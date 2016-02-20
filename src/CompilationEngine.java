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

    public CompilationEngine(String inputPath, String outputPath) {
        jToke = new JackTokenizer(inputPath);
        try{
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

            if (key == JackTokenizer.keys.CLASS){
                writeCurrToke();
            }
            else{
                err.println("No class dec!");
                exit(0);
            }

            realAdvance();

            if (jToke.tokenType() == JackTokenizer.types.IDENTIFIER){
                writeCurrToke();
            }
            else{
                err.println("No class dec!");
                exit(0);
            }

            realAdvance();

            if (currToke().equals("{")){
                writeCurrToke();
            }
            else{
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
                    || key == JackTokenizer.keys.METHOD){

                compileSubroutineDec();

                realAdvance();
                key = jToke.keyWord();
            }

            if (currToke().equals("}")){
                writeCurrToke();
                writer.write("</class>");

            }
            else{
                err.println("incorrect format!");
                exit(0);
            }
            writer.close();
        } catch (IOException x){
            err.println(x);
        }
    }

    public void compileClassVarDec(){
        try {
            writer.write("<classVarDec>\n");
            writeCurrToke();
            realAdvance();

            if (jToke.keyWord() != JackTokenizer.keys.INT
                    && jToke.keyWord() != JackTokenizer.keys.CHAR
                    && jToke.keyWord() != JackTokenizer.keys.BOOLEAN
                    && jToke.tokenType() != JackTokenizer.types.IDENTIFIER){

                err.println("incorrect format!");
                exit(0);
            }

            writeCurrToke();
            realAdvance();

            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER){
                err.println("incorrect format!");
                exit(0);
            }

            writeCurrToke();
            realAdvance();

            writeLoop();

            writeCurrToke();


            writer.write("</classVarDec>\n");
        } catch (IOException x){
            err.println(x);
        }
    }

    public void compileSubroutineDec(){
        try {
            writer.write("<subroutineDec>\n");
            writeCurrToke();
            realAdvance();

            if (jToke.keyWord() != JackTokenizer.keys.INT
                    && jToke.keyWord() != JackTokenizer.keys.CHAR
                    && jToke.keyWord() != JackTokenizer.keys.BOOLEAN
                    && jToke.tokenType() != JackTokenizer.types.IDENTIFIER
                    && jToke.keyWord() != JackTokenizer.keys.VOID){

                err.println("incorrect format!");
                return;
            }

            writeCurrToke();
            realAdvance();

            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER){
                err.println("incorrect format!");
                exit(0);
            }

            writeCurrToke();
            realAdvance();

            if (!currToke().equals("(")){
                err.println("incorrect format!");
                exit(0);
            }

            writeCurrToke();
            realAdvance();

            compileParameterList();

            writeCurrToke();
            realAdvance();

            if (!currToke().equals("{")){
                err.println("incorrect format!");
                exit(0);
            }

            compileSubroutineBody();


            writer.write("</subroutineDec>\n");
        } catch (IOException x){
            err.println(x);
        }
    }

    public void compileSubroutineBody(){
        try {
            writer.write("<subroutineBody>\n");
            writeCurrToke();
            realAdvance();

            while (jToke.tokenType() == JackTokenizer.types.KEYWORD
            && jToke.keyWord() == JackTokenizer.keys.VAR){
                compileVarDec();
            }


            while (jToke.tokenType() == JackTokenizer.types.KEYWORD &&
                    (jToke.keyWord() == JackTokenizer.keys.IF
                    || jToke.keyWord() == JackTokenizer.keys.DO
                    || jToke.keyWord() == JackTokenizer.keys.ELSE
                    || jToke.keyWord() == JackTokenizer.keys.WHILE
                    || jToke.keyWord() == JackTokenizer.keys.RETURN
                    || jToke.keyWord() == JackTokenizer.keys.LET)){
                compileStatements();
            }

            if (!currToke().equals("}")){
                err.println("incorrect format!");
                exit(0);
            }

            writeCurrToke();
            realAdvance();

            writer.write("</subroutineBody>\n");
        } catch (IOException x){
            err.println(x);
        }    }

    public void compileParameterList(){
        try {
            writer.write("</parameterList>");
            if (!currToke().equals(")")) {
                if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER) {
                    err.println("incorrect format!");
                    exit(0);
                }
                writeCurrToke();
                realAdvance();
                while (!currToke().equals(")")){
                    if (!currToke().equals(",")){
                        err.println("incorrect format!");
                        exit(0);
                    }
                    writeCurrToke();
                    realAdvance();
                    if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER){
                        err.println("incorrect format!");
                        exit(0);
                    }
                    writeCurrToke();
                    realAdvance();
                }

            }

            writer.write("</parameterList>\n");

        }
        catch (IOException x){
            err.println(x);
        }
    }

    public void compileVarDec(){
        try {
            writer.write("<varDec>\n");
            writer.write("<keyword> var </keyword>\n");
            writeCurrToke();
            realAdvance();
            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER){
                err.println("incorrect format!");
                exit(0);
            }
            writeCurrToke();
            realAdvance();
            writeLoop();
            writer.write("</varDec>\n");
        } catch (IOException x){
            err.println(x);
        }
    }

    public void compileStatements(){
        switch (currToke()){
            case "let":
                compileLet();
                break;
            case "if":
                compileIf();
                break;
            case "while":
                compileWhile();
                break;
            case "do":
                compileDo();
                break;
            case "return":
                compileReturn();
                break;
            default:
                err.println("incorrect format! in compileStatements()");
                exit(0);
        }
    }

    public void compileDo(){
        // TODO compiles a do statement
    }

    public void compileLet(){
        try {
            writer.write("<letStatement>\n");

            writeCurrToke();
            realAdvance();

            if(jToke.tokenType() != JackTokenizer.types.IDENTIFIER){
                err.println("incorrect format! in compileLet()");
                exit(0);
            }
            writeCurrToke();
            jToke.advance();

            if(currToke().equals("[")){
                writeCurrToke();
                compileExpression();
                writeCurrToke();
            }
            else if(currToke().equals("=")){
                writeCurrToke();
            }
            else{
                err.println("incorrect format! in compileLet()");
                exit(0);
            }
            compileExpression();
            if(currToke().equals(";")){
                writeCurrToke();
            }
            else{
                err.println("incorrect format! in compileLet()");
                exit(0);
            }

            writer.write("</letStatement>\n");

        } catch (IOException x){
            err.println(x);
        }

    }

    public void compileWhile(){
        // TODO
    }

    public void compileReturn(){
        // TODO
    }

    public void compileIf(){
        try {
            writer.write("<ifStatement>\n");
            if(!currToke().equals("(")){
                err.println("incorrect format! in compileLet()");
                exit(0);
            }
            writeCurrToke();
            jToke.advance();
            compileExpression();
            if(!currToke().equals(")")) {
                err.println("incorrect format! in compileLet()");
                exit(0);
            }
            writeCurrToke();
            jToke.advance();
            if(currToke().equals("{")) {
                err.println("incorrect format! in compileLet()");
                exit(0);
            }
            writeCurrToke();
            jToke.advance();


            if(jToke.tokenType() != JackTokenizer.types.IDENTIFIER){
                err.println("incorrect format! in compileLet()");
                exit(0);
            }
            writeCurrToke();
            jToke.advance();

            if(currToke().equals("[")){
                writeCurrToke();
                compileExpression();
                writeCurrToke();
            }
            else if(currToke().equals("=")){
                writeCurrToke();
            }
            else{
                err.println("incorrect format! in compileLet()");
                exit(0);
            }
            compileExpression();
            if(currToke().equals(";")){
                writeCurrToke();
            }
            else{
                err.println("incorrect format! in compileLet()");
                exit(0);
            }

            writer.write("</letStatement>");

        } catch (IOException x){
            err.println(x);
        }
    }

    public void compileExpression(){
        // TODO
    }

    public void compileTerm(){
        // TODO
        /*Compiles a term. This routine is faced with a slight difficulty
        when trying to decide between some of the alternative parsing
        rules. Specifically, if the current token is an identifier, the routine
        must distinguish between a variable, an array entry, and a
        subroutine call. A single look- ahead token, which may be one
        of ‘‘[’’, ‘‘(’’, or ‘‘.’’ suffices to distinguish between the three possi-
        bilities. Any other token is not part of this term and should not
        be advanced over.*/
    }

    public void compileExpressionList(){
        // TODO
        //Compiles a (possibly empty) comma-separated list of expressions.
    }

    private void writeCurrToke(){
        try {
            String tokenType = currTokeType();

            String line = "<" + tokenType + "> " + currToke() + " </" + tokenType + ">\n";
            System.out.print(line);
            writer.write(line);
        } catch (IOException e){
            err.println(e);
        }
    }

    private String currTokeType(){
        String tokenType = jToke.tokenType().toString().toLowerCase();

        if (tokenType.equals("int_const")){
            tokenType = "integerConstant";
        }
        if (tokenType.equals("string_const")){
            tokenType = "stringConstant";
        }
        return tokenType;
    }

    private String currToke(){
        String token = jToke.returnToken();

        token = token.replace("&", "&amp;");
        token = token.replace("<","&lt;");
        token = token.replace(">", "&gt;");

        return token;
    }

    private void writeLoop(){
        while (!currToke().equals(";")) {
            if (!currToke().equals(",")){
                err.println("incorrect format!");
                exit(0);
            }
            writeCurrToke();
            realAdvance();
            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER){
                err.println("incorrect format!");
                exit(0);
            }
            writeCurrToke();
            realAdvance();
        }
    }

    private void realAdvance(){
        if (jToke.hasMoreTokens()){
            jToke.advance();
            if (jToke.tokenType() == JackTokenizer.types.COMMENT)
                realAdvance();
        }
        else {
            err.println("incorrect format!");
            exit(0);
        }

    }


}
