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
        while (jToke.hasMoreTokens()) {
            jToke.advance();
            writer.write();

        }
    }
}
