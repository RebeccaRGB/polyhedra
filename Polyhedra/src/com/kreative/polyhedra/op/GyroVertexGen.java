package com.kreative.polyhedra.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public enum GyroVertexGen {
	FIXED_DISTANCE_FROM_VERTEX_ALONG_EDGE("u", Type.REAL, "create vertices along edges at a fixed distance from the vertex") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			if (size == 0) return vertex;
			Point3D midpoint = edge.midpoint();
			return midpoint.subtract(vertex).normalize(size).add(vertex);
		}
	},
	RELATIVE_DISTANCE_FROM_VERTEX_ALONG_EDGE("U", Type.REAL, "create vertices along edges at a relative distance from the vertex") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			if (size == 0) return vertex;
			Point3D midpoint = edge.midpoint();
			return midpoint.subtract(vertex).multiply(size).add(vertex);
		}
	},
	FIXED_ANGLE_FROM_VERTEX_ALONG_EDGE("W", Type.REAL, "create vertices along edges at a fixed angle from the vertex") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			if (size == 0) return vertex;
			Point3D midpoint = edge.midpoint();
			Point3D center = face.center();
			double b = center.distance(vertex) * Math.sin(Math.toRadians(size));
			double c = Math.sin(vertex.angleRad(midpoint, center) + Math.toRadians(size));
			return midpoint.subtract(vertex).normalize(b / c).add(vertex);
		}
	},
	FIXED_DISTANCE_FROM_MIDPOINT_ALONG_EDGE("l", Type.REAL, "create vertices along edges at a fixed distance from the midpoint") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			Point3D midpoint = edge.midpoint();
			if (size == 0) return midpoint;
			return vertex.subtract(midpoint).normalize(size).add(midpoint);
		}
	},
	RELATIVE_DISTANCE_FROM_MIDPOINT_ALONG_EDGE("L", Type.REAL, "create vertices along edges at a relative distance from the midpoint") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			Point3D midpoint = edge.midpoint();
			if (size == 0) return midpoint;
			return vertex.subtract(midpoint).multiply(size).add(midpoint);
		}
	},
	FIXED_ANGLE_FROM_MIDPOINT_ALONG_EDGE("N", Type.REAL, "create vertices along edges at a fixed angle from the midpoint") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			Point3D midpoint = edge.midpoint();
			if (size == 0) return midpoint;
			Point3D center = face.center();
			double a = center.distance(midpoint) * Math.tan(Math.toRadians(size));
			return vertex.subtract(midpoint).normalize(a).add(midpoint);
		}
	},
	TWIST_ANGLE("t", Type.REAL, "create vertices from edges at a fixed twist angle") {
		public Point3D createVertex(
			Polyhedron seed, List<Point3D> seedVertices,
			Polyhedron.Face face, List<Point3D> faceVertices,
			Polyhedron.Edge edge, Point3D vertex, Object arg
		) {
			double size = (arg instanceof Number) ? ((Number)arg).doubleValue() : 0;
			if (size == 0) return vertex;
			Point3D midpoint = edge.midpoint();
			Point3D center = face.center();
			double a = center.distance(vertex);
			double b = a * Math.sin(Math.toRadians(size));
			double c = Math.sin(vertex.angleRad(midpoint, center) + Math.toRadians(size));
			Point3D v = midpoint.subtract(vertex).normalize(b / c).add(vertex);
			return v.subtract(center).normalize(a).add(center);
		}
	};
	
	private final String flagWithoutDash;
	private final String flagWithDash;
	private final Type argDataType;
	private final String description;
	
	private GyroVertexGen(String flagWithoutDash, Type argDataType, String description) {
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
		for (GyroVertexGen gen : values()) if (gen != this) mutexes.add(gen.flagWithoutDash);
		return mutexes.toArray(new String[mutexes.size()]);
	}
	
	public static String[] allOptionMutexes(String... mutex) {
		ArrayList<String> mutexes = new ArrayList<String>(Arrays.asList(mutex));
		for (GyroVertexGen gen : values()) mutexes.add(gen.flagWithoutDash);
		return mutexes.toArray(new String[mutexes.size()]);
	}
	
	public static GyroVertexGen forFlag(String flag) {
		for (GyroVertexGen gen : values()) {
			if (gen.flagWithDash.equals(flag)) {
				return gen;
			}
		}
		return null;
	}
}