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
                if (jToke.tokenType() != JackTokenizer.types.COMMENT){
                    token = token.replace("<","&lt;");
                    token = token.replace(">", "&gt;");
                    token = token.replace("&", "&amp;");
                    String line = "<" + tokenType + "> " + token + " </" + tokenType + ">\n  ";
                    writer.write(line);
                }
            }
            writer.write("</tokens>");
            writer.close();
        } catch (IOException x){
            System.err.println(x);
        }
    }
}
