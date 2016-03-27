import java.io.File;

public class Jack {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String path = args[0];
        System.out.println(args[0]);
        String[] files = new String[1];
        File directory = new File(path);

        CompilationEngine compiler = null;

        if (directory.isDirectory()) {
            String fileName = directory.getName();
            files = directory.list();
        } else {
            files[0] = directory.getName();
            path = directory.getParent();
        }

        for (String inputFile : files) {
            System.out.println(inputFile);
            int period = inputFile.indexOf('.');
            String extension = inputFile.substring(period + 1);
            String file = inputFile.substring(0, period);

            if (extension.equals("jack")) {
                compiler = new CompilationEngine(path + "/" + inputFile, path + "/" + file + ".xml");
                compiler.compileClass();
            }

        }
    }

}