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
	
	public static void createVertices(List<Point3D> points, int n, double r, double z) {
		for (int i = 0; i < n; i++) {
			double a = i * 2 * Math.PI / n;
			double x = r * Math.cos(a);
			double y = r * Math.sin(a);
			points.add(new Point3D(x, y, z));
		}
	}
	
	private final int n;
	private final int m;
	private final double r;
	private final double z;
	private final Color c;
	
	public Polygon(int n, double r, Color c) { this(n, 1, r, 0, c); }
	public Polygon(int n, int m, double r, Color c) { this(n, m, r, 0, c); }
	public Polygon(int n, double r, double z, Color c) { this(n, 1, r, z, c); }
	public Polygon(int n, int m, double r, double z, Color c) {
		this.n = n;
		this.m = m;
		this.r = r;
		this.z = z;
		this.c = c;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>(n);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(2);
		List<Color> faceColors = new ArrayList<Color>(2);
		
		createVertices(vertices, n, r, z);
		
		List<Integer> face = new ArrayList<Integer>(n);
		for (int index = 0, i = 0; i < n; i++) {
			face.add(index);
			index = (index + m) % n;
			if (face.contains(index)) {
				faces.add(face);
				faceColors.add(c);
				face = new ArrayList<Integer>(n);
				index = (index + 1) % n;
			}
		}
		
		for (int i = 0, j = faces.size(); i < j; i++) {
			face = new ArrayList<Integer>(n);
			face.addAll(faces.get(i));
			Collections.reverse(face);
			faces.add(face);
			faceColors.add(c);
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static void main(String[] args) {
		int n = 3;
		int m = 1;
		SizeSpecifier spec = SizeSpecifier.RADIUS;
		double size = 1;
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
			} else if (arg.equalsIgnoreCase("-z") && argi < args.length) {
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
				System.err.println("  -z <real>   z-coordinate");
				System.err.println("  -c <color>  color");
				return;
			}
		}
		main(new Polygon(n, m, spec.toRadius(size, n), z, c));
	}
}