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

public class Antiprism extends PolyhedronGen {
	private final int n;
	private final int m;
	private final double r;
	private final Axis axis;
	private final double h;
	private final Color baseColor;
	private final Color joinColor;
	
	public Antiprism(int n, int m, double r, Axis axis, double h, Color base, Color join) {
		this.n = n;
		this.m = m;
		this.r = r;
		this.axis = axis;
		this.h = h;
		this.baseColor = base;
		this.joinColor = join;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>(n);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(2);
		List<Color> faceColors = new ArrayList<Color>(2);
		Polygon.createVertices(vertices, n, r, 0, axis, h/2);
		Polygon.createVertices(vertices, n, r, 0.5, axis, -h/2);
		Polygon.createFaces(faces, faceColors, n, m, 0, false, baseColor);
		Polygon.createFaces(faces, faceColors, n, m, n, true, baseColor);
		for (int i = 0; i < n; i++) {
			int j = (i + m) % n;
			faces.add(Arrays.asList(j, i, i+n));
			faces.add(Arrays.asList(j, i+n, j+n));
			faceColors.add(joinColor);
			faceColors.add(joinColor);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Antiprism> {
		public String name() { return "Antiprism"; }
		
		public Antiprism parse(String[] args) {
			int n = 3;
			int m = 1;
			SizeSpecifier spec = SizeSpecifier.RADIUS;
			double size = 1;
			Axis axis = Axis.Y;
			Double h = null;
			Color c = Color.GRAY;
			Color baseColor = null;
			Color joinColor = null;
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
				} else if (arg.equalsIgnoreCase("-b") && argi < args.length) {
					baseColor = parseColor(args[argi++], baseColor);
				} else if (arg.equalsIgnoreCase("-p") && argi < args.length) {
					joinColor = parseColor(args[argi++], joinColor);
				} else if (arg.equalsIgnoreCase("-j") && argi < args.length) {
					joinColor = parseColor(args[argi++], joinColor);
				} else {
					return null;
				}
			}
			double r = spec.toRadius(size, n);
			if (h == null) {
				h = Math.sqrt((Math.cos(Math.PI / n) - Math.cos(Math.PI * 2 / n)) / 2) * r * 2;
			}
			return new Antiprism(
				n, m, r, axis, h,
				((baseColor != null) ? baseColor : c),
				((joinColor != null) ? joinColor : c)
			);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("n", Type.INT, "sides"),
				new Option("m", Type.INT, "stellation"),
				new Option("r", Type.REAL, "radius", "d","s","a"),
				new Option("d", Type.REAL, "diameter", "r","s","a"),
				new Option("s", Type.REAL, "side length", "r","d","a"),
				new Option("a", Type.REAL, "apothem", "r","d","s"),
				new Option("x", Type.VOID, "align central axis to X axis", "y","z"),
				new Option("y", Type.VOID, "align central axis to Y axis", "x","z"),
				new Option("z", Type.VOID, "align central axis to Z axis", "x","y"),
				new Option("h", Type.REAL, "height"),
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