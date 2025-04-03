package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Octahedron extends PolyhedronGen {
	private static final double HR6 = Math.sqrt(6) / 2;
	private static final double R2 = Math.sqrt(2);
	private static final double R3 = Math.sqrt(3);
	private static final double DR2 = Math.sqrt(2) * 2;
	
	public static enum SizeSpecifier {
		INRADIUS {
			public double toXScale(double radius) { return radius * HR6; }
			public double toYScale(double radius) { return radius * R3; }
			public double fromXScale(double xScale) { return xScale / HR6; }
			public double fromYScale(double yScale) { return yScale / R3; }
		},
		FACE_HEIGHT {
			public double toXScale(double height) { return height / R3; }
			public double toYScale(double height) { return height / HR6; }
			public double fromXScale(double xScale) { return xScale * R3; }
			public double fromYScale(double yScale) { return yScale * HR6; }
		},
		MIDRADIUS {
			public double toXScale(double radius) { return radius; }
			public double toYScale(double radius) { return radius * R2; }
			public double fromXScale(double xScale) { return xScale; }
			public double fromYScale(double yScale) { return yScale / R2; }
		},
		EDGE_LENGTH {
			public double toXScale(double length) { return length / 2; }
			public double toYScale(double length) { return length / R2; }
			public double fromXScale(double xScale) { return xScale * 2; }
			public double fromYScale(double yScale) { return yScale * R2; }
		},
		CIRCUMRADIUS {
			public double toXScale(double radius) { return radius / R2; }
			public double toYScale(double radius) { return radius; }
			public double fromXScale(double xScale) { return xScale * R2; }
			public double fromYScale(double yScale) { return yScale; }
		},
		SPACE_DIAGONAL {
			public double toXScale(double diagonal) { return diagonal / DR2; }
			public double toYScale(double diagonal) { return diagonal / 2; }
			public double fromXScale(double xScale) { return xScale * DR2; }
			public double fromYScale(double yScale) { return yScale * 2; }
		};
		public abstract double toXScale(double size);
		public abstract double toYScale(double size);
		public abstract double fromXScale(double xScale);
		public abstract double fromYScale(double yScale);
	}
	
	private final double xScale;
	private final double yScale;
	private final Color color;
	
	public Octahedron(SizeSpecifier spec, double size, Color color) {
		this.xScale = spec.toXScale(size);
		this.yScale = spec.toYScale(size);
		this.color = color;
	}
	
	public Polyhedron gen() {
		return new Polyhedron(
			Arrays.asList(
				new Point3D(xScale, 0, xScale),
				new Point3D(xScale, 0, -xScale),
				new Point3D(-xScale, 0, xScale),
				new Point3D(-xScale, 0, -xScale),
				new Point3D(0, yScale, 0),
				new Point3D(0, -yScale, 0)
			),
			Arrays.asList(
				Arrays.asList(4, 2, 0),
				Arrays.asList(4, 0, 1),
				Arrays.asList(4, 1, 3),
				Arrays.asList(4, 3, 2),
				Arrays.asList(5, 0, 2),
				Arrays.asList(5, 2, 3),
				Arrays.asList(5, 3, 1),
				Arrays.asList(5, 1, 0)
			),
			Arrays.asList(
				color, color, color, color,
				color, color, color, color
			)
		);
	}
	
	public static Octahedron parse(String[] args) {
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
				spec = SizeSpecifier.FACE_HEIGHT;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
				spec = SizeSpecifier.EDGE_LENGTH;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				c = parseColor(args[argi++], c);
			} else {
				printOptions(options());
				return null;
			}
		}
		return new Octahedron(spec, size, c);
	}
	
	public static Option[] options() {
		return new Option[] {
			new Option("r", Type.REAL, "radius of circumscribed sphere", "m","i","d","f","a"),
			new Option("m", Type.REAL, "radius of sphere tangent to edges", "r","i","d","f","a"),
			new Option("i", Type.REAL, "radius of inscribed sphere", "r","m","d","f","a"),
			new Option("d", Type.REAL, "space diagonal", "r","m","i","f","a"),
			new Option("f", Type.REAL, "height of triangular face", "r","m","i","d","a"),
			new Option("a", Type.REAL, "edge length", "r","m","i","d","f"),
			new Option("c", Type.COLOR, "color"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}