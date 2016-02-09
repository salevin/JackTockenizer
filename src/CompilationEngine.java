import com.sun.xml.internal.bind.v2.TODO;

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
            writer.write("<tokens>\n");
            String tokenType;
            String token;
            while (jToke.hasMoreTokens()) {
                jToke.advance();
                tokenType = jToke.tokenType().toString().toLowerCase();
                token = jToke.returnToken();
                if (tokenType.equals("int_const")){
                    tokenType = "integerConstant";
                }
                if (tokenType.equals("string_const")){
                    tokenType = "stringConstant";
                }
                if (jToke.tokenType() != JackTokenizer.types.COMMENT){
                    token = token.replace("&", "&amp;");
                    token = token.replace("<","&lt;");
                    token = token.replace(">", "&gt;");
                    String line = "<" + tokenType + "> " + token + " </" + tokenType + ">\n";
                    writer.write(line);
                }
            }
            writer.write("</tokens>");
            writer.close();
        } catch (IOException x){
            System.err.println(x);
        }
    }

    public void compileClassVarDec(){
        //TODO
        //Compiles a static declaration or a field declaration.
    }

    public void compileSubroutine(){
        //TODO
        //Compiles a complete method,function, or constructor.
    }

    public void compileParameterList(){
        //TODO
        //Compiles a (possibly empty) parameter list, not including the enclosing ‘‘ () ’’.
    }

    public void compileVarDec(){
        // TODO Compiles a var declaration.
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


}
