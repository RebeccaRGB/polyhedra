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
	private final EdgeVertexGen evgen;
	
	public Ortho(FaceVertexGen fvgen, EdgeVertexGen evgen) {
		this.fvgen = fvgen;
		this.evgen = evgen;
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
			vertices.add(evgen.createVertex(seed, seedVertices, null, null, e, e.midpoint()));
		}
		
		int faceStartIndex = vertices.size();
		for (Polyhedron.Face f : seed.faces) {
			vertices.add(fvgen.createVertex(seed, seedVertices, f, f.points()));
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
			FaceVertexGen fvgen = new FaceVertexGen.AverageMagnitudeOffset(0);
			EdgeVertexGen evgen = new EdgeVertexGen.AverageMagnitudeOffset(0);
			FaceVertexGen.Builder fvtmp;
			EdgeVertexGen.Builder evtmp;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-s")) {
					fvgen = new FaceVertexGen.FaceOffset(0);
					evgen = new EdgeVertexGen.FaceOffset(0);
				} else if ((fvtmp = FaceVertexGen.Builder.forFlag(arg)) != null && (fvtmp.ignoresArgument() || argi < args.length)) {
					fvgen = fvtmp.buildFromArgument(fvtmp.ignoresArgument() ? null : args[argi++]);
				} else if ((evtmp = EdgeVertexGen.Builder.forFlag(arg)) != null && (evtmp.ignoresArgument() || argi < args.length)) {
					evgen = evtmp.buildFromArgument(evtmp.ignoresArgument() ? null : args[argi++]);
				} else {
					return null;
				}
			}
			return new Ortho(fvgen, evgen);
		}
		
		public Option[] options() {
			return new Option[] {
				FaceVertexGen.Builder.FACE_OFFSET.option("s"),
				FaceVertexGen.Builder.MAX_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.Builder.AVERAGE_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.Builder.FACE_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.Builder.MAX_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.Builder.AVERAGE_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.Builder.EDGE_MAGNITUDE_OFFSET.option("s"),
				new Option(
					"s", Type.VOID, "create new vertices at centers of original faces (strict mode)",
					FaceVertexGen.Builder.allOptionMutexes(EdgeVertexGen.Builder.allOptionMutexes())
				),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}