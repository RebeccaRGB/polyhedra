package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Ortho extends PolyhedronOp {
	private final FaceVertexGen fvgen;
	private final Object fvarg;
	private final EdgeVertexGen evgen;
	private final Object evarg;
	
	public Ortho(FaceVertexGen fvgen, Object fvarg, EdgeVertexGen evgen, Object evarg) {
		this.fvgen = fvgen;
		this.fvarg = fvarg;
		this.evgen = evgen;
		this.evarg = evarg;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vefSize = seed.vertices.size() + seed.edges.size() + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>(vefSize);
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		
		List<Point3D> seedVertices = seed.points();
		vertices.addAll(seedVertices);
		
		int edgeStartIndex = vertices.size();
		for (Polyhedron.Edge e : seed.edges) {
			vertices.add(evgen.createVertex(seed, seedVertices, null, null, e, e.midpoint(), evarg));
		}
		
		int faceStartIndex = vertices.size();
		for (Polyhedron.Face f : seed.faces) {
			vertices.add(fvgen.createVertex(seed, seedVertices, f, f.points(), fvarg));
			int fi = faceStartIndex + f.index;
			for (int i = 0, n = f.vertices.size(); i < n; i++) {
				int vi = f.vertices.get(i).index;
				int nei = edgeStartIndex + seed.edges.indexOf(f.edges.get(i));
				int pei = edgeStartIndex + seed.edges.indexOf(f.edges.get((i + n - 1) % n));
				faces.add(Arrays.asList(fi, pei, vi, nei));
				faceColors.add(f.color);
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Ortho> {
		public String name() { return "Ortho"; }
		
		public Ortho parse(String[] args) {
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
					return null;
				}
			}
			return new Ortho(fvgen, fvarg, evgen, evarg);
		}
		
		public Option[] options() {
			return new Option[] {
				FaceVertexGen.FACE_OFFSET.option("s"),
				FaceVertexGen.MAX_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.FACE_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.MAX_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.EDGE_MAGNITUDE_OFFSET.option("s"),
				new Option("s", Type.VOID, "create new vertices at centers of original faces (strict mode)", FaceVertexGen.allOptionMutexes(EdgeVertexGen.allOptionMutexes())),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}