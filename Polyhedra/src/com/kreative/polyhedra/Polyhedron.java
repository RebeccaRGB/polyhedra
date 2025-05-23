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
		public double length() {
			return vertex1.point.distance(vertex2.point);
		}
		public Point3D midpoint() {
			return vertex1.point.midpoint(vertex2.point);
		}
		public Vertex oppositeVertex(Vertex v) {
			return getOppositeVertex(this, v);
		}
		public Point3D partition(double a, double b) {
			return vertex1.point.partition(vertex2.point, a, b);
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
		public Point3D center() {
			return Point3D.average(points());
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
		public List<Point3D> points() {
			List<Point3D> points = new ArrayList<Point3D>(vertices.size());
			for (Vertex v : vertices) points.add(v.point);
			return points;
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
		boolean reverse = (tx != null) && tx.isReflection();
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
			if (reverse) {
				face = new ArrayList<Vertex>(face);
				Collections.reverse(face);
			}
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
	
	public Point3D center() {
		return Point3D.average(points());
	}
	
	public List<Point3D> points() {
		List<Point3D> points = new ArrayList<Point3D>(vertices.size());
		for (Vertex v : vertices) points.add(v.point);
		return points;
	}
	
	/** Returns a new list containing the edges adjacent to the specified vertex. */
	public List<Edge> getEdges(Vertex v) {
		List<Edge> edges = new ArrayList<Edge>();
		for (Edge edge : this.edges) {
			if (edge.vertex1.equals(v) || edge.vertex2.equals(v)) {
				edges.add(edge);
			}
		}
		return edges;
	}
	
	/** Returns a new list containing the faces adjacent to the specified vertex. */
	public List<Face> getFaces(Vertex v) {
		List<Face> faces = new ArrayList<Face>();
		for (Face face : this.faces) {
			if (face.vertices.contains(v)) {
				faces.add(face);
			}
		}
		return faces;
	}
	
	/** Returns a new list containing the faces adjacent to the specified edge. */
	public List<Face> getFaces(Edge e) {
		List<Face> faces = new ArrayList<Face>();
		for (Face face : this.faces) {
			if (face.edges.contains(e)) {
				faces.add(face);
			}
		}
		return faces;
	}
	
	/**
	 * Returns a new list containing the faces adjacent to the specified edge
	 * but excluding the specified face. Under normal circumstances the size of
	 * the returned list will be 1. Degenerate polyhedra may result in a list of
	 * size other than 1.
	 */
	public List<Face> getOppositeFaces(Edge e, Face f) {
		List<Face> faces = new ArrayList<Face>();
		for (Face face : this.faces) {
			if (face.equals(f)) continue;
			if (face.edges.contains(e)) faces.add(face);
		}
		return faces;
	}
	
	/**
	 * Returns a new list containing the edges adjacent to the specified vertex
	 * in order as determined by the winding order of the specified faces.
	 * If <code>edges</code> is null, a new list as created by <code>getEdges(v)</code> is used.
	 * If <code>faces</code> is null, a new list as created by <code>getFaces(v)</code> is used.
	 */
	public List<Edge> getOrderedEdges(Vertex v, List<Edge> edges, List<Face> faces) {
		if (faces == null) faces = getFaces(v);
		if (edges == null) edges = getEdges(v);
		List<Edge> orderedEdges = new ArrayList<Edge>();
		Edge currentEdge = edges.isEmpty() ? null : edges.get(0);
		while (currentEdge != null && !orderedEdges.contains(currentEdge)) {
			orderedEdges.add(currentEdge);
			currentEdge = getNextEdge(faces, currentEdge, v);
		}
		return orderedEdges;
	}
	
	/**
	 * Returns a new list containing the faces adjacent to the specified vertex
	 * in order as determined by the winding order of the specified faces.
	 * If <code>faces</code> is null, a new list as created by <code>getFaces(v)</code> is used.
	 */
	public List<Face> getOrderedFaces(Vertex v, List<Face> faces) {
		if (faces == null) faces = getFaces(v);
		List<Face> orderedFaces = new ArrayList<Face>();
		Face currentFace = faces.isEmpty() ? null : faces.get(0);
		while (currentFace != null && !orderedFaces.contains(currentFace)) {
			orderedFaces.add(currentFace);
			currentFace = getNextFace(faces, currentFace, v);
		}
		return orderedFaces;
	}
	
	/** Given an edge and a vertex on that edge, returns the other vertex on that edge. */
	public static Vertex getOppositeVertex(Edge e, Vertex v) {
		if (e.vertex1.equals(v)) return e.vertex2;
		if (e.vertex2.equals(v)) return e.vertex1;
		return null;
	}
	
	/** 
	 * Given a vertex, an edge, and a list of faces connected to the given vertex, returns
	 * the next edge connected to that vertex as determined by the winding order of the faces.
	 */
	public static Edge getNextEdge(List<Face> faces, Edge e, Vertex v) {
		for (Face f : faces) {
			int fi = f.edges.indexOf(e);
			if (fi < 0) continue;
			int fn = f.edges.size();
			Edge fpe = f.edges.get((fi + fn - 1) % fn);
			if (fpe.vertex1.equals(v) || fpe.vertex2.equals(v)) return fpe;
		}
		return null;
	}
	
	/**
	 * Given a vertex, a face, and a list of faces connected to the given vertex, returns
	 * the next face connected to that vertex as determined by the winding order of the faces.
	 */
	public static Face getNextFace(List<Face> faces, Face f, Vertex v) {
		int fi = f.vertices.indexOf(v);
		if (fi < 0) return null;
		int fn = f.vertices.size();
		Vertex fpv = f.vertices.get((fi + fn - 1) % fn);
		for (Face g : faces) {
			int gi = g.vertices.indexOf(v);
			if (gi < 0) continue;
			int gn = g.vertices.size();
			Vertex gnv = g.vertices.get((gi + 1) % gn);
			if (fpv.equals(gnv)) return g;
		}
		return null;
	}
}