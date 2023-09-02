package com.kreative.polyhedra;

public abstract class PolyhedronGen extends PolyhedronUtils {
	public abstract Polyhedron gen();
	
	protected static void main(PolyhedronGen gen) {
		if (gen == null) return;
		Polyhedron p = gen.gen();
		if (p == null) return;
		OFFWriter w = new OFFWriter(System.out);
		w.writePolyhedron(p);
	}
}