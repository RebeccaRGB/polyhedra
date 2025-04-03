package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Compose extends PolyhedronGen {
	private final List<PolyhedronGen> gens;
	
	public Compose(PolyhedronGen... gens) {
		this(Arrays.asList(gens));
	}
	
	public Compose(List<PolyhedronGen> gens) {
		this.gens = new ArrayList<PolyhedronGen>(gens);
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>();
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		for (PolyhedronGen gen : gens) {
			Polyhedron p = gen.gen();
			int firstIndex = vertices.size();
			for (Polyhedron.Vertex v : p.vertices) {
				vertices.add(v.point);
			}
			for (Polyhedron.Face f : p.faces) {
				List<Integer> face = new ArrayList<Integer>();
				for (Polyhedron.Vertex v : f.vertices) {
					face.add(v.index + firstIndex);
				}
				faces.add(face);
				faceColors.add(f.color);
			}
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Compose parse(String[] args) {
		List<PolyhedronGen> gens = new ArrayList<PolyhedronGen>();
		for (String arg : args) {
			PolyhedronGen gen = parseGen(arg);
			if (gen != null) gens.add(gen);
		}
		return new Compose(gens);
	}
	
	public static Option[] options() {
		return new Option[] {
			new Option(Mult.REPEATED_REQUIRED, Type.GEN, "generator"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}