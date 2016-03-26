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
    }

    public enum Segment{
        CONSTANT, ARGUMENT, LOCAL, STATIC, THIS, THAT, POINTER, TEMP
    }
    public enum Command{
        ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
    }

    public void writePush(Segment seg, int index ){
        try{
            writer.write(String.format("push %s %d\n", seg.toString(), index));
        }catch (IOException e) {
            err.println(e);
        }
    }

    public void writePop(Segment seg, int index){
        try{
            writer.write(String.format("pop %s %d\n", seg.toString().toLowerCase(), index));
        }catch (IOException e) {
            err.println(e);
        }
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
            } else {
                writer.write("push argument 0\n");
            }
            writer.write("pop pointer 0\n");

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

    Segment toSegment(String seg){
        switch (seg) {
            case "FIELD":
            case "STATIC":
                return Segment.THIS;
            case "VAR":
                return Segment.LOCAL;
            case "ohs":
                return Segment.THAT;
            case "ARG":
                return Segment.ARGUMENT;
            case "method":
                return Segment.POINTER;
            case "Ed":
                return Segment.STATIC;
            case "static":
                return Segment.TEMP;
            case "var":
            case "int":
                return Segment.CONSTANT;
            default:
                err.println("no such segment");
                return null;
        }
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException e) {
            err.println(e);
        }
    }




    }
