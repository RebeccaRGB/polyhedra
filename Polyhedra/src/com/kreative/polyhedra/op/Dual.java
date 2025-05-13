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
		NONE("d", "do not rescale dual polyhedron") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return false;
			}
		},
		MAX_VERTEX_MAGNITUDE("rmax", "vmax", "rescale dual polyhedron to match original maximum circumradius") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MAXIMUM, Metric.VERTEX_MAGNITUDE);
			}
		},
		AVERAGE_VERTEX_MAGNITUDE("r", "v", "rescale dual polyhedron to match original average circumradius") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.AVERAGE, Metric.VERTEX_MAGNITUDE);
			}
		},
		MIN_VERTEX_MAGNITUDE("rmin", "vmin", "rescale dual polyhedron to match original minimum circumradius") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MINIMUM, Metric.VERTEX_MAGNITUDE);
			}
		},
		MAX_EDGE_MIDPOINT_MAGNITUDE("emax", "rescale dual polyhedron to match original maximum edge magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MAXIMUM, Metric.EDGE_MIDPOINT_MAGNITUDE);
			}
		},
		AVERAGE_EDGE_MIDPOINT_MAGNITUDE("e", "rescale dual polyhedron to match original average edge magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.AVERAGE, Metric.EDGE_MIDPOINT_MAGNITUDE);
			}
		},
		MIN_EDGE_MIDPOINT_MAGNITUDE("emin", "rescale dual polyhedron to match original minimum edge magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MINIMUM, Metric.EDGE_MIDPOINT_MAGNITUDE);
			}
		},
		MAX_EDGE_DISTANCE_TO_ORIGIN("mmax", "rescale dual polyhedron to match original maximum midradius") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MAXIMUM, Metric.EDGE_DISTANCE_TO_ORIGIN);
			}
		},
		AVERAGE_EDGE_DISTANCE_TO_ORIGIN("m", "rescale dual polyhedron to match original average midradius") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.AVERAGE, Metric.EDGE_DISTANCE_TO_ORIGIN);
			}
		},
		MIN_EDGE_DISTANCE_TO_ORIGIN("mmin", "rescale dual polyhedron to match original minimum midradius") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MINIMUM, Metric.EDGE_DISTANCE_TO_ORIGIN);
			}
		},
		MAX_FACE_CENTER_MAGNITUDE("fmax", "rescale dual polyhedron to match original maximum face magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MAXIMUM, Metric.FACE_CENTER_MAGNITUDE);
			}
		},
		AVERAGE_FACE_CENTER_MAGNITUDE("f", "rescale dual polyhedron to match original average face magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.AVERAGE, Metric.FACE_CENTER_MAGNITUDE);
			}
		},
		MIN_FACE_CENTER_MAGNITUDE("fmin", "rescale dual polyhedron to match original minimum face magnitude") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MINIMUM, Metric.FACE_CENTER_MAGNITUDE);
			}
		},
		MAX_FACE_DISTANCE_TO_ORIGIN("imax", "rescale dual polyhedron to match original maximum inradius") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MAXIMUM, Metric.FACE_DISTANCE_TO_ORIGIN);
			}
		},
		AVERAGE_FACE_DISTANCE_TO_ORIGIN("i", "rescale dual polyhedron to match original average inradius") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.AVERAGE, Metric.FACE_DISTANCE_TO_ORIGIN);
			}
		},
		MIN_FACE_DISTANCE_TO_ORIGIN("imin", "rescale dual polyhedron to match original minimum inradius") {
			public boolean rescale(Polyhedron seed, Polyhedron dual, List<Point3D> vertices) {
				return rescaleChecked(seed, dual, vertices, MetricAggregator.MINIMUM, Metric.FACE_DISTANCE_TO_ORIGIN);
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
	
	private final FaceVertexGen fvgen;
	private final RescaleMode mode;
	private final Color color;
	
	public Dual(FaceVertexGen fvgen, RescaleMode mode, Color color) {
		this.fvgen = fvgen;
		this.mode = mode;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.faces.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.vertices.size());
		List<Color> faceColors = new ArrayList<Color>(seed.vertices.size());
		
		fvgen.reset(seed, seed.points());
		for (Polyhedron.Face face : seed.faces) {
			vertices.add(fvgen.createVertex(face, face.points()));
		}
		
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
			FaceVertexGen fvgen = new FaceVertexGen.PolarReciprocal(MetricAggregator.AVERAGE, Metric.EDGE_DISTANCE_TO_ORIGIN);
			FaceVertexGen.Builder fvtmp;
			RescaleMode mode = RescaleMode.NONE;
			RescaleMode mtmp;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equals("-s")) {
					fvgen = new FaceVertexGen.FaceOffset(0);
					mode = RescaleMode.NONE;
				} else if ((fvtmp = FaceVertexGen.Builder.forFlag(arg)) != null && (fvtmp.ignoresArgument() || argi < args.length)) {
					// -H -X -A -V -F -E -P -R -M -I -S
					fvgen = fvtmp.buildFromArgument(fvtmp.ignoresArgument() ? null : args[argi++]);
				} else if ((mtmp = RescaleMode.forFlag(arg)) != null) {
					// -d -v -r -e -m -f -i -a -x -y -z
					mode = mtmp;
				} else if (arg.equals("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					return null;
				}
			}
			return new Dual(fvgen, mode, color);
		}
		
		public Option[] options() {
			List<Option> options = new ArrayList<Option>();
			options.add(FaceVertexGen.Builder.FACE_OFFSET.option("s")); // H
			options.add(FaceVertexGen.Builder.MAX_VERTEX_MAGNITUDE_OFFSET.option("s")); // X
			options.add(FaceVertexGen.Builder.AVERAGE_VERTEX_MAGNITUDE_OFFSET.option("s")); // A
			options.add(FaceVertexGen.Builder.MIN_VERTEX_MAGNITUDE_OFFSET.option("s")); // V
			options.add(FaceVertexGen.Builder.FACE_CENTER_MAGNITUDE_OFFSET.option("s")); // F
			options.add(FaceVertexGen.Builder.INVERSION_ABOUT_CIRCUMRADIUS.option("s")); // R
			options.add(FaceVertexGen.Builder.INVERSION_ABOUT_MIDRADIUS.option("s")); // M
			options.add(FaceVertexGen.Builder.INVERSION_ABOUT_INRADIUS.option("s")); // I
			options.add(FaceVertexGen.Builder.INVERSION_ABOUT_RADIUS.option("s")); // S
			for (RescaleMode mode : RescaleMode.values()) options.add(mode.option("s")); // dvremfiaxyz
			options.add(new Option("s", Type.VOID, "create new vertices at centers and do not resize (strict mode)"));
			options.add(new Option("c", Type.COLOR, "color"));
			return options.toArray(new Option[options.size()]);
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}