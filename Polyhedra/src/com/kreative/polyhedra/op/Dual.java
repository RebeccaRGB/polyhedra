package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Metric;
import com.kreative.polyhedra.MetricAggregator;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Dual extends PolyhedronOp {
	public static enum RescaleMode {
		NONE("s", "do not rescale dual polyhedron (strict mode)") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return false;
			}
		},
		MAX_VERTEX_MAGNITUDE("vmax", "rmax", "rescale dual polyhedron to match original maximum vertex magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MAXIMUM, Metric.VERTEX_MAGNITUDE);
			}
		},
		AVERAGE_VERTEX_MAGNITUDE("v", "r", "rescale dual polyhedron to match original average vertex magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.AVERAGE, Metric.VERTEX_MAGNITUDE);
			}
		},
		MIN_VERTEX_MAGNITUDE("vmin", "rmin", "rescale dual polyhedron to match original minimum vertex magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MINIMUM, Metric.VERTEX_MAGNITUDE);
			}
		},
		MAX_EDGE_MAGNITUDE("emax", "mmax", "rescale dual polyhedron to match original maximum edge magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MAXIMUM, Metric.EDGE_MAGNITUDE);
			}
		},
		AVERAGE_EDGE_MAGNITUDE("e", "m", "rescale dual polyhedron to match original average edge magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.AVERAGE, Metric.EDGE_MAGNITUDE);
			}
		},
		MIN_EDGE_MAGNITUDE("emin", "mmin", "rescale dual polyhedron to match original minimum edge magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MINIMUM, Metric.EDGE_MAGNITUDE);
			}
		},
		MAX_FACE_MAGNITUDE("fmax", "imax", "rescale dual polyhedron to match original maximum face magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MAXIMUM, Metric.FACE_MAGNITUDE);
			}
		},
		AVERAGE_FACE_MAGNITUDE("f", "i", "rescale dual polyhedron to match original average face magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.AVERAGE, Metric.FACE_MAGNITUDE);
			}
		},
		MIN_FACE_MAGNITUDE("fmin", "imin", "rescale dual polyhedron to match original minimum face magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MINIMUM, Metric.FACE_MAGNITUDE);
			}
		},
		MAX_EDGE_LENGTH("amax", "rescale dual polyhedron to match original maximum edge length") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MAXIMUM, Metric.EDGE_LENGTH);
			}
		},
		AVERAGE_EDGE_LENGTH("a", "rescale dual polyhedron to match original average edge length") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.AVERAGE, Metric.EDGE_LENGTH);
			}
		},
		MIN_EDGE_LENGTH("amin", "rescale dual polyhedron to match original minimum edge length") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MINIMUM, Metric.EDGE_LENGTH);
			}
		},
		X_SIZE("x", "rescale dual polyhedron to match original length along the x axis") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.RANGE, Metric.X_POSITION);
			}
		},
		Y_SIZE("y", "rescale dual polyhedron to match original length along the y axis") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.RANGE, Metric.Y_POSITION);
			}
		},
		Z_SIZE("z", "rescale dual polyhedron to match original length along the z axis") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.RANGE, Metric.Z_POSITION);
			}
		};
		
		private final String flagWithoutDash;
		private final String flagWithDash;
		private final String altFlagWithDash;
		private final String description;
		
		private RescaleMode(String flagWithoutDash, String description) {
			this.flagWithoutDash = flagWithoutDash;
			this.flagWithDash = "-" + flagWithoutDash;
			this.altFlagWithDash = "-" + flagWithoutDash;
			this.description = description;
		}
		
		private RescaleMode(String flagWithoutDash, String altFlagWithoutDash, String description) {
			this.flagWithoutDash = flagWithoutDash;
			this.flagWithDash = "-" + flagWithoutDash;
			this.altFlagWithDash = "-" + altFlagWithoutDash;
			this.description = description;
		}
		
		public abstract boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> dualVertices);
		
		public final Option option(String... mutex) {
			return new Option(flagWithoutDash, Type.VOID, description, optionMutexes(mutex));
		}
		
		public final String[] optionMutexes(String... mutex) {
			ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
			for (RescaleMode mode : values()) if (mode != this) mutexes.add(mode.flagWithoutDash);
			return mutexes.toArray(new String[mutexes.size()]);
		}
		
		public static String[] allOptionMutexes(String... mutex) {
			ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
			for (RescaleMode mode : values()) mutexes.add(mode.flagWithoutDash);
			return mutexes.toArray(new String[mutexes.size()]);
		}
		
		public static RescaleMode forFlag(String flag) {
			for (RescaleMode mode : values()) {
				if (mode.flagWithDash.equals(flag) || mode.altFlagWithDash.equals(flag)) {
					return mode;
				}
			}
			return null;
		}
		
		public static RescaleMode forFlagIgnoreCase(String flag) {
			for (RescaleMode mode : values()) {
				if (mode.flagWithDash.equalsIgnoreCase(flag) || mode.altFlagWithDash.equalsIgnoreCase(flag)) {
					return mode;
				}
			}
			return null;
		}
		
		private static boolean rescaleChecked(Polyhedron seed, Polyhedron dual, List<Point3D> points, MetricAggregator agg, Metric metric) {
			double seedScale = agg.aggregate(metric.iterator(seed, seed.center()));
			double dualScale = agg.aggregate(metric.iterator(dual, dual.center()));
			return seedScale != 0 && dualScale != 0 && seedScale != dualScale && resizeUnsafe(points, Point3D.average(points), seedScale / dualScale);
		}
		
		private static boolean resizeUnsafe(List<Point3D> points, Point3D origin, double m) {
			for (int i = 0, n = points.size(); i < n; i++) {
				points.set(i, points.get(i).subtract(origin).multiply(m).add(origin));
			}
			return true;
		}
	}
	
	private final RescaleMode mode;
	private final Color color;
	
	public Dual(RescaleMode mode, Color color) {
		this.mode = mode;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.faces.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.vertices.size());
		List<Color> faceColors = new ArrayList<Color>(seed.vertices.size());
		for (Polyhedron.Face f : seed.faces) vertices.add(f.center());
		for (Polyhedron.Vertex v : seed.vertices) {
			List<Polyhedron.Face> seedFaces = seed.getFaces(v);
			while (!seedFaces.isEmpty()) {
				List<Integer> dualFace = new ArrayList<Integer>();
				for (Polyhedron.Face seedFace : seed.getOrderedFaces(v, seedFaces)) {
					dualFace.add(seedFace.index);
					seedFaces.remove(seedFace);
				}
				faces.add(dualFace);
				faceColors.add(color);
			}
		}
		Polyhedron dual = new Polyhedron(vertices, faces, faceColors);
		if (!mode.rescale(seed, dual, vertices)) return dual;
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Dual> {
		public String name() { return "Dual"; }
		
		public Dual parse(String[] args) {
			RescaleMode mode = RescaleMode.MAX_VERTEX_MAGNITUDE;
			RescaleMode mtmp;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if ((mtmp = RescaleMode.forFlag(arg)) != null) {
					mode = mtmp;
				} else if ((mtmp = RescaleMode.forFlagIgnoreCase(arg)) != null) {
					mode = mtmp;
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					return null;
				}
			}
			return new Dual(mode, color);
		}
		
		public Option[] options() {
			RescaleMode[] modes = RescaleMode.values();
			Option[] options = new Option[modes.length + 1];
			for (int i = 0; i < modes.length; i++) options[i] = modes[i].option();
			options[modes.length] = new Option("c", Type.COLOR, "color");
			return options;
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}