package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Icosahedron extends PolyhedronGen {
	private static final double PHI = 1.6180339887498948482; // (1+sqrt(5))/2
	private static final double PHI4 = 1.51152262815234146096; // phi*phi/sqrt(3)
	private static final double PHI5 = 1.9021130325903071442; // sqrt(phi*phi+1)
	private static final double R3 = Math.sqrt(3);
	
	public static enum SizeSpecifier {
		CIRCUMRADIUS {
			public double toScale(double radius) { return radius / PHI5; }
			public double fromScale(double scale) { return scale * PHI5; }
		},
		SPACE_DIAGONAL {
			public double toScale(double diagonal) { return diagonal / (PHI5 * 2); }
			public double fromScale(double scale) { return scale * (PHI5 * 2); }
		},
		INRADIUS {
			public double toScale(double radius) { return radius / PHI4; }
			public double fromScale(double scale) { return scale * PHI4; }
		},
		MIDRADIUS {
			public double toScale(double radius) { return radius / PHI; }
			public double fromScale(double scale) { return scale * PHI; }
		},
		FACE_HEIGHT {
			public double toScale(double height) { return height / R3; }
			public double fromScale(double scale) { return scale * R3; }
		},
		EDGE_LENGTH {
			public double toScale(double length) { return length / 2; }
			public double fromScale(double scale) { return scale * 2; }
		};
		public abstract double toScale(double size);
		public abstract double fromScale(double scale);
	}
	
	private final double scale;
	private final Color color;
	
	public Icosahedron(SizeSpecifier spec, double size, Color color) {
		this.scale = spec.toScale(size);
		this.color = color;
	}
	
	public Polyhedron gen() {
		return new Polyhedron(
			// IFTTT: If the order of vertices or faces changes, indices will need to be updated in JohnsonSolid.java.
			Arrays.asList(
				new Point3D(0, scale, scale*PHI),
				new Point3D(0, scale, -scale*PHI),
				new Point3D(0, -scale, scale*PHI),
				new Point3D(0, -scale, -scale*PHI),
				new Point3D(scale, scale*PHI, 0),
				new Point3D(scale, -scale*PHI, 0),
				new Point3D(-scale, scale*PHI, 0),
				new Point3D(-scale, -scale*PHI, 0),
				new Point3D(scale*PHI, 0, scale),
				new Point3D(scale*PHI, 0, -scale),
				new Point3D(-scale*PHI, 0, scale),
				new Point3D(-scale*PHI, 0, -scale)
			),
			Arrays.asList(
				Arrays.asList(2, 5, 8),
				Arrays.asList(11, 10, 6),
				Arrays.asList(11, 6, 1),
				Arrays.asList(9, 8, 5),
				Arrays.asList(0, 10, 2),
				Arrays.asList(0, 2, 8),
				Arrays.asList(0, 6, 10),
				Arrays.asList(7, 10, 11),
				Arrays.asList(7, 5, 2),
				Arrays.asList(7, 2, 10),
				Arrays.asList(4, 6, 0),
				Arrays.asList(4, 0, 8),
				Arrays.asList(4, 8, 9),
				Arrays.asList(4, 9, 1),
				Arrays.asList(4, 1, 6),
				Arrays.asList(3, 5, 7),
				Arrays.asList(3, 7, 11),
				Arrays.asList(3, 11, 1),
				Arrays.asList(3, 1, 9),
				Arrays.asList(3, 9, 5)
			),
			Arrays.asList(
				color, color, color, color, color,
				color, color, color, color, color,
				color, color, color, color, color,
				color, color, color, color, color
			)
		);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Icosahedron> {
		public String name() { return "Icosahedron"; }
		
		public Icosahedron parse(String[] args) {
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
					return null;
				}
			}
			return new Icosahedron(spec, size, c);
		}
		
		public Option[] options() {
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
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}