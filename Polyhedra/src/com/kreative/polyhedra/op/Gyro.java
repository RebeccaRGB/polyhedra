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
	private final GyroVertexGen gvgen;
	private final EdgeVertexGen evgen;
	
	public Gyro(FaceVertexGen fvgen, GyroVertexGen gvgen, EdgeVertexGen evgen) {
		this.fvgen = fvgen;
		this.gvgen = gvgen;
		this.evgen = evgen;
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
				Point3D v = gvgen.createVertex(seed, seedVertices, f, fv, e, e.vertex2.point);
				vertices.add(evgen.createVertex(seed, seedVertices, f, fv, e, v));
			}
		}
		
		int faceStartIndex = vertices.size();
		for (Polyhedron.Face f : seed.faces) {
			List<Point3D> faceVertices = faceVertexMap.get(f.index);
			vertices.add(fvgen.createVertex(seed, seedVertices, f, faceVertices));
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
	
	public static class Factory extends PolyhedronOp.Factory<Gyro> {
		public String name() { return "Gyro"; }
		
		public Gyro parse(String[] args) {
			FaceVertexGen fvgen = new FaceVertexGen.AverageMagnitudeOffset(0);
			GyroVertexGen gvgen = new GyroVertexGen.RelativeDistanceFromMidpointAlongEdge(1.0/3.0);
			EdgeVertexGen evgen = new EdgeVertexGen.AverageMagnitudeOffset(0);
			FaceVertexGen.Builtin fvtmp;
			GyroVertexGen.Builtin gvtmp;
			EdgeVertexGen.Builtin evtmp;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-s")) {
					fvgen = new FaceVertexGen.FaceOffset(0);
					gvgen = new GyroVertexGen.RelativeDistanceFromMidpointAlongEdge(1.0/3.0);
					evgen = new EdgeVertexGen.FaceOffset(0);
				} else if ((fvtmp = FaceVertexGen.Builtin.forFlag(arg)) != null && (fvtmp.isVoidType() || argi < args.length)) {
					fvgen = fvtmp.parse(fvtmp.isVoidType() ? null : args[argi++]);
				} else if ((gvtmp = GyroVertexGen.Builtin.forFlag(arg)) != null && (gvtmp.isVoidType() || argi < args.length)) {
					gvgen = gvtmp.parse(gvtmp.isVoidType() ? null : args[argi++]);
				} else if ((evtmp = EdgeVertexGen.Builtin.forFlag(arg)) != null && (evtmp.isVoidType() || argi < args.length)) {
					evgen = evtmp.parse(evtmp.isVoidType() ? null : args[argi++]);
				} else {
					return null;
				}
			}
			return new Gyro(fvgen, gvgen, evgen);
		}
		
		public Option[] options() {
			return new Option[] {
				FaceVertexGen.Builtin.FACE_OFFSET.option("s"),
				FaceVertexGen.Builtin.MAX_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.Builtin.AVERAGE_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.Builtin.FACE_MAGNITUDE_OFFSET.option("s"),
				GyroVertexGen.Builtin.FIXED_DISTANCE_FROM_VERTEX_ALONG_EDGE.option("s"),
				GyroVertexGen.Builtin.RELATIVE_DISTANCE_FROM_VERTEX_ALONG_EDGE.option("s"),
				GyroVertexGen.Builtin.FIXED_ANGLE_FROM_VERTEX_ALONG_EDGE.option("s"),
				GyroVertexGen.Builtin.FIXED_DISTANCE_FROM_MIDPOINT_ALONG_EDGE.option("s"),
				GyroVertexGen.Builtin.RELATIVE_DISTANCE_FROM_MIDPOINT_ALONG_EDGE.option("s"),
				GyroVertexGen.Builtin.FIXED_ANGLE_FROM_MIDPOINT_ALONG_EDGE.option("s"),
				GyroVertexGen.Builtin.TWIST_ANGLE.option("s"),
				EdgeVertexGen.Builtin.FACE_OFFSET.option("s"),
				EdgeVertexGen.Builtin.MAX_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.Builtin.AVERAGE_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.Builtin.EDGE_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.Builtin.VERTEX_MAGNITUDE_OFFSET.option("s"),
				new Option(
					"s", Type.VOID, "create new vertices at centers of original faces (strict mode)",
					FaceVertexGen.Builtin.allOptionMutexes(GyroVertexGen.Builtin.allOptionMutexes(EdgeVertexGen.Builtin.allOptionMutexes()))
				),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}