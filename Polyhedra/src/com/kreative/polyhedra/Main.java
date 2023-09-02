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
		
		Class<? extends PolyhedronOp> opClass = com.kreative.polyhedra.op.BOM.MAP.get(command);
		if (opClass != null) {
			try {
				Method main = opClass.getMethod("main", String[].class);
				main.invoke(null, (Object)cargs);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Class<? extends PolyhedronGen> genClass = com.kreative.polyhedra.gen.BOM.MAP.get(command);
		if (genClass != null) {
			try {
				Method main = genClass.getMethod("main", String[].class);
				main.invoke(null, (Object)cargs);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Viewer.main(args);
	}
}