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

public class Gyro extends PolyhedronOp {
	public static enum FaceVertexGen {
		FACE_OFFSET {
			public Point3D createVertex(
				Polyhedron s, List<Point3D> sv,
				Polyhedron.Face f, List<Point3D> fv,
				double size
			) {
				Point3D c = Point3D.average(fv);
				if (size == 0) return c;
				return c.add(c.normal(fv).multiply(size));
			}
		},
		MAX_MAGNITUDE_OFFSET {
			public Point3D createVertex(
				Polyhedron s, List<Point3D> sv,
				Polyhedron.Face f, List<Point3D> fv,
				double size
			) {
				Point3D c = Point3D.average(fv);
				double m = Point3D.maxMagnitude(sv) + size;
				return c.multiply(m / c.magnitude());
			}
		},
		AVERAGE_MAGNITUDE_OFFSET {
			public Point3D createVertex(
				Polyhedron s, List<Point3D> sv,
				Polyhedron.Face f, List<Point3D> fv,
				double size
			) {
				Point3D c = Point3D.average(fv);
				double m = Point3D.averageMagnitude(sv) + size;
				return c.multiply(m / c.magnitude());
			}
		},
		FACE_MAGNITUDE_OFFSET {
			public Point3D createVertex(
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
		};
		public abstract Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			double size
		);
	}
	
