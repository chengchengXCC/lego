package transformers;

import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.util.*;

import java.io.*;
import java.util.*;

import soot.toolkits.graph.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.spark.*;

import soot.tools.*;

public class Main {
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException
    {
        SootClass sClass;
        SootMethod method;
        
        // Resolve Dependencies
           Scene.v().loadClassAndSupport("java.lang.Object");
           Scene.v().loadClassAndSupport("java.lang.System");
           
        // Declare 'public class HelloWorld'   
           sClass = new SootClass("soot.HelloWorld", Modifier.PUBLIC);
        
        // 'extends Object'
           sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
           
           Scene.v().addClass(sClass);
           
        // Create the method, public static void main(String[])
           method = new SootMethod("main",
                Arrays.asList(new Type[] {ArrayType.v(RefType.v("java.lang.String"), 1)}),
                VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
        
           
           sClass.addMethod(method);
           
        // Create the method body
        //{
            // create empty body
            JimpleBody body = Jimple.v().newBody(method);
            
            method.setActiveBody(body);
            Chain units = body.getUnits();
            Local arg, tmpRef;
            Local arg1;
            Local arg2;
            Local arg3;
            Local arg4, arg5;
            //Constant con;
            
            	
            
            // Add some locals, java.lang.String l0
                arg = Jimple.v().newLocal("t0", ArrayType.v(RefType.v("java.lang.String"), 1));
                body.getLocals().add(arg);
                
                arg = Jimple.v().newLocal("l0", ArrayType.v(RefType.v("java.lang.String"), 1));
                body.getLocals().add(arg);
                
                arg1 = Jimple.v().newLocal("l1", ArrayType.v(RefType.v("java.lang.String"), 1));
                body.getLocals().add(arg1);
                
                arg2 = Jimple.v().newLocal("l2", ArrayType.v(RefType.v("java.lang.String"), 1));
                body.getLocals().add(arg2);
                
                arg3 = Jimple.v().newLocal("l3", ArrayType.v(RefType.v("java.lang.String"), 1));
                body.getLocals().add(arg3);
                
                arg4 = Jimple.v().newLocal("l4", ArrayType.v(RefType.v("java.lang.String"), 1));
                body.getLocals().add(arg4);
                
                arg5 = Jimple.v().newLocal("l5", ArrayType.v(RefType.v("java.lang.String"), 1));
                body.getLocals().add(arg5);
            
            // Add locals, java.io.printStream tmpRef
                tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
                body.getLocals().add(tmpRef);
                
                System.out.println("first local variable:" + body.getLocals().getFirst());
                System.out.println("first local variable:" + body.getLocals().getLast());
                
            // add "l0 = @parameter0"
                units.add(Jimple.v().newIdentityStmt(arg, 
                     Jimple.v().newParameterRef(ArrayType.v(RefType.v("java.lang.String"), 1), 0)));
                
                units.add(Jimple.v().newAssignStmt(arg1, arg));
            
            // add "tmpRef = java.lang.System.out"
                units.add(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
                    Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));
            
            // insert "tmpRef.println("Hello world!")"
            {
                SootMethod toCall = Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
                units.add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), StringConstant.v("Hello world!"))));
            }   
            
            units.add(Jimple.v().newAssignStmt(arg2, arg));
            
            units.add(Jimple.v().newAssignStmt(arg3, arg1));
            
            units.add(Jimple.v().newAssignStmt(arg4, arg3));
            
            
            units.add(Jimple.v().newAssignStmt(arg1, IntConstant.v(1)));
            
            units.add(Jimple.v().newAssignStmt(arg3, arg1));
            
            units.add(Jimple.v().newAssignStmt(arg5, arg3));
            
            // insert "return"
                units.add(Jimple.v().newReturnVoidStmt());
                     
        //}
        
        //CallGraph cg = Scene.v().getCallGraph();
       
                
        ExceptionalUnitGraph eug = new ExceptionalUnitGraph(body);
        MyReachingDefinition mrd = new MyReachingDefinition(eug);

        //dump internal Jimple
        File file = new File("/home/muzhang/dalvik/test/helloworld_class.dump");
        PrintWriter pw = new PrintWriter(file);
        Printer.v().printTo(sClass, pw);
        pw.flush();
        
        
        
        
        
        Vector<Stmt> defs = new Vector<Stmt>();
        Vector<Stmt> delta = new Vector<Stmt>();
        {
            Iterator it = body.getUnits().iterator();
            while(it.hasNext()){
            	Stmt s = (Stmt)it.next();
            	//System.out.println("stmt: " + s);
            	
               	Iterator defIt = s.getDefBoxes().iterator();
            	while(defIt.hasNext()){
            		ValueBox vbox = (ValueBox)defIt.next();
            		if(vbox.getValue() instanceof Local){
            			Local l = (Local)vbox.getValue();
            			if(l.getName().equals("l2")){
            				if(!defs.contains(s)){
            					defs.add(s);
            					delta.add(s);
            				}
            			}
            		}
            	}
            }
        }
        
        while(!delta.isEmpty()){
        	
        	delta = new Vector<Stmt>();
        
            Iterator it = body.getUnits().iterator();
            while(it.hasNext()){
            	Stmt s = (Stmt)it.next();
            	Iterator usesIt = s.getUseBoxes().iterator();
                while (usesIt.hasNext()){
                	ValueBox vbox = (ValueBox)usesIt.next();
                	if (vbox.getValue() instanceof Local) {
                		Local l = (Local)vbox.getValue();
            	
                		Iterator rDefsIt = mrd.getDefsOfAt(l, s).iterator();
                		while(rDefsIt.hasNext()){
                			Stmt next = (Stmt)rDefsIt.next();
                			if(defs.contains(next)){
                				
                            	if(!defs.contains(s)){
                            		defs.add(s);
                            		delta.add(s);
                            	}
                			}
                		}
                	}
                }
           	}
   
        
        }//end while(!delta.isEmpty())
        
        System.out.println("dataflow for l2:");
        Iterator i = defs.iterator();
        while(i.hasNext()){
        	Stmt s = (Stmt)i.next();
        	
        	System.out.println(s);
        }
        
        /*
        Iterator it = body.getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            System.out.println("stmt: "+s);
            Iterator usesIt = s.getUseBoxes().iterator();
            while (usesIt.hasNext()){
                ValueBox vbox = (ValueBox)usesIt.next();
                if (vbox.getValue() instanceof Local) {
                    Local l = (Local)vbox.getValue();
                    System.out.println("local: "+l);
                    Iterator rDefsIt = mrd.getDefsOfAt(l, s).iterator();
                    while (rDefsIt.hasNext()){
                        Stmt next = (Stmt)rDefsIt.next();
                        String info = l+" has reaching def: "+next.toString();
                        //s.addTag(new LinkTag(info, next, b.getMethod().getDeclaringClass().getName(), "Reaching Defs"));
                        System.out.println(info);
                    }
                    System.out.println();
                }
            }
            System.out.println();
        }
        */
        
        
