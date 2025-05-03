package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Kis extends PolyhedronOp {
	private final Set<Integer> sides;
	private final Set<Integer> indices;
	private final FaceVertexGen fvgen;
	private final Object fvarg;
	
	public Kis(int[] sides, int[] indices, FaceVertexGen fvgen, Object fvarg) {
		this.sides = new HashSet<Integer>();
		if (sides != null) for (int i : sides) this.sides.add(i);
		this.indices = new HashSet<Integer>();
		if (indices != null) for (int i : indices) this.indices.add(i);
		this.fvgen = fvgen;
		this.fvarg = fvarg;
	}
	
	public Kis(Integer[] sides, Integer[] indices, FaceVertexGen fvgen, Object fvarg) {
		this.sides = new HashSet<Integer>();
		if (sides != null) for (int i : sides) this.sides.add(i);
		this.indices = new HashSet<Integer>();
		if (indices != null) for (int i : indices) this.indices.add(i);
		this.fvgen = fvgen;
		this.fvarg = fvarg;
	}
	
	public Kis(Iterable<? extends Integer> sides, Iterable<? extends Integer> indices, FaceVertexGen fvgen, Object fvarg) {
		this.sides = new HashSet<Integer>();
		if (sides != null) for (int i : sides) this.sides.add(i);
		this.indices = new HashSet<Integer>();
		if (indices != null) for (int i : indices) this.indices.add(i);
		this.fvgen = fvgen;
		this.fvarg = fvarg;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		
		List<Point3D> seedVertices = seed.points();
		vertices.addAll(seedVertices);
		
		int currentIndex = 0;
		for (Polyhedron.Face f : seed.faces) {
			if (sides.isEmpty() || sides.contains(f.vertices.size())) {
				currentIndex++;
				if (indices.isEmpty() || indices.contains(currentIndex)) {
					Point3D newVertex = fvgen.createVertex(seed, seedVertices, f, f.points(), fvarg);
					if (newVertex != null) {
						int i0 = vertices.size();
						vertices.add(newVertex);
						for (int i = 0, n = f.vertices.size(); i < n; i++) {
							int i1 = f.vertices.get(i).index;
							int i2 = f.vertices.get((i + 1) % n).index;
							faces.add(Arrays.asList(i0, i1, i2));
							faceColors.add(f.color);
						}
						continue;
					}
				}
			}
			List<Integer> face = new ArrayList<Integer>();
			for (Polyhedron.Vertex v : f.vertices) face.add(v.index);
			faces.add(face);
			faceColors.add(f.color);
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Kis> {
		public String name() { return "Kis"; }
		
		public Kis parse(String[] args) {
			List<Integer> sides = null;
			List<Integer> indices = null;
			FaceVertexGen fvgen = FaceVertexGen.EQUILATERAL;
			FaceVertexGen fvtmp;
			Object fvarg = 0;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-n") && argi < args.length) {
					sides = parseIntList(args[argi++]);
				} else if (arg.equalsIgnoreCase("-i") && argi < args.length) {
					indices = parseIntList(args[argi++]);
				} else if (arg.equalsIgnoreCase("-s")) {
					fvgen = FaceVertexGen.FACE_OFFSET;
					fvarg = 0;
				} else if ((fvtmp = FaceVertexGen.forFlagIgnoreCase(arg)) != null && (fvtmp.isVoidType() || argi < args.length)) {
					fvgen = fvtmp;
					fvarg = fvtmp.isVoidType() ? null : fvtmp.parseArgument(args[argi++]);
				} else {
					return null;
				}
			}
			return new Kis(sides, indices, fvgen, fvarg);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("n", Type.INTS, "only operate on faces with the specified number of edges"),
				new Option("i", Type.INTS, "only operate on faces with the specified indices"),
				FaceVertexGen.FACE_OFFSET.option("s"),
				FaceVertexGen.MAX_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.FACE_MAGNITUDE_OFFSET.option("s"),
				FaceVertexGen.EQUILATERAL.option("s"),
				new Option("s", Type.VOID, "create new vertices at centers of original faces (strict mode)", FaceVertexGen.allOptionMutexes()),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}