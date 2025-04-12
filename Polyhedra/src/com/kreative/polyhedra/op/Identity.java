package com.kreative.polyhedra.op;

import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Identity extends PolyhedronOp {
	public Polyhedron op(Polyhedron seed) {
		return seed;
	}
	
	public static class Factory extends PolyhedronOp.Factory<Identity> {
		public String name() { return "Identity"; }
		
		public Identity parse(String[] args) {
			if (args.length > 0) return null;
			return new Identity();
		}
		
		public Option[] options() {
			return null;
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}