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
	private final boolean gyro;
	private final double e;
	private final Color prismColor;
	private final Color pyramidColor;
	
	public Bipyramid(int n, int m, double r, Axis axis, double h, boolean gyro, double e, Color prism, Color pyramid) {
		this.n = n;
		this.m = m;
		this.r = r;
		this.axis = axis;
		this.h = h;
		this.gyro = gyro;
		this.e = e;
		this.prismColor = prism;
		this.pyramidColor = pyramid;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>(n);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(2);
		List<Color> faceColors = new ArrayList<Color>(2);
		if (e != 0) {
			Polygon.createVertices(vertices, n, r, 0, axis, -e/2);
			Polygon.createVertices(vertices, n, r, (gyro ? 0.5 : 0), axis, e/2);
		} else {
			Polygon.createVertices(vertices, n, r, 0, axis, 0);
		}
		vertices.add(axis.createVertex(0, 0, h+e/2));
		vertices.add(axis.createVertex(0, 0, -h-e/2));
		for (int i = 0; i < n; i++) {
			int j = (i + m) % n;
			if (e != 0) {
				if (gyro) {
					faces.add(Arrays.asList(i+n, i, j));
					faces.add(Arrays.asList(j+n, i+n, j));
					faceColors.add(prismColor);
					faceColors.add(prismColor);
				} else {
					faces.add(Arrays.asList(j+n, i+n, i, j));
					faceColors.add(prismColor);
				}
				faces.add(Arrays.asList(i+n, j+n, n+n));
				faces.add(Arrays.asList(j, i, n+n+1));
				faceColors.add(pyramidColor);
				faceColors.add(pyramidColor);
			} else {
				faces.add(Arrays.asList(i, j, n));
				faces.add(Arrays.asList(j, i, n+1));
				faceColors.add(pyramidColor);
				faceColors.add(pyramidColor);
			}
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Bipyramid> {
		public String name() { return "Bipyramid"; }
		
		public Bipyramid parse(String[] args) {
			int n = 3;
			int m = 1;
			SizeSpecifier spec = SizeSpecifier.RADIUS;
			double size = 1;
			Axis axis = Axis.Y;
			Double h = null;
			boolean gyro = false;
			double e = 0;
			Color c = Color.GRAY;
			Color prismColor = null;
			Color pyramidColor = null;
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
					h = parseDouble(args[argi++], ((h == null) ? 1.0 : h.doubleValue()));
				} else if (arg.equalsIgnoreCase("-e") && argi < args.length) {
					gyro = false; e = parseDouble(args[argi++], e);
				} else if (arg.equalsIgnoreCase("-g") && argi < args.length) {
					gyro = true; e = parseDouble(args[argi++], e);
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					c = parseColor(args[argi++], c);
				} else if (arg.equalsIgnoreCase("-p") && argi < args.length) {
					prismColor = parseColor(args[argi++], prismColor);
				} else if (arg.equalsIgnoreCase("-j") && argi < args.length) {
					pyramidColor = parseColor(args[argi++], pyramidColor);
				} else {
					return null;
				}
			}
			double r = spec.toRadius(size, n);
			if (h == null) h = r;
			return new Bipyramid(
				n, m, r, axis, h, gyro, e,
				((prismColor != null) ? prismColor : c),
				((pyramidColor != null) ? pyramidColor : c)
			);
		}
		
		public Option[] options() {
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
				new Option("e", Type.REAL, "height of prism (elongate)"),
				new Option("g", Type.REAL, "height of antiprism (gyroelongate)"),
				new Option("c", Type.COLOR, "color", "p","j"),
				new Option("p", Type.COLOR, "prism color", "c"),
				new Option("j", Type.COLOR, "pyramid color", "c"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}