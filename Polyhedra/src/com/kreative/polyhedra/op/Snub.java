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

public class Snub extends PolyhedronOp {
	private final GyroVertexGen gvgen;
	private final EdgeVertexGen evgen;
	private final Color color;
	
	public Snub(GyroVertexGen gvgen, EdgeVertexGen evgen, Color color) {
		this.gvgen = gvgen;
		this.evgen = evgen;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.edges.size() * 2);
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		
		List<Point3D> seedVertices = seed.points();
		gvgen.reset(seed, seedVertices);
		evgen.reset(seed, seedVertices);
		
		Map<Integer,Integer> edgeStartIndexMap = new HashMap<Integer,Integer>();
		Map<Integer,List<Point3D>> faceVertexMap = new HashMap<Integer,List<Point3D>>();
		for (Polyhedron.Face f : seed.faces) {
			edgeStartIndexMap.put(f.index, vertices.size());
			List<Point3D> fv = f.points();
			faceVertexMap.put(f.index, fv);
			List<Integer> faceVertexIndices = new ArrayList<Integer>(f.edges.size());
			for (Polyhedron.Edge e : f.edges) {
				faceVertexIndices.add(vertices.size());
				Point3D v = gvgen.createVertex(f, fv, e, e.vertex1.point);
				vertices.add(evgen.createVertex(f, fv, e, v));
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
	
	public static class Factory extends PolyhedronOp.Factory<Snub> {
		public String name() { return "Snub"; }
		
		public Snub parse(String[] args) {
			GyroVertexGen gvgen = new GyroVertexGen.RelativeDistanceFromMidpointAlongEdge(1.0/3.0);
			EdgeVertexGen evgen = new EdgeVertexGen.SeedVertexMagnitudeOffset(MetricAggregator.AVERAGE, 0);
			GyroVertexGen.Builder gvtmp;
			EdgeVertexGen.Builder evtmp;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equals("-s")) {
					gvgen = new GyroVertexGen.RelativeDistanceFromMidpointAlongEdge(1.0/3.0);
					evgen = new EdgeVertexGen.FaceOffset(0);
				} else if ((gvtmp = GyroVertexGen.Builder.forFlag(arg)) != null && (gvtmp.ignoresArgument() || argi < args.length)) {
					// -u -U -w -l -L -m -t
					gvgen = gvtmp.buildFromArgument(gvtmp.ignoresArgument() ? null : args[argi++]);
				} else if ((evtmp = EdgeVertexGen.Builder.forFlag(arg)) != null && (evtmp.ignoresArgument() || argi < args.length)) {
					// -h -x -a -v -e -d -o
					evgen = evtmp.buildFromArgument(evtmp.ignoresArgument() ? null : args[argi++]);
				} else if (arg.equals("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					return null;
				}
			}
			return new Snub(gvgen, evgen, color);
		}
		
		public Option[] options() {
			return new Option[] {
				GyroVertexGen.Builder.FIXED_DISTANCE_FROM_VERTEX_ALONG_EDGE.option("s"), // u
				GyroVertexGen.Builder.RELATIVE_DISTANCE_FROM_VERTEX_ALONG_EDGE.option("s"), // U
				GyroVertexGen.Builder.FIXED_ANGLE_FROM_VERTEX_ALONG_EDGE.option("s"), // w
				GyroVertexGen.Builder.FIXED_DISTANCE_FROM_MIDPOINT_ALONG_EDGE.option("s"), // l
				GyroVertexGen.Builder.RELATIVE_DISTANCE_FROM_MIDPOINT_ALONG_EDGE.option("s"), // L
				GyroVertexGen.Builder.FIXED_ANGLE_FROM_MIDPOINT_ALONG_EDGE.option("s"), // m
				GyroVertexGen.Builder.TWIST_ANGLE.option("s"), // t
				EdgeVertexGen.Builder.FACE_OFFSET.option("s"), // h
				EdgeVertexGen.Builder.MAX_MAGNITUDE_OFFSET.option("s"), // x
				EdgeVertexGen.Builder.AVERAGE_MAGNITUDE_OFFSET.option("s"), // a
				EdgeVertexGen.Builder.MIN_MAGNITUDE_OFFSET.option("s"), // v
				EdgeVertexGen.Builder.EDGE_MAGNITUDE_OFFSET.option("s"), // e
				EdgeVertexGen.Builder.VERTEX_MAGNITUDE_OFFSET.option("s"), // d
				EdgeVertexGen.Builder.FACE_OFFSET_FROM_ORIGIN.option("s"), // o
				new Option(
					"s", Type.VOID, "create new vertices along original edges (strict mode)",
					GyroVertexGen.Builder.allOptionMutexes(EdgeVertexGen.Builder.allOptionMutexes())
				),
				new Option("c", Type.COLOR, "color of new faces generated from original vertices"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}