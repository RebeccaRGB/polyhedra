package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.Polyhedron.Edge;
import com.kreative.polyhedra.Polyhedron.Face;
import com.kreative.polyhedra.Polyhedron.Vertex;
import com.kreative.polyhedra.PolyhedronOp;

public class Bevel extends PolyhedronOp {
	public static enum VertexGen {
		FIXED_DISTANCE_FROM_CENTER_ALONG_APOTHEM {
			public Point3D createVertex(
				Polyhedron seed,
				Face face, Point3D center,
				Edge edge, Point3D midpoint,
				Point3D vertex, double size
			) {
				double a = vertex.angleRad(center, midpoint);
				double s = Math.cos((Math.PI - a * 2) / 4);
				double t = size / s;
				return center.angleBisector(midpoint, vertex, t);
			}
		},
		RELATIVE_DISTANCE_FROM_CENTER_ALONG_APOTHEM {
			public Point3D createVertex(
				Polyhedron seed,
				Face face, Point3D center,
				Edge edge, Point3D midpoint,
				Point3D vertex, double size
			) {
				double a = vertex.angleRad(center, midpoint);
				double s = Math.cos((Math.PI - a * 2) / 4);
				double t = midpoint.distance(center) * size / s;
				return center.angleBisector(midpoint, vertex, t);
			}
		},
		FIXED_DISTANCE_FROM_EDGE_ALONG_APOTHEM {
			public Point3D createVertex(
				Polyhedron seed,
				Face face, Point3D center,
				Edge edge, Point3D midpoint,
				Point3D vertex, double size
			) {
				double a = vertex.angleRad(center, midpoint);
				double s = Math.cos((Math.PI - a * 2) / 4);
				double t = (midpoint.distance(center) - size) / s;
				return center.angleBisector(midpoint, vertex, t);
			}
		},
		RELATIVE_DISTANCE_FROM_EDGE_ALONG_APOTHEM {
			public Point3D createVertex(
				Polyhedron seed,
				Face face, Point3D center,
				Edge edge, Point3D midpoint,
				Point3D vertex, double size
			) {
				double a = vertex.angleRad(center, midpoint);
				double s = Math.cos((Math.PI - a * 2) / 4);
				double t = midpoint.distance(center) * (1 - size) / s;
				return center.angleBisector(midpoint, vertex, t);
			}
		},
		REGULAR {
			public Point3D createVertex(
				Polyhedron seed,
				Face face, Point3D center,
				Edge edge, Point3D midpoint,
				Point3D vertex, double size
			) {
				double fa = 0;
				for (Face f : seed.getOppositeFaces(edge, face)) {
					double a = midpoint.angleRad(center, f.center());
					if (a > fa) fa = a;
				}
				double ea = vertex.angleRad(center, midpoint);
				double s1 = Math.cos((Math.PI - ea * 2) / 4);
				double s2 = Math.tan((Math.PI - ea * 2) / 4) / Math.sin(fa / 2);
				double t = midpoint.distance(center) / (s1 + s1 * s2);
				return center.angleBisector(midpoint, vertex, t);
			}
		};
		public abstract Point3D createVertex(
			Polyhedron seed,
			Face face, Point3D center,
			Edge edge, Point3D midpoint,
			Point3D vertex, double size
		);
	}
	
	private final VertexGen gen;
	private final double size;
	private final Color edgeColor;
	private final Color vertexColor;
	
