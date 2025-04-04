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
					Point3D m = edge.vertex1.point.midpoint(edge.vertex2.point);
					Point3D z = center.subtract(m);
					z = z.multiply(size / z.magnitude());
					vertices.add(z.add(m));
				}
				return vertices;
			}
		},
		FIXED_DISTANCE_FROM_CENTER {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Edge edge : face.edges) {
					Point3D m = edge.vertex1.point.midpoint(edge.vertex2.point);
					Point3D z = m.subtract(center);
					z = z.multiply(size / z.magnitude());
					vertices.add(z.add(center));
				}
				return vertices;
			}
		},
		RELATIVE_DISTANCE_FROM_EDGE {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Edge edge : face.edges) {
					Point3D m = edge.vertex1.point.midpoint(edge.vertex2.point);
					vertices.add(center.subtract(m).multiply(size).add(m));
				}
				return vertices;
			}
		},
		RELATIVE_DISTANCE_FROM_CENTER {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Edge edge : face.edges) {
					Point3D m = edge.vertex1.point.midpoint(edge.vertex2.point);
					vertices.add(m.subtract(center).multiply(size).add(center));
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
			List<Point3D> seedVertices = new ArrayList<Point3D>();
			for (Polyhedron.Vertex v : face.vertices) seedVertices.add(v.point);
			Point3D center = Point3D.average(seedVertices);
			List<Point3D> zippedVertices = gen.createFace(face, center, size);
			vertices.addAll(zippedVertices);
			int n = zippedVertices.size();
			List<Integer> zippedFace = new ArrayList<Integer>(n);
			for (int i = 0; i < n; i++) zippedFace.add(startIndex++);
			faces.add(zippedFace);
			faceColors.add(face.color);
		}
		
		for (Polyhedron.Vertex vertex : seed.vertices) {
			List<Polyhedron.Face> seedFaces = seed.getFaces(vertex);
			while (!seedFaces.isEmpty()) {
				List<Integer> zippedFace = new ArrayList<Integer>();
				Polyhedron.Face seedFace = seedFaces.remove(0);
				int i = faceStartIndexMap.get(seedFace), n = seedFace.vertices.size();
				zippedFace.add(i + seedFace.vertices.indexOf(vertex));
				zippedFace.add(i + (seedFace.vertices.indexOf(vertex) + n - 1) % n);
				seedFace = Polyhedron.getNextFace(seedFaces, seedFace, vertex);
				while (seedFace != null) {
					seedFaces.remove(seedFace);
					i = faceStartIndexMap.get(seedFace); n = seedFace.vertices.size();
					zippedFace.add(i + seedFace.vertices.indexOf(vertex));
					zippedFace.add(i + (seedFace.vertices.indexOf(vertex) + n - 1) % n);
					seedFace = Polyhedron.getNextFace(seedFaces, seedFace, vertex);
				}
				faces.add(zippedFace);
				faceColors.add(color);
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Zip parse(String[] args) {
		ZippedFaceGen gen = ZippedFaceGen.RELATIVE_DISTANCE_FROM_EDGE;
		double size = 0.5;
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
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				color = parseColor(args[argi++], color);
			} else {
				printOptions(options());
				return null;
			}
		}
		return new Zip(gen, size, color);
	}
	
	public static Option[] options() {
		return new Option[] {
			new Option("a", Type.REAL, "create vertices at a fixed distance from the original edges", "A","d","D","s"),
			new Option("A", Type.REAL, "create vertices at a relative distance from the original edges", "a","d","D","s"),
			new Option("d", Type.REAL, "create vertices at a fixed distance from the face center point", "a","A","D","s"),
			new Option("D", Type.REAL, "create vertices at a relative distance from the face center point", "a","A","d","s"),
			new Option("s", Type.VOID, "create vertices halfway between the center and the original edge", "a","A","d","D"),
			new Option("c", Type.COLOR, "color of new faces generated between original faces"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}