package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Tetrahedron extends PolyhedronGen {
	private static final double R29 = Math.sqrt(2.0/9.0);
	private static final double R23 = Math.sqrt(2.0/3.0);
	private static final double R89 = Math.sqrt(8.0/9.0);
	private static final double R83 = Math.sqrt(8.0/3.0);
	private static final double R2 = Math.sqrt(2);
	private static final double R3 = Math.sqrt(3);
	
	public static enum SizeSpecifier {
		CIRCUMRADIUS {
			public double toRadius(double radius) { return radius; }
			public double fromRadius(double radius) { return radius; }
		},
		CIRCUMDIAMETER {
			public double toRadius(double diameter) { return diameter / 2; }
			public double fromRadius(double radius) { return radius * 2; }
		},
		MIDRADIUS {
			public double toRadius(double radius) { return radius * R3; }
			public double fromRadius(double radius) { return radius / R3; }
		},
		EDGE_TO_OPPOSITE_EDGE {
			public double toRadius(double distance) { return distance * R3 / 2; }
			public double fromRadius(double radius) { return radius * 2 / R3; }
		},
		INRADIUS {
			public double toRadius(double radius) { return radius * 3; }
			public double fromRadius(double radius) { return radius / 3; }
		},
		EXRADIUS {
			public double toRadius(double radius) { return radius * 3 / 2; }
			public double fromRadius(double radius) { return radius * 2 / 3; }
		},
		FACE_TO_OPPOSITE_VERTEX {
			public double toRadius(double distance) { return distance * 3 / 4; }
			public double fromRadius(double radius) { return radius * 4 / 3; }
		},
		FACE_HEIGHT {
			public double toRadius(double height) { return height / R2; }
			public double fromRadius(double radius) { return radius * R2; }
		},
		EDGE_LENGTH {
			public double toRadius(double length) { return length / R83; }
			public double fromRadius(double radius) { return radius * R83; }
		};
		public abstract double toRadius(double size);
		public abstract double fromRadius(double radius);
	}
	
	private final double radius;
	private final Color color;
	
	public Tetrahedron(SizeSpecifier spec, double size, Color color) {
		this.radius = spec.toRadius(size);
		this.color = color;
	}
	
	public Polyhedron gen() {
		return new Polyhedron(
			Arrays.asList(
				new Point3D(0, radius / -3, radius * R89),
				new Point3D(radius * R23, radius / -3, radius * -R29),
				new Point3D(radius * -R23, radius / -3, radius * -R29),
				new Point3D(0, radius, 0)
			),
			Arrays.asList(
				Arrays.asList(2, 1, 0),
				Arrays.asList(1, 2, 3),
				Arrays.asList(0, 3, 2),
				Arrays.asList(3, 0, 1)
			),
			Arrays.asList(color, color, color, color)
		);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Tetrahedron> {
		public String name() { return "Tetrahedron"; }
		
		public Tetrahedron parse(String[] args) {
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
				} else if (arg.equalsIgnoreCase("-e") && argi < args.length) {
					spec = SizeSpecifier.EXRADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-d") && argi < args.length) {
					spec = SizeSpecifier.CIRCUMDIAMETER;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
					spec = SizeSpecifier.FACE_TO_OPPOSITE_VERTEX;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-l") && argi < args.length) {
					spec = SizeSpecifier.EDGE_TO_OPPOSITE_EDGE;
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
			return new Tetrahedron(spec, size, c);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("r", Type.REAL, "radius of circumscribed sphere", "m","i","e","d","h","l","f","a"),
				new Option("m", Type.REAL, "radius of sphere tangent to edges", "r","i","e","d","h","l","f","a"),
				new Option("i", Type.REAL, "radius of inscribed sphere", "r","m","e","d","h","l","f","a"),
				new Option("e", Type.REAL, "radius of exspheres", "r","m","i","d","h","l","f","a"),
				new Option("d", Type.REAL, "diameter of circumscribed sphere", "r","m","i","e","h","l","f","a"),
				new Option("h", Type.REAL, "distance from face to opposite vertex, height of pyramid", "r","m","i","e","d","l","f","a"),
				new Option("l", Type.REAL, "distance from edge to opposite edge", "r","m","i","e","d","h","f","a"),
				new Option("f", Type.REAL, "height of triangular face", "r","m","i","e","d","h","l","a"),
				new Option("a", Type.REAL, "edge length", "r","m","i","e","d","h","l","f"),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}