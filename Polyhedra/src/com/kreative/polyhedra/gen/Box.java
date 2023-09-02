package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Box extends PolyhedronGen {
	private final double sx;
	private final double sy;
	private final double sz;
	private final Color color;
	
	public Box(double sx, double sy, double sz, Color color) {
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
		this.color = color;
	}
	
	public Polyhedron gen() {
		return new Polyhedron(
			Arrays.asList(
				new Point3D(sx, sy, sz),
				new Point3D(sx, sy, -sz),
				new Point3D(sx, -sy, sz),
				new Point3D(sx, -sy, -sz),
				new Point3D(-sx, sy, sz),
				new Point3D(-sx, sy, -sz),
				new Point3D(-sx, -sy, sz),
				new Point3D(-sx, -sy, -sz)
			),
			Arrays.asList(
				Arrays.asList(2, 0, 4, 6),
				Arrays.asList(6, 4, 5, 7),
				Arrays.asList(7, 5, 1, 3),
				Arrays.asList(3, 1, 0, 2),
				Arrays.asList(0, 1, 5, 4),
				Arrays.asList(3, 2, 6, 7)
			),
			Arrays.asList(color, color, color, color, color, color)
		);
	}
	
	public static Box parse(String[] args) {
		double sx = 1;
		double sy = 1;
		double sz = 1;
		Color c = Color.GRAY;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equals("-x") && argi < args.length) {
				sx = parseDouble(args[argi++], sx);
			} else if (arg.equals("-y") && argi < args.length) {
				sy = parseDouble(args[argi++], sy);
			} else if (arg.equals("-z") && argi < args.length) {
				sz = parseDouble(args[argi++], sz);
			} else if (arg.equals("-X") && argi < args.length) {
				sx = parseDouble(args[argi++], sx * 2) / 2;
			} else if (arg.equals("-Y") && argi < args.length) {
				sy = parseDouble(args[argi++], sy * 2) / 2;
			} else if (arg.equals("-Z") && argi < args.length) {
				sz = parseDouble(args[argi++], sz * 2) / 2;
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				c = parseColor(args[argi++], c);
			} else {
				System.err.println("Options:");
				System.err.println("  -X <real>   x-axis side length");
				System.err.println("  -Y <real>   y-axis side length");
				System.err.println("  -Z <real>   z-axis side length");
				System.err.println("  -x <real>   x-axis distance");
				System.err.println("  -y <real>   y-axis distance");
				System.err.println("  -z <real>   z-axis distance");
				System.err.println("  -c <color>  color");
				return null;
			}
		}
		return new Box(sx, sy, sz, c);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}