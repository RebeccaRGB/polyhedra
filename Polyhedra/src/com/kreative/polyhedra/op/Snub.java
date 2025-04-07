package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Snub extends PolyhedronOp {
	private final EdgeVertexGen gen;
	private final double size;
	private final Color color;
	
	public Snub(EdgeVertexGen gen, double size, Color color) {
		this.gen = gen;
		this.size = size;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.edges.size() * 2);
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		
		List<Point3D> seedVertices = seed.points();
		
		Map<Integer,Integer> edgeStartIndexMap = new HashMap<Integer,Integer>();
		Map<Integer,List<Point3D>> faceVertexMap = new HashMap<Integer,List<Point3D>>();
		for (Polyhedron.Face f : seed.faces) {
			edgeStartIndexMap.put(f.index, vertices.size());
			List<Point3D> fv = f.points();
			faceVertexMap.put(f.index, fv);
			List<Integer> faceVertexIndices = new ArrayList<Integer>(f.edges.size());
			for (Polyhedron.Edge e : f.edges) {
				faceVertexIndices.add(vertices.size());
				vertices.add(gen.createVertex(seed, seedVertices, f, fv, e, e.partition(1, 2), size));
			}
			faces.add(faceVertexIndices);
			faceColors.add(f.color);
		}
		
		for (Polyhedron.Face f : seed.faces) {
			int edgeStartIndex = edgeStartIndexMap.get(f.index);
			for (int i = 0, n = f.vertices.size(); i < n; i++) {
				int nei = edgeStartIndex + i;
				int pei = edgeStartIndex + ((i + n - 1) % n);
				Polyhedron.Edge prevEdge = f.edges.get((i + n - 1) % n);
				List<Polyhedron.Face> adjacentFaces = seed.getFaces(prevEdge);
				adjacentFaces.remove(f);
				for (Polyhedron.Face af : adjacentFaces) {
					int afesi = edgeStartIndexMap.get(af.index);
					int afei = afesi + af.edges.indexOf(prevEdge);
					faces.add(Arrays.asList(pei, afei, nei));
					faceColors.add(f.color);
				}
			}
		}
		
		for (Polyhedron.Vertex vertex : seed.vertices) {
			List<Polyhedron.Face> seedFaces = seed.getFaces(vertex);
			while (!seedFaces.isEmpty()) {
				List<Integer> truncatedFace = new ArrayList<Integer>();
				Polyhedron.Face seedFace = seedFaces.get(0);
				while (seedFace != null) {
					seedFaces.remove(seedFace);
					int sfesi = edgeStartIndexMap.get(seedFace.index);
					int sfei = sfesi + seedFace.vertices.indexOf(vertex);
					truncatedFace.add(sfei);
					seedFace = Polyhedron.getNextFace(seedFaces, seedFace, vertex);
				}
				faces.add(truncatedFace);
				faceColors.add(color);
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Snub parse(String[] args) {
		EdgeVertexGen gen = EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET;
		double size = 0;
		Color color = Color.GRAY;
		EdgeVertexGen tmp;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-s")) {
				gen = EdgeVertexGen.FACE_OFFSET;
				size = 0;
			} else if ((tmp = EdgeVertexGen.forFlagIgnoreCase(arg)) != null && (tmp.isVoidType() || argi < args.length)) {
				gen = tmp;
				size = tmp.isVoidType() ? 0 : parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				color = parseColor(args[argi++], color);
			} else {
				printOptions(options());
				return null;
			}
		}
		return new Snub(gen, size, color);
	}
	
	public static Option[] options() {
		return new Option[] {
			EdgeVertexGen.FACE_OFFSET.option("s"),
			EdgeVertexGen.MAX_MAGNITUDE_OFFSET.option("s"),
			EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"),
			EdgeVertexGen.EDGE_MAGNITUDE_OFFSET.option("s"),
			EdgeVertexGen.VERTEX_MAGNITUDE_OFFSET.option("s"),
			new Option("s", Type.VOID, "create new vertices along original edges (strict mode)", EdgeVertexGen.allOptionMutexes()),
			new Option("c", Type.COLOR, "color of new faces generated from original vertices"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}