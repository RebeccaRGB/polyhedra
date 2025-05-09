package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class NormalizeVertices extends PolyhedronOp {
	private final double magnitude;
	
	public NormalizeVertices() {
		this.magnitude = 1;
	}
	
	public NormalizeVertices(double magnitude) {
		this.magnitude = magnitude;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		for (Polyhedron.Vertex vertex : seed.vertices) {
			vertices.add(vertex.point.normalize(magnitude));
		}
		for (Polyhedron.Face face : seed.faces) {
			List<Integer> indices = new ArrayList<Integer>(face.vertices.size());
			for (Polyhedron.Vertex v : face.vertices) indices.add(v.index);
			faces.add(indices);
			faceColors.add(face.color);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<NormalizeVertices> {
		public String name() { return "NormalizeVertices"; }
		
		public NormalizeVertices parse(String[] args) {
			double magnitude = 1;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equals("-m") && argi < args.length) {
					magnitude = parseDouble(args[argi++], magnitude);
				} else {
					return null;
				}
			}
			return new NormalizeVertices(magnitude);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("m", Type.REAL, "scale normalized vertices to the specified magnitude")
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}