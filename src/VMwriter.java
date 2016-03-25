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
    private String className;
    private boolean constructor;
    private int fields;

    public VMwriter(String outputPath) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath + ".vm1")));
        } catch (IOException x) {
            err.println(x);
        }

        className = outputPath.substring(outputPath.lastIndexOf("/") + 1,outputPath.length());
        constructor = false;

        System.out.println(className);
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
        try {
            writer.write("function " + className + "." + name + " " +
                    + nLocals + "\n");

            if (constructor){
                writer.write("push constant "  + fields +
                        "\ncall Memory.alloc 1\n");
                constructor = false;
            }

        } catch (IOException e) {
            err.println(e);
        }
    }

    public void writeReturn(){
        //TODO
    }

    public void setConstructor(int num){
        constructor = true;
        fields = num;
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException e) {
            err.println(e);
        }
    }




    }
