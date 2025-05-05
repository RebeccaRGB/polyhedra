package com.kreative.polyhedra.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public abstract class FaceVertexGen {
	public abstract Point3D createVertex(
		Polyhedron seed, List<Point3D> seedVertices,
		Polyhedron.Face face, List<Point3D> faceVertices
	);
	
	public static final class FaceOffset extends FaceVertexGen {
		private final double size;
		public FaceOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv
		) {
			Point3D c = Point3D.average(fv);
			return (size == 0) ? c : c.add(c.normal(fv).multiply(size));
		}
	}
	
	public static final class MaxMagnitudeOffset extends FaceVertexGen {
		private final double size;
		public MaxMagnitudeOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv
		) {
			Point3D c = Point3D.average(fv);
			return c.normalize(Point3D.maxMagnitude(sv) + size);
		}
	}
	
	public static final class AverageMagnitudeOffset extends FaceVertexGen {
		private final double size;
		public AverageMagnitudeOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv
		) {
			Point3D c = Point3D.average(fv);
			return c.normalize(Point3D.averageMagnitude(sv) + size);
		}
	}
	
	public static final class MinMagnitudeOffset extends FaceVertexGen {
		private final double size;
		public MinMagnitudeOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv
		) {
			Point3D c = Point3D.average(fv);
			return c.normalize(Point3D.minMagnitude(sv) + size);
		}
	}
	
	public static final class FaceMagnitudeOffset extends FaceVertexGen {
		private final double size;
		public FaceMagnitudeOffset(double size) { this.size = size; }
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv
		) {
			Point3D c = Point3D.average(fv);
			return (size == 0) ? c : c.normalize(c.magnitude() + size);
		}
	}
	
	public static final class Equilateral extends FaceVertexGen {
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv
		) {
			Point3D c = Point3D.average(fv);
			if (fv.size() > 5) return c;
			double heights = 0;
			for (int i = 0, n = fv.size(); i < n; i++) {
				Point3D v1 = fv.get(i);
				Point3D v2 = fv.get((i + 1) % n);
				Point3D m = v1.midpoint(v2);
				double h2 = v1.distanceSq(v2) * 0.75 - m.distanceSq(c);
				if (h2 > 0) heights += Math.sqrt(h2);
			}
			return c.add(c.normal(fv).multiply(heights / fv.size()));
		}
	}
	
	public static final class Planar extends FaceVertexGen {
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv
		) {
			Point3D c = Point3D.average(fv);
			double heights = 0;
			for (Polyhedron.Edge e : f.edges) {
				List<Point3D> avs = new ArrayList<Point3D>();
				for (Polyhedron.Face af : s.getOppositeFaces(e, f)) {
					for (Polyhedron.Vertex av : af.vertices) {
						avs.add(av.point);
					}
				}
				Point3D ac = Point3D.average(avs), m = e.midpoint();
				double d = m.distance(c), ad = m.distance(ac);
				heights += d / Math.tan(m.angleRad(c, ac) * d / (d + ad));
			}
			return c.add(c.normal(fv).multiply(heights / f.edges.size()));
		}
	}
	
	public static enum Builtin {
		FACE_OFFSET ("H", Type.REAL, "create vertices from faces along the normal to the original face") {
			public FaceVertexGen gen(Object arg) {
				return new FaceOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MAX_MAGNITUDE_OFFSET ("M", Type.REAL, "create vertices from faces relative to the maximum magnitude") {
			public FaceVertexGen gen(Object arg) {
				return new MaxMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		AVERAGE_MAGNITUDE_OFFSET ("A", Type.REAL, "create vertices from faces relative to the average magnitude") {
			public FaceVertexGen gen(Object arg) {
				return new AverageMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		MIN_MAGNITUDE_OFFSET ("I", Type.REAL, "create vertices from faces relative to the minimum magnitude") {
			public FaceVertexGen gen(Object arg) {
				return new MinMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		FACE_MAGNITUDE_OFFSET ("F", Type.REAL, "create vertices from faces relative to the face magnitude") {
			public FaceVertexGen gen(Object arg) {
				return new FaceMagnitudeOffset((arg instanceof Number) ? ((Number)arg).doubleValue() : 0);
			}
		},
		EQUILATERAL ("E", Type.VOID, "attempt to create equilateral faces (not always possible)") {
			public FaceVertexGen gen(Object arg) {
				return new Equilateral();
			}
		},
		PLANAR ("P", Type.VOID, "attempt to create planar faces (not always possible)") {
			public FaceVertexGen gen(Object arg) {
				return new Planar();
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
		public abstract FaceVertexGen gen(Object arg);
		public final boolean isVoidType() { return argDataType == Type.VOID; }
		public final Object parseArgument(String s) { return argDataType.parse(s); }
		public final FaceVertexGen parse(String s) { return gen(parseArgument(s)); }
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