package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
					Vertex v = seedEdge.oppositeVertex(seedVertex);
					if (v != null) points.add(v.point);
				}
				Point3D vertexVector = Point3D.average(points).subtract(seedVertex.point).normalize(size);
				List<TruncatedVertex> tvs = new ArrayList<TruncatedVertex>(seedEdges.size());
				for (Edge seedEdge : seedEdges) {
					Vertex v = seedEdge.oppositeVertex(seedVertex); if (v == null) continue;
					Point3D seedEdgeVector = v.point.subtract(seedVertex.point);
					double a = vertexVector.angleRad(seedEdgeVector);
					double h = vertexVector.magnitude() / Math.cos(a);
					Point3D truncatedEdgeVector = seedEdgeVector.normalize(h);
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
					Vertex v = seedEdge.oppositeVertex(seedVertex);
					if (v != null) points.add(v.point);
				}
				Point3D vertexVector = Point3D.average(points).subtract(seedVertex.point).multiply(size);
				List<TruncatedVertex> tvs = new ArrayList<TruncatedVertex>(seedEdges.size());
				for (Edge seedEdge : seedEdges) {
					Vertex v = seedEdge.oppositeVertex(seedVertex); if (v == null) continue;
					Point3D seedEdgeVector = v.point.subtract(seedVertex.point);
					double a = vertexVector.angleRad(seedEdgeVector);
					double h = vertexVector.magnitude() / Math.cos(a);
					Point3D truncatedEdgeVector = seedEdgeVector.normalize(h);
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
					Vertex v = seedEdge.oppositeVertex(seedVertex); if (v == null) continue;
					Point3D seedEdgeVector = v.point.subtract(seedVertex.point);
					Point3D truncatedEdgeVector = seedEdgeVector.normalize(size);
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
					Vertex v = seedEdge.oppositeVertex(seedVertex); if (v == null) continue;
					Point3D seedEdgeVector = v.point.subtract(seedVertex.point);
					Point3D truncatedEdgeVector = seedEdgeVector.multiply(size);
					Point3D truncatedVertex = truncatedEdgeVector.add(seedVertex.point);
					tvs.add(new TruncatedVertex(seedVertex, seedEdge, truncatedVertex));
				}
				return tvs;
			}
		},
		REGULAR {
			public List<TruncatedVertex> createVertices(
				List<Edge> seedEdges,
				Vertex seedVertex,
				double size
			) {
				List<TruncatedVertex> tvs = new ArrayList<TruncatedVertex>(seedEdges.size());
				for (int i = 0, n = seedEdges.size(); i < n; i++) {
					Vertex cv = seedEdges.get(i).oppositeVertex(seedVertex); if (cv == null) continue;
					Vertex pv = seedEdges.get((i + n - 1) % n).oppositeVertex(seedVertex);
					Vertex nv = seedEdges.get((i + 1) % n).oppositeVertex(seedVertex);
					double pa = (pv != null) ? seedVertex.point.angleRad(pv.point, cv.point) : 0;
					double na = (nv != null) ? seedVertex.point.angleRad(nv.point, cv.point) : 0;
					double ps = (pv != null) ? (1 / (2 + 2 * Math.sin(pa / 2))) : 0;
					double ns = (nv != null) ? (1 / (2 + 2 * Math.sin(na / 2))) : 0;
					size = (ps == 0) ? ns : (ns == 0) ? ps : ((ps + ns) / 2);
					Point3D seedEdgeVector = cv.point.subtract(seedVertex.point);
					Point3D truncatedEdgeVector = seedEdgeVector.multiply(size);
					Point3D truncatedVertex = truncatedEdgeVector.add(seedVertex.point);
					tvs.add(new TruncatedVertex(seedVertex, seedEdges.get(i), truncatedVertex));
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
	
	private final List<VertexPredicate> predicates;
	private final TruncatedVertexGen gen;
	private final double size;
	private final Color color;
	
	public Truncate(List<VertexPredicate> predicates, TruncatedVertexGen gen, double size, Color color) {
		this.predicates = predicates;
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
		VertexPredicate.reset(predicates, seed);
		for (Vertex vertex : seed.vertices) {
			List<Face> seedFaces = seed.getFaces(vertex);
			List<Edge> seedEdges = seed.getEdges(vertex);
			if (VertexPredicate.matches(predicates, vertex, seedEdges, seedFaces)) {
				Map<Edge,Integer> edgeMap = new HashMap<Edge,Integer>();
				for (TruncatedVertex tv : gen.createVertices(seedEdges, vertex, size)) {
					edgeMap.put(tv.seedEdge, vertices.size());
					vertices.add(tv.truncatedVertex);
				}
				vertexEdgeMap.put(vertex, edgeMap);
				while (!seedEdges.isEmpty()) {
					List<Integer> truncatedFace = new ArrayList<Integer>();
					for (Polyhedron.Edge seedEdge : seed.getOrderedEdges(vertex, seedEdges, seedFaces)) {
						truncatedFace.add(edgeMap.get(seedEdge));
						seedEdges.remove(seedEdge);
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
	
	public static class Factory extends PolyhedronOp.Factory<Truncate> {
		public String name() { return "Truncate"; }
		
		public Truncate parse(String[] args) {
			List<VertexPredicate> predicates = new ArrayList<VertexPredicate>();
			VertexPredicate.Builder predtmp;
			TruncatedVertexGen gen = TruncatedVertexGen.REGULAR;
			double size = 0;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if ((predtmp = VertexPredicate.Builder.forFlag(arg)) != null && (predtmp.ignoresArgument() || argi < args.length)) {
					// -n -i -t -j
					predicates.add(predtmp.buildFromArgument(predtmp.ignoresArgument() ? null : args[argi++]));
				} else if (arg.equals("-s")) {
					gen = TruncatedVertexGen.RELATIVE_DISTANCE_ALONG_EDGE;
					size = (double)1 / (double)3;
				} else if (arg.equals("-A") && argi < args.length) {
					gen = TruncatedVertexGen.RELATIVE_DISTANCE_ALONG_EDGE;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-a") && argi < args.length) {
					gen = TruncatedVertexGen.FIXED_DISTANCE_ALONG_EDGE;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-H") && argi < args.length) {
					gen = TruncatedVertexGen.RELATIVE_DISTANCE_FROM_VERTEX;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-h") && argi < args.length) {
					gen = TruncatedVertexGen.FIXED_DISTANCE_FROM_VERTEX;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-r")) {
					gen = TruncatedVertexGen.REGULAR;
					size = 0;
				} else if (arg.equals("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					return null;
				}
			}
			return new Truncate(predicates, gen, size, color);
		}
		
		public Option[] options() {
			List<Option> options = new ArrayList<Option>();
			for (VertexPredicate.Builder bi : VertexPredicate.Builder.values()) options.add(bi.option()); // nitj
			options.add(new Option("h", Type.REAL, "truncate at a fixed distance from the original vertices", "H","a","A","r","s"));
			options.add(new Option("H", Type.REAL, "truncate at a relative distance from the original vertices", "h","a","A","r","s"));
			options.add(new Option("a", Type.REAL, "truncate at a fixed distance along the original edges", "h","H","A","r","s"));
			options.add(new Option("A", Type.REAL, "truncate at a relative distance along the original edges", "h","H","a","r","s"));
			options.add(new Option("r", Type.VOID, "attempt to create regular faces (not always possible)", "h","H","a","A","s"));
			options.add(new Option("s", Type.VOID, "truncate at the trisection points of the original edges", "h","H","a","A","r"));
			options.add(new Option("c", Type.COLOR, "color of faces generated from truncated vertices"));
			return options.toArray(new Option[options.size()]);
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}