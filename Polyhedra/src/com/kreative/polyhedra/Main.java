package com.kreative.polyhedra;

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
		
		if (command.equals("report")) {
			Report.main(cargs);
			return;
		}
		
		if (command.equals("convert")) {
			Convert.main(cargs);
			return;
		}
		
		if (command.equals("ops")) {
			SortedSet<String> ops = new TreeSet<String>();
			for (PolyhedronOp.Factory<? extends PolyhedronOp> opFactory : com.kreative.polyhedra.op.BOM.BOM) {
				ops.add(opFactory.name());
			}
			for (String op : ops) System.out.println(op);
			return;
		}
		
		if (command.equals("gens")) {
			SortedSet<String> gens = new TreeSet<String>();
			for (PolyhedronGen.Factory<? extends PolyhedronGen> genFactory : com.kreative.polyhedra.gen.BOM.BOM) {
				gens.add(genFactory.name());
			}
			for (String gen : gens) System.out.println(gen);
			return;
		}
		
		PolyhedronOp.Factory<? extends PolyhedronOp> opFactory;
		opFactory = com.kreative.polyhedra.op.BOM.MAP.get(args[0]);
		if (opFactory != null) { opFactory.main(cargs); return; }
		
		PolyhedronGen.Factory<? extends PolyhedronGen> genFactory;
		genFactory = com.kreative.polyhedra.gen.BOM.MAP.get(args[0]);
		if (genFactory != null) { genFactory.main(cargs); return; }
		
		Viewer.main(args);
	}
}