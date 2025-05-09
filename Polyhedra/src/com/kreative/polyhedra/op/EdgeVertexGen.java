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
	
	public static final class SeedVertexMagnitudeOffset extends EdgeVertexGen {
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
		public Point3D createVertex(
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D dv
		) {
			return dv.subtract(sc).normalize(sm + size).add(sc);
		}
	}
	
	public static final class EdgeMagnitudeOffset extends EdgeVertexGen {
		private final double size;
		public EdgeMagnitudeOffset(double size) {
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
			double em = edge.midpoint().subtract(sc).magnitude();
			return dv.subtract(sc).normalize(em + size).add(sc);
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
			dv = dv.subtract(sc);
			dv = dv.normalize(dv.magnitude() + size);
			return dv.add(sc);
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
			double fm = Point3D.average(fv).subtract(sc).magnitude();
			return dv.normal(fv).multiply(size - fm).add(dv);
		}
	}
	
	public static enum Builder {
		FACE_OFFSET ("h", Type.REAL, "create vertices from edges along a normal to the original face (r)") {
			public EdgeVertexGen build(Object arg) {
				return new FaceOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MAX_MAGNITUDE_OFFSET ("m", Type.REAL, "create vertices from edges relative to the maximum magnitude") {
			public EdgeVertexGen build(Object arg) {
				return new SeedVertexMagnitudeOffset(MetricAggregator.MAXIMUM, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		AVERAGE_MAGNITUDE_OFFSET ("a", Type.REAL, "create vertices from edges relative to the average magnitude") {
			public EdgeVertexGen build(Object arg) {
				return new SeedVertexMagnitudeOffset(MetricAggregator.AVERAGE, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MIN_MAGNITUDE_OFFSET ("i", Type.REAL, "create vertices from edges relative to the minimum magnitude") {
			public EdgeVertexGen build(Object arg) {
				return new SeedVertexMagnitudeOffset(MetricAggregator.MINIMUM, (arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		EDGE_MAGNITUDE_OFFSET ("e", Type.REAL, "create vertices from edges relative to the edge magnitude") {
			public EdgeVertexGen build(Object arg) {
				return new EdgeMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		VERTEX_MAGNITUDE_OFFSET ("v", Type.REAL, "create vertices from edges relative to the vertex magnitude") {
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
		public static Builder forFlagIgnoreCase(String flag) {
			for (Builder bi : values()) if (bi.flagWithDash.equalsIgnoreCase(flag)) return bi;
			return null;
		}
	}
}