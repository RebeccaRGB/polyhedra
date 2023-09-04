package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Polygon extends PolyhedronGen {
	public static enum SizeSpecifier {
		RADIUS {
			public double toRadius(double radius, int n) { return radius; }
			public double fromRadius(double radius, int n) { return radius; }
		},
		DIAMETER {
			public double toRadius(double diameter, int n) { return diameter / 2; }
			public double fromRadius(double radius, int n) { return radius * 2; }
		},
		SIDE_LENGTH {
			public double toRadius(double s, int n) { return s / (2 * Math.sin(Math.PI / n)); }
			public double fromRadius(double r, int n) { return r * (2 * Math.sin(Math.PI / n)); }
		},
		APOTHEM {
			public double toRadius(double a, int n) { return a / Math.cos(Math.PI / n); }
			public double fromRadius(double r, int n) { return r * Math.cos(Math.PI / n); }
		};
		public abstract double toRadius(double size, int n);
		public abstract double fromRadius(double radius, int n);
	}
	
	public static enum Axis {
		X {public Point3D createVertex(double x, double y, double z) {return new Point3D(z,x,y);}},
		Y {public Point3D createVertex(double x, double y, double z) {return new Point3D(y,z,x);}},
		Z {public Point3D createVertex(double x, double y, double z) {return new Point3D(x,y,z);}};
		public abstract Point3D createVertex(double x, double y, double z);
	}
	
	public static void createVertices(
		List<Point3D> points, int n, double r, double ph, Axis axis, double z
	) {
		for (int i = 0; i < n; i++) {
			double a = (i + ph) * 2 * Math.PI / n;
			double x = r * Math.cos(a);
			double y = r * Math.sin(a);
			points.add(axis.createVertex(x, y, z));
		}
	}
	
	public static void createFaces(
		List<List<Integer>> faces, List<Color> faceColors,
		int n, int m, int firstIndex, boolean reverse, Color color
	) {
		List<Integer> face = new ArrayList<Integer>(n);
		for (int index = 0, i = 0; i < n; i++) {
			face.add(firstIndex + index);
			index = (index + m) % n;
			if (face.contains(firstIndex + index)) {
				if (reverse) Collections.reverse(face);
				faces.add(face);
				faceColors.add(color);
				face = new ArrayList<Integer>(n);
				index = (index + 1) % n;
			}
		}
	}
	
	private final int n;
	private final int m;
	private final double r;
	private final Axis axis;
	private final double z;
	private final Color c;
	
	public Polygon(int n, double r, Axis axis, Color c) { this(n, 1, r, axis, 0, c); }
	public Polygon(int n, int m, double r, Axis axis, Color c) { this(n, m, r, axis, 0, c); }
	public Polygon(int n, double r, Axis axis, double z, Color c) { this(n, 1, r, axis, z, c); }
	public Polygon(int n, int m, double r, Axis axis, double z, Color c) {
		this.n = n;
		this.m = m;
		this.r = r;
		this.axis = axis;
		this.z = z;
		this.c = c;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>(n);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(2);
		List<Color> faceColors = new ArrayList<Color>(2);
		createVertices(vertices, n, r, 0, axis, z);
		createFaces(faces, faceColors, n, m, 0, false, c);
		createFaces(faces, faceColors, n, m, 0, true, c);
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Polygon parse(String[] args) {
		int n = 3;
		int m = 1;
		SizeSpecifier spec = SizeSpecifier.RADIUS;
		double size = 1;
		Axis axis = Axis.Y;
		double z = 0;
		Color c = Color.GRAY;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-n") && argi < args.length) {
				if ((n = Math.abs(parseInt(args[argi++], n))) < 3) n = 3;
			} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
				if ((m = Math.abs(parseInt(args[argi++], m))) < 1) m = 1;
			} else if (arg.equalsIgnoreCase("-r") && argi < args.length) {
				spec = SizeSpecifier.RADIUS;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-d") && argi < args.length) {
				spec = SizeSpecifier.DIAMETER;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-s") && argi < args.length) {
				spec = SizeSpecifier.SIDE_LENGTH;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
				spec = SizeSpecifier.APOTHEM;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-x") && argi < args.length) {
				axis = Axis.X;
				z = parseDouble(args[argi++], z);
			} else if (arg.equalsIgnoreCase("-y") && argi < args.length) {
				axis = Axis.Y;
				z = parseDouble(args[argi++], z);
			} else if (arg.equalsIgnoreCase("-z") && argi < args.length) {
				axis = Axis.Z;
				z = parseDouble(args[argi++], z);
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				c = parseColor(args[argi++], c);
			} else {
				System.err.println("Options:");
				System.err.println("  -n <int>    sides");
				System.err.println("  -m <int>    stellation");
				System.err.println("  -r <real>   radius");
				System.err.println("  -d <real>   diameter");
				System.err.println("  -s <real>   side length");
				System.err.println("  -a <real>   apothem");
				System.err.println("  -x <real>   x-coordinate of polygon parallel to yz plane");
				System.err.println("  -y <real>   y-coordinate of polygon parallel to xz plane");
				System.err.println("  -z <real>   z-coordinate of polygon parallel to xy plane");
				System.err.println("  -c <color>  color");
				return null;
			}
		}
		return new Polygon(n, m, spec.toRadius(size, n), axis, z, c);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}