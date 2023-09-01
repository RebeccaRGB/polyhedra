package com.kreative.polyhedra;

public abstract class PolyhedronGen extends PolyhedronUtils {
	public abstract Polyhedron gen();
	
	protected static void main(PolyhedronGen gen) {
		OFFWriter w = new OFFWriter(System.out);
		w.writePolyhedron(gen.gen());
	}
}