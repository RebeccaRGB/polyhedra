package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.MetricAggregator;
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
		
		vertices.addAll(seed.points());
		fvgen.reset(seed, vertices);
		gvgen.reset(seed, vertices);
		evgen.reset(seed, vertices);
		
		Map<Integer,Integer> edgeStartIndexMap = new HashMap<Integer,Integer>();
		Map<Integer,List<Point3D>> faceVertexMap = new HashMap<Integer,List<Point3D>>();
		for (Polyhedron.Face f : seed.faces) {
			edgeStartIndexMap.put(f.index, vertices.size());
			List<Point3D> fv = f.points();
			faceVertexMap.put(f.index, fv);
			for (Polyhedron.Edge e : f.edges) {
				Point3D v = gvgen.createVertex(f, fv, e, e.vertex2.point);
				vertices.add(evgen.createVertex(f, fv, e, v));
			}
		}
		
		int faceStartIndex = vertices.size();
		for (Polyhedron.Face f : seed.faces) {
			List<Point3D> faceVertices = faceVertexMap.get(f.index);
			vertices.add(fvgen.createVertex(f, faceVertices));
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
			FaceVertexGen fvgen = new FaceVertexGen.SeedVertexMagnitudeOffset(MetricAggregator.AVERAGE, 0);
			GyroVertexGen gvgen = new GyroVertexGen.RelativeDistanceFromMidpointAlongEdge(1.0/3.0);
			EdgeVertexGen evgen = new EdgeVertexGen.SeedVertexMagnitudeOffset(MetricAggregator.AVERAGE, 0);
			FaceVertexGen.Builder fvtmp;
			GyroVertexGen.Builder gvtmp;
			EdgeVertexGen.Builder evtmp;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-s")) {
					fvgen = new FaceVertexGen.FaceOffset(0);
					gvgen = new GyroVertexGen.RelativeDistanceFromMidpointAlongEdge(1.0/3.0);
					evgen = new EdgeVertexGen.FaceOffset(0);
				} else if ((fvtmp = FaceVertexGen.Builder.forFlag(arg)) != null && (fvtmp.ignoresArgument() || argi < args.length)) {
					fvgen = fvtmp.buildFromArgument(fvtmp.ignoresArgument() ? null : args[argi++]);
				} else if ((gvtmp = GyroVertexGen.Builder.forFlag(arg)) != null && (gvtmp.ignoresArgument() || argi < args.length)) {
					gvgen = gvtmp.buildFromArgument(gvtmp.ignoresArgument() ? null : args[argi++]);
				} else if ((evtmp = EdgeVertexGen.Builder.forFlag(arg)) != null && (evtmp.ignoresArgument() || argi < args.length)) {
					evgen = evtmp.buildFromArgument(evtmp.ignoresArgument() ? null : args[argi++]);
				} else {
					return null;
				}
			}
			return new Gyro(fvgen, gvgen, evgen);
		}
		
		public Option[] options() {
			return new Option[] {
				FaceVertexGen.Builder.FACE_OFFSET.option("s"),
				FaceVertexGen.Builder.MAX_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.Builder.AVERAGE_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.Builder.FACE_MAGNITUDE_OFFSET.option("s"),
				GyroVertexGen.Builder.FIXED_DISTANCE_FROM_VERTEX_ALONG_EDGE.option("s"),
				GyroVertexGen.Builder.RELATIVE_DISTANCE_FROM_VERTEX_ALONG_EDGE.option("s"),
				GyroVertexGen.Builder.FIXED_ANGLE_FROM_VERTEX_ALONG_EDGE.option("s"),
				GyroVertexGen.Builder.FIXED_DISTANCE_FROM_MIDPOINT_ALONG_EDGE.option("s"),
				GyroVertexGen.Builder.RELATIVE_DISTANCE_FROM_MIDPOINT_ALONG_EDGE.option("s"),
				GyroVertexGen.Builder.FIXED_ANGLE_FROM_MIDPOINT_ALONG_EDGE.option("s"),
				GyroVertexGen.Builder.TWIST_ANGLE.option("s"),
				EdgeVertexGen.Builder.FACE_OFFSET.option("s"),
				EdgeVertexGen.Builder.MAX_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.Builder.AVERAGE_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.Builder.EDGE_MAGNITUDE_OFFSET.option("s"),
				EdgeVertexGen.Builder.VERTEX_MAGNITUDE_OFFSET.option("s"),
				new Option(
					"s", Type.VOID, "create new vertices at centers of original faces (strict mode)",
					FaceVertexGen.Builder.allOptionMutexes(GyroVertexGen.Builder.allOptionMutexes(EdgeVertexGen.Builder.allOptionMutexes()))
				),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}