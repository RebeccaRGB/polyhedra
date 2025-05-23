package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Expand extends PolyhedronOp {
	public static enum ExpandedFaceGen {
		FIXED_DISTANCE_FROM_VERTEX {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Vertex vertex : face.vertices) {
					vertices.add(center.subtract(vertex.point).normalize(size).add(vertex.point));
				}
				return vertices;
			}
		},
		FIXED_DISTANCE_FROM_CENTER {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Vertex vertex : face.vertices) {
					vertices.add(vertex.point.subtract(center).normalize(size).add(center));
				}
				return vertices;
			}
		},
		RELATIVE_DISTANCE_FROM_VERTEX {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Vertex vertex : face.vertices) {
					vertices.add(center.subtract(vertex.point).multiply(size).add(vertex.point));
				}
				return vertices;
			}
		},
		RELATIVE_DISTANCE_FROM_CENTER {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Vertex vertex : face.vertices) {
					vertices.add(vertex.point.subtract(center).multiply(size).add(center));
				}
				return vertices;
			}
		},
		REGULAR {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Edge edge : face.edges) {
					Point3D midpoint = edge.midpoint();
					double fa = 0;
					for (Polyhedron.Face f : face.parent.getOppositeFaces(edge, face)) {
						double a = midpoint.angleRad(center, f.center());
						if (a > fa) fa = a;
					}
					Point3D vertex = edge.vertex1.point;
					double ea = vertex.angleRad(center, midpoint);
					double s = vertex.distance(midpoint);
					double t = s / (Math.sin(ea) * Math.sin(fa / 2) + Math.cos(ea));
					vertices.add(center.subtract(vertex).normalize(t).add(vertex));
				}
				return vertices;
			}
		};
		public abstract List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size);
	}
	
	private final ExpandedFaceGen gen;
	private final double size;
	private final Color edgeColor;
	private final Color vertexColor;
	
	public Expand(ExpandedFaceGen gen, double size, Color edgeColor, Color vertexColor) {
		this.gen = gen;
		this.size = size;
		this.edgeColor = edgeColor;
		this.vertexColor = vertexColor;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vefSize = seed.vertices.size() + seed.edges.size() + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>();
		List<List<Integer>> faces = new ArrayList<List<Integer>>(vefSize);
		List<Color> faceColors = new ArrayList<Color>(vefSize);
		
		Map<Polyhedron.Face,Integer> faceStartIndexMap = new HashMap<Polyhedron.Face,Integer>();
		for (Polyhedron.Face face : seed.faces) {
			int startIndex = vertices.size();
			faceStartIndexMap.put(face, startIndex);
			vertices.addAll(gen.createFace(face, face.center(), size));
			int n = vertices.size() - startIndex;
			List<Integer> expandedFace = new ArrayList<Integer>(n);
			for (int i = 0; i < n; i++) expandedFace.add(startIndex++);
			faces.add(expandedFace);
			faceColors.add(face.color);
		}
		
		for (Polyhedron.Edge edge : seed.edges) {
			List<Integer> expandedFace = new ArrayList<Integer>();
			for (Polyhedron.Face seedFace : seed.getFaces(edge)) {
				int i = faceStartIndexMap.get(seedFace), n = seedFace.edges.size();
				expandedFace.add(i + (seedFace.edges.indexOf(edge) + 1) % n);
				expandedFace.add(i + seedFace.edges.indexOf(edge));
			}
			faces.add(expandedFace);
			faceColors.add(edgeColor);
		}
		
		for (Polyhedron.Vertex vertex : seed.vertices) {
			List<Polyhedron.Face> seedFaces = seed.getFaces(vertex);
			while (!seedFaces.isEmpty()) {
				List<Integer> expandedFace = new ArrayList<Integer>();
				for (Polyhedron.Face seedFace : seed.getOrderedFaces(vertex, seedFaces)) {
					int i = faceStartIndexMap.get(seedFace);
					expandedFace.add(i + seedFace.vertices.indexOf(vertex));
					seedFaces.remove(seedFace);
				}
				faces.add(expandedFace);
				faceColors.add(vertexColor);
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Expand> {
		public String name() { return "Expand"; }
		
		public Expand parse(String[] args) {
			ExpandedFaceGen gen = ExpandedFaceGen.REGULAR;
			double size = 0;
			Color edgeColor = Color.GRAY;
			Color vertexColor = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equals("-s")) {
					gen = ExpandedFaceGen.RELATIVE_DISTANCE_FROM_VERTEX;
					size = 0.5;
				} else if (arg.equals("-A") && argi < args.length) {
					gen = ExpandedFaceGen.RELATIVE_DISTANCE_FROM_VERTEX;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-a") && argi < args.length) {
					gen = ExpandedFaceGen.FIXED_DISTANCE_FROM_VERTEX;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-D") && argi < args.length) {
					gen = ExpandedFaceGen.RELATIVE_DISTANCE_FROM_CENTER;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-d") && argi < args.length) {
					gen = ExpandedFaceGen.FIXED_DISTANCE_FROM_CENTER;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-r")) {
					gen = ExpandedFaceGen.REGULAR;
					size = 0;
				} else if (arg.equals("-c") && argi < args.length) {
					Color c = parseColor(args[argi++], null);
					if (c != null) edgeColor = vertexColor = c;
				} else if (arg.equals("-e") && argi < args.length) {
					edgeColor = parseColor(args[argi++], edgeColor);
				} else if (arg.equals("-v") && argi < args.length) {
					vertexColor = parseColor(args[argi++], vertexColor);
				} else {
					return null;
				}
			}
			return new Expand(gen, size, edgeColor, vertexColor);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("a", Type.REAL, "create vertices at a fixed distance from the original vertices", "A","d","D","r","s"),
				new Option("A", Type.REAL, "create vertices at a relative distance from the original vertices", "a","d","D","r","s"),
				new Option("d", Type.REAL, "create vertices at a fixed distance from the face center point", "a","A","D","r","s"),
				new Option("D", Type.REAL, "create vertices at a relative distance from the face center point", "a","A","d","r","s"),
				new Option("r", Type.VOID, "attempt to create regular faces (not always possible)", "a","A","d","D","s"),
				new Option("s", Type.VOID, "create vertices halfway between the center and original vertex", "a","A","d","D","r"),
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