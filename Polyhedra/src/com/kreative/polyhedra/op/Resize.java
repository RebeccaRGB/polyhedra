package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Resize extends PolyhedronOp {
	public static enum Metric {
		MAX_MAGNITUDE {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				double current = Point3D.maxMagnitude(points);
				if (current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					points.set(i, points.get(i).multiply(size / current));
				}
			}
		},
		AVERAGE_MAGNITUDE {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				double current = Point3D.averageMagnitude(points);
				if (current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					points.set(i, points.get(i).multiply(size / current));
				}
			}
		},
		MIN_MAGNITUDE {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				double current = Point3D.minMagnitude(points);
				if (current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					points.set(i, points.get(i).multiply(size / current));
				}
			}
		},
		MAX_EDGE_LENGTH {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				Double current = null;
				for (Polyhedron.Edge e : seed.edges) {
					double length = e.vertex1.point.distance(e.vertex2.point);
					if (current == null || length > current) current = length;
				}
				if (current == null || current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					points.set(i, points.get(i).multiply(size / current));
				}
			}
		},
		AVERAGE_EDGE_LENGTH {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				double current = 0;
				for (Polyhedron.Edge e : seed.edges) {
					current += e.vertex1.point.distance(e.vertex2.point);
				}
				if (current == 0 || (current /= seed.edges.size()) == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					points.set(i, points.get(i).multiply(size / current));
				}
			}
		},
		MIN_EDGE_LENGTH {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				Double current = null;
				for (Polyhedron.Edge e : seed.edges) {
					double length = e.vertex1.point.distance(e.vertex2.point);
					if (current == null || length < current) current = length;
				}
				if (current == null || current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					points.set(i, points.get(i).multiply(size / current));
				}
			}
		},
		X_SIZE_PROPORTIONAL {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				Point3D min = Point3D.min(points);
				Point3D max = Point3D.max(points);
				double current = max.getX() - min.getX();
				if (current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					points.set(i, points.get(i).multiply(size / current));
				}
			}
		},
		Y_SIZE_PROPORTIONAL {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				Point3D min = Point3D.min(points);
				Point3D max = Point3D.max(points);
				double current = max.getY() - min.getY();
				if (current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					points.set(i, points.get(i).multiply(size / current));
				}
			}
		},
		Z_SIZE_PROPORTIONAL {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				Point3D min = Point3D.min(points);
				Point3D max = Point3D.max(points);
				double current = max.getZ() - min.getZ();
				if (current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					points.set(i, points.get(i).multiply(size / current));
				}
			}
		},
		X_SIZE {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				Point3D min = Point3D.min(points);
				Point3D max = Point3D.max(points);
				double current = max.getX() - min.getX();
				if (current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					double x = points.get(i).getX() * size / current;
					double y = points.get(i).getY();
					double z = points.get(i).getZ();
					points.set(i, new Point3D(x, y, z));
				}
			}
		},
		Y_SIZE {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				Point3D min = Point3D.min(points);
				Point3D max = Point3D.max(points);
				double current = max.getY() - min.getY();
				if (current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					double x = points.get(i).getX();
					double y = points.get(i).getY() * size / current;
					double z = points.get(i).getZ();
					points.set(i, new Point3D(x, y, z));
				}
			}
		},
		Z_SIZE {
			public void resize(Polyhedron seed, List<Point3D> points, double size) {
				Point3D min = Point3D.min(points);
				Point3D max = Point3D.max(points);
				double current = max.getZ() - min.getZ();
				if (current == 0 || current == size) return;
				for (int i = 0, n = points.size(); i < n; i++) {
					double x = points.get(i).getX();
					double y = points.get(i).getY();
					double z = points.get(i).getZ() * size / current;
					points.set(i, new Point3D(x, y, z));
				}
			}
		};
		public abstract void resize(Polyhedron seed, List<Point3D> points, double size);
	}
	
	private final Metric metric;
	private final double size;
	private final boolean reverse;
	
	public Resize(Metric metric, double size) {
		this.metric = metric;
		this.size = size;
		this.reverse = size < 0;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		for (Polyhedron.Vertex vertex : seed.vertices) vertices.add(vertex.point);
		metric.resize(seed, vertices, size);
		for (Polyhedron.Face face : seed.faces) {
			List<Integer> indices = new ArrayList<Integer>(face.vertices.size());
			for (Polyhedron.Vertex v : face.vertices) indices.add(v.index);
			if (reverse) Collections.reverse(indices);
			faces.add(indices);
			faceColors.add(face.color);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Resize> {
		public String name() { return "Resize"; }
		
		public Resize parse(String[] args) {
			Metric metric = Metric.MAX_MAGNITUDE;
			double size = 1;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-mmax") && argi < args.length) {
					metric = Metric.MAX_MAGNITUDE;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
					metric = Metric.AVERAGE_MAGNITUDE;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-mmin") && argi < args.length) {
					metric = Metric.MIN_MAGNITUDE;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-amax") && argi < args.length) {
					metric = Metric.MAX_EDGE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
					metric = Metric.AVERAGE_EDGE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-amin") && argi < args.length) {
					metric = Metric.MIN_EDGE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-x") && argi < args.length) {
					metric = Metric.X_SIZE_PROPORTIONAL;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-y") && argi < args.length) {
					metric = Metric.Y_SIZE_PROPORTIONAL;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-z") && argi < args.length) {
					metric = Metric.Z_SIZE_PROPORTIONAL;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-X") && argi < args.length) {
					metric = Metric.X_SIZE;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-Y") && argi < args.length) {
					metric = Metric.Y_SIZE;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-Z") && argi < args.length) {
					metric = Metric.Z_SIZE;
					size = parseDouble(args[argi++], size);
				} else {
					return null;
				}
			}
			return new Resize(metric, size);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("mmax", Type.REAL, "scale uniformly to match the specified maximum magnitude", "mmin","m","amax","amin","a","x","y","z","X","Y","Z"),
				new Option("mmin", Type.REAL, "scale uniformly to match the specified minimum magnitude", "mmax","m","amax","amin","a","x","y","z","X","Y","Z"),
				new Option("m", Type.REAL, "scale uniformly to match the specified average magnitude", "mmax","mmin","amax","amin","a","x","y","z","X","Y","Z"),
				new Option("amax", Type.REAL, "scale uniformly to match the specified maximum edge length", "mmax","mmin","m","amin","a","x","y","z","X","Y","Z"),
				new Option("amin", Type.REAL, "scale uniformly to match the specified minimum edge length", "mmax","mmin","m","amax","a","x","y","z","X","Y","Z"),
				new Option("a", Type.REAL, "scale uniformly to match the specified average edge length", "mmax","mmin","m","amax","amin","x","y","z","X","Y","Z"),
				new Option("x", Type.REAL, "scale uniformly to match the specified length along the x axis", "mmax","mmin","m","amax","amin","a","y","z","X","Y","Z"),
				new Option("y", Type.REAL, "scale uniformly to match the specified length along the y axis", "mmax","mmin","m","amax","amin","a","x","z","X","Y","Z"),
				new Option("z", Type.REAL, "scale uniformly to match the specified length along the z axis", "mmax","mmin","m","amax","amin","a","x","y","X","Y","Z"),
				new Option("X", Type.REAL, "scale along the x axis only to match the specified length", "mmax","mmin","m","amax","amin","a","x","y","z"),
				new Option("Y", Type.REAL, "scale along the y axis only to match the specified length", "mmax","mmin","m","amax","amin","a","x","y","z"),
				new Option("Z", Type.REAL, "scale along the z axis only to match the specified length", "mmax","mmin","m","amax","amin","a","x","y","z"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}