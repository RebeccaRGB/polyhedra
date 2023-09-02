package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Cube extends PolyhedronGen {
	public static enum SizeSpecifier {
		DISTANCE_TO_FACE {
			public double toScale(double toFace) { return toFace; }
			public double fromScale(double scale) { return scale; }
		},
		SIDE_LENGTH {
			public double toScale(double sideLength) { return sideLength / 2; }
			public double fromScale(double scale) { return scale * 2; }
		},
		DISTANCE_TO_EDGE {
			public double toScale(double toEdge) { return toEdge / Math.sqrt(2); }
			public double fromScale(double scale) { return scale * Math.sqrt(2); }
		},
		FACE_DIAGONAL {
			public double toScale(double diagonal) { return diagonal / (2 * Math.sqrt(2)); }
			public double fromScale(double scale) { return scale * (2 * Math.sqrt(2)); }
		},
		RADIUS {
			public double toScale(double radius) { return radius / Math.sqrt(3); }
			public double fromScale(double scale) { return scale * Math.sqrt(3); }
		},
		DIAMETER {
			public double toScale(double diameter) { return diameter / (2 * Math.sqrt(3)); }
			public double fromScale(double scale) { return scale * (2 * Math.sqrt(3)); }
		};
		public abstract double toScale(double size);
		public abstract double fromScale(double hsl);
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
			Arrays.asList(color, color, color, color, color, color)
		);
	}
	
	public static Cube parse(String[] args) {
		SizeSpecifier spec = SizeSpecifier.RADIUS;
		double size = 1;
		Color c = Color.GRAY;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-r") && argi < args.length) {
				spec = SizeSpecifier.RADIUS;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-d") && argi < args.length) {
				spec = SizeSpecifier.DIAMETER;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-e") && argi < args.length) {
				spec = SizeSpecifier.DISTANCE_TO_EDGE;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-f") && argi < args.length) {
				spec = SizeSpecifier.FACE_DIAGONAL;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-s") && argi < args.length) {
				spec = SizeSpecifier.SIDE_LENGTH;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
				spec = SizeSpecifier.DISTANCE_TO_FACE;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				c = parseColor(args[argi++], c);
			} else {
				System.err.println("Options:");
				System.err.println("  -r <real>   radius");
				System.err.println("  -d <real>   diameter");
				System.err.println("  -e <real>   distance to edge");
				System.err.println("  -f <real>   face diagonal");
				System.err.println("  -s <real>   side length");
				System.err.println("  -a <real>   distance to face");
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