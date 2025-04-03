package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Snub extends PolyhedronOp {
	public static enum EdgeVertexGen {
		FACE_OFFSET {
			public Point3D createVertex(
				Polyhedron seed, List<Point3D> sv,
				Polyhedron.Face face, List<Point3D> fv,
				Polyhedron.Edge edge, double size
			) {
				Point3D c = edge.vertex1.point.multiply(2).add(edge.vertex2.point).divide(3);
				if (size == 0) return c;
				return c.add(c.normal(fv).multiply(size));
			}
		},
		MAX_MAGNITUDE_OFFSET {
			public Point3D createVertex(
				Polyhedron seed, List<Point3D> sv,
				Polyhedron.Face face, List<Point3D> fv,
				Polyhedron.Edge edge, double size
			) {
				Point3D c = edge.vertex1.point.multiply(2).add(edge.vertex2.point).divide(3);
				double m = Point3D.maxMagnitude(sv) + size;
				return c.multiply(m / c.magnitude());
			}
		},
		AVERAGE_MAGNITUDE_OFFSET {
			public Point3D createVertex(
				Polyhedron seed, List<Point3D> sv,
				Polyhedron.Face face, List<Point3D> fv,
				Polyhedron.Edge edge, double size
			) {
				Point3D c = edge.vertex1.point.multiply(2).add(edge.vertex2.point).divide(3);
				double m = Point3D.averageMagnitude(sv) + size;
				return c.multiply(m / c.magnitude());
			}
		},
		EDGE_MAGNITUDE_OFFSET {
			public Point3D createVertex(
				Polyhedron seed, List<Point3D> sv,
				Polyhedron.Face face, List<Point3D> fv,
				Polyhedron.Edge edge, double size
			) {
				Point3D c = edge.vertex1.point.multiply(2).add(edge.vertex2.point).divide(3);
				double m = edge.vertex1.point.midpoint(edge.vertex2.point).magnitude() + size;
				return c.multiply(m / c.magnitude());
			}
		},
		VERTEX_MAGNITUDE_OFFSET {
			public Point3D createVertex(
				Polyhedron seed, List<Point3D> sv,
				Polyhedron.Face face, List<Point3D> fv,
				Polyhedron.Edge edge, double size
			) {
				Point3D c = edge.vertex1.point.multiply(2).add(edge.vertex2.point).divide(3);
				if (size == 0) return c;
				double cm = c.magnitude();
				double m = cm + size;
				return c.multiply(m / cm);
			}
		};
		public abstract Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, double size
		);
	}
	
	private final EdgeVertexGen evgen;
	private final double evsize;
	private final Color color;
	
	public Snub(EdgeVertexGen evgen, double evsize, Color color) {
		this.evgen = evgen;
		this.evsize = evsize;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.edges.size() * 2);
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		
		List<Point3D> seedVertices = new ArrayList<Point3D>(seed.vertices.size());
		for (Polyhedron.Vertex v : seed.vertices) seedVertices.add(v.point);
		
		Map<Integer,Integer> edgeStartIndexMap = new HashMap<Integer,Integer>();
		Map<Integer,List<Point3D>> faceVertexMap = new HashMap<Integer,List<Point3D>>();
		for (Polyhedron.Face f : seed.faces) {
			edgeStartIndexMap.put(f.index, vertices.size());
			List<Point3D> faceVertices = new ArrayList<Point3D>(f.vertices.size());
			for (Polyhedron.Vertex v : f.vertices) faceVertices.add(v.point);
			faceVertexMap.put(f.index, faceVertices);
			List<Integer> faceVertexIndices = new ArrayList<Integer>(f.edges.size());
			for (Polyhedron.Edge e : f.edges) {
				faceVertexIndices.add(vertices.size());
				vertices.add(evgen.createVertex(seed, seedVertices, f, faceVertices, e, evsize));
			}
			faces.add(faceVertexIndices);
			faceColors.add(f.color);
		}
		
		for (Polyhedron.Face f : seed.faces) {
			int edgeStartIndex = edgeStartIndexMap.get(f.index);
			for (int i = 0, n = f.vertices.size(); i < n; i++) {
				int nei = edgeStartIndex + i;
				int pei = edgeStartIndex + ((i + n - 1) % n);
				Polyhedron.Edge prevEdge = f.edges.get((i + n - 1) % n);
				List<Polyhedron.Face> adjacentFaces = seed.getFaces(prevEdge);
				adjacentFaces.remove(f);
				for (Polyhedron.Face af : adjacentFaces) {
					int afesi = edgeStartIndexMap.get(af.index);
					int afei = afesi + af.edges.indexOf(prevEdge);
					faces.add(Arrays.asList(pei, afei, nei));
					faceColors.add(f.color);
				}
			}
		}
		
		for (Polyhedron.Vertex vertex : seed.vertices) {
			List<Polyhedron.Face> seedFaces = seed.getFaces(vertex);
			while (!seedFaces.isEmpty()) {
				List<Integer> truncatedFace = new ArrayList<Integer>();
				Polyhedron.Face seedFace = seedFaces.get(0);
				while (seedFace != null) {
					seedFaces.remove(seedFace);
					int sfesi = edgeStartIndexMap.get(seedFace.index);
					int sfei = sfesi + seedFace.vertices.indexOf(vertex);
					truncatedFace.add(sfei);
					seedFace = Polyhedron.getNextFace(seedFaces, seedFace, vertex);
				}
				faces.add(truncatedFace);
				faceColors.add(color);
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Snub parse(String[] args) {
		EdgeVertexGen evgen = EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET;
		double evsize = 0;
		Color color = Color.GRAY;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-s")) {
				evgen = EdgeVertexGen.FACE_OFFSET;
				evsize = 0;
			} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
				evgen = EdgeVertexGen.FACE_OFFSET;
				evsize = parseDouble(args[argi++], evsize);
			} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
				evgen = EdgeVertexGen.MAX_MAGNITUDE_OFFSET;
				evsize = parseDouble(args[argi++], evsize);
			} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
				evgen = EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET;
				evsize = parseDouble(args[argi++], evsize);
			} else if (arg.equalsIgnoreCase("-e") && argi < args.length) {
				evgen = EdgeVertexGen.EDGE_MAGNITUDE_OFFSET;
				evsize = parseDouble(args[argi++], evsize);
			} else if (arg.equalsIgnoreCase("-v") && argi < args.length) {
				evgen = EdgeVertexGen.VERTEX_MAGNITUDE_OFFSET;
				evsize = parseDouble(args[argi++], evsize);
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				color = parseColor(args[argi++], color);
			} else {
				printOptions(options());
				return null;
			}
		}
		return new Snub(evgen, evsize, color);
	}
	
	public static Option[] options() {
		return new Option[] {
			new Option("h", Type.REAL, "create new vertices a specified distance from the original faces", "m","a","e","v","s"),
			new Option("m", Type.REAL, "create new vertices relative to the maximum magnitude", "h","a","e","v","s"),
			new Option("a", Type.REAL, "create new vertices relative to the average magnitude", "h","m","e","v","s"),
			new Option("e", Type.REAL, "create new vertices relative to the edge magnitude", "h","m","a","v","s"),
			new Option("v", Type.REAL, "create new vertices relative to the vertex magnitude", "h","m","a","e","s"),
			new Option("s", Type.VOID, "create new vertices along original edges (strict mode)", "h","m","a","e","v"),
			new Option("c", Type.COLOR, "color of new faces generated from original vertices"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}