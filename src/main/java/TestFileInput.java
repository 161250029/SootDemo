import soot.*;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.SpecialInvokeExpr;
import soot.options.Options;
import soot.util.Chain;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class TestFileInput {
	public static final String path = "src/main/java";
 
	public static void main(String args[]) throws IOException {
		initial(path);
		SootClass appclass = Scene.v().loadClassAndSupport("TestMain");//若无法找到，则生成一个。
		appclass.setApplicationClass();
		System.out.println("the main class is :" + appclass.getName());
		//获取类中的相关的方法
		Iterator<SootMethod> methodIt = appclass.getMethods().iterator();
		while(methodIt.hasNext()){
			SootMethod method = methodIt.next();
			if (method.isConcrete()) {
				method.retrieveActiveBody();
			}
//			if (!method.hasActiveBody()) {
//				Body body = method.retrieveActiveBody();
//				method.setActiveBody(body);
//				System.out.println(method.getParameterCount());
//			}
			System.out.println("the function member is : " +  method.getSignature());
		}
//		createConstrutor(appclass);
		String fileName= SourceLocator.v().getFileNameFor(appclass, Options.output_format_jimple);
		OutputStream streamOut=new FileOutputStream(fileName);
		PrintWriter writerOut=new PrintWriter(new OutputStreamWriter(streamOut));

		Printer.v().printTo(appclass, writerOut);
		writerOut.flush();
		streamOut.close();
	}

	private static void createConstrutor(SootClass sClass)
	{
		SootMethod method = new SootMethod("<init>", Arrays.asList(new Type[] {}), VoidType.v(), Modifier.PUBLIC);
		sClass.addMethod(method);
		//添加body
		JimpleBody body = Jimple.v().newBody(method);
		method.setActiveBody(body);
		Chain<Unit> units = body.getUnits();

		//添加this local
		Local thisLocal = Jimple.v().newLocal("this", sClass.getType());
		body.getLocals().add(thisLocal);
		units.add(Jimple.v().newIdentityStmt(thisLocal,
				Jimple.v().newThisRef(sClass.getType())));

		//调用init
		SootMethod oncreateMethod = Scene.v().getMethod("<TestMain: void <init>(int)>");
		SpecialInvokeExpr sie = Jimple.v().newSpecialInvokeExpr(thisLocal, oncreateMethod.makeRef());
		units.add(Jimple.v().newInvokeStmt(sie));

		// insert "return"
		units.add(Jimple.v().newReturnVoidStmt());
	}


	private static void initial(String apkPath) {
		soot.G.reset();
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_prepend_classpath(true);
		Options.v().set_validate(true);
		Options.v().set_output_format(Options.output_format_jimple);
		Options.v().set_src_prec(Options.src_prec_java);
		Options.v().set_process_dir(Collections.singletonList(apkPath));//路径应为文件夹
		Options.v().set_keep_line_number(true);
//		Options.v().set_whole_program(true);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_app(true);
//		 Scene.v().setMainClass(appclass); // how to make it work ?

		Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.Thread", SootClass.SIGNATURES);
		Scene.v().loadNecessaryClasses();
	}
}