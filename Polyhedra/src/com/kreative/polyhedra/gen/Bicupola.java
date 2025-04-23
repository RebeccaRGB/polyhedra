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

public class Bicupola extends PolyhedronGen {
	private final int n;
	private final double R;
	private final double r;
	private final Axis axis;
	private final double h;
	private final Color bc;
	private final Color jc;
	
	public Bicupola(int n, double R, double r, Axis axis, double h, Color c) {
		this(n, R, r, axis, h, c, c);
	}
	public Bicupola(int n, double R, double r, Axis axis, double h, Color base, Color join) {
		this.n = n;
		this.R = R;
		this.r = r;
		this.axis = axis;
		this.h = h;
		this.bc = base;
		this.jc = join;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>(n*4);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(n*4+2);
		List<Color> faceColors = new ArrayList<Color>(n*4+2);
		Polygon.createVertices(vertices, n, r, 0, axis, h);
		Polygon.createVertices(vertices, n*2, R, 0.5, axis, 0);
		Polygon.createVertices(vertices, n, r, 0, axis, -h);
		Polygon.createFaces(faces, faceColors, n, 1, 0, false, bc);
		Polygon.createFaces(faces, faceColors, n, 1, n*3, true, bc);
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			int k = (n + i + i);
			int l = (n + i + i + 1);
			int m = (n + j + j);
			int p = (n*3 + i);
			int q = (n*3 + j);
			faces.add(Arrays.asList(j, i, k, l));
			faces.add(Arrays.asList(j, l, m));
			faces.add(Arrays.asList(p, q, l, k));
			faces.add(Arrays.asList(q, m, l));
			faceColors.add(jc);
			faceColors.add(jc);
			faceColors.add(jc);
			faceColors.add(jc);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Bicupola> {
		public String name() { return "Bicupola"; }
		
		public Bicupola parse(String[] args) {
			int n = 3;
			SizeSpecifier Spec = SizeSpecifier.RADIUS;
			SizeSpecifier spec = SizeSpecifier.RADIUS;
			double Size = 1;
			double size = 0.5;
			Axis axis = Axis.Y;
			double h = 1;
			Color c = Color.GRAY;
			Color bc = null;
			Color jc = null;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-n") && argi < args.length) {
					if ((n = Math.abs(parseInt(args[argi++], n))) < 3) n = 3;
				} else if (arg.equals("-R") && argi < args.length) {
					Spec = SizeSpecifier.RADIUS;
					Size = parseDouble(args[argi++], Size);
				} else if (arg.equals("-D") && argi < args.length) {
					Spec = SizeSpecifier.DIAMETER;
					Size = parseDouble(args[argi++], Size);
				} else if (arg.equals("-S") && argi < args.length) {
					Spec = SizeSpecifier.SIDE_LENGTH;
					Size = parseDouble(args[argi++], Size);
				} else if (arg.equals("-A") && argi < args.length) {
					Spec = SizeSpecifier.APOTHEM;
					Size = parseDouble(args[argi++], Size);
				} else if (arg.equals("-r") && argi < args.length) {
					spec = SizeSpecifier.RADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-d") && argi < args.length) {
					spec = SizeSpecifier.DIAMETER;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-s") && argi < args.length) {
					spec = SizeSpecifier.SIDE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-a") && argi < args.length) {
					spec = SizeSpecifier.APOTHEM;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-x")) {
					axis = Axis.X;
				} else if (arg.equalsIgnoreCase("-y")) {
					axis = Axis.Y;
				} else if (arg.equalsIgnoreCase("-z")) {
					axis = Axis.Z;
				} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
					h = parseDouble(args[argi++], h);
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					c = parseColor(args[argi++], c);
				} else if (arg.equalsIgnoreCase("-b") && argi < args.length) {
					bc = parseColor(args[argi++], bc);
				} else if (arg.equalsIgnoreCase("-j") && argi < args.length) {
					jc = parseColor(args[argi++], jc);
				} else {
					return null;
				}
			}
			double R = Spec.toRadius(Size, n);
			double r = spec.toRadius(size, n);
			return new Bicupola(n, R, r, axis, h, ((bc != null) ? bc : c), ((jc != null) ? jc : c));
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("n", Type.INT, "number of sides on top face"),
				new Option("R", Type.REAL, "radius of bottom face", "D","S","A"),
				new Option("D", Type.REAL, "diameter of bottom face", "R","S","A"),
				new Option("S", Type.REAL, "side length of bottom face", "R","D","A"),
				new Option("A", Type.REAL, "apothem of bottom face", "R","D","S"),
				new Option("r", Type.REAL, "radius of top face", "d","s","a"),
				new Option("d", Type.REAL, "diameter of top face", "r","s","a"),
				new Option("s", Type.REAL, "side length of top face", "r","d","a"),
				new Option("a", Type.REAL, "apothem of top face", "r","d","s"),
				new Option("x", Type.VOID, "align central axis to X axis", "y","z"),
				new Option("y", Type.VOID, "align central axis to Y axis", "x","z"),
				new Option("z", Type.VOID, "align central axis to Z axis", "x","y"),
				new Option("h", Type.REAL, "height of cupola"),
				new Option("c", Type.COLOR, "color", "b","j"),
				new Option("b", Type.COLOR, "base color", "c"),
				new Option("j", Type.COLOR, "join color", "c"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}