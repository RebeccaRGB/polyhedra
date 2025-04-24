package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Resize extends PolyhedronOp {
	public static enum Metric {
		MAX_VERTEX_MAGNITUDE("vmax", "rmax", Type.REAL, "scale uniformly to match the specified maximum vertex magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, Point3D.maxMagnitude(points));
			}
		},
		AVERAGE_VERTEX_MAGNITUDE("v", "r", Type.REAL, "scale uniformly to match the specified average vertex magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, Point3D.averageMagnitude(points));
			}
		},
		MIN_VERTEX_MAGNITUDE("vmin", "rmin", Type.REAL, "scale uniformly to match the specified minimum vertex magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, Point3D.minMagnitude(points));
			}
		},
		MAX_EDGE_MAGNITUDE("emax", "mmax", Type.REAL, "scale uniformly to match the specified maximum edge magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				Double current = null;
				for (Polyhedron.Edge e : seed.edges) {
					double length = e.midpoint().magnitude();
					if (current == null || length > current) current = length;
				}
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, current);
			}
		},
		AVERAGE_EDGE_MAGNITUDE("e", "m", Type.REAL, "scale uniformly to match the specified average edge magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double current = 0;
				for (Polyhedron.Edge e : seed.edges) {
					current += e.midpoint().magnitude();
				}
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, current, seed.edges.size());
			}
		},
		MIN_EDGE_MAGNITUDE("emin", "mmin", Type.REAL, "scale uniformly to match the specified minimum edge magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				Double current = null;
				for (Polyhedron.Edge e : seed.edges) {
					double length = e.midpoint().magnitude();
					if (current == null || length < current) current = length;
				}
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, current);
			}
		},
		MAX_FACE_MAGNITUDE("fmax", "imax", Type.REAL, "scale uniformly to match the specified maximum face magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				Double current = null;
				for (Polyhedron.Face f : seed.faces) {
					double length = f.center().magnitude();
					if (current == null || length > current) current = length;
				}
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, current);
			}
		},
		AVERAGE_FACE_MAGNITUDE("f", "i", Type.REAL, "scale uniformly to match the specified average face magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double current = 0;
				for (Polyhedron.Face f : seed.faces) {
					current += f.center().magnitude();
				}
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, current, seed.faces.size());
			}
		},
		MIN_FACE_MAGNITUDE("fmin", "imin", Type.REAL, "scale uniformly to match the specified minimum face magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				Double current = null;
				for (Polyhedron.Face f : seed.faces) {
					double length = f.center().magnitude();
					if (current == null || length < current) current = length;
				}
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, current);
			}
		},
		MAX_EDGE_LENGTH("amax", Type.REAL, "scale uniformly to match the specified maximum edge length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				Double current = null;
				for (Polyhedron.Edge e : seed.edges) {
					double length = e.length();
					if (current == null || length > current) current = length;
				}
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, current);
			}
		},
		AVERAGE_EDGE_LENGTH("a", Type.REAL, "scale uniformly to match the specified average edge length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double current = 0;
				for (Polyhedron.Edge e : seed.edges) {
					current += e.length();
				}
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, current, seed.edges.size());
			}
		},
		MIN_EDGE_LENGTH("amin", Type.REAL, "scale uniformly to match the specified minimum edge length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				Double current = null;
				for (Polyhedron.Edge e : seed.edges) {
					double length = e.length();
					if (current == null || length < current) current = length;
				}
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, current);
			}
		},
		X_SIZE_PROPORTIONAL("x", Type.REAL, "scale uniformly to match the specified length along the x axis") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double min = Point3D.min(points).getX();
				double max = Point3D.max(points).getX();
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, max - min);
			}
		},
		Y_SIZE_PROPORTIONAL("y", Type.REAL, "scale uniformly to match the specified length along the y axis") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double min = Point3D.min(points).getY();
				double max = Point3D.max(points).getY();
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, max - min);
			}
		},
		Z_SIZE_PROPORTIONAL("z", Type.REAL, "scale uniformly to match the specified length along the z axis") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double min = Point3D.min(points).getZ();
				double max = Point3D.max(points).getZ();
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return resizeChecked(points, size, max - min);
			}
		},
		X_SIZE("X", Type.REAL, "scale along the x axis only to match the specified length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				Point3D min = Point3D.min(points);
				Point3D max = Point3D.max(points);
				double current = max.getX() - min.getX();
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return current != 0 && current != size && resizeUnsafe(points, size / current, 1, 1);
			}
		},
		Y_SIZE("Y", Type.REAL, "scale along the y axis only to match the specified length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				Point3D min = Point3D.min(points);
				Point3D max = Point3D.max(points);
				double current = max.getY() - min.getY();
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return current != 0 && current != size && resizeUnsafe(points, 1, size / current, 1);
			}
		},
		Z_SIZE("Z", Type.REAL, "scale along the z axis only to match the specified length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				Point3D min = Point3D.min(points);
				Point3D max = Point3D.max(points);
				double current = max.getZ() - min.getZ();
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return current != 0 && current != size && resizeUnsafe(points, 1, 1, size / current);
			}
		};
		
		private final String flagWithoutDash;
		private final String flagWithDash;
		private final String altFlagWithDash;
		private final Type argDataType;
		private final String description;
		
		private Metric(String flagWithoutDash, Type argDataType, String description) {
			this.flagWithoutDash = flagWithoutDash;
			this.flagWithDash = "-" + flagWithoutDash;
			this.altFlagWithDash = "-" + flagWithoutDash;
			this.argDataType = argDataType;
			this.description = description;
		}
		
		private Metric(String flagWithoutDash, String altFlagWithoutDash, Type argDataType, String description) {
			this.flagWithoutDash = flagWithoutDash;
			this.flagWithDash = "-" + flagWithoutDash;
			this.altFlagWithDash = "-" + altFlagWithoutDash;
			this.argDataType = argDataType;
			this.description = description;
		}
		
		public abstract boolean resize(Polyhedron seed, List<Point3D> points, Object arg);
		
		public final boolean isVoidType() {
			return argDataType == Type.VOID;
		}
		
		public final Object parseArgument(String s) {
			return argDataType.parse(s);
		}
		
		public final Option option(String... mutex) {
			return new Option(flagWithoutDash, argDataType, description, optionMutexes(mutex));
		}
		
		public final String[] optionMutexes(String... mutex) {
			ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
			for (Metric metric : values()) if (metric != this) mutexes.add(metric.flagWithoutDash);
			return mutexes.toArray(new String[mutexes.size()]);
		}
		
		public static String[] allOptionMutexes(String... mutex) {
			ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
			for (Metric metric : values()) mutexes.add(metric.flagWithoutDash);
			return mutexes.toArray(new String[mutexes.size()]);
		}
		
		public static Metric forFlag(String flag) {
			for (Metric metric : values()) {
				if (metric.flagWithDash.equals(flag) || metric.altFlagWithDash.equals(flag)) {
					return metric;
				}
			}
			return null;
		}
		
		public static Metric forFlagIgnoreCase(String flag) {
			for (Metric metric : values()) {
				if (metric.flagWithDash.equalsIgnoreCase(flag) || metric.altFlagWithDash.equalsIgnoreCase(flag)) {
					return metric;
				}
			}
			return null;
		}
		
		private static boolean resizeChecked(List<Point3D> points, double size, double current) {
			return current != 0 && current != size && resizeUnsafe(points, size / current);
		}
		
		private static boolean resizeChecked(List<Point3D> points, double size, Double current) {
			return current != null && current != 0 && current != size && resizeUnsafe(points, size / current);
		}
		
		private static boolean resizeChecked(List<Point3D> points, double size, double current, int count) {
			return current != 0 && count != 0 && (current /= count) != size && resizeUnsafe(points, size / current);
		}
		
		private static boolean resizeUnsafe(List<Point3D> points, double m) {
			for (int i = 0, n = points.size(); i < n; i++) {
				points.set(i, points.get(i).multiply(m));
			}
			return m < 0;
		}
		
		private static boolean resizeUnsafe(List<Point3D> points, double x, double y, double z) {
			for (int i = 0, n = points.size(); i < n; i++) {
				double nx = points.get(i).getX() * x;
				double ny = points.get(i).getY() * y;
				double nz = points.get(i).getZ() * z;
				points.set(i, new Point3D(nx, ny, nz));
			}
			return (x < 0) != (y < 0) != (z < 0);
		}
	}
	
	private final Metric metric;
	private final Object metarg;
	
	public Resize(Metric metric, Object metarg) {
		this.metric = metric;
		this.metarg = metarg;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		for (Polyhedron.Vertex vertex : seed.vertices) vertices.add(vertex.point);
		boolean reverse = metric.resize(seed, vertices, metarg);
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
			Metric metric = Metric.MAX_VERTEX_MAGNITUDE;
			Metric mettmp;
			Object metarg = 1.0;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if ((mettmp = Metric.forFlag(arg)) != null && (mettmp.isVoidType() || argi < args.length)) {
					metric = mettmp;
					metarg = mettmp.isVoidType() ? null : mettmp.parseArgument(args[argi++]);
				} else if ((mettmp = Metric.forFlagIgnoreCase(arg)) != null && (mettmp.isVoidType() || argi < args.length)) {
					metric = mettmp;
					metarg = mettmp.isVoidType() ? null : mettmp.parseArgument(args[argi++]);
				} else {
					return null;
				}
			}
			return new Resize(metric, metarg);
		}
		
		public Option[] options() {
			Metric[] metrics = Metric.values();
			Option[] options = new Option[metrics.length];
			for (int i = 0; i < metrics.length; i++) options[i] = metrics[i].option();
			return options;
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}