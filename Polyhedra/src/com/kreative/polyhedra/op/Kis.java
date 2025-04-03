package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Kis extends PolyhedronOp {
	public static enum VertexGen {
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
		},
		EQUILATERAL {
			public Point3D createVertex(
				Polyhedron s, List<Point3D> sv,
				Polyhedron.Face f, List<Point3D> fv,
				double size
			) {
				Point3D c = Point3D.average(fv);
				if (fv.size() > 5) return c;
				double heights = 0;
				for (int i = 0, n = fv.size(); i < n; i++) {
					Point3D v1 = fv.get(i);
					Point3D v2 = fv.get((i + 1) % n);
					Point3D m = v1.midpoint(v2);
					double h2 = v1.distanceSq(v2) * 0.75 - m.distanceSq(c);
					if (h2 > 0) heights += Math.sqrt(h2);
				}
				return c.add(c.normal(fv).multiply(heights / fv.size()));
			}
		},
		PLANAR {
			public Point3D createVertex(
				Polyhedron s, List<Point3D> sv,
				Polyhedron.Face f, List<Point3D> fv,
				double size
			) {
				Point3D c = Point3D.average(fv);
				double heights = 0;
				for (Polyhedron.Edge e : f.edges) {
					List<Point3D> avs = new ArrayList<Point3D>();
					for (Polyhedron.Face af : s.getFaces(e)) {
						if (!af.equals(f)) {
							for (Polyhedron.Vertex av : af.vertices) {
								avs.add(av.point);
							}
						}
					}
					Point3D ac = Point3D.average(avs);
					Point3D m = e.vertex1.point.midpoint(e.vertex2.point);
					double d = m.distance(c), ad = m.distance(ac);
					heights += d / Math.tan(m.angleRad(c, ac) * d / (d + ad));
				}
				return c.add(c.normal(fv).multiply(heights / f.edges.size()));
			}
		};
		public abstract Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			double size
		);
	}
	
	private final Set<Integer> sides;
	private final VertexGen gen;
	private final double size;
	
	public Kis(int[] sides, VertexGen gen, double size) {
		this.sides = new HashSet<Integer>();
		if (sides != null) for (int i : sides) this.sides.add(i);
		this.gen = gen;
		this.size = size;
	}
	
	public Kis(Integer[] sides, VertexGen gen, double size) {
		this.sides = new HashSet<Integer>();
		if (sides != null) for (int i : sides) this.sides.add(i);
		this.gen = gen;
		this.size = size;
	}
	
	public Kis(Iterable<? extends Integer> sides, VertexGen gen, double size) {
		this.sides = new HashSet<Integer>();
		if (sides != null) for (int i : sides) this.sides.add(i);
		this.gen = gen;
		this.size = size;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		
		for (Polyhedron.Vertex v : seed.vertices) vertices.add(v.point);
		List<Point3D> seedVertices = new ArrayList<Point3D>(vertices);
		
		for (Polyhedron.Face f : seed.faces) {
			if (sides.isEmpty() || sides.contains(f.vertices.size())) {
				List<Point3D> faceVertices = new ArrayList<Point3D>(f.vertices.size());
				for (Polyhedron.Vertex v : f.vertices) faceVertices.add(v.point);
				Point3D newVertex = gen.createVertex(seed, seedVertices, f, faceVertices, size);
				if (newVertex != null) {
					int i0 = vertices.size();
					vertices.add(newVertex);
					for (int i = 0, n = f.vertices.size(); i < n; i++) {
						int i1 = f.vertices.get(i).index;
						int i2 = f.vertices.get((i + 1) % n).index;
						faces.add(Arrays.asList(i0, i1, i2));
						faceColors.add(f.color);
					}
					continue;
				}
			}
			List<Integer> face = new ArrayList<Integer>();
			for (Polyhedron.Vertex v : f.vertices) face.add(v.index);
			faces.add(face);
			faceColors.add(f.color);
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Kis parse(String[] args) {
		List<Integer> sides = null;
		VertexGen gen = VertexGen.EQUILATERAL;
		double size = 0;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-n") && argi < args.length) {
				sides = parseIntList(args[argi++]);
			} else if (arg.equalsIgnoreCase("-s")) {
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
			} else if (arg.equalsIgnoreCase("-e")) {
				gen = VertexGen.EQUILATERAL;
				size = 0;
			} else if (arg.equalsIgnoreCase("-p")) {
				gen = VertexGen.PLANAR;
				size = 0;
			} else {
				System.err.println("Options:");
				System.err.println("  -n <ints>   only operate on faces with the specified number of edges");
				System.err.println("  -h <real>   create new vertices a specified distance from the original faces");
				System.err.println("  -m <real>   create new vertices relative to the maximum magnitude");
				System.err.println("  -a <real>   create new vertices relative to the average magnitude");
				System.err.println("  -f <real>   create new vertices relative to the face magnitude");
				System.err.println("  -e          attempt to create equilateral triangles (not always possible)");
				System.err.println("  -s          create new vertices at centers of original faces (strict mode)");
				return null;
			}
		}
		return new Kis(sides, gen, size);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}