package com.kreative.polyhedra;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import com.kreative.polyhedra.viewer.Viewer;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			Viewer.main(args);
			return;
		}
		
		String command = args[0].toLowerCase();
		List<String> cargl = Arrays.asList(args).subList(1, args.length);
		String[] cargs = cargl.toArray(new String[args.length - 1]);
		
		if (command.equals("view")) {
			Viewer.main(cargs);
			return;
		}
		
		if (command.equals("ops")) {
			SortedSet<String> ops = new TreeSet<String>();
			ops.addAll(com.kreative.polyhedra.op.BOM.MAP.keySet());
			for (String op : ops) System.out.println(op);
			return;
		}
		
		if (command.equals("gens")) {
			SortedSet<String> gens = new TreeSet<String>();
			gens.addAll(com.kreative.polyhedra.gen.BOM.MAP.keySet());
			for (String gen : gens) System.out.println(gen);
			return;
		}
		
		Class<? extends PolyhedronOp> opClass;
		Class<? extends PolyhedronGen> genClass;
		
		opClass = com.kreative.polyhedra.op.BOM.MAP.get(args[0]);
		if (opClass != null && invokeMain(opClass, cargs)) return;
		
		genClass = com.kreative.polyhedra.gen.BOM.MAP.get(args[0]);
		if (genClass != null && invokeMain(genClass, cargs)) return;
		
		opClass = com.kreative.polyhedra.op.BOM.MAP.get(command);
		if (opClass != null && invokeMain(opClass, cargs)) return;
		
		genClass = com.kreative.polyhedra.gen.BOM.MAP.get(command);
		if (genClass != null && invokeMain(genClass, cargs)) return;
		
		Viewer.main(args);
	}
	
	private static boolean invokeMain(Class<?> cls, String[] args) {
		try {
			Method main = cls.getMethod("main", String[].class);
			main.invoke(null, (Object)args);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}