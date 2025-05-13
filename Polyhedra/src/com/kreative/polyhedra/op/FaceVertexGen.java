package com.kreative.polyhedra.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Metric;
import com.kreative.polyhedra.MetricAggregator;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public abstract class FaceVertexGen {
	public void reset(Polyhedron seed, List<Point3D> seedVertices) {}
	
	public abstract Point3D createVertex(Polyhedron.Face face, List<Point3D> faceVertices);
	
	public static final class FaceOffset extends FaceVertexGen {
		private final double size;
		public FaceOffset(double size) {
			this.size = size;
		}
		public Point3D createVertex(Polyhedron.Face f, List<Point3D> fv) {
			Point3D fc = Point3D.average(fv);
			return (size == 0) ? fc : fc.normal(fv).multiply(size).add(fc);
		}
	}
	
	public static final class MetricOffset extends FaceVertexGen {
		private final MetricAggregator agg;
		private final Metric met;
		private final double size;
		public MetricOffset(MetricAggregator aggregator, Metric metric, double size) {
			this.agg = aggregator;
			this.met = metric;
			this.size = size;
		}
		private Point3D sc;
		private double sm;
		public void reset(Polyhedron s, List<Point3D> sv) {
			this.sc = Point3D.average(sv);
			this.sm = agg.aggregate(met.iterator(s, sc));
		}
		public Point3D createVertex(Polyhedron.Face f, List<Point3D> fv) {
			return Point3D.average(fv).subtract(sc).normalize(sm + size).add(sc);
		}
	}
	
	public static final class FaceCenterMagnitudeOffset extends FaceVertexGen {
		private final double size;
		public FaceCenterMagnitudeOffset(double size) {
			this.size = size;
		}
		private Point3D sc;
		public void reset(Polyhedron s, List<Point3D> sv) {
			this.sc = Point3D.average(sv);
		}
		public Point3D createVertex(Polyhedron.Face f, List<Point3D> fv) {
			Point3D fc = Point3D.average(fv);
			if (size == 0) return fc;
			double m = fc.distance(sc) + size;
			return fc.subtract(sc).normalize(m).add(sc);
		}
	}
	
	public static final class Equilateral extends FaceVertexGen {
		public Point3D createVertex(Polyhedron.Face f, List<Point3D> fv) {
			Point3D fc = Point3D.average(fv);
			if (fv.size() > 5) return fc;
			double heights = 0;
			for (int i = 0, n = fv.size(); i < n; i++) {
				Point3D v1 = fv.get(i);
				Point3D v2 = fv.get((i + 1) % n);
				Point3D m = v1.midpoint(v2);
				double h2 = v1.distanceSq(v2) * 0.75 - m.distanceSq(fc);
				if (h2 > 0) heights += Math.sqrt(h2);
			}
			return (heights == 0) ? fc : fc.normal(fv).multiply(heights / fv.size()).add(fc);
		}
	}
	
	public static final class Planar extends FaceVertexGen {
		public Point3D createVertex(Polyhedron.Face f, List<Point3D> fv) {
			Point3D fc = Point3D.average(fv);
			double heights = 0;
			for (Polyhedron.Edge e : f.edges) {
				List<Point3D> avs = new ArrayList<Point3D>();
				for (Polyhedron.Face af : f.parent.getOppositeFaces(e, f)) {
					for (Polyhedron.Vertex av : af.vertices) {
						avs.add(av.point);
					}
				}
				Point3D ac = Point3D.average(avs), m = e.midpoint();
				double d = m.distance(fc), ad = m.distance(ac);
				heights += d / Math.tan(m.angleRad(fc, ac) * d / (d + ad));
			}
			return (heights == 0) ? fc : fc.normal(fv).multiply(heights / f.edges.size()).add(fc);
		}
	}
	
	public static final class PolarReciprocal extends FaceVertexGen {
		private final MetricAggregator agg;
		private final Metric met;
		private final Double rad;
		public PolarReciprocal(MetricAggregator aggregator, Metric metric) {
			this.agg = aggregator;
			this.met = metric;
			this.rad = null;
		}
		public PolarReciprocal(double radius) {
			this.agg = null;
			this.met = null;
			this.rad = radius;
		}
		private Point3D sc;
		private double sm;
		public void reset(Polyhedron s, List<Point3D> sv) {
			this.sc = Point3D.average(sv);
			this.sm = (agg != null && met != null) ? agg.aggregate(met.iterator(s, sc)) : rad;
		}
		public Point3D createVertex(Polyhedron.Face f, List<Point3D> fv) {
			Point3D fc = Point3D.average(fv);
			Point3D rc = fc.subtract(sc);
			double rm = rc.magnitude();
			if (rm == 0) return fc;
			double h = sm * sm / rm;
			return rc.normalize(h).add(sc);
		}
	}
	
	public static enum Builder {
		FACE_OFFSET ("H", Type.REAL, "create vertices from faces along the normal to the original face") {
			public FaceVertexGen build(Object arg) {
				return new FaceOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MAX_VERTEX_MAGNITUDE_OFFSET ("X", Type.REAL, "create vertices from faces relative to the maximum circumradius") {
			public FaceVertexGen build(Object arg) {
				return new MetricOffset(MetricAggregator.MAXIMUM, Metric.VERTEX_MAGNITUDE, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		AVERAGE_VERTEX_MAGNITUDE_OFFSET ("A", Type.REAL, "create vertices from faces relative to the average circumradius") {
			public FaceVertexGen build(Object arg) {
				return new MetricOffset(MetricAggregator.AVERAGE, Metric.VERTEX_MAGNITUDE, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MIN_VERTEX_MAGNITUDE_OFFSET ("V", Type.REAL, "create vertices from faces relative to the minimum circumradius") {
			public FaceVertexGen build(Object arg) {
				return new MetricOffset(MetricAggregator.MINIMUM, Metric.VERTEX_MAGNITUDE, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		FACE_CENTER_MAGNITUDE_OFFSET ("F", Type.REAL, "create vertices from faces relative to the center magnitude") {
			public FaceVertexGen build(Object arg) {
				return new FaceCenterMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		EQUILATERAL ("E", Type.VOID, "attempt to create equilateral faces (not always possible)") {
			public FaceVertexGen build(Object arg) {
				return new Equilateral();
			}
		},
		PLANAR ("P", Type.VOID, "attempt to create planar faces (not always possible)") {
			public FaceVertexGen build(Object arg) {
				return new Planar();
			}
		},
		INVERSION_ABOUT_CIRCUMRADIUS ("R", Type.VOID, "create vertices by inversion about average circumradius") {
			public FaceVertexGen build(Object arg) {
				return new PolarReciprocal(MetricAggregator.AVERAGE, Metric.VERTEX_MAGNITUDE);
			}
		},
		INVERSION_ABOUT_MIDRADIUS ("M", Type.VOID, "create vertices by inversion about average midradius") {
			public FaceVertexGen build(Object arg) {
				return new PolarReciprocal(MetricAggregator.AVERAGE, Metric.EDGE_DISTANCE_TO_ORIGIN);
			}
		},
		INVERSION_ABOUT_INRADIUS ("I", Type.VOID, "create vertices by inversion about average inradius") {
			public FaceVertexGen build(Object arg) {
				return new PolarReciprocal(MetricAggregator.AVERAGE, Metric.FACE_DISTANCE_TO_ORIGIN);
			}
		},
		INVERSION_ABOUT_RADIUS ("S", Type.REAL, "create vertices by inversion about a specified radius") {
			public FaceVertexGen build(Object arg) {
				return new PolarReciprocal((arg instanceof Number) ? ((Number)arg).doubleValue() : 1);
			}
		};
		private final String flagWithoutDash;
		private final String flagWithDash;
		private final Type argDataType;
		private final String description;
		private Builder(String flagWithoutDash, Type argDataType, String description) {
			this.flagWithoutDash = flagWithoutDash;
			this.flagWithDash = "-" + flagWithoutDash;
			this.argDataType = argDataType;
			this.description = description;
		}
		public abstract FaceVertexGen build(Object arg);
		public final boolean ignoresArgument() { return argDataType == Type.VOID; }
		public final Object parseArgument(String s) { return argDataType.parse(s); }
		public final FaceVertexGen buildFromArgument(String s) { return build(parseArgument(s)); }
		public final Option option(String... mutex) {
			return new Option(flagWithoutDash, argDataType, description, optionMutexes(mutex));
		}
		public final String[] optionMutexes(String... mutex) {
			ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
			for (Builder bi : values()) if (bi != this) mutexes.add(bi.flagWithoutDash);
			return mutexes.toArray(new String[mutexes.size()]);
		}
		public static String[] allOptionMutexes(String... mutex) {
			ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
			for (Builder bi : values()) mutexes.add(bi.flagWithoutDash);
			return mutexes.toArray(new String[mutexes.size()]);
		}
		public static Builder forFlag(String flag) {
			for (Builder bi : values()) if (bi.flagWithDash.equals(flag)) return bi;
			return null;
		}
	}
}