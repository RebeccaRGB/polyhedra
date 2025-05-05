package com.kreative.polyhedra.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public abstract class EdgeVertexGen {
	public abstract Point3D createVertex(
		Polyhedron seed, List<Point3D> seedVertices,
		Polyhedron.Face face, List<Point3D> faceVertices,
		Polyhedron.Edge edge, Point3D defaultVertex
	);
	
	public static final class FaceOffset extends EdgeVertexGen {
		private final double size;
		public FaceOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c
		) {
			if (fv == null || fv.isEmpty() || size == 0) return c;
			return c.add(c.normal(fv).multiply(size));
		}
	}
	
	public static final class MaxMagnitudeOffset extends EdgeVertexGen {
		private final double size;
		public MaxMagnitudeOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c
		) {
			return c.normalize(Point3D.maxMagnitude(sv) + size);
		}
	}
	
	public static final class AverageMagnitudeOffset extends EdgeVertexGen {
		private final double size;
		public AverageMagnitudeOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c
		) {
			return c.normalize(Point3D.averageMagnitude(sv) + size);
		}
	}
	
	public static final class MinMagnitudeOffset extends EdgeVertexGen {
		private final double size;
		public MinMagnitudeOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c
		) {
			return c.normalize(Point3D.minMagnitude(sv) + size);
		}
	}
	
	public static final class EdgeMagnitudeOffset extends EdgeVertexGen {
		private final double size;
		public EdgeMagnitudeOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c
		) {
			return c.normalize(edge.midpoint().magnitude() + size);
		}
	}
	
	public static final class VertexMagnitudeOffset extends EdgeVertexGen {
		private final double size;
		public VertexMagnitudeOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c
		) {
			return (size == 0) ? c : c.normalize(c.magnitude() + size);
		}
	}
	
	public static final class FaceOffsetFromOrigin extends EdgeVertexGen {
		private final double size;
		public FaceOffsetFromOrigin(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c
		) {
			if (fv == null || fv.isEmpty()) return c;
			double centerMagnitude = Point3D.average(fv).magnitude();
			return c.add(c.normal(fv).multiply(size - centerMagnitude));
		}
	}
	
	public static enum Builtin {
		FACE_OFFSET ("h", Type.REAL, "create vertices from edges along a normal to the original face (r)") {
			public EdgeVertexGen gen(Object arg) {
				return new FaceOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MAX_MAGNITUDE_OFFSET ("m", Type.REAL, "create vertices from edges relative to the maximum magnitude") {
			public EdgeVertexGen gen(Object arg) {
				return new MaxMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		AVERAGE_MAGNITUDE_OFFSET ("a", Type.REAL, "create vertices from edges relative to the average magnitude") {
			public EdgeVertexGen gen(Object arg) {
				return new AverageMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MIN_MAGNITUDE_OFFSET ("i", Type.REAL, "create vertices from edges relative to the minimum magnitude") {
			public EdgeVertexGen gen(Object arg) {
				return new MinMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		EDGE_MAGNITUDE_OFFSET ("e", Type.REAL, "create vertices from edges relative to the edge magnitude") {
			public EdgeVertexGen gen(Object arg) {
				return new EdgeMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		VERTEX_MAGNITUDE_OFFSET ("v", Type.REAL, "create vertices from edges relative to the vertex magnitude") {
			public EdgeVertexGen gen(Object arg) {
				return new VertexMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		FACE_OFFSET_FROM_ORIGIN ("o", Type.REAL, "create vertices from edges along a normal to the original face (a)") {
			public EdgeVertexGen gen(Object arg) {
				return new FaceOffsetFromOrigin((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		};
		private final String flagWithoutDash;
		private final String flagWithDash;
		private final Type argDataType;
		private final String description;
		private Builtin(String flagWithoutDash, Type argDataType, String description) {
			this.flagWithoutDash = flagWithoutDash;
			this.flagWithDash = "-" + flagWithoutDash;
			this.argDataType = argDataType;
			this.description = description;
		}
		public abstract EdgeVertexGen gen(Object arg);
		public final boolean isVoidType() { return argDataType == Type.VOID; }
		public final Object parseArgument(String s) { return argDataType.parse(s); }
		public final EdgeVertexGen parse(String s) { return gen(parseArgument(s)); }
		public final Option option(String... mutex) {
			return new Option(flagWithoutDash, argDataType, description, optionMutexes(mutex));
		}
		public final String[] optionMutexes(String... mutex) {
			ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
			for (Builtin bi : values()) if (bi != this) mutexes.add(bi.flagWithoutDash);
			return mutexes.toArray(new String[mutexes.size()]);
		}
		public static String[] allOptionMutexes(String... mutex) {
			ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
			for (Builtin bi : values()) mutexes.add(bi.flagWithoutDash);
			return mutexes.toArray(new String[mutexes.size()]);
		}
		public static Builtin forFlag(String flag) {
			for (Builtin bi : values()) if (bi.flagWithDash.equals(flag)) return bi;
			return null;
		}
		public static Builtin forFlagIgnoreCase(String flag) {
			for (Builtin bi : values()) if (bi.flagWithDash.equalsIgnoreCase(flag)) return bi;
			return null;
		}
	}
}