import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
            System.err.println(x);
        }
    }

    public void compileClass() {
        try {
            writer.write("<class>\n");
            JackTokenizer.keys key;

            jToke.advance();
            key = jToke.keyWord();

            if (key == JackTokenizer.keys.CLASS){
                writeCurrToke();
            }
            else{
                System.err.println("No class dec!");
                return;
            }

            jToke.advance();

            if (currToke().equals("{")){
                writeCurrToke();
            }
            else{
                System.err.println("incorrect format!");
                return;
            }

            jToke.advance();
            key = jToke.keyWord();


            while (key == JackTokenizer.keys.STATIC
                    || key == JackTokenizer.keys.FIELD) {

                compileClassVarDec();

                jToke.advance();
                key = jToke.keyWord();
            }

            while (key == JackTokenizer.keys.CONSTRUCTOR
                    || key == JackTokenizer.keys.FUNCTION
                    || key == JackTokenizer.keys.METHOD){

                compileSubroutineDec();

                jToke.advance();
                key = jToke.keyWord();
            }

            if (currToke().equals("}")){
                writeCurrToke();
                writer.write("</class>\n");

            }
            else{
                System.err.println("incorrect format!");
            }
            writer.close();
        } catch (IOException x){
            System.err.println(x);
        }
    }

    public void compileClassVarDec(){
        try {
            writer.write("<classVarDec>\n");
            writeCurrToke();
            jToke.advance();

            if (jToke.keyWord() != JackTokenizer.keys.INT
                    && jToke.keyWord() != JackTokenizer.keys.CHAR
                    && jToke.keyWord() != JackTokenizer.keys.BOOLEAN
                    && jToke.tokenType() != JackTokenizer.types.IDENTIFIER
                    && jToke.keyWord() != JackTokenizer.keys.VOID){

                System.err.println("incorrect format!");
                return;
            }

            writeCurrToke();
            jToke.advance();

            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER){
                System.err.println("incorrect format!");
                return;
            }

            writeCurrToke();
            jToke.advance();

            if (!currToke().equals("{")){
                System.err.println("incorrect format!");
                return;
            }

            writeCurrToke();
            jToke.advance();

            writer.write("</classVarDec>");
        } catch (IOException x){
            System.err.println(x);
        }
    }

    public void compileSubroutineDec(){
        try {
            writer.write("<subroutineDec>\n");
            writeCurrToke();
            jToke.advance();

            if (jToke.keyWord() != JackTokenizer.keys.VOID
                    && jToke.keyWord() != JackTokenizer.keys.CHAR
                    && jToke.keyWord() != JackTokenizer.keys.BOOLEAN
                    && jToke.tokenType() != JackTokenizer.types.IDENTIFIER){

                System.err.println("incorrect format!");
                return;
            }

            writeCurrToke();
            jToke.advance();

            if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER){
                System.err.println("incorrect format!");
                return;
            }

            writeCurrToke();
            jToke.advance();

            while (!currToke().equals(";")) {
                if (!currToke().equals(",")){
                    System.err.println("incorrect format!");
                    return;
                }
                writeCurrToke();
                jToke.advance();
                if (jToke.tokenType() != JackTokenizer.types.IDENTIFIER){
                    System.err.println("incorrect format!");
                    return;
                }
                writeCurrToke();
                jToke.advance();
            }
            writeCurrToke();


            writer.write("</subroutineDec>");
        } catch (IOException x){
            System.err.println(x);
        }
    }

    public void compileSubroutineBody(){
        //TODO Compiles a complete method,function, or constructor.
    }

    public void compileParameterList(){
        //TODO
        //Compiles a (possibly empty) parameter list, not including the enclosing ‘‘ () ’’.
    }

    public void compileVarDec(){
        try {
            writer.write("<varDec>\n");
            writer.write("<keyword> var </keyword>\n");
            while (!currToke().equals(";")) {
                jToke.advance();
                writeCurrToke();
            }
            writer.write("</varDec>");
        } catch (IOException x){
            System.err.println(x);
        }
    }

    public void compileStatements(){
        // TODO Compiles a sequence of statements, not including the enclosing ‘‘{}’’.
    }

    public void compileDo(){
        // TODO compiles a do statement
    }

    public void compileLet(){
        // TODO compiles a let statement
    }

    public void compileWhile(){
        // TODO
    }

    public void compileReturn(){
        // TODO
    }

    public void compileIf(){
        // TODO
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
            writer.write(line);
        } catch (IOException e){
            System.err.println(e);
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


}
