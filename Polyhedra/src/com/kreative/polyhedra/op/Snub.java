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
	private final GyroVertexGen gvgen;
	private final Object gvarg;
	private final EdgeVertexGen evgen;
	private final Object evarg;
	private final Color color;
	
	public Snub(
		GyroVertexGen gvgen, Object gvarg,
		EdgeVertexGen evgen, Object evarg,
		Color color
	) {
		this.gvgen = gvgen;
		this.gvarg = gvarg;
		this.evgen = evgen;
		this.evarg = evarg;
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
				Point3D v = gvgen.createVertex(seed, seedVertices, f, fv, e, e.vertex1.point, gvarg);
				vertices.add(evgen.createVertex(seed, seedVertices, f, fv, e, v, evarg));
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
				for (Polyhedron.Face af : seed.getOppositeFaces(prevEdge, f)) {
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
				for (Polyhedron.Face seedFace : seed.getOrderedFaces(vertex, seedFaces)) {
					int sfesi = edgeStartIndexMap.get(seedFace.index);
					int sfei = sfesi + seedFace.vertices.indexOf(vertex);
					truncatedFace.add(sfei);
					seedFaces.remove(seedFace);
				}
				faces.add(truncatedFace);
				faceColors.add(color);
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Snub parse(String[] args) {
		GyroVertexGen gvgen = GyroVertexGen.RELATIVE_DISTANCE_FROM_MIDPOINT;
		EdgeVertexGen evgen = EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET;
		GyroVertexGen gvtmp;
		EdgeVertexGen evtmp;
		Object gvarg = 1.0 / 3.0;
		Object evarg = 0;
		Color color = Color.GRAY;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-s")) {
				gvgen = GyroVertexGen.RELATIVE_DISTANCE_FROM_MIDPOINT;
				evgen = EdgeVertexGen.FACE_OFFSET;
				gvarg = 1.0 / 3.0;
				evarg = 0;
			} else if ((gvtmp = GyroVertexGen.forFlag(arg)) != null && (gvtmp.isVoidType() || argi < args.length)) {
				gvgen = gvtmp;
				gvarg = gvtmp.isVoidType() ? null : gvtmp.parseArgument(args[argi++]);
			} else if ((evtmp = EdgeVertexGen.forFlagIgnoreCase(arg)) != null && (evtmp.isVoidType() || argi < args.length)) {
				evgen = evtmp;
				evarg = evtmp.isVoidType() ? null : evtmp.parseArgument(args[argi++]);
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				color = parseColor(args[argi++], color);
			} else {
				printOptions(options());
				return null;
			}
		}
		return new Snub(gvgen, gvarg, evgen, evarg, color);
	}
	
	public static Option[] options() {
		return new Option[] {
			GyroVertexGen.FIXED_DISTANCE_FROM_VERTEX.option("s"),
			GyroVertexGen.RELATIVE_DISTANCE_FROM_VERTEX.option("s"),
			GyroVertexGen.FIXED_ANGLE_FROM_VERTEX.option("s"),
			GyroVertexGen.FIXED_DISTANCE_FROM_MIDPOINT.option("s"),
			GyroVertexGen.RELATIVE_DISTANCE_FROM_MIDPOINT.option("s"),
			GyroVertexGen.FIXED_ANGLE_FROM_MIDPOINT.option("s"),
			EdgeVertexGen.FACE_OFFSET.option("s"),
			EdgeVertexGen.MAX_MAGNITUDE_OFFSET.option("s"),
			EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"),
			EdgeVertexGen.EDGE_MAGNITUDE_OFFSET.option("s"),
			EdgeVertexGen.VERTEX_MAGNITUDE_OFFSET.option("s"),
			new Option(
				"s", Type.VOID, "create new vertices along original edges (strict mode)",
				GyroVertexGen.allOptionMutexes(EdgeVertexGen.allOptionMutexes())
			),
			new Option("c", Type.COLOR, "color of new faces generated from original vertices"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}