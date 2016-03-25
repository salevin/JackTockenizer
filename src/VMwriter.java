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
        try{
            writer.write(command.toString());
        }catch (IOException x) {
            err.println(x);
        }
    }

    public void writeLabel(String label){
        try{
            writer.write(label);
        }catch (IOException x) {
            err.println(x);
        }
    }

    public void writeGoto(String str){
        try{
            writer.write("goto " + str + "\n");
        }catch (IOException e) {
            err.println(e);
        }
    }

    public void writeIf(String str){
        try{
            writer.write("if-goto " + str + "\n");
        }catch (IOException e) {
            err.println(e);
        }
    }

    public void writeCall(String name, int nArgs){
        try{
            writer.write(String.format("call %s %d\n", name, nArgs));
        }catch (IOException e) {
            err.println(e);
        }
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
        try{
            writer.write("return\n");
        }catch (IOException e) {
            err.println(e);
        }
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
