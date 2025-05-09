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
	
	public static final class SeedVertexMagnitudeOffset extends FaceVertexGen {
		private final MetricAggregator agg;
		private final double size;
		public SeedVertexMagnitudeOffset(MetricAggregator aggregator, double size) {
			this.agg = aggregator;
			this.size = size;
		}
		private Point3D sc;
		private double sm;
		public void reset(Polyhedron s, List<Point3D> sv) {
			this.sc = Point3D.average(sv);
			this.sm = agg.aggregate(Metric.VERTEX_MAGNITUDE.iterator(s, sc));
		}
		public Point3D createVertex(Polyhedron.Face f, List<Point3D> fv) {
			return Point3D.average(fv).subtract(sc).normalize(sm + size).add(sc);
		}
	}
	
	public static final class FaceMagnitudeOffset extends FaceVertexGen {
		private final double size;
		public FaceMagnitudeOffset(double size) {
			this.size = size;
		}
		private Point3D sc;
		public void reset(Polyhedron s, List<Point3D> sv) {
			this.sc = Point3D.average(sv);
		}
		public Point3D createVertex(Polyhedron.Face f, List<Point3D> fv) {
			Point3D fc = Point3D.average(fv);
			if (size == 0) return fc;
			fc = fc.subtract(sc);
			fc = fc.normalize(fc.magnitude() + size);
			return fc.add(sc);
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
		public PolarReciprocal(MetricAggregator aggregator, Metric metric) {
			this.agg = aggregator;
			this.met = metric;
		}
		private Point3D sc;
		private double sm;
		public void reset(Polyhedron s, List<Point3D> sv) {
			this.sc = Point3D.average(sv);
			this.sm = agg.aggregate(met.iterator(s, sc));
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
		MAX_MAGNITUDE_OFFSET ("M", Type.REAL, "create vertices from faces relative to the maximum magnitude") {
			public FaceVertexGen build(Object arg) {
				return new SeedVertexMagnitudeOffset(MetricAggregator.MAXIMUM, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		AVERAGE_MAGNITUDE_OFFSET ("A", Type.REAL, "create vertices from faces relative to the average magnitude") {
			public FaceVertexGen build(Object arg) {
				return new SeedVertexMagnitudeOffset(MetricAggregator.AVERAGE, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MIN_MAGNITUDE_OFFSET ("I", Type.REAL, "create vertices from faces relative to the minimum magnitude") {
			public FaceVertexGen build(Object arg) {
				return new SeedVertexMagnitudeOffset(MetricAggregator.MINIMUM, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		FACE_MAGNITUDE_OFFSET ("F", Type.REAL, "create vertices from faces relative to the face magnitude") {
			public FaceVertexGen build(Object arg) {
				return new FaceMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
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
		public static Builder forFlagIgnoreCase(String flag) {
			for (Builder bi : values()) if (bi.flagWithDash.equalsIgnoreCase(flag)) return bi;
			return null;
		}
	}
}