package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class GreatStellatedDodecahedron extends PolyhedronGen {
	private static final double PHI = 1.6180339887498948482; // (1+sqrt(5))/2
	private static final double PHI1 = 0.6180339887498948482; // 1/phi, phi-1
	private static final double R3 = Math.sqrt(3);
	private static final double INRADIUS_FACTOR = 0.10040570794311363993; // sqrt(10*(25−11*sqrt(5)))/20
	private static final double MIDRADIUS_FACTOR = 0.19098300562505257590; // (3−sqrt(5))/4
	private static final double CIRCUMRADIUS_FACTOR = 0.53523313465963489791; // (sqrt(15)−sqrt(3))/4
	
	public static enum SizeSpecifier {
		CIRCUMRADIUS {
			public double toScale(double radius) { return radius / R3; }
			public double fromScale(double scale) { return scale * R3; }
		},
		INRADIUS {
			public double toScale(double radius) { return radius * CIRCUMRADIUS_FACTOR / INRADIUS_FACTOR / R3; }
			public double fromScale(double scale) { return scale * R3 * INRADIUS_FACTOR / CIRCUMRADIUS_FACTOR; }
		},
		MIDRADIUS {
			public double toScale(double radius) { return radius * CIRCUMRADIUS_FACTOR / MIDRADIUS_FACTOR / R3; }
			public double fromScale(double scale) { return scale * R3 * MIDRADIUS_FACTOR / CIRCUMRADIUS_FACTOR; }
		},
		EDGE_LENGTH {
			public double toScale(double length) { return length * CIRCUMRADIUS_FACTOR / R3; }
			public double fromScale(double scale) { return scale * R3 / CIRCUMRADIUS_FACTOR; }
		};
		public abstract double toScale(double size);
		public abstract double fromScale(double scale);
	}
	
	private final double scale;
	private final Color color;
	
	public GreatStellatedDodecahedron(SizeSpecifier spec, double size, Color color) {
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
				Arrays.asList(0, 11, 9, 2, 15),
				Arrays.asList(0, 15, 14, 1, 19),
				Arrays.asList(0, 19, 17, 4, 11),
				Arrays.asList(1, 10, 5, 17, 19),
				Arrays.asList(1, 14, 3, 8, 10),
				Arrays.asList(2, 9, 6, 16, 18),
				Arrays.asList(2, 18, 3, 14, 15),
				Arrays.asList(3, 18, 16, 7, 8),
				Arrays.asList(4, 13, 6, 9, 11),
				Arrays.asList(4, 17, 5, 12, 13),
				Arrays.asList(5, 10, 8, 7, 12),
				Arrays.asList(6, 13, 12, 7, 16)
			),
			Arrays.asList(
				color, color, color, color, color, color,
				color, color, color, color, color, color
			)
		);
	}
	
	public static class Factory extends PolyhedronGen.Factory<GreatStellatedDodecahedron> {
		public String name() { return "GreatStellatedDodecahedron"; }
		
		public GreatStellatedDodecahedron parse(String[] args) {
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
			return new GreatStellatedDodecahedron(spec, size, c);
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