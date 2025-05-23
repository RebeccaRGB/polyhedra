package com.kreative.polyhedra.gen;

import java.util.Scanner;
import com.kreative.polyhedra.OFFReader;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Constant extends PolyhedronGen {
	private final Polyhedron seed;
	
	public Constant(Polyhedron seed) {
		this.seed = seed;
	}
	
	public Polyhedron gen() {
		return seed;
	}
	
	public static class Factory extends PolyhedronGen.Factory<Constant> {
		public String name() { return "Constant"; }
		
		public Constant parse(String[] args) {
			StringBuffer sb = new StringBuffer();
			for (String arg : args) {
				sb.append(arg);
				sb.append("\n");
			}
			Scanner s = new Scanner(sb.toString());
			OFFReader r = new OFFReader(s);
			Polyhedron p = r.readPolyhedron();
			if (p == null) {
				System.err.println("Error: No polyhedron found in arguments.");
				return null;
			}
			return new Constant(p);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option(Mult.REPEATED_REQUIRED, Type.TEXT, "polyhedron in OFF format"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}