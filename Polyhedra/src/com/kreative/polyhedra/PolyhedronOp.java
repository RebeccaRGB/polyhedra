package com.kreative.polyhedra;

public abstract class PolyhedronOp extends PolyhedronUtils {
	public abstract Polyhedron op(Polyhedron seed);
	
	protected static void main(PolyhedronOp op) {
		OFFReader r = new OFFReader(System.in);
		Polyhedron p = r.readPolyhedron();
		if (p == null) return;
		OFFWriter w = new OFFWriter(System.out);
		w.writePolyhedron(op.op(p));
	}
}