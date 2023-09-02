package com.kreative.polyhedra;

public abstract class PolyhedronOp extends PolyhedronUtils {
	public abstract Polyhedron op(Polyhedron seed);
	
	protected static void main(PolyhedronOp op) {
		if (op == null) return;
		OFFReader r = new OFFReader(System.in);
		Polyhedron p = r.readPolyhedron();
		if (p == null) return;
		p = op.op(p);
		if (p == null) return;
		OFFWriter w = new OFFWriter(System.out);
		w.writePolyhedron(p);
	}
}