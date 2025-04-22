package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Quadrille extends PolyhedronGen {
	private static final double R2 = Math.sqrt(2);
	private static final double DR2 = Math.sqrt(2) * 2;
	
	public static enum SizeSpecifier {
		RADIUS {
			public double toScale(double radius) { return radius / R2; }
			public double fromScale(double scale) { return scale * R2; }
		},
		DIAGONAL {
			public double toScale(double diagonal) { return diagonal / DR2; }
			public double fromScale(double scale) { return scale * DR2; }
		},
		SIDE_LENGTH {
			public double toScale(double length) { return length / 2; }
			public double fromScale(double scale) { return scale * 2; }
		},
		APOTHEM {
			public double toScale(double apothem) { return apothem; }
			public double fromScale(double scale) { return scale; }
		};
		public abstract double toScale(double size);
		public abstract double fromScale(double scale);
	}
	
	public static enum Axis {
		X {public Point3D createVertex(double x, double y, double z) {return new Point3D(z,x,y);}},
		Y {public Point3D createVertex(double x, double y, double z) {return new Point3D(y,z,x);}},
		Z {public Point3D createVertex(double x, double y, double z) {return new Point3D(x,y,z);}};
		public abstract Point3D createVertex(double x, double y, double z);
	}
	
	private final int n;
	private final double scale;
	private final Axis axis;
	private final double z;
	private final Color color;
	
	public Quadrille(int n, SizeSpecifier spec, double size, Axis axis, double z, Color color) {
		this.n = n;
		this.scale = spec.toScale(size);
		this.axis = axis;
		this.z = z;
		this.color = color;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>();
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		for (int y = 0; y <= n; y++) {
			double dy = (y + y - n) * scale;
			for (int x = 0; x <= n; x++) {
				double dx = (x + x - n) * scale;
				vertices.add(axis.createVertex(dx, dy, z));
			}
		}
		for (int i = 0, y = 0; y < n; y++, i++) {
			for (int x = 0; x < n; x++, i++) {
				faces.add(Arrays.asList(i, i+1, i+n+2, i+n+1));
				faceColors.add(color);
			}
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Quadrille> {
		public String name() { return "Quadrille"; }
		
		public Quadrille parse(String[] args) {
			int n = 25;
			SizeSpecifier spec = SizeSpecifier.SIDE_LENGTH;
			double size = 1;
			Axis axis = Axis.Y;
			double z = 0;
			Color c = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-n") && argi < args.length) {
					if ((n = Math.abs(parseInt(args[argi++], n))) < 1) n = 1;
				} else if (arg.equalsIgnoreCase("-r") && argi < args.length) {
					spec = SizeSpecifier.RADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-d") && argi < args.length) {
					spec = SizeSpecifier.DIAGONAL;
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
					return null;
				}
			}
			return new Quadrille(n, spec, size, axis, z, c);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("n", Type.INT, "number of polygons wide"),
				new Option("r", Type.REAL, "radius", "d","s","a"),
				new Option("d", Type.REAL, "diagonal", "r","s","a"),
				new Option("s", Type.REAL, "side length", "r","d","a"),
				new Option("a", Type.REAL, "apothem", "r","d","s"),
				new Option("x", Type.REAL, "x-coordinate of polygons parallel to yz plane", "y","z"),
				new Option("y", Type.REAL, "y-coordinate of polygons parallel to xz plane", "x","z"),
				new Option("z", Type.REAL, "z-coordinate of polygons parallel to xy plane", "x","y"),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}