	public static enum EdgeVertexGen {
		FACE_OFFSET {
			public Point3D createVertex(
				Polyhedron seed, List<Point3D> sv,
				Polyhedron.Face face, List<Point3D> fv,
				Polyhedron.Edge edge, double size
			) {
				Point3D c = edge.vertex1.point.add(edge.vertex2.point.multiply(2)).divide(3);
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
				Point3D c = edge.vertex1.point.add(edge.vertex2.point.multiply(2)).divide(3);
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
				Point3D c = edge.vertex1.point.add(edge.vertex2.point.multiply(2)).divide(3);
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
				Point3D c = edge.vertex1.point.add(edge.vertex2.point.multiply(2)).divide(3);
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
				Point3D c = edge.vertex1.point.add(edge.vertex2.point.multiply(2)).divide(3);
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
	
	private final FaceVertexGen fvgen;
	private final double fvsize;
	private final EdgeVertexGen evgen;
	private final double evsize;
	
	public Gyro(FaceVertexGen fvgen, double fvsize, EdgeVertexGen evgen, double evsize) {
		this.fvgen = fvgen;
		this.fvsize = fvsize;
		this.evgen = evgen;
		this.evsize = evsize;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vefSize = seed.vertices.size() + (seed.edges.size() * 2) + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>(vefSize);
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		
		for (Polyhedron.Vertex v : seed.vertices) vertices.add(v.point);
		List<Point3D> seedVertices = new ArrayList<Point3D>(vertices);
		
		Map<Integer,Integer> edgeStartIndexMap = new HashMap<Integer,Integer>();
		Map<Integer,List<Point3D>> faceVertexMap = new HashMap<Integer,List<Point3D>>();
		for (Polyhedron.Face f : seed.faces) {
			edgeStartIndexMap.put(f.index, vertices.size());
			List<Point3D> faceVertices = new ArrayList<Point3D>(f.vertices.size());
			for (Polyhedron.Vertex v : f.vertices) faceVertices.add(v.point);
			faceVertexMap.put(f.index, faceVertices);
			for (Polyhedron.Edge e : f.edges) {
				vertices.add(evgen.createVertex(seed, seedVertices, f, faceVertices, e, evsize));
			}
		}
		
		int faceStartIndex = vertices.size();
		for (Polyhedron.Face f : seed.faces) {
			List<Point3D> faceVertices = faceVertexMap.get(f.index);
			vertices.add(fvgen.createVertex(seed, seedVertices, f, faceVertices, fvsize));
			int fi = faceStartIndex + f.index;
			int edgeStartIndex = edgeStartIndexMap.get(f.index);
			for (int i = 0, n = f.vertices.size(); i < n; i++) {
				int vi = f.vertices.get(i).index;
				int nei = edgeStartIndex + i;
				int pei = edgeStartIndex + ((i + n - 1) % n);
				Polyhedron.Edge nextEdge = f.edges.get(i);
				List<Polyhedron.Face> adjacentFaces = seed.getFaces(nextEdge);
				adjacentFaces.remove(f);
				for (Polyhedron.Face af : adjacentFaces) {
					int afesi = edgeStartIndexMap.get(af.index);
					int afei = afesi + af.edges.indexOf(nextEdge);
					faces.add(Arrays.asList(fi, pei, vi, afei, nei));
					faceColors.add(f.color);
				}
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Gyro parse(String[] args) {
		FaceVertexGen fvgen = FaceVertexGen.AVERAGE_MAGNITUDE_OFFSET;
		double fvsize = 0;
		EdgeVertexGen evgen = EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET;
		double evsize = 0;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-s")) {
				fvgen = FaceVertexGen.FACE_OFFSET;
				fvsize = 0;
				evgen = EdgeVertexGen.FACE_OFFSET;
				evsize = 0;
			} else if (arg.equals("-H") && argi < args.length) {
				fvgen = FaceVertexGen.FACE_OFFSET;
				fvsize = parseDouble(args[argi++], fvsize);
			} else if (arg.equals("-M") && argi < args.length) {
				fvgen = FaceVertexGen.MAX_MAGNITUDE_OFFSET;
				fvsize = parseDouble(args[argi++], fvsize);
			} else if (arg.equals("-A") && argi < args.length) {
				fvgen = FaceVertexGen.AVERAGE_MAGNITUDE_OFFSET;
				fvsize = parseDouble(args[argi++], fvsize);
			} else if (arg.equals("-F") && argi < args.length) {
				fvgen = FaceVertexGen.FACE_MAGNITUDE_OFFSET;
				fvsize = parseDouble(args[argi++], fvsize);
			} else if (arg.equals("-h") && argi < args.length) {
				evgen = EdgeVertexGen.FACE_OFFSET;
				evsize = parseDouble(args[argi++], evsize);
			} else if (arg.equals("-m") && argi < args.length) {
				evgen = EdgeVertexGen.MAX_MAGNITUDE_OFFSET;
				evsize = parseDouble(args[argi++], evsize);
			} else if (arg.equals("-a") && argi < args.length) {
				evgen = EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET;
				evsize = parseDouble(args[argi++], evsize);
			} else if (arg.equals("-e") && argi < args.length) {
				evgen = EdgeVertexGen.EDGE_MAGNITUDE_OFFSET;
				evsize = parseDouble(args[argi++], evsize);
			} else if (arg.equals("-v") && argi < args.length) {
				evgen = EdgeVertexGen.VERTEX_MAGNITUDE_OFFSET;
				evsize = parseDouble(args[argi++], evsize);
			} else {
				System.err.println("Options:");
				System.err.println("  -H <real>   create new vertices from faces normal to the original face");
				System.err.println("  -M <real>   create new vertices from faces relative to the maximum magnitude");
				System.err.println("  -A <real>   create new vertices from faces relative to the average magnitude");
				System.err.println("  -F <real>   create new vertices from faces relative to the face magnitude");
				System.err.println("  -h <real>   create new vertices from edges normal to the original face");
				System.err.println("  -m <real>   create new vertices from edges relative to the maximum magnitude");
				System.err.println("  -a <real>   create new vertices from edges relative to the average magnitude");
				System.err.println("  -e <real>   create new vertices from edges relative to the edge magnitude");
				System.err.println("  -v <real>   create new vertices from edges relative to the vertex magnitude");
				System.err.println("  -s          create new vertices at centers of original faces (strict mode)");
				return null;
			}
		}
		return new Gyro(fvgen, fvsize, evgen, evsize);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}