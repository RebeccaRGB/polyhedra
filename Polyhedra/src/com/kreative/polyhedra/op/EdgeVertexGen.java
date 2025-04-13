package com.kreative.polyhedra.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public enum EdgeVertexGen {
	FACE_OFFSET("h", Type.REAL, "create vertices from edges along a normal to the original face (r)") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, Object arg
		) {
			if (fv == null || fv.isEmpty()) return c;
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			return (size == 0) ? c : c.add(c.normal(fv).multiply(size));
		}
	},
	MAX_MAGNITUDE_OFFSET("m", Type.REAL, "create vertices from edges relative to the maximum magnitude") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			return c.normalize(Point3D.maxMagnitude(sv) + size);
		}
	},
	AVERAGE_MAGNITUDE_OFFSET("a", Type.REAL, "create vertices from edges relative to the average magnitude") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			return c.normalize(Point3D.averageMagnitude(sv) + size);
		}
	},
	EDGE_MAGNITUDE_OFFSET("e", Type.REAL, "create vertices from edges relative to the edge magnitude") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			return c.normalize(edge.midpoint().magnitude() + size);
		}
	},
	VERTEX_MAGNITUDE_OFFSET("v", Type.REAL, "create vertices from edges relative to the vertex magnitude") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			return (size == 0) ? c : c.normalize(c.magnitude() + size);
		}
	},
	FACE_OFFSET_FROM_ORIGIN("o", Type.REAL, "create vertices from edges along a normal to the original face (a)") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> sv,
			Polyhedron.Face face, List<Point3D> fv,
			Polyhedron.Edge edge, Point3D c, Object arg
		) {
			if (fv == null || fv.isEmpty()) return c;
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			double centerMagnitude = Point3D.average(fv).magnitude();
			return c.add(c.normal(fv).multiply(size - centerMagnitude));
		}
	};
	
	private final String flagWithoutDash;
	private final String flagWithDash;
	private final Type argDataType;
	private final String description;
	
	private EdgeVertexGen(String flagWithoutDash, Type argDataType, String description) {
		this.flagWithoutDash = flagWithoutDash;
		this.flagWithDash = "-" + flagWithoutDash;
		this.argDataType = argDataType;
		this.description = description;
	}
	
	public abstract Point3D createVertex(
		Polyhedron seed, List<Point3D> seedVertices,
		Polyhedron.Face face, List<Point3D> faceVertices,
		Polyhedron.Edge edge, Point3D vertex, Object arg
	);
	
	public final boolean isVoidType() {
		return argDataType == Type.VOID;
	}
	
	public final Object parseArgument(String s) {
		return argDataType.parse(s);
	}
	
	public final Option option(String... mutex) {
		return new Option(flagWithoutDash, argDataType, description, optionMutexes(mutex));
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