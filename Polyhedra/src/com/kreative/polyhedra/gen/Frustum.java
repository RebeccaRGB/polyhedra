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

public class Frustum extends PolyhedronGen {
	private final int n;
	private final int m;
	private final double R;
	private final double r;
	private final Axis axis;
	private final double h;
	private final Color bc;
	private final Color jc;
	
	public Frustum(int n, double R, double r, Axis axis, double h, Color c) {
		this(n, 1, R, r, axis, h, c, c);
	}
	public Frustum(int n, int m, double R, double r, Axis axis, double h, Color c) {
		this(n, m, R, r, axis, h, c, c);
	}
	public Frustum(int n, double R, double r, Axis axis, double h, Color base, Color join) {
		this(n, 1, R, r, axis, h, base, join);
	}
	public Frustum(
		int n, int m, double R, double r, Axis axis, double h, Color base, Color join
	) {
		this.n = n;
		this.m = m;
		this.R = R;
		this.r = r;
		this.axis = axis;
		this.h = h;
		this.bc = base;
		this.jc = join;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>(n);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(2);
		List<Color> faceColors = new ArrayList<Color>(2);
		Polygon.createVertices(vertices, n, r, 0, axis, h/2);
		Polygon.createVertices(vertices, n, R, 0, axis, -h/2);
		Polygon.createFaces(faces, faceColors, n, m, 0, false, bc);
		Polygon.createFaces(faces, faceColors, n, m, n, true, bc);
		for (int i = 0; i < n; i++) {
			int j = (i + m) % n;
			faces.add(Arrays.asList(j, i, i + n, j + n));
			faceColors.add(jc);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Frustum> {
		public String name() { return "Frustum"; }
		
		public Frustum parse(String[] args) {
			int n = 3;
			int m = 1;
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
				} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
					if ((m = Math.abs(parseInt(args[argi++], m))) < 1) m = 1;
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
			return new Frustum(
				n, m, R, r, axis, h, ((bc != null) ? bc : c), ((jc != null) ? jc : c)
			);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("n", Type.INT, "sides"),
				new Option("m", Type.INT, "stellation"),
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
				new Option("h", Type.REAL, "height of frustum"),
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