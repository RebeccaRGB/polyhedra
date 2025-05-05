package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class RemoveVertices extends PolyhedronOp {
	private final List<VertexPredicate> predicates;
	private final Color color;
	
	public RemoveVertices(List<VertexPredicate> predicates, Color color) {
		this.predicates = predicates;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		if (predicates == null || predicates.isEmpty()) return seed;
		
		List<Point3D> vertices = new ArrayList<Point3D>();
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		
		// Get indices of vertices to be removed.
		Set<Integer> indices = new HashSet<Integer>();
		for (VertexPredicate p : predicates) p.reset();
		for (Polyhedron.Vertex v : seed.vertices) {
			List<Polyhedron.Face> seedFaces = seed.getFaces(v);
			List<Polyhedron.Edge> seedEdges = seed.getEdges(v);
			boolean matches = true;
			for (VertexPredicate p : predicates) {
				if (!p.matches(v, seedEdges, seedFaces)) {
					matches = false;
					break;
				}
			}
			if (matches) indices.add(v.index);
		}
		
		// Group together removed vertices that were once connected. (vertexGroupMap)
		// Create map of old vertex indices to new vertex indices. (vertexIndexMap)
		// Add retained vertices to new polyhedron. (vertices)
		Map<Integer,Integer> vertexGroupMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> vertexIndexMap = new HashMap<Integer,Integer>();
		for (Polyhedron.Vertex v : seed.vertices) {
			if (indices.contains(v.index)) {
				Integer vgroup = vertexGroupMap.get(v.index);
				if (vgroup == null) {
					vertexGroupMap.put(v.index, (vgroup = v.index));
				}
				for (Polyhedron.Edge e : seed.getEdges(v)) {
					Polyhedron.Vertex w = e.oppositeVertex(v);
					if (indices.contains(w.index)) {
						Integer wgroup = vertexGroupMap.get(w.index);
						if (wgroup == null || wgroup > vgroup) {
							vertexGroupMap.put(w.index, vgroup);
						}
					}
				}
			} else {
				vertexIndexMap.put(v.index, vertices.size());
				vertices.add(v.point);
			}
		}
		
		// Remember directed edges no longer connected by removed vertices. (disconnectedEdges)
		// Recreate existing faces without removed vertices. (faces, faceColors)
		Map<Integer,Map<Integer,Set<Integer>>> disconnectedEdges = new HashMap<Integer,Map<Integer,Set<Integer>>>();
		for (Polyhedron.Face f : seed.faces) {
			Map<Integer,Integer> disconnectedIndices = new HashMap<Integer,Integer>();
			List<Integer> face = new ArrayList<Integer>();
			for (Polyhedron.Vertex v : f.vertices) {
				if (indices.contains(v.index)) {
					disconnectedIndices.put(vertexGroupMap.get(v.index), face.size());
				} else {
					face.add(vertexIndexMap.get(v.index));
				}
			}
			int n = face.size();
			if (n >= 2) {
				for (Map.Entry<Integer,Integer> e : disconnectedIndices.entrySet()) {
					int i = face.get(e.getValue() % n);
					int j = face.get((e.getValue() + n - 1) % n);
					Map<Integer,Set<Integer>> m = disconnectedEdges.get(e.getKey());
					if (m == null) {
						m = new HashMap<Integer,Set<Integer>>();
						disconnectedEdges.put(e.getKey(), m);
					}
					Set<Integer> s = m.get(i);
					if (s == null) {
						s = new HashSet<Integer>();
						m.put(i, s);
					}
					s.add(j);
				}
			}
			if (n >= 3) {
				faces.add(face);
				faceColors.add(f.color);
			}
		}
		
		// Create new faces connecting disconnected edges.
		for (Map<Integer,Set<Integer>> edges : disconnectedEdges.values()) {
			Iterator<Integer> iter = edges.keySet().iterator();
			if (iter.hasNext()) {
				List<Integer> prefix = Arrays.asList(iter.next());
				List<Integer> face = findPath(edges, prefix);
				if (face != null && face.size() >= 3) {
					faces.add(face);
					faceColors.add(color);
				}
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	private static List<Integer> findPath(Map<Integer,Set<Integer>> edges, List<Integer> prefix) {
		Integer currentIndex = prefix.get(prefix.size() - 1);
		Set<Integer> nextIndices = edges.get(currentIndex);
		if (nextIndices != null) {
			if (prefix.size() == edges.size()) {
				if (nextIndices.contains(prefix.get(0))) return prefix;
			} else {
				for (Integer nextIndex : nextIndices) {
					if (prefix.contains(nextIndex)) continue;
					List<Integer> newPrefix = new ArrayList<Integer>(prefix);
					newPrefix.add(nextIndex);
					newPrefix = findPath(edges, newPrefix);
					if (newPrefix != null) return newPrefix;
				}
			}
		}
		return null;
	}
	
	public static class Factory extends PolyhedronOp.Factory<RemoveVertices> {
		public String name() { return "RemoveVertices"; }
		
		public RemoveVertices parse(String[] args) {
			List<VertexPredicate> predicates = new ArrayList<VertexPredicate>();
			VertexPredicate.Builder predtmp;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if ((predtmp = VertexPredicate.Builder.forFlagIgnoreCase(arg)) != null && (predtmp.ignoresArgument() || argi < args.length)) {
					predicates.add(predtmp.buildFromArgument(predtmp.ignoresArgument() ? null : args[argi++]));
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					return null;
				}
			}
			return new RemoveVertices(predicates, color);
		}
		
		public Option[] options() {
			List<Option> options = new ArrayList<Option>();
			for (VertexPredicate.Builder bi : VertexPredicate.Builder.values()) options.add(bi.option());
			options.add(new Option("c", Type.COLOR, "color of replacement faces"));
			return options.toArray(new Option[options.size()]);
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}