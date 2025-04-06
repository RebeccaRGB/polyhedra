package com.kreative.polyhedra.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public enum EdgeVertexGen {
	FACE_OFFSET("h", Type.REAL, "create new vertices from edges normal to the original face") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, double size
		) {
			if (fv == null || fv.isEmpty() || size == 0) return c;
			return c.add(c.normal(fv).multiply(size));
		}
	},
	MAX_MAGNITUDE_OFFSET("m", Type.REAL, "create new vertices from edges relative to the maximum magnitude") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, double size
		) {
			double m = Point3D.maxMagnitude(sv) + size;
			return c.multiply(m / c.magnitude());
		}
	},
	AVERAGE_MAGNITUDE_OFFSET("a", Type.REAL, "create new vertices from edges relative to the average magnitude") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, double size
		) {
			double m = Point3D.averageMagnitude(sv) + size;
			return c.multiply(m / c.magnitude());
		}
	},
	EDGE_MAGNITUDE_OFFSET("e", Type.REAL, "create new vertices from edges relative to the edge magnitude") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, double size
		) {
			double m = edge.vertex1.point.midpoint(edge.vertex2.point).magnitude() + size;
			return c.multiply(m / c.magnitude());
		}
	},
	VERTEX_MAGNITUDE_OFFSET("v", Type.REAL, "create new vertices from edges relative to the vertex magnitude") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, double size
		) {
			if (size == 0) return c;
			double cm = c.magnitude();
			double m = cm + size;
			return c.multiply(m / cm);
		}
	};
	
	private final String flagWithoutDash;
	private final String flagWithDash;
	private final Type sizeDataType;
	private final String description;
	
	private EdgeVertexGen(String flagWithoutDash, Type sizeDataType, String description) {
		this.flagWithoutDash = flagWithoutDash;
		this.flagWithDash = "-" + flagWithoutDash;
		this.sizeDataType = sizeDataType;
		this.description = description;
	}
	
	public abstract Point3D createVertex(
		Polyhedron seed, List<Point3D> seedVertices,
		Polyhedron.Face face, List<Point3D> faceVertices,
		Polyhedron.Edge edge, Point3D vertex, double size
	);
	
	public final Point3D createVertex(
		Polyhedron seed, List<Point3D> seedVertices,
		Polyhedron.Face face, List<Point3D> faceVertices,
		Polyhedron.Edge edge, double a, double b, double size
	) {
		Point3D vertex = (
			(a == b) ? edge.vertex1.point.midpoint(edge.vertex2.point) :
			(a == 0) ? edge.vertex2.point : (b == 0) ? edge.vertex1.point :
			(a == 1) ? edge.vertex1.point.add(edge.vertex2.point.multiply(b)).divide(b + 1) :
			(b == 1) ? edge.vertex1.point.multiply(a).add(edge.vertex2.point).divide(a + 1) :
			edge.vertex1.point.multiply(a).add(edge.vertex2.point.multiply(b)).divide(a + b)
		);
		return createVertex(seed, seedVertices, face, faceVertices, edge, vertex, size);
	}
	
	public final boolean isVoidType() {
		return sizeDataType == Type.VOID;
	}
	
	public final Option option(String... mutex) {
		return new Option(flagWithoutDash, sizeDataType, description, optionMutexes(mutex));
	}
	
	public final String[] optionMutexes(String... mutex) {
		ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
		for (EdgeVertexGen gen : values()) if (gen != this) mutexes.add(gen.flagWithoutDash);
		return mutexes.toArray(new String[mutexes.size()]);
	}
	
	public static String[] allOptionMutexes(String... mutex) {
		ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
		for (EdgeVertexGen gen : values()) mutexes.add(gen.flagWithoutDash);
		return mutexes.toArray(new String[mutexes.size()]);
	}
	
	public static EdgeVertexGen forFlag(String flag) {
		for (EdgeVertexGen gen : values()) {
			if (gen.flagWithDash.equals(flag)) {
				return gen;
			}
		}
		return null;
	}
	
	public static EdgeVertexGen forFlagIgnoreCase(String flag) {
		for (EdgeVertexGen gen : values()) {
			if (gen.flagWithDash.equalsIgnoreCase(flag)) {
				return gen;
			}
		}
		return null;
	}
}