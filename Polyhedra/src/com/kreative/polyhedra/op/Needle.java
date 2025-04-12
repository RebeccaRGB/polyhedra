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

public class Needle extends PolyhedronOp {
	private final FaceVertexGen fvgen;
	private final Object fvarg;
	private final Color color;
	
	public Needle(FaceVertexGen fvgen, Object fvarg, Color color) {
		this.fvgen = fvgen;
		this.fvarg = fvarg;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vfSize = seed.vertices.size() + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>(vfSize);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.edges.size());
		List<Color> faceColors = new ArrayList<Color>(seed.edges.size());
		
		List<Point3D> seedVertices = seed.points();
		vertices.addAll(seedVertices);
		
		Map<Polyhedron.Edge,Integer> edgeVertexMap = new HashMap<Polyhedron.Edge,Integer>();
		for (Polyhedron.Face f : seed.faces) {
			Point3D newVertex = fvgen.createVertex(seed, seedVertices, f, f.points(), fvarg);
			if (newVertex != null) {
				int i0 = vertices.size();
				vertices.add(newVertex);
				for (int i = 0, n = f.edges.size(); i < n; i++) {
					Integer i2 = edgeVertexMap.get(f.edges.get(i));
					if (i2 == null) {
						edgeVertexMap.put(f.edges.get(i), i0);
					} else {
						int i1 = f.vertices.get(i).index;
						int i3 = f.vertices.get((i + 1) % n).index;
						faces.add(Arrays.asList(i0, i1, i2));
						faces.add(Arrays.asList(i2, i3, i0));
						faceColors.add(color);
						faceColors.add(color);
					}
				}
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Needle> {
		public String name() { return "Needle"; }
		
		public Needle parse(String[] args) {
			FaceVertexGen fvgen = FaceVertexGen.FACE_OFFSET;
			FaceVertexGen fvtmp;
			Object fvarg = 0;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-s")) {
					fvgen = FaceVertexGen.FACE_OFFSET;
					fvarg = 0;
				} else if ((fvtmp = FaceVertexGen.forFlagIgnoreCase(arg)) != null && (fvtmp.isVoidType() || argi < args.length)) {
					fvgen = fvtmp;
					fvarg = fvtmp.isVoidType() ? null : fvtmp.parseArgument(args[argi++]);
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					return null;
				}
			}
			return new Needle(fvgen, fvarg, color);
		}
		
		public Option[] options() {
			return new Option[] {
				FaceVertexGen.FACE_OFFSET.option("s"),
				FaceVertexGen.MAX_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.FACE_MAGNITUDE_OFFSET.option("s"),
				new Option("s", Type.VOID, "create new vertices at centers of original faces (strict mode)", FaceVertexGen.allOptionMutexes()),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}