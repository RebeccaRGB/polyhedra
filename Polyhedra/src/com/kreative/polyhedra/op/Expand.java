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
					Point3D z = center.subtract(vertex.point);
					z = z.multiply(size / z.magnitude());
					vertices.add(z.add(vertex.point));
				}
				return vertices;
			}
		},
		FIXED_DISTANCE_FROM_CENTER {
			public List<Point3D> createFace(Polyhedron.Face face, Point3D center, double size) {
				List<Point3D> vertices = new ArrayList<Point3D>();
				for (Polyhedron.Vertex vertex : face.vertices) {
					Point3D z = vertex.point.subtract(center);
					z = z.multiply(size / z.magnitude());
					vertices.add(z.add(center));
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
			List<Point3D> seedVertices = new ArrayList<Point3D>();
			for (Polyhedron.Vertex v : face.vertices) seedVertices.add(v.point);
			Point3D center = Point3D.average(seedVertices);
			List<Point3D> expandedVertices = gen.createFace(face, center, size);
			vertices.addAll(expandedVertices);
			int n = expandedVertices.size();
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
				Polyhedron.Face seedFace = seedFaces.remove(0);
				int i = faceStartIndexMap.get(seedFace);
				expandedFace.add(i + seedFace.vertices.indexOf(vertex));
				seedFace = Polyhedron.getNextFace(seedFaces, seedFace, vertex);
				while (seedFace != null) {
					seedFaces.remove(seedFace);
					i = faceStartIndexMap.get(seedFace);
					expandedFace.add(i + seedFace.vertices.indexOf(vertex));
					seedFace = Polyhedron.getNextFace(seedFaces, seedFace, vertex);
				}
				faces.add(expandedFace);
				faceColors.add(vertexColor);
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Expand parse(String[] args) {
		ExpandedFaceGen gen = ExpandedFaceGen.RELATIVE_DISTANCE_FROM_VERTEX;
		double size = 0.5;
		Color edgeColor = Color.GRAY;
		Color vertexColor = Color.GRAY;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-s")) {
				gen = ExpandedFaceGen.RELATIVE_DISTANCE_FROM_VERTEX;
				size = 0.5;
			} else if (arg.equalsIgnoreCase("-A")) {
				gen = ExpandedFaceGen.RELATIVE_DISTANCE_FROM_VERTEX;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
				gen = ExpandedFaceGen.FIXED_DISTANCE_FROM_VERTEX;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-D") && argi < args.length) {
				gen = ExpandedFaceGen.RELATIVE_DISTANCE_FROM_CENTER;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-d") && argi < args.length) {
				gen = ExpandedFaceGen.FIXED_DISTANCE_FROM_CENTER;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				Color c = parseColor(args[argi++], null);
				if (c != null) edgeColor = vertexColor = c;
			} else if (arg.equalsIgnoreCase("-e") && argi < args.length) {
				edgeColor = parseColor(args[argi++], edgeColor);
			} else if (arg.equalsIgnoreCase("-v") && argi < args.length) {
				vertexColor = parseColor(args[argi++], vertexColor);
			} else {
				printOptions(options());
				return null;
			}
		}
		return new Expand(gen, size, edgeColor, vertexColor);
	}
	
	public static Option[] options() {
		return new Option[] {
			new Option("a", Type.REAL, "create vertices at a fixed distance from the original vertices", "A","d","D","s"),
			new Option("A", Type.REAL, "create vertices at a relative distance from the original vertices", "a","d","D","s"),
			new Option("d", Type.REAL, "create vertices at a fixed distance from the face center point", "a","A","D","s"),
			new Option("D", Type.REAL, "create vertices at a relative distance from the face center point", "a","A","d","s"),
			new Option("s", Type.VOID, "create vertices halfway between the center and original vertex", "a","A","d","D"),
			new Option("c", Type.COLOR, "color of new faces generated between original faces", "e","v"),
			new Option("e", Type.COLOR, "color of new faces generated from original edges", "c"),
			new Option("v", Type.COLOR, "color of new faces generated from original vertices", "c"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}