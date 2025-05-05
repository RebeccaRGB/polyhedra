package com.kreative.polyhedra.op;

import java.util.HashSet;
import java.util.Set;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public abstract class FacePredicate {
	public void reset(Polyhedron seed) {}
	public abstract boolean matches(Polyhedron.Face face);
	
	public static final class Degree extends FacePredicate {
		private final Set<Integer> degrees;
		public Degree(Integer... degrees) {
			this.degrees = new HashSet<Integer>();
			for (int d : degrees) this.degrees.add(d);
		}
		public Degree(Iterable<? extends Integer> degrees) {
			this.degrees = new HashSet<Integer>();
			for (int d : degrees) this.degrees.add(d);
		}
		public boolean matches(Polyhedron.Face face) {
			return degrees.contains(face.vertices.size());
		}
	}
	
	public static final class Index extends FacePredicate {
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
		public boolean matches(Polyhedron.Face face) {
			return indices.contains(currentIndex++);
		}
	}
	
	public static final class AtAngle extends FacePredicate {
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
		public boolean matches(Polyhedron.Face face) {
			if (indices.contains(face.index)) return true;
			Point3D c1 = face.center();
			for (Point3D c2 : centers) {
				boolean matches = false;
				double angle = center.angle(c1, c2);
				for (Number a : angles) {
					double diff = Math.abs(angle - a.doubleValue());
					if (diff < 1e-12) matches = true;
				}
				if (!matches) return false;
			}
			indices.add(face.index);
			centers.add(c1);
			return true;
		}
	}
	
	public static final class AdjacentVertexDegree extends FacePredicate {
		private final Set<Integer> degrees;
		public AdjacentVertexDegree(Integer... degrees) {
			this.degrees = new HashSet<Integer>();
			for (int d : degrees) this.degrees.add(d);
		}
		public AdjacentVertexDegree(Iterable<? extends Integer> degrees) {
			this.degrees = new HashSet<Integer>();
			for (int d : degrees) this.degrees.add(d);
		}
		public boolean matches(Polyhedron.Face face) {
			for (Polyhedron.Vertex v : face.vertices) {
				int d = face.parent.getEdges(v).size();
				if (!degrees.contains(d)) return false;
			}
			return true;
		}
	}
	
	public static enum Builder {
		DEGREE ("n", Type.INTS, "only operate on faces with the specified number of edges") {
			@SuppressWarnings("unchecked")
			public FacePredicate build(Object arg) {
				return new Degree((Iterable<? extends Integer>)arg);
			}
		},
		INDEX ("i", Type.INTS, "only operate on faces with the specified indices") {
			@SuppressWarnings("unchecked")
			public FacePredicate build(Object arg) {
				return new Index((Iterable<? extends Integer>)arg);
			}
		},
		AT_ANGLE ("t", Type.REALS, "only operate on faces at the specified angle from each other") {
			@SuppressWarnings("unchecked")
			public FacePredicate build(Object arg) {
				return new AtAngle((Iterable<? extends Number>)arg);
			}
		},
		ADJACENT_VERTEX_DEGREE ("j", Type.INTS, "only operate on faces with vertices of the specified degree") {
			@SuppressWarnings("unchecked")
			public FacePredicate build(Object arg) {
				return new AdjacentVertexDegree((Iterable<? extends Integer>)arg);
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
		public abstract FacePredicate build(Object arg);
		public final boolean ignoresArgument() { return argDataType == Type.VOID; }
		public final Object parseArgument(String s) { return argDataType.parse(s); }
		public final FacePredicate buildFromArgument(String s) { return build(parseArgument(s)); }
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
}