/*
        UseMyFlowAnalysis umfa = new UseMyFlowAnalysis(eug);
        
        Iterator iter = eug.iterator();
        int counter = 0;
        
        while(iter.hasNext()){
        	
        	counter++;
        	
        	Unit u = (Unit)iter.next();
        	List liveLocalsBefore = umfa.getLiveLocalsBefore(u);
        	List liveLocalsAfter = umfa.getLiveLocalsAfter(u);
        	
        	//dump
        	System.out.println("Statement " + counter + " :");
        	List boxes = u.getUseAndDefBoxes();
        	Iterator iterBoxes = boxes.iterator();
        	while(iterBoxes.hasNext()){
        		ValueBox box = (ValueBox)iterBoxes.next();
        		Value v = box.getValue();
        		//if(v instanceof Stmt){
        			System.out.println(v.toString());
        		//}
        	}
        	
        	System.out.println("Before " + counter + " :");
        	
        	Iterator iterBefore = liveLocalsBefore.iterator();
        	while(iterBefore.hasNext()){
        		Value v = (Value)iterBefore.next();
        		//if(v instanceof Local){
        		//	System.out.println(((Local) v).getName());
        		//} 
        		System.out.println(v.toString());
        	}
        	
        	System.out.println();
        	System.out.println("After " + counter + " :");
        	
        	Iterator iterAfter = liveLocalsAfter.iterator();
        	while(iterAfter.hasNext()){
        		Value v = (Value)iterAfter.next();
        		//if(v instanceof Local){
        		//	System.out.println(((Local) v).getName());
        		//}  
        		System.out.println(v.toString());
        	}
        	
        	System.out.println();
        }
        
*/        
        
        

		// output Helloworld.class file to sootOutput/ directory
		
        String fileName = SourceLocator.v().getFileNameFor(sClass, Options.output_format_class);
        OutputStream streamOut = new JasminOutputStream(
                                    new FileOutputStream(fileName));
        PrintWriter writerOut = new PrintWriter(
                                    new OutputStreamWriter(streamOut));
        JasminClass jasminClass = new soot.jimple.JasminClass(sClass);
        jasminClass.print(writerOut);
        writerOut.flush();
        streamOut.close();
        
        
    }
}
