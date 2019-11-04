package CreateClassFromSoot;

import soot.*;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.util.Chain;

import java.io.*;
import java.util.Arrays;


public class CreateHelloWorldInJimple {
    public static void main(String[] args) throws IOException {
        SootMethod method;
        SootClass sClass=new SootClass("HelloWorld",Modifier.PUBLIC);
        //Scene代表一个容器，包含了一个程序里所有的SootClass
        Scene.v().loadClassAndSupport("java.lang.Object");
        Scene.v().loadClassAndSupport("java.lang.System");
        sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
        Scene.v().addClass(sClass);

        //add method
        //RefType代表引用类型
        method=new SootMethod("main",Arrays.asList(new Type[]{ArrayType.v(RefType.v("java.lang.String"),1)}),VoidType.v(),Modifier.PUBLIC|Modifier.STATIC);
        sClass.addMethod(method);

        //add code to method
        JimpleBody body=Jimple.v().newBody(method);//attach the body to method
        method.setActiveBody(body);

        Local arg=Jimple.v().newLocal("l0",RefType.v("java.lang.String"));
        //Local arg=Jimple.v().newLocal("l0",ArrayType.v(RefType.v("java.lang.String"),1));
        body.getLocals().add(arg);

        //add unit,Units are the statements themselves
        Chain<Unit> units=body.getUnits();
        Unit u=Jimple.v().newIdentityStmt(arg,Jimple.v().newParameterRef(ArrayType.v(RefType.v("java.lang.String"),1),0));
        units.add(u);

        Local tmpRef=Jimple.v().newLocal("tempRef",RefType.v(Scene.v().getSootClass("java.io.PrintStream")));
        //Local tmpRef=Jimple.v().newLocal("tempRef",RefType.v("java.io.PrintStream"));
        body.getLocals().add(tmpRef);
        units.add(Jimple.v().newAssignStmt(tmpRef,Jimple.v().newStaticFieldRef(Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));
        {
            SootMethod toCallMethod=Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
            units.add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef,toCallMethod.makeRef(),StringConstant.v("Hello World!"))));
        }

        System.out.println();
        //insert "return"
        units.add(Jimple.v().newReturnVoidStmt());
        //write to class file
        String fileName=SourceLocator.v().getFileNameFor(sClass, Options.output_format_jimple);
        OutputStream streamOut=new FileOutputStream(fileName);
        PrintWriter writerOut=new PrintWriter(new OutputStreamWriter(streamOut));

        //covert from Jimple to Jasmin
        /*
        JasminClass jasminClass=new JasminClass(sClass);
        jasminClass.print(writerOut);
        writerOut.flush();
        streamOut.close();
        */
        //output jimple source

        Printer.v().printTo(sClass, writerOut);
        writerOut.flush();
        streamOut.close();
        //System.out.println(RefType.v("java.lang.String").toString());
        //System.out.println(RefType.v("java.lang.String").getClassName());
        //System.out.println(method.getActiveBody());
        //System.out.println(sClass.getType());
    }

}