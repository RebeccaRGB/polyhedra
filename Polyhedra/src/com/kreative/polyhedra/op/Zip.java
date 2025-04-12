package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Zip extends PolyhedronOp {
	public static enum ZippedFaceGen {
		FIXED_DISTANCE_FROM_EDGE {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Edge edge : face.edges) {
					Point3D m = edge.midpoint();
					vertices.add(center.subtract(m).normalize(size).add(m));
				}
				return vertices;
			}
		},
		FIXED_DISTANCE_FROM_CENTER {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Edge edge : face.edges) {
					Point3D m = edge.midpoint();
					vertices.add(m.subtract(center).normalize(size).add(center));
				}
				return vertices;
			}
		},
		RELATIVE_DISTANCE_FROM_EDGE {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Edge edge : face.edges) {
					Point3D m = edge.midpoint();
					vertices.add(center.subtract(m).multiply(size).add(m));
				}
				return vertices;
			}
		},
		RELATIVE_DISTANCE_FROM_CENTER {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Edge edge : face.edges) {
					Point3D m = edge.midpoint();
					vertices.add(m.subtract(center).multiply(size).add(center));
				}
				return vertices;
			}
		},
		REGULAR {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Edge edge : face.edges) {
					Point3D m = edge.midpoint();
					double fa = 0;
					for (Polyhedron.Face f : face.parent.getOppositeFaces(edge, face)) {
						double a = m.angleRad(center, f.center());
						if (a > fa) fa = a;
					}
					Point3D vertex = edge.vertex1.point;
					double ea = vertex.angleRad(center, m);
					double s = center.distance(m);
					double t = s / (Math.sin(fa / 2) / Math.cos(ea) + 1);
					vertices.add(center.subtract(m).normalize(t).add(m));
				}
				return vertices;
			}
		};
		public abstract List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size);
	}
	
	private final ZippedFaceGen gen;
	private final double size;
	private final Color color;
	
	public Zip(ZippedFaceGen gen, double size, Color color) {
		this.gen = gen;
		this.size = size;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vfSize = seed.vertices.size() + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>(seed.edges.size() * 2);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(vfSize);
		List<Color> faceColors = new ArrayList<Color>(vfSize);
		
		Map<Polyhedron.Face,Integer> faceStartIndexMap = new HashMap<Polyhedron.Face,Integer>();
		for (Polyhedron.Face face : seed.faces) {
			int startIndex = vertices.size();
			faceStartIndexMap.put(face, startIndex);
			vertices.addAll(gen.createFace(face, face.center(), size));
			int n = vertices.size() - startIndex;
			List<Integer> zippedFace = new ArrayList<Integer>(n);
			for (int i = 0; i < n; i++) zippedFace.add(startIndex++);
			faces.add(zippedFace);
			faceColors.add(face.color);
		}
		
		for (Polyhedron.Vertex vertex : seed.vertices) {
			List<Polyhedron.Face> seedFaces = seed.getFaces(vertex);
			while (!seedFaces.isEmpty()) {
				List<Integer> zippedFace = new ArrayList<Integer>();
				for (Polyhedron.Face seedFace : seed.getOrderedFaces(vertex, seedFaces)) {
					int i = faceStartIndexMap.get(seedFace), n = seedFace.vertices.size();
					zippedFace.add(i + seedFace.vertices.indexOf(vertex));
					zippedFace.add(i + (seedFace.vertices.indexOf(vertex) + n - 1) % n);
					seedFaces.remove(seedFace);
				}
				faces.add(zippedFace);
				faceColors.add(color);
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Zip> {
		public String name() { return "Zip"; }
		
		public Zip parse(String[] args) {
			ZippedFaceGen gen = ZippedFaceGen.REGULAR;
			double size = 0;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-s")) {
					gen = ZippedFaceGen.RELATIVE_DISTANCE_FROM_EDGE;
					size = 0.5;
				} else if (arg.equals("-A") && argi < args.length) {
					gen = ZippedFaceGen.RELATIVE_DISTANCE_FROM_EDGE;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-a") && argi < args.length) {
					gen = ZippedFaceGen.FIXED_DISTANCE_FROM_EDGE;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-D") && argi < args.length) {
					gen = ZippedFaceGen.RELATIVE_DISTANCE_FROM_CENTER;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-d") && argi < args.length) {
					gen = ZippedFaceGen.FIXED_DISTANCE_FROM_CENTER;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-r")) {
					gen = ZippedFaceGen.REGULAR;
					size = 0;
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					return null;
				}
			}
			return new Zip(gen, size, color);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("a", Type.REAL, "create vertices at a fixed distance from the original edges", "A","d","D","r","s"),
				new Option("A", Type.REAL, "create vertices at a relative distance from the original edges", "a","d","D","r","s"),
				new Option("d", Type.REAL, "create vertices at a fixed distance from the face center point", "a","A","D","r","s"),
				new Option("D", Type.REAL, "create vertices at a relative distance from the face center point", "a","A","d","r","s"),
				new Option("r", Type.VOID, "attempt to create regular faces (not always possible)", "a","A","d","D","s"),
				new Option("s", Type.VOID, "create vertices halfway between the center and the original edge", "a","A","d","D","r"),
				new Option("c", Type.COLOR, "color of new faces generated between original faces"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}