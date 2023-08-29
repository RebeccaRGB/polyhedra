package com.kreative.polyhedra.op;

import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Seed extends PolyhedronOp {
	public Polyhedron op(Polyhedron seed) {
		return seed;
	}
	
	public static void main(String[] args) {
		main(new Seed());
	}
}