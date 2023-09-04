package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.Polyhedron.Face;
import com.kreative.polyhedra.Polyhedron.Vertex;
import com.kreative.polyhedra.PolyhedronOp;

public class Dual extends PolyhedronOp {
	private final boolean rescale;
	private final Color color;
	
	public Dual(boolean rescale, Color color) {
		this.rescale = rescale;
		this.color = color;
	}
	
	private static List<Face> getFaces(Polyhedron p, Vertex v) {
		List<Face> faces = new ArrayList<Face>();
		for (Face f : p.faces) {
			if (f.vertices.contains(v)) {
				faces.add(f);
			}
		}
		return faces;
	}
	
	private static Face getNextFace(List<Face> faces, Face f, Vertex v) {
		int fi = f.vertices.indexOf(v);
		if (fi < 0) return null;
		int fn = f.vertices.size();
		int fpvi = f.vertices.get((fi + fn - 1) % fn).index;
		for (Face g : faces) {
			int gi = g.vertices.indexOf(v);
			if (gi < 0) continue;
			int gn = g.vertices.size();
			int gnvi = g.vertices.get((gi + 1) % gn).index;
			if (fpvi == gnvi) return g;
		}
		return null;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.faces.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.vertices.size());
		List<Color> faceColors = new ArrayList<Color>(seed.vertices.size());
		
		for (Face f : seed.faces) {
			List<Point3D> face = new ArrayList<Point3D>(f.vertices.size());
			for (Vertex v : f.vertices) face.add(v.point);
			vertices.add(Point3D.average(face));
		}
		
		for (Vertex v : seed.vertices) {
			List<Face> seedFaces = getFaces(seed, v);
			while (!seedFaces.isEmpty()) {
				List<Integer> dualFace = new ArrayList<Integer>();
				Face seedFace = seedFaces.remove(0);
				dualFace.add(seedFace.index);
				seedFace = getNextFace(seedFaces, seedFace, v);
				while (seedFace != null) {
					seedFaces.remove(seedFace);
					dualFace.add(seedFace.index);
					seedFace = getNextFace(seedFaces, seedFace, v);
				}
				faces.add(dualFace);
				faceColors.add(color);
			}
		}
		
		if (rescale) {
			List<Point3D> seedVertices = new ArrayList<Point3D>(seed.vertices.size());
			for (Vertex v : seed.vertices) seedVertices.add(v.point);
			double seedScale = Point3D.averageMagnitude(seedVertices);
			double dualScale = Point3D.averageMagnitude(vertices);
			if (seedScale != 0 && dualScale != 0) {
				for (int i = 0, n = vertices.size(); i < n; i++) {
					vertices.set(i, vertices.get(i).multiply(seedScale / dualScale));
				}
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Dual parse(String[] args) {
		boolean rescale = false;
		Color color = Color.GRAY;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-r")) {
				rescale = true;
			} else if (arg.equalsIgnoreCase("-s")) {
				rescale = false;
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				color = parseColor(args[argi++], color);
			} else {
				System.err.println("Options:");
				System.err.println("  -r          rescale dual polyhedron to match original size");
				System.err.println("  -s          do not rescale dual polyhedron (strict mode)");
				System.err.println("  -c <color>  color");
				return null;
			}
		}
		return new Dual(rescale, color);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}