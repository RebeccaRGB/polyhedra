package com.kreative.polyhedra.gen;

import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Seed extends PolyhedronGen {
	private final Polyhedron seed;
	
	public Seed(Polyhedron seed) {
		this.seed = seed;
	}
	
	public Polyhedron gen() {
		return seed;
	}
}