	public Bevel(VertexGen gen, double size, Color edgeColor, Color vertexColor) {
		this.gen = gen;
		this.size = size;
		this.edgeColor = edgeColor;
		this.vertexColor = vertexColor;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vefSize = seed.vertices.size() + seed.edges.size() + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>(seed.edges.size() * 4);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(vefSize);
		List<Color> faceColors = new ArrayList<Color>(vefSize);
		
		Map<Face,Integer> faceStartIndexMap = new HashMap<Face,Integer>();
		for (Face f : seed.faces) {
			int startIndex = vertices.size();
			faceStartIndexMap.put(f, startIndex);
			Point3D c = f.center();
			List<Integer> beveledFace = new ArrayList<Integer>();
			for (Edge e : f.edges) {
				Point3D m = e.midpoint();
				vertices.add(gen.createVertex(seed, f, c, e, m, e.vertex1.point, size));
				vertices.add(gen.createVertex(seed, f, c, e, m, e.vertex2.point, size));
				beveledFace.add(startIndex++);
				beveledFace.add(startIndex++);
			}
			faces.add(beveledFace);
			faceColors.add(f.color);
		}
		
		for (Edge e : seed.edges) {
			List<Integer> beveledFace = new ArrayList<Integer>();
			for (Face f : seed.getFaces(e)) {
				int i = faceStartIndexMap.get(f);
				beveledFace.add(i + f.edges.indexOf(e) * 2 + 1);
				beveledFace.add(i + f.edges.indexOf(e) * 2);
			}
			faces.add(beveledFace);
			faceColors.add(edgeColor);
		}
		
		for (Vertex v : seed.vertices) {
			List<Face> seedFaces = seed.getFaces(v);
			while (!seedFaces.isEmpty()) {
				List<Integer> beveledFace = new ArrayList<Integer>();
				for (Face seedFace : seed.getOrderedFaces(v, seedFaces)) {
					int i = faceStartIndexMap.get(seedFace);
					int n = seedFace.edges.size() * 2;
					beveledFace.add(i + seedFace.vertices.indexOf(v) * 2);
					beveledFace.add(i + (seedFace.vertices.indexOf(v) * 2 + n - 1) % n);
					seedFaces.remove(seedFace);
				}
				faces.add(beveledFace);
				faceColors.add(vertexColor);
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Bevel> {
		public String name() { return "Bevel"; }
		
		public Bevel parse(String[] args) {
			VertexGen gen = VertexGen.REGULAR;
			double size = 0;
			Color edgeColor = Color.GRAY;
			Color vertexColor = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-s")) {
					gen = VertexGen.RELATIVE_DISTANCE_FROM_EDGE_ALONG_APOTHEM;
					size = 1.0 / 3.0;
				} else if (arg.equals("-A") && argi < args.length) {
					gen = VertexGen.RELATIVE_DISTANCE_FROM_EDGE_ALONG_APOTHEM;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-a") && argi < args.length) {
					gen = VertexGen.FIXED_DISTANCE_FROM_EDGE_ALONG_APOTHEM;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-D") && argi < args.length) {
					gen = VertexGen.RELATIVE_DISTANCE_FROM_CENTER_ALONG_APOTHEM;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-d") && argi < args.length) {
					gen = VertexGen.FIXED_DISTANCE_FROM_CENTER_ALONG_APOTHEM;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-r")) {
					gen = VertexGen.REGULAR;
					size = 0;
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					Color c = parseColor(args[argi++], null);
					if (c != null) edgeColor = vertexColor = c;
				} else if (arg.equalsIgnoreCase("-e") && argi < args.length) {
					edgeColor = parseColor(args[argi++], edgeColor);
				} else if (arg.equalsIgnoreCase("-v") && argi < args.length) {
					vertexColor = parseColor(args[argi++], vertexColor);
				} else {
					return null;
				}
			}
			return new Bevel(gen, size, edgeColor, vertexColor);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("a", Type.REAL, "create edges at a fixed distance from the original edge", "A","d","D","r","s"),
				new Option("A", Type.REAL, "create edges at a relative distance from the original edge", "a","d","D","r","s"),
				new Option("d", Type.REAL, "create edges at a fixed distance from the face center point", "a","A","D","r","s"),
				new Option("D", Type.REAL, "create edges at a relative distance from the face center point", "a","A","d","r","s"),
				new Option("r", Type.VOID, "attempt to create regular faces (not always possible)", "a","A","d","D","s"),
				new Option("s", Type.VOID, "create edges at one third of the apothem from the original edge", "a","A","d","D","r"),
				new Option("c", Type.COLOR, "color of new faces generated between original faces", "e","v"),
				new Option("e", Type.COLOR, "color of new faces generated from original edges", "c"),
				new Option("v", Type.COLOR, "color of new faces generated from original vertices", "c"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}
