package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;
import com.kreative.polyhedra.gen.Polygon.Axis;

public class Trapezohedron extends PolyhedronGen {
	public static enum SizeSpecifier {
		SHORT_EDGE_LENGTH {
			public double toShortEdgeLength(double length, int n) { return length; }
			public double fromShortEdgeLength(double length, int n) { return length; }
		},
		LONG_EDGE_LENGTH {
			public double toShortEdgeLength(double length, int n) {
				return length * (2 - 2 * Math.cos(Math.PI / n));
			}
			public double fromShortEdgeLength(double length, int n) {
				return length / (2 - 2 * Math.cos(Math.PI / n));
			}
		},
		HALF_HEIGHT {
			public double toShortEdgeLength(double height, int n) {
				double sp2n = Math.sin(Math.PI / (2 * n));
				return height * (8 * sp2n * sp2n * sp2n) / Math.sin(Math.PI / n);
			}
			public double fromShortEdgeLength(double length, int n) {
				double sp2n = Math.sin(Math.PI / (2 * n));
				return length * Math.sin(Math.PI / n) / (8 * sp2n * sp2n * sp2n);
			}
		},
		INRADIUS {
			public double toShortEdgeLength(double radius, int n) {
				double cpn = Math.cos(Math.PI / n);
				double r1 = 2 * cpn - 3;
				double cp2n = Math.cos(Math.PI / (2 * n));
				double r2 = r1 / (cp2n * cp2n) - 4 * r1;
				double c2pn = Math.cos(2 * Math.PI / n);
				double d = 4 * (4 - 5 * cpn + c2pn);
				return radius * d / (Math.sin(Math.PI / n) * Math.sqrt(r2));
			}
			public double fromShortEdgeLength(double length, int n) {
				double cpn = Math.cos(Math.PI / n);
				double r1 = 2 * cpn - 3;
				double cp2n = Math.cos(Math.PI / (2 * n));
				double r2 = r1 / (cp2n * cp2n) - 4 * r1;
				double c2pn = Math.cos(2 * Math.PI / n);
				double d = 4 * (4 - 5 * cpn + c2pn);
				return length * (Math.sin(Math.PI / n) * Math.sqrt(r2)) / d;
			}
		},
		MIDRADIUS {
			public double toShortEdgeLength(double radius, int n) {
				double sp2n = Math.sin(Math.PI / (2 * n));
				double cpn = Math.cos(Math.PI / n);
				return radius * (4 * sp2n) / Math.sqrt(2 * cpn + 1);
			}
			public double fromShortEdgeLength(double length, int n) {
				double sp2n = Math.sin(Math.PI / (2 * n));
				double cpn = Math.cos(Math.PI / n);
				return length * Math.sqrt(2 * cpn + 1) / (4 * sp2n);
			}
			public double toMidradius(double radius, int n) { return radius; }
			public double fromMidradius(double radius, int n) { return radius; }
		},
		POLYGON_RADIUS {
			public double toMidradius(double radius, int n) {
				return radius * (2 * Math.sin(Math.PI / n));
			}
			public double fromMidradius(double radius, int n) {
				return radius / (2 * Math.sin(Math.PI / n));
			}
			public double toPolygonRadius(double radius, int n) { return radius; }
			public double fromPolygonRadius(double radius, int n) { return radius; }
		},
		POLYGON_Z {
			public double toMidradius(double z, int n) {
				double cp2n = Math.cos(Math.PI / (2 * n));
				double pzn = Math.sqrt(4 - 1 / (cp2n * cp2n));
				double pzd = 4 + 8 * Math.cos(Math.PI / n);
				return z * pzd / pzn;
			}
			public double fromMidradius(double radius, int n) {
				double cp2n = Math.cos(Math.PI / (2 * n));
				double pzn = Math.sqrt(4 - 1 / (cp2n * cp2n));
				double pzd = 4 + 8 * Math.cos(Math.PI / n);
				return radius * pzn / pzd;
			}
			public double toPolygonZ(double z, int n) { return z; }
			public double fromPolygonZ(double z, int n) { return z; }
		},
		APEX_Z {
			public double toMidradius(double z, int n) {
				double cp2n = Math.cos(Math.PI / (2 * n));
				double azn = Math.sqrt(4 - 1 / (cp2n * cp2n)) * cp2n;
				double azd = 4 * Math.tan(Math.PI / (2 * n)) * Math.sin(3 * Math.PI / (2 * n));
				return z * azd / azn;
			}
			public double fromMidradius(double radius, int n) {
				double cp2n = Math.cos(Math.PI / (2 * n));
				double azn = Math.sqrt(4 - 1 / (cp2n * cp2n)) * cp2n;
				double azd = 4 * Math.tan(Math.PI / (2 * n)) * Math.sin(3 * Math.PI / (2 * n));
				return radius * azn / azd;
			}
			public double toApexZ(double z, int n) { return z; }
			public double fromApexZ(double z, int n) { return z; }
		};
		public double toShortEdgeLength(double size, int n) {
			return MIDRADIUS.toShortEdgeLength(this.toMidradius(size, n), n);
		}
		public double fromShortEdgeLength(double length, int n) {
			return this.fromMidradius(MIDRADIUS.fromShortEdgeLength(length, n), n);
		}
		public double toMidradius(double size, int n) {
			return MIDRADIUS.fromShortEdgeLength(this.toShortEdgeLength(size, n), n);
		}
		public double fromMidradius(double radius, int n) {
			return this.fromShortEdgeLength(MIDRADIUS.toShortEdgeLength(radius, n), n);
		}
		public double toPolygonRadius(double size, int n) {
			return POLYGON_RADIUS.fromMidradius(this.toMidradius(size, n), n);
		}
		public double fromPolygonRadius(double radius, int n) {
			return this.fromMidradius(POLYGON_RADIUS.toMidradius(radius, n), n);
		}
		public double toPolygonZ(double size, int n) {
			return POLYGON_Z.fromMidradius(this.toMidradius(size, n), n);
		}
		public double fromPolygonZ(double z, int n) {
			return this.fromMidradius(POLYGON_Z.toMidradius(z, n), n);
		}
		public double toApexZ(double size, int n) {
			return APEX_Z.fromMidradius(this.toMidradius(size, n), n);
		}
		public double fromApexZ(double z, int n) {
			return this.fromMidradius(APEX_Z.toMidradius(z, n), n);
		}
	}
	
