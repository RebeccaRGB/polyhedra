package com.kreative.polyhedra.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public abstract class GyroVertexGen {
	public abstract Point3D createVertex(
		Polyhedron seed, List<Point3D> seedVertices,
		Polyhedron.Face face, List<Point3D> faceVertices,
		Polyhedron.Edge edge, Point3D vertex
	);
	
	public static final class FixedDistanceFromVertexAlongEdge extends GyroVertexGen {
		private final double size;
		public FixedDistanceFromVertexAlongEdge(double distance) { this.size = distance; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex
		) {
			if (size == 0) return vertex;
			Point3D midpoint = edge.midpoint();
			return midpoint.subtract(vertex).normalize(size).add(vertex);
		}
	}
	
	public static final class RelativeDistanceFromVertexAlongEdge extends GyroVertexGen {
		private final double size;
		public RelativeDistanceFromVertexAlongEdge(double distance) { this.size = distance; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex
		) {
			if (size == 0) return vertex;
			Point3D midpoint = edge.midpoint();
			return midpoint.subtract(vertex).multiply(size).add(vertex);
		}
	}
	
	public static final class FixedAngleFromVertexAlongEdge extends GyroVertexGen {
		private final double size;
		public FixedAngleFromVertexAlongEdge(double angle) { this.size = angle; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex
		) {
			if (size == 0) return vertex;
			Point3D midpoint = edge.midpoint();
			Point3D center = face.center();
			double b = center.distance(vertex) * Math.sin(Math.toRadians(size));
			double c = Math.sin(vertex.angleRad(midpoint, center) + Math.toRadians(size));
			return midpoint.subtract(vertex).normalize(b / c).add(vertex);
		}
	}
	
	public static final class FixedDistanceFromMidpointAlongEdge extends GyroVertexGen {
		private final double size;
		public FixedDistanceFromMidpointAlongEdge(double distance) { this.size = distance; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex
		) {
			Point3D midpoint = edge.midpoint();
			if (size == 0) return midpoint;
			return vertex.subtract(midpoint).normalize(size).add(midpoint);
		}
	}
	
	public static final class RelativeDistanceFromMidpointAlongEdge extends GyroVertexGen {
		private final double size;
		public RelativeDistanceFromMidpointAlongEdge(double distance) { this.size = distance; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex
		) {
			Point3D midpoint = edge.midpoint();
			if (size == 0) return midpoint;
			return vertex.subtract(midpoint).multiply(size).add(midpoint);
		}
	}
	
	public static final class FixedAngleFromMidpointAlongEdge extends GyroVertexGen {
		private final double size;
		public FixedAngleFromMidpointAlongEdge(double angle) { this.size = angle; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex
		) {
			Point3D midpoint = edge.midpoint();
			if (size == 0) return midpoint;
			Point3D center = face.center();
			double a = center.distance(midpoint) * Math.tan(Math.toRadians(size));
			return vertex.subtract(midpoint).normalize(a).add(midpoint);
		}
	}
	
	public static final class TwistAngle extends GyroVertexGen {
		private final double size;
		public TwistAngle(double angle) { this.size = angle; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex
		) {
			if (size == 0) return vertex;
			Point3D midpoint = edge.midpoint();
			Point3D center = face.center();
			double a = center.distance(vertex);
			double b = a * Math.sin(Math.toRadians(size));
			double c = Math.sin(vertex.angleRad(midpoint, center) + Math.toRadians(size));
			Point3D v = midpoint.subtract(vertex).normalize(b / c).add(vertex);
			return v.subtract(center).normalize(a).add(center);
		}
	}
	
	public static enum Builder {
		FIXED_DISTANCE_FROM_VERTEX_ALONG_EDGE ("u", Type.REAL, "create vertices along edges at a fixed distance from the vertex") {
			public GyroVertexGen build(Object arg) {
				return new FixedDistanceFromVertexAlongEdge((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		RELATIVE_DISTANCE_FROM_VERTEX_ALONG_EDGE ("U", Type.REAL, "create vertices along edges at a relative distance from the vertex") {
			public GyroVertexGen build(Object arg) {
				return new RelativeDistanceFromVertexAlongEdge((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		FIXED_ANGLE_FROM_VERTEX_ALONG_EDGE ("W", Type.REAL, "create vertices along edges at a fixed angle from the vertex") {
			public GyroVertexGen build(Object arg) {
				return new FixedAngleFromVertexAlongEdge((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		FIXED_DISTANCE_FROM_MIDPOINT_ALONG_EDGE ("l", Type.REAL, "create vertices along edges at a fixed distance from the midpoint") {
			public GyroVertexGen build(Object arg) {
				return new FixedDistanceFromMidpointAlongEdge((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		RELATIVE_DISTANCE_FROM_MIDPOINT_ALONG_EDGE ("L", Type.REAL, "create vertices along edges at a relative distance from the midpoint") {
			public GyroVertexGen build(Object arg) {
				return new RelativeDistanceFromMidpointAlongEdge((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		FIXED_ANGLE_FROM_MIDPOINT_ALONG_EDGE ("N", Type.REAL, "create vertices along edges at a fixed angle from the midpoint") {
			public GyroVertexGen build(Object arg) {
				return new FixedAngleFromMidpointAlongEdge((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		TWIST_ANGLE ("t", Type.REAL, "create vertices from edges at a fixed twist angle") {
			public GyroVertexGen build(Object arg) {
				return new TwistAngle((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
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
		public abstract GyroVertexGen build(Object arg);
		public final boolean ignoresArgument() { return argDataType == Type.VOID; }
		public final Object parseArgument(String s) { return argDataType.parse(s); }
		public final GyroVertexGen buildFromArgument(String s) { return build(parseArgument(s)); }
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