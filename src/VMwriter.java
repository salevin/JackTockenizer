import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static java.lang.System.err;

/**
 * Created by john on 3/24/16.
 */
class VMwriter {


    private BufferedWriter writer;
    private String className;
    private boolean constructor;
    private boolean function;
    private int fields;
    private boolean commandB;
    private String[] prevToken = {"", "", ""};

    VMwriter(String outputPath) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath + ".vm1")));
        } catch (IOException x) {
            err.println(x);
        }

        className = outputPath.substring(outputPath.lastIndexOf("/") + 1, outputPath.length());
        constructor = false;
        commandB = false;
        function = false;
    }

    void writePush(Segment seg, int index) {
        try {
            writer.write(String.format("push %s %d\n", seg.toString().toLowerCase(), index));
            commandB = false;
        } catch (IOException e) {
            err.println(e);
        }
    }

    void writePop(Segment seg, int index) {
        commandB = false;
        try {
            writer.write(String.format("pop %s %d\n", seg.toString().toLowerCase(), index));
        } catch (IOException e) {
            err.println(e);
        }
    }

    void writeArithmetic(Command command) {

        try {
            writer.write(command.toString().toLowerCase() + "\n");
        } catch (IOException x) {
            err.println(x);
        }
    }

    void writeLabel(String label) {

        try {
            writer.write(String.format("label %s\n", label));
        } catch (IOException x) {
            err.println(x);
        }
    }

    void writeGoto(String str) {

        try {
            writer.write(String.format("goto %s\n", str));
        } catch (IOException e) {
            err.println(e);
        }
    }

    void writeIf(String str) {

        try {
            writer.write(String.format("if-goto %s\n", str));
        } catch (IOException e) {
            err.println(e);
        }
    }

    void writeCall(String name, int nArgs) {
        commandB = false;
        try {
            writer.write(String.format("call %s %d\n", name, nArgs));
        } catch (IOException e) {
            err.println(e);
        }
    }

    void writeFunction(String name, int nLocals) {
        commandB = false;
        try {
            writer.write(String.format("function %s.%s %d\n", className, name, +nLocals));

            if (name.equals("main")) {
                return;
            }

            if (constructor) {
                writer.write(String.format("push constant %d\ncall Memory.alloc 1\n", fields));
                constructor = false;
            }

            else if (function){
                constructor = false;
                function = false;
                return;
            }

            else writer.write("push argument 0\n");
            writer.write("pop pointer 0\n");

        } catch (IOException e) {
            err.println(e);
        }
    }

    void writeReturn(boolean isVoid) {

        try {
            if (isVoid)
                writer.write("push constant 0\n");
            writer.write("return\n");
        } catch (IOException e) {
            err.println(e);
        }
    }

    void setConstructor(int num) {
        commandB = false;
        constructor = true;
        fields = num;
    }

    void setFunction () {
        commandB = false;
        function = true;
    }

    Segment toSegment(String seg) {

        switch (seg) {
            case "FIELD":
                return Segment.THIS;
            case "VAR":
                return Segment.LOCAL;
            case "ohs":
                return Segment.THAT;
            case "ARG":
                return Segment.ARGUMENT;
            case "method":
                return Segment.POINTER;
            case "STATIC":
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

    Command toCommand(String com) {

        switch (com) {
            case "<":
                commandB = true;
                return Command.LT;
            case ">":
                commandB = true;
                return Command.GT;
            case "-":
                if (commandB || "{([,&|<>=~,".contains(prevToken[2])) {
                    return Command.NEG;
                }
                commandB = true;
                return Command.SUB;
            case "=":
                commandB = true;
                return Command.EQ;
            case "|":
                commandB = true;
                return Command.OR;
            case "+":
                commandB = true;
                return Command.ADD;
            case "~":
                commandB = true;
                return Command.NOT;
            case "&":
                commandB = true;
                return Command.AND;
            default:
                err.println("no such command");
                return null;
        }
    }

    void setPrevToken(String prev) {
        prevToken[2] = prevToken[1];
        prevToken[1] = prevToken[0];
        prevToken[0] = prev;
    }

    void close() {
        try {
            writer.close();
        } catch (IOException e) {
            err.println(e);
        }
    }

    enum Segment {
        CONSTANT, ARGUMENT, LOCAL, STATIC, THIS, THAT, POINTER, TEMP
    }

    enum Command {
        ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
    }


}
