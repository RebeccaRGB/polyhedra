package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Ortho extends PolyhedronOp {
	public static enum VertexGen {
		FACE_OFFSET {
			public Point3D createFaceVertex(
				Polyhedron s, List<Point3D> sv,
				Polyhedron.Face f, List<Point3D> fv,
				double size
			) {
				Point3D c = Point3D.average(fv);
				if (size == 0) return c;
				return c.add(c.normal(fv).multiply(size));
			}
			public Point3D createEdgeVertex(
				Polyhedron seed, List<Point3D> sv,
				Polyhedron.Edge edge, double size
			) {
				return edge.vertex1.point.midpoint(edge.vertex2.point);
			}
		},
		MAX_MAGNITUDE_OFFSET {
			public Point3D createFaceVertex(
				Polyhedron s, List<Point3D> sv,
				Polyhedron.Face f, List<Point3D> fv,
				double size
			) {
				Point3D c = Point3D.average(fv);
				double m = Point3D.maxMagnitude(sv) + size;
				return c.multiply(m / c.magnitude());
			}
			public Point3D createEdgeVertex(
				Polyhedron seed, List<Point3D> sv,
				Polyhedron.Edge edge, double size
			) {
				Point3D c = edge.vertex1.point.midpoint(edge.vertex2.point);
				double m = Point3D.maxMagnitude(sv) + size;
				return c.multiply(m / c.magnitude());
			}
		},
		AVERAGE_MAGNITUDE_OFFSET {
			public Point3D createFaceVertex(
				Polyhedron s, List<Point3D> sv,
				Polyhedron.Face f, List<Point3D> fv,
				double size
			) {
				Point3D c = Point3D.average(fv);
				double m = Point3D.averageMagnitude(sv) + size;
				return c.multiply(m / c.magnitude());
			}
			public Point3D createEdgeVertex(
				Polyhedron seed, List<Point3D> sv,
				Polyhedron.Edge edge, double size
			) {
				Point3D c = edge.vertex1.point.midpoint(edge.vertex2.point);
				double m = Point3D.averageMagnitude(sv) + size;
				return c.multiply(m / c.magnitude());
			}
		},
		FACE_MAGNITUDE_OFFSET {
			public Point3D createFaceVertex(
				Polyhedron s, List<Point3D> sv,
				Polyhedron.Face f, List<Point3D> fv,
				double size
			) {
				Point3D c = Point3D.average(fv);
				if (size == 0) return c;
				double cm = c.magnitude();
				double m = cm + size;
				return c.multiply(m / cm);
			}
			public Point3D createEdgeVertex(
				Polyhedron seed, List<Point3D> sv,
				Polyhedron.Edge edge, double size
			) {
				Point3D c = edge.vertex1.point.midpoint(edge.vertex2.point);
				if (size == 0) return c;
				double cm = c.magnitude();
				double m = cm + size;
				return c.multiply(m / cm);
			}
		};
		public abstract Point3D createFaceVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			double size
		);
		public abstract Point3D createEdgeVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Edge edge, double size
		);
	}
	
	private final VertexGen gen;
	private final double size;
	
	public Ortho(VertexGen gen, double size) {
		this.gen = gen;
		this.size = size;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vefSize = seed.vertices.size() + seed.edges.size() + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>(vefSize);
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		
		for (Polyhedron.Vertex v : seed.vertices) vertices.add(v.point);
		List<Point3D> seedVertices = new ArrayList<Point3D>(vertices);
		
		int edgeStartIndex = vertices.size();
		for (Polyhedron.Edge e : seed.edges) {
			vertices.add(gen.createEdgeVertex(seed, seedVertices, e, size));
		}
		
		int faceStartIndex = vertices.size();
		for (Polyhedron.Face f : seed.faces) {
			List<Point3D> faceVertices = new ArrayList<Point3D>(f.vertices.size());
			for (Polyhedron.Vertex v : f.vertices) faceVertices.add(v.point);
			vertices.add(gen.createFaceVertex(seed, seedVertices, f, faceVertices, size));
			int fi = faceStartIndex + f.index;
			for (int i = 0, n = f.vertices.size(); i < n; i++) {
				int vi = f.vertices.get(i).index;
				int nei = edgeStartIndex + seed.edges.indexOf(f.edges.get(i));
				int pei = edgeStartIndex + seed.edges.indexOf(f.edges.get((i + n - 1) % n));
				faces.add(Arrays.asList(fi, pei, vi, nei));
				faceColors.add(f.color);
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Ortho parse(String[] args) {
		VertexGen gen = VertexGen.AVERAGE_MAGNITUDE_OFFSET;
		double size = 0;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-s")) {
				gen = VertexGen.FACE_OFFSET;
				size = 0;
			} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
				gen = VertexGen.FACE_OFFSET;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
				gen = VertexGen.MAX_MAGNITUDE_OFFSET;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
				gen = VertexGen.AVERAGE_MAGNITUDE_OFFSET;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-f") && argi < args.length) {
				gen = VertexGen.FACE_MAGNITUDE_OFFSET;
				size = parseDouble(args[argi++], size);
			} else {
				System.err.println("Options:");
				System.err.println("  -h <real>   create new vertices a specified distance from the original faces");
				System.err.println("  -m <real>   create new vertices relative to the maximum magnitude");
				System.err.println("  -a <real>   create new vertices relative to the average magnitude");
				System.err.println("  -f <real>   create new vertices relative to the face magnitude");
				System.err.println("  -s          create new vertices at centers of original faces (strict mode)");
				return null;
			}
		}
		return new Ortho(gen, size);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}