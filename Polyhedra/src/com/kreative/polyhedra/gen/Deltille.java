package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Deltille extends PolyhedronGen {
	private static final double R3 = Math.sqrt(3);
	
	public static enum SizeSpecifier {
		APOTHEM {
			public double toXScale(double size) { return size * R3; }
			public double toYScale(double size) { return size; }
		},
		RADIUS {
			public double toXScale(double size) { return size * R3 / 2; }
			public double toYScale(double size) { return size / 2; }
		},
		FACE_HEIGHT {
			public double toXScale(double size) { return size * R3 / 3; }
			public double toYScale(double size) { return size / 3; }
		},
		DIAMETER {
			public double toXScale(double size) { return size * R3 / 4; }
			public double toYScale(double size) { return size / 4; }
		},
		X_SCALE {
			public double toXScale(double size) { return size; }
			public double toYScale(double size) { return size / R3; }
		},
		SIDE_LENGTH {
			public double toXScale(double size) { return size / 2; }
			public double toYScale(double size) { return size / R3 / 2; }
		};
		public abstract double toXScale(double size);
		public abstract double toYScale(double size);
	}
	
	public static enum Axis {
		X {public Point3D createVertex(double x, double y, double z) {return new Point3D(z,x,y);}},
		Y {public Point3D createVertex(double x, double y, double z) {return new Point3D(y,z,x);}},
		Z {public Point3D createVertex(double x, double y, double z) {return new Point3D(x,y,z);}};
		public abstract Point3D createVertex(double x, double y, double z);
	}
	
	private final int n;
	private final double xscale;
	private final double yscale;
	private final Axis axis;
	private final double z;
	private final Color color;
	
	public Deltille(int n, SizeSpecifier spec, double size, Axis axis, double z, Color color) {
		this.n = n;
		this.xscale = spec.toXScale(size);
		this.yscale = spec.toYScale(size);
		this.axis = axis;
		this.z = z;
		this.color = color;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>();
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		for (int y = 0; y <= n; y++) {
			double dy = (y + y + y - n - n) * yscale;
			for (int x = 0; x <= y; x++) {
				double dx = (x + x - y) * xscale;
				vertices.add(axis.createVertex(dx, dy, z));
			}
		}
		for (int i = 0, y = 0; y < n; y++) {
			for (int x = 0; x <= y; x++, i++) {
				if (x > 0) {
					faces.add(Arrays.asList(i-1, i, i+y+1));
					faceColors.add(color);
				}
				faces.add(Arrays.asList(i, i+y+2, i+y+1));
				faceColors.add(color);
			}
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Deltille> {
		public String name() { return "Deltille"; }
		
		public Deltille parse(String[] args) {
			int n = 35;
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
				} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
					spec = SizeSpecifier.APOTHEM;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-r") && argi < args.length) {
					spec = SizeSpecifier.RADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
					spec = SizeSpecifier.FACE_HEIGHT;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-d") && argi < args.length) {
					spec = SizeSpecifier.DIAMETER;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-b") && argi < args.length) {
					spec = SizeSpecifier.X_SCALE;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-s") && argi < args.length) {
					spec = SizeSpecifier.SIDE_LENGTH;
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
			return new Deltille(n, spec, size, axis, z, c);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("n", Type.INT, "number of polygons wide"),
				new Option("r", Type.REAL, "radius", "d","b","s","a","h"),
				new Option("d", Type.REAL, "diameter", "r","b","s","a","h"),
				new Option("b", Type.REAL, "half side length", "r","d","s","a","h"),
				new Option("s", Type.REAL, "side length", "r","d","b","a","h"),
				new Option("a", Type.REAL, "apothem", "r","d","b","s","h"),
				new Option("h", Type.REAL, "face height", "r","d","b","s","a"),
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