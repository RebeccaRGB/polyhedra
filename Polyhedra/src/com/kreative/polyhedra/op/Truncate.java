package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.Polyhedron.Edge;
import com.kreative.polyhedra.Polyhedron.Face;
import com.kreative.polyhedra.Polyhedron.Vertex;
import com.kreative.polyhedra.PolyhedronOp;

public class Truncate extends PolyhedronOp {
	public static class TruncatedVertex {
		public final Vertex seedVertex;
		public final Edge seedEdge;
		public final Point3D truncatedVertex;
		private TruncatedVertex(Vertex sv, Edge se, Point3D tv) {
			this.seedVertex = sv;
			this.seedEdge = se;
			this.truncatedVertex = tv;
		}
		public boolean equals(Object obj) {
			return (
				(obj instanceof TruncatedVertex)
				&& this.seedVertex.equals(((TruncatedVertex)obj).seedVertex)
				&& this.seedEdge.equals(((TruncatedVertex)obj).seedEdge)
				&& this.truncatedVertex.equals(((TruncatedVertex)obj).truncatedVertex)
			);
		}
		public int hashCode() {
			return Arrays.asList(seedVertex, seedEdge, truncatedVertex).hashCode();
		}
	}
	
	public static enum TruncatedVertexGen {
		FIXED_DISTANCE_FROM_VERTEX {
			public List<TruncatedVertex> createVertices(
				List<Edge> seedEdges,
				Vertex seedVertex,
				double size
			) {
				List<Point3D> points = new ArrayList<Point3D>(seedEdges.size());
				for (Edge seedEdge : seedEdges) {
					Vertex v = Polyhedron.getOppositeVertex(seedEdge, seedVertex);
					if (v != null) points.add(v.point);
				}
				Point3D vertexVector = Point3D.average(points).subtract(seedVertex.point);
				vertexVector = vertexVector.multiply(size / vertexVector.magnitude());
				List<TruncatedVertex> tvs = new ArrayList<TruncatedVertex>(seedEdges.size());
				for (Edge seedEdge : seedEdges) {
					Vertex v = Polyhedron.getOppositeVertex(seedEdge, seedVertex);
					if (v == null) continue;
					Point3D seedEdgeVector = v.point.subtract(seedVertex.point);
					double n = vertexVector.magnitude() * vertexVector.distance(seedEdgeVector);
					double d = seedEdgeVector.magnitude() * vertexVector.dotProduct(seedEdgeVector);
					Point3D truncatedEdgeVector = seedEdgeVector.multiply(n / d);
					Point3D truncatedVertex = truncatedEdgeVector.add(seedVertex.point);
					tvs.add(new TruncatedVertex(seedVertex, seedEdge, truncatedVertex));
				}
				return tvs;
			}
		},
		RELATIVE_DISTANCE_FROM_VERTEX {
			public List<TruncatedVertex> createVertices(
				List<Edge> seedEdges,
				Vertex seedVertex,
				double size
			) {
				List<Point3D> points = new ArrayList<Point3D>(seedEdges.size());
				for (Edge seedEdge : seedEdges) {
					Vertex v = Polyhedron.getOppositeVertex(seedEdge, seedVertex);
					if (v != null) points.add(v.point);
				}
				Point3D vertexVector = Point3D.average(points).subtract(seedVertex.point);
				vertexVector = vertexVector.multiply(size);
				List<TruncatedVertex> tvs = new ArrayList<TruncatedVertex>(seedEdges.size());
				for (Edge seedEdge : seedEdges) {
					Vertex v = Polyhedron.getOppositeVertex(seedEdge, seedVertex);
					if (v == null) continue;
					Point3D seedEdgeVector = v.point.subtract(seedVertex.point);
					double n = vertexVector.magnitude() * vertexVector.distance(seedEdgeVector);
					double d = seedEdgeVector.magnitude() * vertexVector.dotProduct(seedEdgeVector);
					Point3D truncatedEdgeVector = seedEdgeVector.multiply(n / d);
					Point3D truncatedVertex = truncatedEdgeVector.add(seedVertex.point);
					tvs.add(new TruncatedVertex(seedVertex, seedEdge, truncatedVertex));
				}
				return tvs;
			}
		},
		FIXED_DISTANCE_ALONG_EDGE {
			public List<TruncatedVertex> createVertices(
				List<Edge> seedEdges,
				Vertex seedVertex,
				double size
			) {
				List<TruncatedVertex> tvs = new ArrayList<TruncatedVertex>(seedEdges.size());
				for (Edge seedEdge : seedEdges) {
					Vertex v = Polyhedron.getOppositeVertex(seedEdge, seedVertex);
					if (v == null) continue;
					Point3D seedEdgeVector = v.point.subtract(seedVertex.point);
					double m = size / seedEdgeVector.magnitude();
					Point3D truncatedEdgeVector = seedEdgeVector.multiply(m);
					Point3D truncatedVertex = truncatedEdgeVector.add(seedVertex.point);
					tvs.add(new TruncatedVertex(seedVertex, seedEdge, truncatedVertex));
				}
				return tvs;
			}
		},
		RELATIVE_DISTANCE_ALONG_EDGE {
			public List<TruncatedVertex> createVertices(
				List<Edge> seedEdges,
				Vertex seedVertex,
				double size
			) {
				List<TruncatedVertex> tvs = new ArrayList<TruncatedVertex>(seedEdges.size());
				for (Edge seedEdge : seedEdges) {
					Vertex v = Polyhedron.getOppositeVertex(seedEdge, seedVertex);
					if (v == null) continue;
					Point3D seedEdgeVector = v.point.subtract(seedVertex.point);
					Point3D truncatedEdgeVector = seedEdgeVector.multiply(size);
					Point3D truncatedVertex = truncatedEdgeVector.add(seedVertex.point);
					tvs.add(new TruncatedVertex(seedVertex, seedEdge, truncatedVertex));
				}
				return tvs;
			}
		};
		public abstract List<TruncatedVertex> createVertices(
			List<Edge> seedEdges,
			Vertex seedVertex,
			double size
		);
	}
	
