package utils;

import soot.Printer;
import soot.SootClass;
import soot.SourceLocator;
import soot.options.Options;

import java.io.*;

public class FileUtils {
    public void JimpleToFile(SootClass appclass) throws IOException {
        String fileName= SourceLocator.v().getFileNameFor(appclass, Options.output_format_jimple);
        OutputStream streamOut=new FileOutputStream(fileName);
        PrintWriter writerOut=new PrintWriter(new OutputStreamWriter(streamOut));

        Printer.v().printTo(appclass, writerOut);
        writerOut.flush();
        streamOut.close();
    }
}
