package com.kreative.polyhedra;

import java.awt.Color;
import java.util.*;

public class Polyhedron {
	public static final class Vertex {
		public final Polyhedron parent;
		public final Point3D point;
		public final int index;
		private Vertex(Polyhedron parent, Point3D point, int index) {
			this.parent = parent;
			this.point = point;
			this.index = index;
		}
		public boolean equals(Object obj) {
			return (
				(obj instanceof Vertex)
				&& this.parent == ((Vertex)obj).parent
				&& this.index == ((Vertex)obj).index
			);
		}
		public int hashCode() {
			return index;
		}
	}
	
	public static final class Edge {
		public final Polyhedron parent;
		public final Vertex vertex1;
		public final Vertex vertex2;
		private Edge(Polyhedron parent, Vertex vertex1, Vertex vertex2) {
			this.parent = parent;
			this.vertex1 = vertex1;
			this.vertex2 = vertex2;
		}
		private int minIndex() { return Math.min(vertex1.index, vertex2.index); }
		private int maxIndex() { return Math.max(vertex1.index, vertex2.index); }
		public boolean equals(Object obj) {
			return (
				(obj instanceof Edge)
				&& this.parent == ((Edge)obj).parent
				&& this.minIndex() == ((Edge)obj).minIndex()
				&& this.maxIndex() == ((Edge)obj).maxIndex()
			);
		}
		public int hashCode() {
			return vertex1.index + vertex2.index;
		}
	}
	
	public static final class Face {
		public final Polyhedron parent;
		public final List<Vertex> vertices;
		public final List<Edge> edges;
		public final Color color;
		public final int index;
		private Face(Polyhedron parent, List<Vertex> vertices, List<Edge> edges, Color color, int index) {
			this.parent = parent;
			this.vertices = Collections.unmodifiableList(vertices);
			this.edges = Collections.unmodifiableList(edges);
			this.color = color;
			this.index = index;
		}
		public boolean equals(Object obj) {
			return (
				(obj instanceof Face)
				&& this.parent == ((Face)obj).parent
				&& this.vertices.equals(((Face)obj).vertices)
			);
		}
		public int hashCode() {
			return vertices.hashCode();
		}
	}
	
	public final List<Vertex> vertices;
	public final List<Edge> edges;
	public final List<Face> faces;
	
	public Polyhedron(
		List<? extends Point3D> vertices,
		List<? extends List<? extends Integer>> faces,
		List<? extends Color> faceColors
	) {
		List<Vertex> tmpVertices = new ArrayList<Vertex>();
		List<Edge> tmpEdges = new ArrayList<Edge>();
		List<Face> tmpFaces = new ArrayList<Face>();
		for (int i = 0, n = vertices.size(); i < n; i++) {
			tmpVertices.add(new Vertex(this, vertices.get(i), i));
		}
		for (int i = 0, n = faces.size(); i < n; i++) {
			List<Vertex> faceVertices = new ArrayList<Vertex>();
			List<Edge> faceEdges = new ArrayList<Edge>();
			List<? extends Integer> face = faces.get(i);
			for (int j = 0, m = face.size(); j < m; j++) {
				Vertex v1 = tmpVertices.get(face.get(j) % tmpVertices.size());
				Vertex v2 = tmpVertices.get(face.get((j + 1) % m) % tmpVertices.size());
				Edge e = new Edge(this, v1, v2);
				faceVertices.add(v1);
				faceEdges.add(e);
				if (!tmpEdges.contains(e)) tmpEdges.add(e);
			}
			Color color = faceColors.isEmpty() ? Color.GRAY : faceColors.get(i % faceColors.size());
			tmpFaces.add(new Face(this, faceVertices, faceEdges, color, i));
		}
		this.vertices = Collections.unmodifiableList(tmpVertices);
		this.edges = Collections.unmodifiableList(tmpEdges);
		this.faces = Collections.unmodifiableList(tmpFaces);
	}
	
	public Polyhedron(Polyhedron seed, PointTransform3D tx) {
		List<Vertex> tmpVertices = new ArrayList<Vertex>();
		List<Edge> tmpEdges = new ArrayList<Edge>();
		List<Face> tmpFaces = new ArrayList<Face>();
		for (int i = 0, n = seed.vertices.size(); i < n; i++) {
			Point3D point = seed.vertices.get(i).point;
			if (tx != null) point = tx.transform(point);
			tmpVertices.add(new Vertex(this, point, i));
		}
		for (int i = 0, n = seed.faces.size(); i < n; i++) {
			List<Vertex> faceVertices = new ArrayList<Vertex>();
			List<Edge> faceEdges = new ArrayList<Edge>();
			List<Vertex> face = seed.faces.get(i).vertices;
			for (int j = 0, m = face.size(); j < m; j++) {
				Vertex v1 = tmpVertices.get(face.get(j).index);
				Vertex v2 = tmpVertices.get(face.get((j + 1) % m).index);
				Edge e = new Edge(this, v1, v2);
				faceVertices.add(v1);
				faceEdges.add(e);
				if (!tmpEdges.contains(e)) tmpEdges.add(e);
			}
			Color color = seed.faces.get(i).color;
			tmpFaces.add(new Face(this, faceVertices, faceEdges, color, i));
		}
		this.vertices = Collections.unmodifiableList(tmpVertices);
		this.edges = Collections.unmodifiableList(tmpEdges);
		this.faces = Collections.unmodifiableList(tmpFaces);
	}
}