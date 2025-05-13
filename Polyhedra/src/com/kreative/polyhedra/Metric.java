package com.kreative.polyhedra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public enum Metric {
	VERTEX_MAGNITUDE {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Vertex> iter = p.vertices.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().point.distance(o); }
			};
		}
	},
	EDGE_MIDPOINT_MAGNITUDE {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Edge> iter = p.edges.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().midpoint().distance(o); }
			};
		}
	},
	EDGE_DISTANCE_TO_ORIGIN {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Edge> iter = p.edges.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() {
					Polyhedron.Edge edge = iter.next();
					Point3D p1 = edge.vertex1.point;
					Point3D p2 = edge.vertex2.point;
					return o.distanceToLine(p1, p2);
				}
			};
		}
	},
	FACE_CENTER_MAGNITUDE {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Face> iter = p.faces.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().center().distance(o); }
			};
		}
	},
	FACE_DISTANCE_TO_ORIGIN {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Face> iter = p.faces.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() {
					Polyhedron.Face face = iter.next();
					List<Point3D> points = face.points();
					Point3D center = Point3D.average(points);
					Point3D normal = center.normal(points);
					return o.distanceToPlane(center, normal);
				}
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
	DIHEDRAL_ANGLE {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Edge> iter = p.edges.iterator();
				private final List<Double> angles = new LinkedList<Double>();
				public boolean hasNext() {
					while (angles.isEmpty()) {
						if (iter.hasNext()) queueNext();
						else return false;
					}
					return true;
				}
				public Double next() {
					while (angles.isEmpty()) queueNext();
					return angles.remove(0);
				}
				private void queueNext() {
					Polyhedron.Edge edge = iter.next();
					List<Polyhedron.Face> faces = p.getFaces(edge);
					List<Point3D> normals = new ArrayList<Point3D>();
					for (Polyhedron.Face face : faces) {
						List<Point3D> p = face.points();
						normals.add(Point3D.average(p).normal(p));
					}
					for (int i = 0; i < normals.size(); i++) {
						Point3D ni = normals.get(i);
						for (int j = i + 1; j < normals.size(); j++) {
							Point3D nj = normals.get(j);
							angles.add(180 - ni.angle(nj));
						}
					}
				}
			};
		}
	},
	VERTEX_ANGLE {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Face> iter = p.faces.iterator();
				private final List<Double> angles = new LinkedList<Double>();
				public boolean hasNext() {
					while (angles.isEmpty()) {
						if (iter.hasNext()) queueNext();
						else return false;
					}
					return true;
				}
				public Double next() {
					while (angles.isEmpty()) queueNext();
					return angles.remove(0);
				}
				private void queueNext() {
					Polyhedron.Face face = iter.next();
					for (int i = 0, n = face.vertices.size(); i < n; i++) {
						Point3D vp = face.vertices.get(i).point;
						Point3D np = face.vertices.get((i + 1) % n).point;
						Point3D pp = face.vertices.get((i + n - 1) % n).point;
						angles.add(vp.angle(pp, np));
					}
				}
			};
		}
	},
	X_POSITION {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Vertex> iter = p.vertices.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().point.getX() - o.getX(); }
			};
		}
	},
	Y_POSITION {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Vertex> iter = p.vertices.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().point.getY() - o.getY(); }
			};
		}
	},
	Z_POSITION {
		public Iterator<Double> iterator(final Polyhedron p, final Point3D o) {
			return new Iterator<Double>() {
				private final Iterator<Polyhedron.Vertex> iter = p.vertices.iterator();
				public boolean hasNext() { return iter.hasNext(); }
				public Double next() { return iter.next().point.getZ() - o.getZ(); }
			};
		}
	};
	public abstract Iterator<Double> iterator(Polyhedron p, Point3D origin);
}