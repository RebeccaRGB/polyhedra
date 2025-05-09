package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.kreative.polyhedra.Metric;
import com.kreative.polyhedra.MetricAggregator;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Resize extends PolyhedronOp {
	public static enum ResizeMode {
		MAX_VERTEX_MAGNITUDE("vmax", "rmax", Type.REAL, "scale uniformly to match the specified maximum vertex magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.MAXIMUM, Metric.VERTEX_MAGNITUDE);
			}
		},
		AVERAGE_VERTEX_MAGNITUDE("v", "r", Type.REAL, "scale uniformly to match the specified average vertex magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.AVERAGE, Metric.VERTEX_MAGNITUDE);
			}
		},
		MIN_VERTEX_MAGNITUDE("vmin", "rmin", Type.REAL, "scale uniformly to match the specified minimum vertex magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.MINIMUM, Metric.VERTEX_MAGNITUDE);
			}
		},
		MAX_EDGE_MAGNITUDE("emax", "mmax", Type.REAL, "scale uniformly to match the specified maximum edge magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.MAXIMUM, Metric.EDGE_MAGNITUDE);
			}
		},
		AVERAGE_EDGE_MAGNITUDE("e", "m", Type.REAL, "scale uniformly to match the specified average edge magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.AVERAGE, Metric.EDGE_MAGNITUDE);
			}
		},
		MIN_EDGE_MAGNITUDE("emin", "mmin", Type.REAL, "scale uniformly to match the specified minimum edge magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.MINIMUM, Metric.EDGE_MAGNITUDE);
			}
		},
		MAX_FACE_MAGNITUDE("fmax", "imax", Type.REAL, "scale uniformly to match the specified maximum face magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.MAXIMUM, Metric.FACE_MAGNITUDE);
			}
		},
		AVERAGE_FACE_MAGNITUDE("f", "i", Type.REAL, "scale uniformly to match the specified average face magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.AVERAGE, Metric.FACE_MAGNITUDE);
			}
		},
		MIN_FACE_MAGNITUDE("fmin", "imin", Type.REAL, "scale uniformly to match the specified minimum face magnitude") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.MINIMUM, Metric.FACE_MAGNITUDE);
			}
		},
		MAX_EDGE_LENGTH("amax", Type.REAL, "scale uniformly to match the specified maximum edge length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.MAXIMUM, Metric.EDGE_LENGTH);
			}
		},
		AVERAGE_EDGE_LENGTH("a", Type.REAL, "scale uniformly to match the specified average edge length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.AVERAGE, Metric.EDGE_LENGTH);
			}
		},
		MIN_EDGE_LENGTH("amin", Type.REAL, "scale uniformly to match the specified minimum edge length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.MINIMUM, Metric.EDGE_LENGTH);
			}
		},
		X_SIZE_PROPORTIONAL("x", Type.REAL, "scale uniformly to match the specified length along the x axis") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.RANGE, Metric.X_POSITION);
			}
		},
		Y_SIZE_PROPORTIONAL("y", Type.REAL, "scale uniformly to match the specified length along the y axis") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.RANGE, Metric.Y_POSITION);
			}
		},
		Z_SIZE_PROPORTIONAL("z", Type.REAL, "scale uniformly to match the specified length along the z axis") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				return resizeChecked(seed, points, arg, MetricAggregator.RANGE, Metric.Z_POSITION);
			}
		},
		X_SIZE("X", Type.REAL, "scale along the x axis only to match the specified length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double current = MetricAggregator.RANGE.aggregate(Metric.X_POSITION.iterator(seed, seed.center()));
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return current != 0 && current != size && resizeUnsafe(points, Point3D.average(points), size / current, 1, 1);
			}
		},
		Y_SIZE("Y", Type.REAL, "scale along the y axis only to match the specified length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double current = MetricAggregator.RANGE.aggregate(Metric.Y_POSITION.iterator(seed, seed.center()));
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return current != 0 && current != size && resizeUnsafe(points, Point3D.average(points), 1, size / current, 1);
			}
		},
		Z_SIZE("Z", Type.REAL, "scale along the z axis only to match the specified length") {
			public boolean resize(Polyhedron seed, List<Point3D> points, Object arg) {
				double current = MetricAggregator.RANGE.aggregate(Metric.Z_POSITION.iterator(seed, seed.center()));
				double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
				return current != 0 && current != size && resizeUnsafe(points, Point3D.average(points), 1, 1, size / current);
			}
		};
		
		private final String flagWithoutDash;
		private final String flagWithDash;
		private final String altFlagWithDash;
		private final Type argDataType;
		private final String description;
		
		private ResizeMode(String flagWithoutDash, Type argDataType, String description) {
			this.flagWithoutDash = flagWithoutDash;
			this.flagWithDash = "-" + flagWithoutDash;
			this.altFlagWithDash = "-" + flagWithoutDash;
			this.argDataType = argDataType;
			this.description = description;
		}
		
		private ResizeMode(String flagWithoutDash, String altFlagWithoutDash, Type argDataType, String description) {
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
			for (ResizeMode mode : values()) if (mode != this) mutexes.add(mode.flagWithoutDash);
			return mutexes.toArray(new String[mutexes.size()]);
		}
		
		public static String[] allOptionMutexes(String... mutex) {
			ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
			for (ResizeMode mode : values()) mutexes.add(mode.flagWithoutDash);
			return mutexes.toArray(new String[mutexes.size()]);
		}
		
		public static ResizeMode forFlag(String flag) {
			for (ResizeMode mode : values()) {
				if (mode.flagWithDash.equals(flag) || mode.altFlagWithDash.equals(flag)) {
					return mode;
				}
			}
			return null;
		}
		
		private static boolean resizeChecked(Polyhedron seed, List<Point3D> points, Object arg, MetricAggregator agg, Metric metric) {
			double current = agg.aggregate(metric.iterator(seed, seed.center()));
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 1;
			return current != 0 && current != size && resizeUnsafe(points, Point3D.average(points), size / current);
		}
		
		private static boolean resizeUnsafe(List<Point3D> points, Point3D origin, double m) {
			for (int i = 0, n = points.size(); i < n; i++) {
				points.set(i, points.get(i).subtract(origin).multiply(m).add(origin));
			}
			return m < 0;
		}
		
		private static boolean resizeUnsafe(List<Point3D> points, Point3D origin, double x, double y, double z) {
			for (int i = 0, n = points.size(); i < n; i++) {
				points.set(i, points.get(i).subtract(origin).multiply(x, y, z).add(origin));
			}
			return (x < 0) != (y < 0) != (z < 0);
		}
	}
	
	private final ResizeMode mode;
	private final Object argument;
	
	public Resize(ResizeMode mode, Object argument) {
		this.mode = mode;
		this.argument = argument;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		for (Polyhedron.Vertex vertex : seed.vertices) vertices.add(vertex.point);
		boolean reverse = mode.resize(seed, vertices, argument);
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
			ResizeMode mode = ResizeMode.MAX_VERTEX_MAGNITUDE;
			ResizeMode mtmp;
			Object argument = 1.0;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if ((mtmp = ResizeMode.forFlag(arg)) != null && (mtmp.isVoidType() || argi < args.length)) {
					// -v -r -e -m -f -i -a -x -y -z -X -Y -Z
					mode = mtmp;
					argument = mtmp.isVoidType() ? null : mtmp.parseArgument(args[argi++]);
				} else {
					return null;
				}
			}
			return new Resize(mode, argument);
		}
		
		public Option[] options() {
			ResizeMode[] modes = ResizeMode.values();
			Option[] options = new Option[modes.length];
			for (int i = 0; i < modes.length; i++) options[i] = modes[i].option(); // vremfiaxyzXYZ
			return options;
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}