package com.kreative.polyhedra.op;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.kreative.polyhedra.Polyhedron.Edge;
import com.kreative.polyhedra.Polyhedron.Face;
import com.kreative.polyhedra.Polyhedron.Vertex;
import com.kreative.polyhedra.PolyhedronUtils.Option;
import com.kreative.polyhedra.PolyhedronUtils.Type;

public abstract class VertexPredicate {
	public void reset() {}
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
		public void reset() { currentIndex = 0; }
		public boolean matches(Vertex vertex, List<Edge> edges, List<Face> faces) {
			return indices.contains(currentIndex++);
		}
	}
	
	public static enum Builtin {
		DEGREE ("n", Type.INTS, "only operate on vertices with the specified number of edges") {
			@SuppressWarnings("unchecked")
			public VertexPredicate predicate(Object arg) {
				return new Degree((Iterable<? extends Integer>)arg);
			}
		},
		INDEX ("i", Type.INTS, "only operate on vertices with the specified indices") {
			@SuppressWarnings("unchecked")
			public VertexPredicate predicate(Object arg) {
				return new Index((Iterable<? extends Integer>)arg);
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
		public abstract VertexPredicate predicate(Object arg);
		public final boolean isVoidType() { return argDataType == Type.VOID; }
		public final Object parseArgument(String s) { return argDataType.parse(s); }
		public final VertexPredicate parse(String s) { return predicate(parseArgument(s)); }
		public final Option option(String... mutex) {
			return new Option(flagWithoutDash, argDataType, description, mutex);
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