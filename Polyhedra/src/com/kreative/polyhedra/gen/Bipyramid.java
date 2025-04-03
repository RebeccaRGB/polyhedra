package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;
import com.kreative.polyhedra.gen.Polygon.Axis;
import com.kreative.polyhedra.gen.Polygon.SizeSpecifier;

public class Bipyramid extends PolyhedronGen {
	private final int n;
	private final int m;
	private final double r;
	private final Axis axis;
	private final double h;
	private final Color c;
	
	public Bipyramid(int n, double r, Axis axis, double h, Color c) {
		this(n, 1, r, axis, h, c);
	}
	public Bipyramid(int n, int m, double r, Axis axis, double h, Color c) {
		this.n = n;
		this.m = m;
		this.r = r;
		this.axis = axis;
		this.h = h;
		this.c = c;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>(n);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(2);
		List<Color> faceColors = new ArrayList<Color>(2);
		Polygon.createVertices(vertices, n, r, 0, axis, 0);
		vertices.add(axis.createVertex(0, 0, h));
		vertices.add(axis.createVertex(0, 0, -h));
		for (int i = 0; i < n; i++) {
			int j = (i + m) % n;
			faces.add(Arrays.asList(i, j, n));
			faces.add(Arrays.asList(j, i, n+1));
			faceColors.add(c);
			faceColors.add(c);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Bipyramid parse(String[] args) {
		int n = 3;
		int m = 1;
		SizeSpecifier spec = SizeSpecifier.RADIUS;
		double size = 1;
		Axis axis = Axis.Y;
		Double h = null;
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
			} else if (arg.equalsIgnoreCase("-x")) {
				axis = Axis.X;
			} else if (arg.equalsIgnoreCase("-y")) {
				axis = Axis.Y;
			} else if (arg.equalsIgnoreCase("-z")) {
				axis = Axis.Z;
			} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
				h = parseDouble(args[argi++], ((h == null) ? 1 : h.intValue()));
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				c = parseColor(args[argi++], c);
			} else {
				printOptions(options());
				return null;
			}
		}
		double r = spec.toRadius(size, n);
		if (h == null) h = r;
		return new Bipyramid(n, m, r, axis, h, c);
	}
	
	public static Option[] options() {
		return new Option[] {
			new Option("n", Type.INT, "sides"),
			new Option("m", Type.INT, "stellation"),
			new Option("r", Type.REAL, "radius of base", "d","s","a"),
			new Option("d", Type.REAL, "diameter of base", "r","s","a"),
			new Option("s", Type.REAL, "side length of base", "r","d","a"),
			new Option("a", Type.REAL, "apothem of base", "r","d","s"),
			new Option("x", Type.VOID, "align central axis to X axis", "y","z"),
			new Option("y", Type.VOID, "align central axis to Y axis", "x","z"),
			new Option("z", Type.VOID, "align central axis to Z axis", "x","y"),
			new Option("h", Type.REAL, "height of pyramid"),
			new Option("c", Type.COLOR, "color"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}