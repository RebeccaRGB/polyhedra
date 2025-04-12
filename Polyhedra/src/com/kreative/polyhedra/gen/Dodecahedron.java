package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Dodecahedron extends PolyhedronGen {
	private static final double PHI = 1.6180339887498948482; // (1+sqrt(5))/2
	private static final double PHI1 = 0.6180339887498948482; // 1/phi, phi-1
	private static final double PHI2 = PHI1 * 2; // 2/phi, 2*phi-2, sqrt(5)-1
	private static final double PHI3 = 1.3763819204711735382; // phi/sqrt(3-phi)
	private static final double PHI5 = 1.9021130325903071442; // sqrt(phi*phi+1)
	private static final double R3 = Math.sqrt(3);
	private static final double DR3 = Math.sqrt(3) * 2;
	
	public static enum SizeSpecifier {
		FACE_WIDTH {
			public double toScale(double width) { return width / 2; }
			public double fromScale(double scale) { return scale * 2; }
		},
		CIRCUMRADIUS {
			public double toScale(double radius) { return radius / R3; }
			public double fromScale(double scale) { return scale * R3; }
		},
		SPACE_DIAGONAL {
			public double toScale(double diagonal) { return diagonal / DR3; }
			public double fromScale(double scale) { return scale * DR3; }
		},
		MIDRADIUS {
			public double toScale(double radius) { return radius / PHI; }
			public double fromScale(double scale) { return scale * PHI; }
		},
		EDGE_LENGTH {
			public double toScale(double length) { return length / PHI2; }
			public double fromScale(double scale) { return scale * PHI2; }
		},
		INRADIUS {
			public double toScale(double radius) { return radius / PHI3; }
			public double fromScale(double scale) { return scale * PHI3; }
		},
		FACE_HEIGHT {
			public double toScale(double height) { return height / PHI5; }
			public double fromScale(double scale) { return scale * PHI5; }
		};
		public abstract double toScale(double size);
		public abstract double fromScale(double scale);
	}
	
	private final double scale;
	private final Color color;
	
	public Dodecahedron(SizeSpecifier spec, double size, Color color) {
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
				new Point3D(-scale, -scale, -scale),
				new Point3D(0, scale*PHI, scale*PHI1),
				new Point3D(0, scale*PHI, -scale*PHI1),
				new Point3D(0, -scale*PHI, scale*PHI1),
				new Point3D(0, -scale*PHI, -scale*PHI1),
				new Point3D(scale*PHI1, 0, scale*PHI),
				new Point3D(scale*PHI1, 0, -scale*PHI),
				new Point3D(-scale*PHI1, 0, scale*PHI),
				new Point3D(-scale*PHI1, 0, -scale*PHI),
				new Point3D(scale*PHI, scale*PHI1, 0),
				new Point3D(scale*PHI, -scale*PHI1, 0),
				new Point3D(-scale*PHI, scale*PHI1, 0),
				new Point3D(-scale*PHI, -scale*PHI1, 0)
			),
			Arrays.asList(
				Arrays.asList(0, 8, 4, 14, 12),
				Arrays.asList(0, 12, 2, 17, 16),
				Arrays.asList(0, 16, 1, 9, 8),
				Arrays.asList(5, 9, 1, 13, 15),
				Arrays.asList(5, 15, 7, 19, 18),
				Arrays.asList(5, 18, 4, 8, 9),
				Arrays.asList(3, 17, 2, 10, 11),
				Arrays.asList(3, 11, 7, 15, 13),
				Arrays.asList(3, 13, 1, 16, 17),
				Arrays.asList(6, 14, 4, 18, 19),
				Arrays.asList(6, 19, 7, 11, 10),
				Arrays.asList(6, 10, 2, 12, 14)
			),
			Arrays.asList(
				color, color, color, color, color, color,
				color, color, color, color, color, color
			)
		);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Dodecahedron> {
		public String name() { return "Dodecahedron"; }
		
		public Dodecahedron parse(String[] args) {
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
				} else if (arg.equalsIgnoreCase("-w") && argi < args.length) {
					spec = SizeSpecifier.FACE_WIDTH;
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
			return new Dodecahedron(spec, size, c);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("r", Type.REAL, "radius of circumscribed sphere", "m","i","d","f","w","a"),
				new Option("m", Type.REAL, "radius of sphere tangent to edges", "r","i","d","f","w","a"),
				new Option("i", Type.REAL, "radius of inscribed sphere", "r","m","d","f","w","a"),
				new Option("d", Type.REAL, "space diagonal", "r","m","i","f","w","a"),
				new Option("f", Type.REAL, "height of pentagonal face", "r","m","i","d","w","a"),
				new Option("w", Type.REAL, "width/diagonal of pentagonal face", "r","m","i","d","f","a"),
				new Option("a", Type.REAL, "edge length", "r","m","i","d","f","w"),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}