	private final int n;
	private final int m;
	private final double polygonRadius;
	private final double polygonZ; // 474
	private final double apexZ;
	private final Axis axis;
	private final Color c;
	
	public Trapezohedron(int n, SizeSpecifier spec, double size, Axis axis, Color c) {
		this(n, 1, spec, size, axis, c);
	}
	public Trapezohedron(int n, int m, SizeSpecifier spec, double size, Axis axis, Color c) {
		this.n = n;
		this.m = m;
		this.polygonRadius = spec.toPolygonRadius(size, n);
		this.polygonZ = spec.toPolygonZ(size, n);
		this.apexZ = spec.toApexZ(size, n);
		this.axis = axis;
		this.c = c;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>(n);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(2);
		List<Color> faceColors = new ArrayList<Color>(2);
		Polygon.createVertices(vertices, n, polygonRadius, 0, axis, polygonZ);
		Polygon.createVertices(vertices, n, polygonRadius, 0.5, axis, -polygonZ);
		vertices.add(axis.createVertex(0, 0, apexZ));
		vertices.add(axis.createVertex(0, 0, -apexZ));
		for (int i = 0; i < n; i++) {
			int j = (i + m) % n;
			faces.add(Arrays.asList(i, i + n, j, n + n));
			faces.add(Arrays.asList(j + n, j, i + n, n + n + 1));
			faceColors.add(c);
			faceColors.add(c);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Trapezohedron> {
		public String name() { return "Trapezohedron"; }
		
		public Trapezohedron parse(String[] args) {
			int n = 3;
			int m = 1;
			SizeSpecifier spec = SizeSpecifier.APEX_Z;
			double size = 1;
			Axis axis = Axis.Y;
			Color c = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-n") && argi < args.length) {
					if ((n = Math.abs(parseInt(args[argi++], n))) < 3) n = 3;
				} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
					if ((m = Math.abs(parseInt(args[argi++], m))) < 1) m = 1;
				} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
					spec = SizeSpecifier.SHORT_EDGE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-e") && argi < args.length) {
					spec = SizeSpecifier.LONG_EDGE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
					spec = SizeSpecifier.HALF_HEIGHT;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-r") && argi < args.length) {
					spec = SizeSpecifier.INRADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-p") && argi < args.length) {
					spec = SizeSpecifier.MIDRADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-u") && argi < args.length) {
					spec = SizeSpecifier.POLYGON_RADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-v") && argi < args.length) {
					spec = SizeSpecifier.POLYGON_Z;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-w") && argi < args.length) {
					spec = SizeSpecifier.APEX_Z;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-x")) {
					axis = Axis.X;
				} else if (arg.equalsIgnoreCase("-y")) {
					axis = Axis.Y;
				} else if (arg.equalsIgnoreCase("-z")) {
					axis = Axis.Z;
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					c = parseColor(args[argi++], c);
				} else {
					return null;
				}
			}
			return new Trapezohedron(n, m, spec, size, axis, c);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("n", Type.INT, "sides"),
				new Option("m", Type.INT, "stellation"),
				new Option("a", Type.REAL, "short edge length", "e","h","r","p","u","v","w"),
				new Option("e", Type.REAL, "long edge length", "a","h","r","p","u","v","w"),
				new Option("h", Type.REAL, "(half-)height", "a","e","r","p","u","v","w"),
				new Option("r", Type.REAL, "radius of inscribed sphere", "a","e","h","p","u","v","w"),
				new Option("p", Type.REAL, "radius of sphere tangent to edges", "a","e","h","r","u","v","w"),
				new Option("u", Type.REAL, "radius of center polygon", "a","e","h","r","p","v","w"),
				new Option("v", Type.REAL, "distance from origin to plane of center polygon", "a","e","h","r","p","u","w"),
				new Option("w", Type.REAL, "distance from origin to apex", "a","e","h","r","p","u","v"),
				new Option("x", Type.VOID, "align central axis to X axis", "y","z"),
				new Option("y", Type.VOID, "align central axis to Y axis", "x","z"),
				new Option("z", Type.VOID, "align central axis to Z axis", "x","y"),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}