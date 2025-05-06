package com.kreative.polyhedra.op;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.Polyhedron.Edge;
import com.kreative.polyhedra.Polyhedron.Face;
import com.kreative.polyhedra.Polyhedron.Vertex;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public abstract class VertexPredicate {
	public void reset(Polyhedron seed) {}
	public abstract boolean matches(Vertex vertex, List<Edge> edges, List<Face> faces);
	
	public static final class Degree extends VertexPredicate {
		private final Set<Integer> degrees;
		public Degree(Integer... degrees) {
			this.degrees = new HashSet<Integer>();
			for (int d : degrees) this.degrees.add(d);
		}
		public Degree(Iterable<? extends Integer> degrees) {
			this.degrees = new HashSet<Integer>();
			for (int d : degrees) this.degrees.add(d);
		}
		public boolean matches(Vertex vertex, List<Edge> edges, List<Face> faces) {
			if (edges == null) edges = vertex.parent.getEdges(vertex);
			return degrees.contains(edges.size());
		}
	}
	
	public static final class Index extends VertexPredicate {
		private final Set<Integer> indices;
		public Index(Integer... indices) {
			this.indices = new HashSet<Integer>();
			for (int i : indices) this.indices.add(i);
		}
		public Index(Iterable<? extends Integer> indices) {
			this.indices = new HashSet<Integer>();
			for (int i : indices) this.indices.add(i);
		}
		private int currentIndex = 0;
		public void reset(Polyhedron seed) { currentIndex = 0; }
		public boolean matches(Vertex vertex, List<Edge> edges, List<Face> faces) {
			return indices.contains(currentIndex++);
		}
	}
	
	public static final class AtAngle extends VertexPredicate {
		private final Set<Number> angles;
		public AtAngle(Number... angles) {
			this.angles = new HashSet<Number>();
			for (Number a : angles) this.angles.add(a);
		}
		public AtAngle(Iterable<? extends Number> angles) {
			this.angles = new HashSet<Number>();
			for (Number a : angles) this.angles.add(a);
		}
		private Point3D center = null;
		private final Set<Integer> indices = new HashSet<Integer>();
		private final Set<Point3D> centers = new HashSet<Point3D>();
		public void reset(Polyhedron seed) {
			this.center = seed.center();
			this.indices.clear();
			this.centers.clear();
		}
		public boolean matches(Vertex vertex, List<Edge> edges, List<Face> faces) {
			if (indices.contains(vertex.index)) return true;
			Point3D c1 = vertex.point;
			for (Point3D c2 : centers) {
				boolean matches = false;
				double angle = center.angle(c1, c2);
				for (Number a : angles) {
					double diff = Math.abs(angle - a.doubleValue());
					if (diff < 1e-12) matches = true;
				}
				if (!matches) return false;
			}
			indices.add(vertex.index);
			centers.add(c1);
			return true;
		}
	}
	
	public static final class AdjacentFaceDegree extends VertexPredicate {
		private final Set<Integer> degrees;
		public AdjacentFaceDegree(Integer... degrees) {
			this.degrees = new HashSet<Integer>();
			for (int d : degrees) this.degrees.add(d);
		}
		public AdjacentFaceDegree(Iterable<? extends Integer> degrees) {
			this.degrees = new HashSet<Integer>();
			for (int d : degrees) this.degrees.add(d);
		}
		public boolean matches(Vertex vertex, List<Edge> edges, List<Face> faces) {
			if (faces == null) faces = vertex.parent.getFaces(vertex);
			for (Face face : faces) if (!degrees.contains(face.vertices.size())) return false;
			return true;
		}
	}
	
	public static enum Builder {
		DEGREE ("n", Type.INTS, "only operate on vertices with the specified number of edges") {
			@SuppressWarnings("unchecked")
			public VertexPredicate build(Object arg) {
				return new Degree((Iterable<? extends Integer>)arg);
			}
		},
		INDEX ("i", Type.INTS, "only operate on vertices with the specified indices") {
			@SuppressWarnings("unchecked")
			public VertexPredicate build(Object arg) {
				return new Index((Iterable<? extends Integer>)arg);
			}
		},
		AT_ANGLE ("t", Type.REALS, "only operate on vertices at the specified angle from each other") {
			@SuppressWarnings("unchecked")
			public VertexPredicate build(Object arg) {
				return new AtAngle((Iterable<? extends Number>)arg);
			}
		},
		ADJACENT_FACE_DEGREE ("j", Type.INTS, "only operate on vertices on faces of the specified degree") {
			@SuppressWarnings("unchecked")
			public VertexPredicate build(Object arg) {
				return new AdjacentFaceDegree((Iterable<? extends Integer>)arg);
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
		public abstract VertexPredicate build(Object arg);
		public final boolean ignoresArgument() { return argDataType == Type.VOID; }
		public final Object parseArgument(String s) { return argDataType.parse(s); }
		public final VertexPredicate buildFromArgument(String s) { return build(parseArgument(s)); }
		public final Option option(String... mutex) {
			return new Option(flagWithoutDash, argDataType, description, mutex);
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
	
	public static void reset(Collection<? extends VertexPredicate> c, Polyhedron seed) {
		if (c != null) for (VertexPredicate p : c) p.reset(seed);
	}
	
	public static boolean matches(Collection<? extends VertexPredicate> c, Vertex v, List<Edge> e, List<Face> f) {
		if (c != null) for (VertexPredicate p : c) if (!p.matches(v, e, f)) return false;
		return true;
	}
}