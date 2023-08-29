package com.kreative.polyhedra.op;

import com.kreative.polyhedra.AffineTransform3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Reflect extends PolyhedronOp {
	private static final AffineTransform3D TX = AffineTransform3D.getScaleInstance(-1, -1, -1);
	
	public Polyhedron op(Polyhedron seed) {
		return new Polyhedron(seed, TX);
	}
	
	public static void main(String[] args) {
		main(new Reflect());
	}
}