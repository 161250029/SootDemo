

import java.io.*;
import java.util.Collections;
import java.util.HashMap;

import soot.*;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;

public class SPARKBasicTest {
    public static String processDir = "src/main/java";

    public static void main(String[] args) throws IOException {

        System.out.println("start ...");
        soot.G.reset();
        Options.v().set_src_prec(Options.src_prec_java);
        Options.v().set_process_dir(Collections.singletonList(processDir));
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
//        Options.v().set_verbose(true);
        Options.v().set_output_format(Options.output_format_none);
        Options.v().setPhaseOption("cg.spark", "on");
        Options.v().set_no_bodies_for_excluded(true);
//        Options.v().set_app(true);
        enableSpark();
        Scene.v().loadNecessaryClasses();
        Scene.v().loadBasicClasses();
        String mainClass = "Test1";
        loadClass("Item",false);
        loadClass("Container",false);
        SootClass c = loadClass(mainClass,true);

//        PackManager.v().runPacks();

        CallGraph callGraph = Scene.v().getCallGraph();
        System.out.println("the call graph edge number is : "+ callGraph.size());
        System.out.println("done ...");
    }

    private static SootClass loadClass(String name, boolean main) {
        SootClass c = Scene.v().loadClassAndSupport(name);
        c.setApplicationClass();
        if (main)
            Scene.v().setMainClass(c);
        return c;
    }


    private static void enableSpark(){
        HashMap opt = new HashMap();
        opt.put("verbose","true");
        opt.put("propagator","worklist");
        opt.put("simple-edges-bidirectional","false");
        opt.put("on-fly-cg","true");
        opt.put("apponly", "true");
        opt.put("set-impl","double");
        opt.put("double-set-old","hybrid");
        opt.put("double-set-new","hybrid");
        SparkTransformer.v().transform("",opt);
    }
}
