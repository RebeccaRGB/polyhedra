package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Cube extends PolyhedronGen {
	private static final double R2 = Math.sqrt(2);
	private static final double R3 = Math.sqrt(3);
	private static final double DR2 = Math.sqrt(2) * 2;
	private static final double DR3 = Math.sqrt(3) * 2;
	
	public static enum SizeSpecifier {
		INRADIUS {
			public double toScale(double radius) { return radius; }
			public double fromScale(double scale) { return scale; }
		},
		EDGE_LENGTH {
			public double toScale(double length) { return length / 2; }
			public double fromScale(double scale) { return scale * 2; }
		},
		MIDRADIUS {
			public double toScale(double radius) { return radius / R2; }
			public double fromScale(double scale) { return scale * R2; }
		},
		FACE_DIAGONAL {
			public double toScale(double diagonal) { return diagonal / DR2; }
			public double fromScale(double scale) { return scale * DR2; }
		},
		CIRCUMRADIUS {
			public double toScale(double radius) { return radius / R3; }
			public double fromScale(double scale) { return scale * R3; }
		},
		SPACE_DIAGONAL {
			public double toScale(double diagonal) { return diagonal / DR3; }
			public double fromScale(double scale) { return scale * DR3; }
		};
		public abstract double toScale(double size);
		public abstract double fromScale(double scale);
	}
	
	private final double scale;
	private final Color color;
	
	public Cube(SizeSpecifier spec, double size, Color color) {
		this.scale = spec.toScale(size);
		this.color = color;
	}
	
	public Polyhedron gen() {
		return new Polyhedron(
			Arrays.asList(
				new Point3D(scale, scale, scale),
				new Point3D(scale, scale, -scale),
				new Point3D(scale, -scale, scale),
				new Point3D(scale, -scale, -scale),
				new Point3D(-scale, scale, scale),
				new Point3D(-scale, scale, -scale),
				new Point3D(-scale, -scale, scale),
				new Point3D(-scale, -scale, -scale)
			),
			Arrays.asList(
				Arrays.asList(2, 0, 4, 6),
				Arrays.asList(6, 4, 5, 7),
				Arrays.asList(7, 5, 1, 3),
				Arrays.asList(3, 1, 0, 2),
				Arrays.asList(0, 1, 5, 4),
				Arrays.asList(3, 2, 6, 7)
			),
			Arrays.asList(
				color, color, color,
				color, color, color
			)
		);
	}
	
	public static Cube parse(String[] args) {
		SizeSpecifier spec = SizeSpecifier.CIRCUMRADIUS;
		double size = 1;
		Color c = Color.GRAY;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-r") && argi < args.length) {
				spec = SizeSpecifier.CIRCUMRADIUS;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
				spec = SizeSpecifier.MIDRADIUS;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-i") && argi < args.length) {
				spec = SizeSpecifier.INRADIUS;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-d") && argi < args.length) {
				spec = SizeSpecifier.SPACE_DIAGONAL;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-f") && argi < args.length) {
				spec = SizeSpecifier.FACE_DIAGONAL;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
				spec = SizeSpecifier.EDGE_LENGTH;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				c = parseColor(args[argi++], c);
			} else {
				System.err.println("Options:");
				System.err.println("  -r <real>   radius of circumscribed sphere");
				System.err.println("  -m <real>   radius of sphere tangent to edges");
				System.err.println("  -i <real>   radius of inscribed sphere");
				System.err.println("  -d <real>   space diagonal");
				System.err.println("  -f <real>   face diagonal");
				System.err.println("  -a <real>   edge length");
				System.err.println("  -c <color>  color");
				return null;
			}
		}
		return new Cube(spec, size, c);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}