package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class SmallStellatedDodecahedron extends PolyhedronGen {
	private static final double PHI = 1.6180339887498948482; // (1+sqrt(5))/2
	private static final double PHI5 = 1.9021130325903071442; // sqrt(phi*phi+1)
	private static final double INRADIUS_FACTOR = 0.26286555605956680301; // sqrt(10*(5−sqrt(5)))/20
	private static final double MIDRADIUS_FACTOR = 0.30901699437494742410; // (sqrt(5)−1)/4
	private static final double CIRCUMRADIUS_FACTOR = 0.58778525229247312917; // sqrt(2*(5−sqrt(5)))/4
	
	public static enum SizeSpecifier {
		CIRCUMRADIUS {
			public double toScale(double radius) { return radius / PHI5; }
			public double fromScale(double scale) { return scale * PHI5; }
		},
		INRADIUS {
			public double toScale(double radius) { return radius * CIRCUMRADIUS_FACTOR / INRADIUS_FACTOR / PHI5; }
			public double fromScale(double scale) { return scale * PHI5 * INRADIUS_FACTOR / CIRCUMRADIUS_FACTOR; }
		},
		MIDRADIUS {
			public double toScale(double radius) { return radius * CIRCUMRADIUS_FACTOR / MIDRADIUS_FACTOR / PHI5; }
			public double fromScale(double scale) { return scale * PHI5 * MIDRADIUS_FACTOR / CIRCUMRADIUS_FACTOR; }
		},
		EDGE_LENGTH {
			public double toScale(double length) { return length * CIRCUMRADIUS_FACTOR / PHI5; }
			public double fromScale(double scale) { return scale * PHI5 / CIRCUMRADIUS_FACTOR; }
		};
		public abstract double toScale(double size);
		public abstract double fromScale(double scale);
	}
	
	private final double scale;
	private final Color color;
	
	public SmallStellatedDodecahedron(SizeSpecifier spec, double size, Color color) {
		this.scale = spec.toScale(size);
		this.color = color;
	}
	
	public Polyhedron gen() {
		return new Polyhedron(
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
				Arrays.asList(0, 1, 10, 4, 11),
				Arrays.asList(1, 0, 9, 6, 8),
				Arrays.asList(2, 3, 8, 7, 9),
				Arrays.asList(3, 2, 11, 5, 10),
				Arrays.asList(4, 5, 1, 8, 3),
				Arrays.asList(5, 4, 2, 9, 0),
				Arrays.asList(6, 7, 0, 11, 2),
				Arrays.asList(7, 6, 3, 10, 1),
				Arrays.asList(8, 10, 5, 0, 7),
				Arrays.asList(9, 11, 4, 3, 6),
				Arrays.asList(10, 8, 6, 2, 4),
				Arrays.asList(11, 9, 7, 1, 5)
			),
			Arrays.asList(
				color, color, color, color, color, color,
				color, color, color, color, color, color
			)
		);
	}
	
	public static class Factory extends PolyhedronGen.Factory<SmallStellatedDodecahedron> {
		public String name() { return "SmallStellatedDodecahedron"; }
		
		public SmallStellatedDodecahedron parse(String[] args) {
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
				} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
					spec = SizeSpecifier.EDGE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					c = parseColor(args[argi++], c);
				} else {
					return null;
				}
			}
			return new SmallStellatedDodecahedron(spec, size, c);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("r", Type.REAL, "radius of circumscribed sphere", "m","i","a"),
				new Option("m", Type.REAL, "radius of sphere tangent to edges", "r","i","a"),
				new Option("i", Type.REAL, "radius of inscribed sphere", "r","m","a"),
				new Option("a", Type.REAL, "edge length", "r","m","i"),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}