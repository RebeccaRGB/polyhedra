package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Center extends PolyhedronOp {
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		for (Polyhedron.Vertex vertex : seed.vertices) vertices.add(vertex.point);
		Point3D center = Point3D.average(vertices);
		for (int i = 0, n = vertices.size(); i < n; i++) {
			vertices.set(i, vertices.get(i).subtract(center));
		}
		for (Polyhedron.Face face : seed.faces) {
			List<Integer> indices = new ArrayList<Integer>(face.vertices.size());
			for (Polyhedron.Vertex v : face.vertices) indices.add(v.index);
			faces.add(indices);
			faceColors.add(face.color);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Center> {
		public String name() { return "Center"; }
		
		public Center parse(String[] args) {
			if (args.length > 0) return null;
			return new Center();
		}
		
		public Option[] options() {
			return null;
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}