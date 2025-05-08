package com.kreative.polyhedra;

import java.util.Iterator;

public enum Metric {
	VERTEX_MAGNITUDE {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Vertex> iter = p.vertices.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().point.subtract(o).magnitude(); }
			};
		}
	},
	EDGE_MAGNITUDE {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Edge> iter = p.edges.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().midpoint().subtract(o).magnitude(); }
			};
		}
	},
	FACE_MAGNITUDE {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Face> iter = p.faces.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().center().subtract(o).magnitude(); }
			};
		}
	},
	EDGE_LENGTH {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Edge> iter = p.edges.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().length(); }
			};
		}
	},
	X_POSITION {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Vertex> iter = p.vertices.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().point.subtract(o).getX(); }
			};
		}
	},
	Y_POSITION {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Vertex> iter = p.vertices.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().point.subtract(o).getY(); }
			};
		}
	},
	Z_POSITION {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Vertex> iter = p.vertices.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().point.subtract(o).getZ(); }
			};
		}
	};
	public abstract Iterator<Double> iterator(Polyhedron p, Point3D origin);
}