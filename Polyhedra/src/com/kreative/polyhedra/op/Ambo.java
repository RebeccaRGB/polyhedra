package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Ambo extends PolyhedronOp {
	private final Color color;
	
	public Ambo(Color color) {
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vfSize = seed.vertices.size() + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>(seed.edges.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(vfSize);
		List<Color> faceColors = new ArrayList<Color>(vfSize);
		
		for (Polyhedron.Edge edge : seed.edges) {
			vertices.add(edge.vertex1.point.midpoint(edge.vertex2.point));
		}
		
		for (Polyhedron.Vertex vertex : seed.vertices) {
			List<Polyhedron.Face> seedFaces = seed.getFaces(vertex);
			List<Polyhedron.Edge> seedEdges = seed.getEdges(vertex);
			while (!seedEdges.isEmpty()) {
				List<Integer> amboFace = new ArrayList<Integer>();
				Polyhedron.Edge seedEdge = seedEdges.remove(0);
				amboFace.add(seed.edges.indexOf(seedEdge));
				seedEdge = Polyhedron.getNextEdge(seedFaces, seedEdge, vertex);
				while (seedEdges.contains(seedEdge)) {
					seedEdges.remove(seedEdge);
					amboFace.add(seed.edges.indexOf(seedEdge));
					seedEdge = Polyhedron.getNextEdge(seedFaces, seedEdge, vertex);
				}
				faces.add(amboFace);
				faceColors.add(color);
			}
		}
		
		for (Polyhedron.Face face : seed.faces) {
			List<Integer> indices = new ArrayList<Integer>(face.edges.size());
			for (Polyhedron.Edge edge : face.edges) {
				indices.add(seed.edges.indexOf(edge));
			}
			faces.add(indices);
			faceColors.add(face.color);
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Ambo parse(String[] args) {
		Color color = Color.GRAY;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				color = parseColor(args[argi++], color);
			} else {
				System.err.println("Options:");
				System.err.println("  -c <color>  color of faces generated from vertices");
				return null;
			}
		}
		return new Ambo(color);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}