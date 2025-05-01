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

public class Antibicupola extends PolyhedronGen {
	private final int n;
	private final double R;
	private final double r;
	private final Axis axis;
	private final double h;
	private final boolean cgyro;
	private final boolean egyro;
	private final double e;
	private final Color baseColor;
	private final Color prismColor;
	private final Color cupolaColor;
	
	public Antibicupola(
		int n, double R, double r, Axis axis, double h, boolean cgyro,
		boolean egyro, double e, Color base, Color prism, Color cupola
	) {
		this.n = n;
		this.R = R;
		this.r = r;
		this.axis = axis;
		this.h = h;
		this.cgyro = cgyro;
		this.egyro = egyro;
		this.e = e;
		this.baseColor = base;
		this.prismColor = prism;
		this.cupolaColor = cupola;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>(n*4);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(n*6+2);
		List<Color> faceColors = new ArrayList<Color>(n*6+2);
		Polygon.createVertices(vertices, n, r, 0, axis, h+e/2);
		if (e != 0) {
			Polygon.createVertices(vertices, n*2, R, 0, axis, e/2);
			Polygon.createVertices(vertices, n*2, R, (egyro ? 0.5 : 0), axis, -e/2);
			Polygon.createVertices(vertices, n, r, (cgyro ? 0.5 : 0) + (egyro ? 0.25 : 0), axis, -h-e/2);
		} else {
			Polygon.createVertices(vertices, n*2, R, 0, axis, 0);
			Polygon.createVertices(vertices, n, r, (cgyro ? 0.5 : 0), axis, -h-e/2);
		}
		Polygon.createFaces(faces, faceColors, n, 1, 0, false, baseColor);
		Polygon.createFaces(faces, faceColors, n, 1, ((e != 0) ? (n*5) : (n*3)), true, baseColor);
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			int k = (n + i + i);
			int l = (n + i + i + 1);
			int m = (n + j + j);
			int p = (n*3 + i);
			int q = (n*3 + j);
			if (e != 0) {
				if (egyro) {
					faces.add(Arrays.asList(l, k, k+n+n));
					faces.add(Arrays.asList(l, k+n+n, l+n+n));
					faces.add(Arrays.asList(m, l, l+n+n));
					faces.add(Arrays.asList(m, l+n+n, m+n+n));
					faceColors.add(prismColor);
					faceColors.add(prismColor);
					faceColors.add(prismColor);
					faceColors.add(prismColor);
				} else {
					faces.add(Arrays.asList(l, k, k+n+n, l+n+n));
					faces.add(Arrays.asList(m, l, l+n+n, m+n+n));
					faceColors.add(prismColor);
					faceColors.add(prismColor);
				}
				faces.add(Arrays.asList(i, k, l));
				faces.add(Arrays.asList(j, i, l));
				faces.add(Arrays.asList(j, l, m));
				faces.add(Arrays.asList(p+n+n, l+n+n, k+n+n));
				if (cgyro) {
					faces.add(Arrays.asList(p+n+n, q+n+n, m+n+n));
					faces.add(Arrays.asList(p+n+n, m+n+n, l+n+n));
				} else {
					faces.add(Arrays.asList(p+n+n, q+n+n, l+n+n));
					faces.add(Arrays.asList(q+n+n, m+n+n, l+n+n));
				}
				faceColors.add(cupolaColor);
				faceColors.add(cupolaColor);
				faceColors.add(cupolaColor);
				faceColors.add(cupolaColor);
				faceColors.add(cupolaColor);
				faceColors.add(cupolaColor);
			} else {
				faces.add(Arrays.asList(i, k, l));
				faces.add(Arrays.asList(j, i, l));
				faces.add(Arrays.asList(j, l, m));
				faces.add(Arrays.asList(p, l, k));
				if (cgyro) {
					faces.add(Arrays.asList(p, q, m));
					faces.add(Arrays.asList(p, m, l));
				} else {
					faces.add(Arrays.asList(p, q, l));
					faces.add(Arrays.asList(q, m, l));
				}
				faceColors.add(cupolaColor);
				faceColors.add(cupolaColor);
				faceColors.add(cupolaColor);
				faceColors.add(cupolaColor);
				faceColors.add(cupolaColor);
				faceColors.add(cupolaColor);
			}
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Antibicupola> {
		public String name() { return "Antibicupola"; }
		
		public Antibicupola parse(String[] args) {
			int n = 3;
			SizeSpecifier Spec = SizeSpecifier.RADIUS;
			SizeSpecifier spec = SizeSpecifier.RADIUS;
			double Size = 1;
			double size = 0.5;
			Axis axis = Axis.Y;
			double h = 1;
			boolean cgyro = false;
			boolean egyro = false;
			double e = 0;
			Color c = Color.GRAY;
			Color baseColor = null;
			Color prismColor = null;
			Color cupolaColor = null;
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
				} else if (arg.equalsIgnoreCase("-o")) {
					cgyro = false;
				} else if (arg.equalsIgnoreCase("-q")) {
					cgyro = true;
				} else if (arg.equalsIgnoreCase("-e") && argi < args.length) {
					egyro = false; e = parseDouble(args[argi++], e);
				} else if (arg.equalsIgnoreCase("-g") && argi < args.length) {
					egyro = true; e = parseDouble(args[argi++], e);
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					c = parseColor(args[argi++], c);
				} else if (arg.equalsIgnoreCase("-b") && argi < args.length) {
					baseColor = parseColor(args[argi++], baseColor);
				} else if (arg.equalsIgnoreCase("-p") && argi < args.length) {
					prismColor = parseColor(args[argi++], prismColor);
				} else if (arg.equalsIgnoreCase("-j") && argi < args.length) {
					cupolaColor = parseColor(args[argi++], cupolaColor);
				} else {
					return null;
				}
			}
			double R = Spec.toRadius(Size, n);
			double r = spec.toRadius(size, n);
			return new Antibicupola(
				n, R, r, axis, h, cgyro, egyro, e,
				((baseColor != null) ? baseColor : c),
				((prismColor != null) ? prismColor : c),
				((cupolaColor != null) ? cupolaColor : c)
			);
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
				new Option("h", Type.REAL, "height of anticupola"),
				new Option("o", Type.VOID, "specify orthobicupola", "q"),
				new Option("q", Type.VOID, "specify gyrobicupola", "o"),
				new Option("e", Type.REAL, "height of prism (elongate)"),
				new Option("g", Type.REAL, "height of antiprism (gyroelongate)"),
				new Option("c", Type.COLOR, "color", "b","p","j"),
				new Option("b", Type.COLOR, "base color", "c"),
				new Option("p", Type.COLOR, "prism color", "c"),
				new Option("j", Type.COLOR, "cupola color", "c"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}