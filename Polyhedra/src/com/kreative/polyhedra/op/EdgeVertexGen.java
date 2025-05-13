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

public abstract class EdgeVertexGen {
	public void reset(Polyhedron seed, List<Point3D> seedVertices) {}
	
	public abstract Point3D createVertex(
		Polyhedron.Face face, List<Point3D> faceVertices,
		Polyhedron.Edge edge, Point3D defaultVertex
	);
	
	public static final class FaceOffset extends EdgeVertexGen {
		private final double size;
		public FaceOffset(double size) {
			this.size = size;
		}
		public Point3D createVertex(
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D dv
		) {
			if (fv == null || fv.isEmpty() || size == 0) return dv;
			return dv.normal(fv).multiply(size).add(dv);
		}
	}
	
	public static final class MetricOffset extends EdgeVertexGen {
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
		public Point3D createVertex(
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D dv
		) {
			return dv.subtract(sc).normalize(sm + size).add(sc);
		}
	}
	
	public static final class EdgeMidpointMagnitudeOffset extends EdgeVertexGen {
		private final double size;
		public EdgeMidpointMagnitudeOffset(double size) {
			this.size = size;
		}
		private Point3D sc;
		public void reset(Polyhedron s, List<Point3D> sv) {
			this.sc = Point3D.average(sv);
		}
		public Point3D createVertex(
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D dv
		) {
			double m = edge.midpoint().distance(sc) + size;
			return dv.subtract(sc).normalize(m).add(sc);
		}
	}
	
	public static final class DefaultVertexMagnitudeOffset extends EdgeVertexGen {
		private final double size;
		public DefaultVertexMagnitudeOffset(double size) {
			this.size = size;
		}
		private Point3D sc;
		public void reset(Polyhedron s, List<Point3D> sv) {
			this.sc = Point3D.average(sv);
		}
		public Point3D createVertex(
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D dv
		) {
			if (size == 0) return dv;
			double m = dv.distance(sc) + size;
			return dv.subtract(sc).normalize(m).add(sc);
		}
	}
	
	public static final class FaceOffsetFromOrigin extends EdgeVertexGen {
		private final double size;
		public FaceOffsetFromOrigin(double size) {
			this.size = size;
		}
		private Point3D sc;
		public void reset(Polyhedron s, List<Point3D> sv) {
			this.sc = Point3D.average(sv);
		}
		public Point3D createVertex(
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D dv
		) {
			if (fv == null || fv.isEmpty()) return dv;
			Point3D normal = dv.normal(fv);
			double fm = sc.distanceToPlane(dv, normal);
			return normal.multiply(size - fm).add(dv);
		}
	}
	
	public static enum Builder {
		FACE_OFFSET ("h", Type.REAL, "create vertices from edges along a normal to the original face (r)") {
			public EdgeVertexGen build(Object arg) {
				return new FaceOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MAX_VERTEX_MAGNITUDE_OFFSET ("x", Type.REAL, "create vertices from edges relative to the maximum circumradius") {
			public EdgeVertexGen build(Object arg) {
				return new MetricOffset(MetricAggregator.MAXIMUM, Metric.VERTEX_MAGNITUDE, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		AVERAGE_VERTEX_MAGNITUDE_OFFSET ("a", Type.REAL, "create vertices from edges relative to the average circumradius") {
			public EdgeVertexGen build(Object arg) {
				return new MetricOffset(MetricAggregator.AVERAGE, Metric.VERTEX_MAGNITUDE, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MIN_VERTEX_MAGNITUDE_OFFSET ("v", Type.REAL, "create vertices from edges relative to the minimum circumradius") {
			public EdgeVertexGen build(Object arg) {
				return new MetricOffset(MetricAggregator.MINIMUM, Metric.VERTEX_MAGNITUDE, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		EDGE_MIDPOINT_MAGNITUDE_OFFSET ("e", Type.REAL, "create vertices from edges relative to the midpoint magnitude") {
			public EdgeVertexGen build(Object arg) {
				return new EdgeMidpointMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		DEFAULT_VERTEX_MAGNITUDE_OFFSET ("d", Type.REAL, "create vertices from edges relative to the default magnitude") {
			public EdgeVertexGen build(Object arg) {
				return new DefaultVertexMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		FACE_OFFSET_FROM_ORIGIN ("o", Type.REAL, "create vertices from edges along a normal to the original face (a)") {
			public EdgeVertexGen build(Object arg) {
				return new FaceOffsetFromOrigin((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
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
		public abstract EdgeVertexGen build(Object arg);
		public final boolean ignoresArgument() { return argDataType == Type.VOID; }
		public final Object parseArgument(String s) { return argDataType.parse(s); }
		public final EdgeVertexGen buildFromArgument(String s) { return build(parseArgument(s)); }
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