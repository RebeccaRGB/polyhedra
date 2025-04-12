package com.kreative.polyhedra;

public abstract class PolyhedronGen extends PolyhedronUtils {
	public abstract Polyhedron gen();
	
	public static abstract class Factory<T extends PolyhedronGen> {
		public abstract String name();
		public abstract Option[] options();
		public abstract T parse(String[] args);
		public final void main(String[] args) {
			T gen = parse(args);
			if (gen == null) {
				printOptions(options());
			} else {
				Polyhedron p = gen.gen();
				if (p == null) return;
				OFFWriter w = new OFFWriter(System.out);
				w.writePolyhedron(p);
			}
		}
	}
}