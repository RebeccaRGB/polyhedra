package com.kreative.polyhedra;

public abstract class PolyhedronOp extends PolyhedronUtils {
	public abstract Polyhedron op(Polyhedron seed);
	
	public static abstract class Factory<T extends PolyhedronOp> {
		public abstract String name();
		public abstract Option[] options();
		public abstract T parse(String[] args);
		public final void main(String[] args) {
			T op = parse(args);
			if (op == null) {
				printOptions(options());
			} else {
				OFFReader r = new OFFReader(System.in);
				Polyhedron p = r.readPolyhedron();
				if (p == null) return;
				p = op.op(p);
				if (p == null) return;
				OFFWriter w = new OFFWriter(System.out);
				w.writePolyhedron(p);
			}
		}
	}
}