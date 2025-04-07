package com.kreative.polyhedra.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public enum FaceVertexGen {
	FACE_OFFSET("H", Type.REAL, "create new vertices from faces normal to the original face") {
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv,
			double size
		) {
			Point3D c = Point3D.average(fv);
			if (size == 0) return c;
			return c.add(c.normal(fv).multiply(size));
		}
	},
	MAX_MAGNITUDE_OFFSET("M", Type.REAL, "create new vertices from faces relative to the maximum magnitude") {
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv,
			double size
		) {
			Point3D c = Point3D.average(fv);
			double m = Point3D.maxMagnitude(sv) + size;
			return c.normalize(m);
		}
	},
	AVERAGE_MAGNITUDE_OFFSET("A", Type.REAL, "create new vertices from faces relative to the average magnitude") {
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv,
			double size
		) {
			Point3D c = Point3D.average(fv);
			double m = Point3D.averageMagnitude(sv) + size;
			return c.normalize(m);
		}
	},
	FACE_MAGNITUDE_OFFSET("F", Type.REAL, "create new vertices from faces relative to the face magnitude") {
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv,
			double size
		) {
			Point3D c = Point3D.average(fv);
			if (size == 0) return c;
			return c.normalize(c.magnitude() + size);
		}
	},
	EQUILATERAL("E", Type.VOID, "attempt to create equilateral faces (not always possible)") {
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv,
			double size
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
	},
	PLANAR("P", Type.VOID, "attempt to create planar faces (not always possible)") {
		public Point3D createVertex(
			Polyhedron s, List<Point3D> sv,
			Polyhedron.Face f, List<Point3D> fv,
			double size
		) {
			Point3D c = Point3D.average(fv);
			double heights = 0;
			for (Polyhedron.Edge e : f.edges) {
				List<Point3D> avs = new ArrayList<Point3D>();
				for (Polyhedron.Face af : s.getFaces(e)) {
					if (!af.equals(f)) {
						for (Polyhedron.Vertex av : af.vertices) {
							avs.add(av.point);
						}
					}
				}
				Point3D ac = Point3D.average(avs), m = e.midpoint();
				double d = m.distance(c), ad = m.distance(ac);
				heights += d / Math.tan(m.angleRad(c, ac) * d / (d + ad));
			}
			return c.add(c.normal(fv).multiply(heights / f.edges.size()));
		}
	};
	
	private final String flagWithoutDash;
	private final String flagWithDash;
	private final Type sizeDataType;
	private final String description;
	
	private FaceVertexGen(String flagWithoutDash, Type sizeDataType, String description) {
		this.flagWithoutDash = flagWithoutDash;
		this.flagWithDash = "-" + flagWithoutDash;
		this.sizeDataType = sizeDataType;
		this.description = description;
	}
	
	public abstract Point3D createVertex(
		Polyhedron seed, List<Point3D> seedVertices,
		Polyhedron.Face face, List<Point3D> faceVertices,
		double size
	);
	
	public final boolean isVoidType() {
		return sizeDataType == Type.VOID;
	}
	
	public final Option option(String... mutex) {
		return new Option(flagWithoutDash, sizeDataType, description, optionMutexes(mutex));
	}
	
	public final String[] optionMutexes(String... mutex) {
		ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
		for (FaceVertexGen gen : values()) if (gen != this) mutexes.add(gen.flagWithoutDash);
		return mutexes.toArray(new String[mutexes.size()]);
	}
	
	public static String[] allOptionMutexes(String... mutex) {
		ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
		for (FaceVertexGen gen : values()) mutexes.add(gen.flagWithoutDash);
		return mutexes.toArray(new String[mutexes.size()]);
	}
	
	public static FaceVertexGen forFlag(String flag) {
		for (FaceVertexGen gen : values()) {
			if (gen.flagWithDash.equals(flag)) {
				return gen;
			}
		}
		return null;
	}
	
	public static FaceVertexGen forFlagIgnoreCase(String flag) {
		for (FaceVertexGen gen : values()) {
			if (gen.flagWithDash.equalsIgnoreCase(flag)) {
				return gen;
			}
		}
		return null;
	}
}