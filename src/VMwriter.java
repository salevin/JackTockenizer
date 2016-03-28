import java.io.*;

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

        className = outputPath.substring(outputPath.lastIndexOf("/") + 1, outputPath.length());
        constructor = false;
    }

    public void writePush(Segment seg, int index) {
        try {
            writer.write(String.format("push %s %d\n", seg.toString().toLowerCase(), index));
        } catch (IOException e) {
            err.println(e);
        }
    }

    public void writePop(Segment seg, int index) {
        try {
            writer.write(String.format("pop %s %d\n", seg.toString().toLowerCase(), index));
        } catch (IOException e) {
            err.println(e);
        }
    }

    public void writeArithmetic(Command command) {
        try {
            writer.write(command.toString().toLowerCase() + "\n");
        } catch (IOException x) {
            err.println(x);
        }
    }

    public void writeLabel(String label) {
        try {
            writer.write(String.format("label %s\n", label));
        } catch (IOException x) {
            err.println(x);
        }
    }

    public void writeGoto(String str) {
        try {
            writer.write(String.format("goto %s\n", str));
        } catch (IOException e) {
            err.println(e);
        }
    }

    public void writeIf(String str) {
        try {
            writer.write(String.format("if-goto %s\n", str));
        } catch (IOException e) {
            err.println(e);
        }
    }

    public void writeCall(String name, int nArgs) {
        try {
            writer.write(String.format("call %s %d\npop temp 0\n", name, nArgs));
        } catch (IOException e) {
            err.println(e);
        }
    }

    public void writeFunction(String name, int nLocals) {
        try {
            writer.write(String.format("function %s.%s %d\n", className, name, +nLocals));

            if (name.equals("main")) {
                return;
            }

            if (constructor) {
                writer.write(String.format("push constant %d\ncall Memory.alloc 1\n", fields));
                constructor = false;
            } else {
                writer.write("push argument 0\n");
            }
            writer.write("pop pointer 0\n");

        } catch (IOException e) {
            err.println(e);
        }
    }

    public void writeReturn(boolean isVoid) {
        try {
            if (isVoid)
                writer.write("push constant 0\n");
            writer.write("return\n");
        } catch (IOException e) {
            err.println(e);
        }
    }

    public void setConstructor(int num) {
        constructor = true;
        fields = num;
    }

    Segment toSegment(String seg) {
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

    Command toCommand(String com) {
        switch (com) {
            case "<":
                return Command.LT;
            case ">":
                return Command.GT;
            case "-":
                return Command.SUB;
            case "=":
                return Command.EQ;
            case "|":
                return Command.OR;
            case "+":
                return Command.ADD;
            case "~":
                return Command.NOT;
            case "&":
                return Command.AND;
            default:
                err.println("no such command");
                return null;
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            err.println(e);
        }
    }

    public enum Segment {
        CONSTANT, ARGUMENT, LOCAL, STATIC, THIS, THAT, POINTER, TEMP
    }

    public enum Command {
        ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
    }


}
