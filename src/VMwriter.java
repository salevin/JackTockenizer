import com.sun.xml.internal.bind.v2.TODO;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static java.lang.System.err;

/**
 * Created by john on 3/24/16.
 */
public class VMwriter {


    private BufferedWriter writer;

    public VMwriter(String outputPath) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
        } catch (IOException x) {
            err.println(x);
        }
    }


    public enum Segment{
        CONST, ARG, LOCAL, STATIC, THIS, THAT, POINTER, TEMP
    }
    public enum Command{
        ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
    }

    public void writePush(Segment seg, int index ){
    //TODO
    }

    public void writePop(Segment seg, int index){
        //TODO
    }

    public void writeArithmetic(Command command){
       //TODO
    }

    public void writeLabel(String label){
        //TODO
    }

    public void writeGoto(String str){
        //TODO
    }

    public void writeIf(String str){
        //TODO
    }

    public void writeCall(String name, int nArgs){
        //TODO
    }

    public void writeFunction(String name, int nLocals){
        //TODO
    }

    public void writeReturn(){
        //TODO
    }

    public void close(){
        //TODO close the output file
    }




    }
