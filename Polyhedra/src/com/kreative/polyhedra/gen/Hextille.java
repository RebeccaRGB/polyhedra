package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Hextille extends PolyhedronGen {
	private static final double R3 = Math.sqrt(3);
	
	public static enum SizeSpecifier {
		RADIUS {
			public double toXScale(double size) { return size * R3 / 2; }
			public double toYScale(double size) { return size; }
		},
		DIAMETER {
			public double toXScale(double size) { return size * R3 / 4; }
			public double toYScale(double size) { return size / 2; }
		},
		APOTHEM {
			public double toXScale(double size) { return size; }
			public double toYScale(double size) { return size * 2 / R3; }
		},
		FACE_HEIGHT {
			public double toXScale(double size) { return size / 2; }
			public double toYScale(double size) { return size / R3; }
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
	
	public static enum Arrangement {
		TRIANGULAR {
			public void gen(
				int n, double xscale, double yscale, Axis axis, double z, Color color,
				List<Point3D> vertices, List<List<Integer>> faces, List<Color> faceColors
			) {
				for (int y = 0; y < n; y++) {
					double dy1 = (y * 3 - n * 2) * yscale / 2;
					double dy2 = (y * 3 - n * 2 + 1) * yscale / 2;
					for (int x = 0; x <= y; x++) {
						double dx1 = (x + x - y - 1) * xscale;
						double dx2 = (x + x - y) * xscale;
						vertices.add(axis.createVertex(dx1, dy2, z));
						vertices.add(axis.createVertex(dx2, dy1, z));
					}
					double dx3 = (y + 1) * xscale;
					vertices.add(axis.createVertex(dx3, dy2, z));
				}
				{
					double dy3 = n * yscale / 2;
					double dy4 = (n + 1) * yscale / 2;
					for (int x = 0; x < n; x++) {
						double dx1 = (x + x - n) * xscale;
						double dx2 = (x + x - n + 1) * xscale;
						vertices.add(axis.createVertex(dx1, dy3, z));
						vertices.add(axis.createVertex(dx2, dy4, z));
					}
					double dx3 = n * xscale;
					vertices.add(axis.createVertex(dx3, dy3, z));
				}
				for (int y = 1; y < n; y++) {
					for (int a = y*y-1, b = y*y+y+y+1, i = 0; i < y; i++, a += 2, b += 2) {
						faces.add(Arrays.asList(a, a+1, a+2, b+2, b+1, b));
						faceColors.add(color);
					}
				}
				for (int a = n*n-1, b = n*n+n+n, i = 0; i < n; i++, a += 2, b += 2) {
					faces.add(Arrays.asList(a, a+1, a+2, b+2, b+1, b));
					faceColors.add(color);
				}
			}
		},
		HEXAGONAL {
			public void gen(
				int n, double xscale, double yscale, Axis axis, double z, Color color,
				List<Point3D> vertices, List<List<Integer>> faces, List<Color> faceColors
			) {
				for (int y = 0; y < n; y++) {
					double dy1 = ((y - n) * 3 + 1) * yscale / 2;
					double dy2 = ((y - n) * 3 + 2) * yscale / 2;
					for (int x = 0; x < n + y; x++) {
						double dx1 = (x + x - y - n) * xscale;
						double dx2 = (x + x - y - n + 1) * xscale;
						vertices.add(axis.createVertex(dx1, dy2, z));
						vertices.add(axis.createVertex(dx2, dy1, z));
					}
					double dx3 = (n + y) * xscale;
					vertices.add(axis.createVertex(dx3, dy2, z));
				}
				for (int y = n; y > 0; y--) {
					double dy3 = ((n - y) * 3 + 1) * yscale / 2;
					double dy4 = ((n - y) * 3 + 2) * yscale / 2;
					for (int x = 0; x < n + y - 1; x++) {
						double dx1 = (x + x - y - n + 1) * xscale;
						double dx2 = (x + x - y - n + 2) * xscale;
						vertices.add(axis.createVertex(dx1, dy3, z));
						vertices.add(axis.createVertex(dx2, dy4, z));
					}
					double dx3 = (n + y - 1) * xscale;
					vertices.add(axis.createVertex(dx3, dy3, z));
				}
				for (int y = 1; y < n; y++) {
					int a = 2*n*y + y*y - 2*n - 2*y + 1;
					int b = 2*n*y + y*y + 1;
					for (int i = n + y; i > 1; i--, a += 2, b += 2) {
						faces.add(Arrays.asList(a, a+1, a+2, b+2, b+1, b));
						faceColors.add(color);
					}
				}
				{
					int a = 3*n*n - 4*n + 1;
					int b = 3*n*n;
					for (int i = n + n; i > 1; i--, a += 2, b += 2) {
						faces.add(Arrays.asList(a, a+1, a+2, b+2, b+1, b));
						faceColors.add(color);
					}
				}
				for (int y = n; y > 1; y--) {
					int a = 6*n*n - 2*n*y - y*y + 1;
					int b = 6*n*n - 2*n*y - y*y + 2*n + 2*y - 1;
					for (int i = n + y; i > 2; i--, a += 2, b += 2) {
						faces.add(Arrays.asList(a, a+1, a+2, b+2, b+1, b));
						faceColors.add(color);
					}
				}
			}
		};
		public abstract void gen(
			int n, double xscale, double yscale, Axis axis, double z, Color color,
			List<Point3D> vertices, List<List<Integer>> faces, List<Color> faceColors
		);
	}
	
	private final Arrangement a;
	private final int n;
	private final double xscale;
	private final double yscale;
	private final Axis axis;
	private final double z;
	private final Color color;
	
	public Hextille(Arrangement a, int n, SizeSpecifier spec, double size, Axis axis, double z, Color color) {
		this.a = a;
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
		a.gen(n, xscale, yscale, axis, z, color, vertices, faces, faceColors);
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Hextille> {
		public String name() { return "Hextille"; }
		
		public Hextille parse(String[] args) {
			Arrangement a = Arrangement.HEXAGONAL;
			int n = 9;
			SizeSpecifier spec = SizeSpecifier.RADIUS;
			double size = 1;
			Axis axis = Axis.Y;
			double z = 0;
			Color c = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-n") && argi < args.length) {
					a = Arrangement.TRIANGULAR;
					if ((n = Math.abs(parseInt(args[argi++], n))) < 1) n = 1;
				} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
					a = Arrangement.HEXAGONAL;
					if ((n = Math.abs(parseInt(args[argi++], n))) < 1) n = 1;
				} else if (arg.equalsIgnoreCase("-r") && argi < args.length) {
					spec = SizeSpecifier.RADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-d") && argi < args.length) {
					spec = SizeSpecifier.DIAMETER;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
					spec = SizeSpecifier.APOTHEM;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
					spec = SizeSpecifier.FACE_HEIGHT;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-s") && argi < args.length) {
					spec = SizeSpecifier.RADIUS;
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
			return new Hextille(a, n, spec, size, axis, z, c);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("n", Type.INT, "number of polygons wide (triangular arrangement)", "m"),
				new Option("m", Type.INT, "number of polygons wide (hexagonal arrangement)", "n"),
				new Option("r", Type.REAL, "radius", "d","a","h","s"),
				new Option("d", Type.REAL, "diameter", "r","a","h","s"),
				new Option("a", Type.REAL, "apothem", "r","d","h","s"),
				new Option("h", Type.REAL, "face height", "r","d","a","s"),
				new Option("s", Type.REAL, "side length", "r","d","a","h"),
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