	private final Set<Integer> degrees;
	private final TruncatedVertexGen gen;
	private final double size;
	private final Color color;
	
	public Truncate(int[] degrees, TruncatedVertexGen gen, double size, Color color) {
		this.degrees = new HashSet<Integer>();
		if (degrees != null) for (int i : degrees) this.degrees.add(i);
		this.gen = gen;
		this.size = size;
		this.color = color;
	}
	
	public Truncate(Integer[] degrees, TruncatedVertexGen gen, double size, Color color) {
		this.degrees = new HashSet<Integer>();
		if (degrees != null) for (int i : degrees) this.degrees.add(i);
		this.gen = gen;
		this.size = size;
		this.color = color;
	}
	
	public Truncate(
		Iterable<? extends Integer> degrees, TruncatedVertexGen gen, double size, Color color
	) {
		this.degrees = new HashSet<Integer>();
		if (degrees != null) for (int i : degrees) this.degrees.add(i);
		this.gen = gen;
		this.size = size;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vfSize = seed.vertices.size() + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>(seed.edges.size() * 2);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(vfSize);
		List<Color> faceColors = new ArrayList<Color>(vfSize);
		
		Map<Vertex,Map<Edge,Integer>> vertexEdgeMap = new HashMap<Vertex,Map<Edge,Integer>>();
		for (Vertex vertex : seed.vertices) {
			List<Face> seedFaces = seed.getFaces(vertex);
			List<Edge> seedEdges = seed.getEdges(vertex);
			if (degrees.isEmpty() || degrees.contains(seedEdges.size())) {
				Map<Edge,Integer> edgeMap = new HashMap<Edge,Integer>();
				for (TruncatedVertex tv : gen.createVertices(seedEdges, vertex, size)) {
					edgeMap.put(tv.seedEdge, vertices.size());
					vertices.add(tv.truncatedVertex);
				}
				vertexEdgeMap.put(vertex, edgeMap);
				while (!seedEdges.isEmpty()) {
					List<Integer> truncatedFace = new ArrayList<Integer>();
					Edge seedEdge = seedEdges.remove(0);
					truncatedFace.add(edgeMap.get(seedEdge));
					seedEdge = Polyhedron.getNextEdge(seedFaces, seedEdge, vertex);
					while (seedEdges.contains(seedEdge)) {
						seedEdges.remove(seedEdge);
						truncatedFace.add(edgeMap.get(seedEdge));
						seedEdge = Polyhedron.getNextEdge(seedFaces, seedEdge, vertex);
					}
					faces.add(truncatedFace);
					faceColors.add(color);
				}
			} else {
				Map<Edge,Integer> edgeMap = new HashMap<Edge,Integer>();
				for (Edge edge : seedEdges) edgeMap.put(edge, vertices.size());
				vertices.add(vertex.point);
				vertexEdgeMap.put(vertex, edgeMap);
			}
		}
		
		for (Face face : seed.faces) {
			List<Integer> truncatedFace = new ArrayList<Integer>();
			for (int i = 0, n = face.vertices.size(); i < n; i++) {
				Map<Edge,Integer> edgeMap = vertexEdgeMap.get(face.vertices.get(i));
				int pei = edgeMap.get(face.edges.get((i + n - 1) % n));
				int nei = edgeMap.get(face.edges.get(i));
				if (pei != nei) truncatedFace.add(pei);
				truncatedFace.add(nei);
			}
			faces.add(truncatedFace);
			faceColors.add(face.color);
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Truncate parse(String[] args) {
		List<Integer> degrees = null;
		TruncatedVertexGen gen = TruncatedVertexGen.RELATIVE_DISTANCE_ALONG_EDGE;
		double size = (double)1 / (double)3;
		Color color = Color.GRAY;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-n") && argi < args.length) {
				degrees = parseIntList(args[argi++]);
			} else if (arg.equalsIgnoreCase("-s")) {
				gen = TruncatedVertexGen.RELATIVE_DISTANCE_ALONG_EDGE;
				size = (double)1 / (double)3;
			} else if (arg.equalsIgnoreCase("-A")) {
				gen = TruncatedVertexGen.RELATIVE_DISTANCE_ALONG_EDGE;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
				gen = TruncatedVertexGen.FIXED_DISTANCE_ALONG_EDGE;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-H") && argi < args.length) {
				gen = TruncatedVertexGen.RELATIVE_DISTANCE_FROM_VERTEX;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
				gen = TruncatedVertexGen.FIXED_DISTANCE_FROM_VERTEX;
				size = parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				color = parseColor(args[argi++], color);
			} else {
				System.err.println("Options:");
				System.err.println("  -n <ints>   only operate on vertices of the specified degree");
				System.err.println("  -h <real>   truncate at a fixed distance from the original vertices");
				System.err.println("  -H <real>   truncate at a relative distance from the original vertices");
				System.err.println("  -a <real>   truncate at a fixed distance along the original edges");
				System.err.println("  -A <real>   truncate at a relative distance along the original edges");
				System.err.println("  -s          truncate at the trisection points of the original edges");
				System.err.println("  -c <color>  color of faces generated from truncated vertices");
				return null;
			}
		}
		return new Truncate(degrees, gen, size, color);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}