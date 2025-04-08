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

public class Gyro extends PolyhedronOp {
	private final FaceVertexGen fvgen;
	private final Object fvarg;
	private final EdgeVertexGen evgen;
	private final Object evarg;
	
	public Gyro(FaceVertexGen fvgen, Object fvarg, EdgeVertexGen evgen, Object evarg) {
		this.fvgen = fvgen;
		this.fvarg = fvarg;
		this.evgen = evgen;
		this.evarg = evarg;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vefSize = seed.vertices.size() + (seed.edges.size() * 2) + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>(vefSize);
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		
		List<Point3D> seedVertices = seed.points();
		vertices.addAll(seedVertices);
		
		Map<Integer,Integer> edgeStartIndexMap = new HashMap<Integer,Integer>();
		Map<Integer,List<Point3D>> faceVertexMap = new HashMap<Integer,List<Point3D>>();
		for (Polyhedron.Face f : seed.faces) {
			edgeStartIndexMap.put(f.index, vertices.size());
			List<Point3D> fv = f.points();
			faceVertexMap.put(f.index, fv);
			for (Polyhedron.Edge e : f.edges) {
				vertices.add(evgen.createVertex(seed, seedVertices, f, fv, e, e.partition(2, 1), evarg));
			}
		}
		
		int faceStartIndex = vertices.size();
		for (Polyhedron.Face f : seed.faces) {
			List<Point3D> faceVertices = faceVertexMap.get(f.index);
			vertices.add(fvgen.createVertex(seed, seedVertices, f, faceVertices, fvarg));
			int fi = faceStartIndex + f.index;
			int edgeStartIndex = edgeStartIndexMap.get(f.index);
			for (int i = 0, n = f.vertices.size(); i < n; i++) {
				int vi = f.vertices.get(i).index;
				int nei = edgeStartIndex + i;
				int pei = edgeStartIndex + ((i + n - 1) % n);
				Polyhedron.Edge nextEdge = f.edges.get(i);
				for (Polyhedron.Face af : seed.getOppositeFaces(nextEdge, f)) {
					int afesi = edgeStartIndexMap.get(af.index);
					int afei = afesi + af.edges.indexOf(nextEdge);
					faces.add(Arrays.asList(fi, pei, vi, afei, nei));
					faceColors.add(f.color);
				}
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Gyro parse(String[] args) {
		FaceVertexGen fvgen = FaceVertexGen.AVERAGE_MAGNITUDE_OFFSET;
		EdgeVertexGen evgen = EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET;
		FaceVertexGen fvtmp;
		EdgeVertexGen evtmp;
		Object fvarg = 0;
		Object evarg = 0;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-s")) {
				fvgen = FaceVertexGen.FACE_OFFSET;
				evgen = EdgeVertexGen.FACE_OFFSET;
				fvarg = 0;
				evarg = 0;
			} else if ((fvtmp = FaceVertexGen.forFlag(arg)) != null && (fvtmp.isVoidType() || argi < args.length)) {
				fvgen = fvtmp;
				fvarg = fvtmp.isVoidType() ? null : fvtmp.parseArgument(args[argi++]);
			} else if ((evtmp = EdgeVertexGen.forFlag(arg)) != null && (evtmp.isVoidType() || argi < args.length)) {
				evgen = evtmp;
				evarg = evtmp.isVoidType() ? null : evtmp.parseArgument(args[argi++]);
			} else {
				printOptions(options());
				return null;
			}
		}
		return new Gyro(fvgen, fvarg, evgen, evarg);
	}
	
	public static Option[] options() {
		return new Option[] {
			FaceVertexGen.FACE_OFFSET.option("s"),
			FaceVertexGen.MAX_MAGNITUDE_OFFSET.option("s"),
			FaceVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"),
			FaceVertexGen.FACE_MAGNITUDE_OFFSET.option("s"),
			EdgeVertexGen.FACE_OFFSET.option("s"),
			EdgeVertexGen.MAX_MAGNITUDE_OFFSET.option("s"),
			EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"),
			EdgeVertexGen.EDGE_MAGNITUDE_OFFSET.option("s"),
			EdgeVertexGen.VERTEX_MAGNITUDE_OFFSET.option("s"),
			new Option("s", Type.VOID, "create new vertices at centers of original faces (strict mode)", FaceVertexGen.allOptionMutexes(EdgeVertexGen.allOptionMutexes())),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}