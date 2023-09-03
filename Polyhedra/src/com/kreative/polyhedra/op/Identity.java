package com.kreative.polyhedra.op;

import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Identity extends PolyhedronOp {
	public Polyhedron op(Polyhedron seed) {
		return seed;
	}
	
	public static Identity parse(String[] args) {
		if (args.length > 0) {
			System.err.println("This operation has no options.");
			return null;
		}
		return new Identity();
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}