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
	private final FaceVertexGen gen;
	private final double size;
	
	public Kis(int[] sides, FaceVertexGen gen, double size) {
		this.sides = new HashSet<Integer>();
		if (sides != null) for (int i : sides) this.sides.add(i);
		this.gen = gen;
		this.size = size;
	}
	
	public Kis(Integer[] sides, FaceVertexGen gen, double size) {
		this.sides = new HashSet<Integer>();
		if (sides != null) for (int i : sides) this.sides.add(i);
		this.gen = gen;
		this.size = size;
	}
	
	public Kis(Iterable<? extends Integer> sides, FaceVertexGen gen, double size) {
		this.sides = new HashSet<Integer>();
		if (sides != null) for (int i : sides) this.sides.add(i);
		this.gen = gen;
		this.size = size;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		
		List<Point3D> seedVertices = seed.points();
		vertices.addAll(seedVertices);
		
		for (Polyhedron.Face f : seed.faces) {
			if (sides.isEmpty() || sides.contains(f.vertices.size())) {
				Point3D newVertex = gen.createVertex(seed, seedVertices, f, f.points(), size);
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
			List<Integer> face = new ArrayList<Integer>();
			for (Polyhedron.Vertex v : f.vertices) face.add(v.index);
			faces.add(face);
			faceColors.add(f.color);
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Kis parse(String[] args) {
		List<Integer> sides = null;
		FaceVertexGen gen = FaceVertexGen.EQUILATERAL;
		double size = 0;
		FaceVertexGen tmp;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-n") && argi < args.length) {
				sides = parseIntList(args[argi++]);
			} else if (arg.equalsIgnoreCase("-s")) {
				gen = FaceVertexGen.FACE_OFFSET;
				size = 0;
			} else if ((tmp = FaceVertexGen.forFlagIgnoreCase(arg)) != null && (tmp.isVoidType() || argi < args.length)) {
				gen = tmp;
				size = tmp.isVoidType() ? 0 : parseDouble(args[argi++], size);
			} else {
				printOptions(options());
				return null;
			}
		}
		return new Kis(sides, gen, size);
	}
	
	public static Option[] options() {
		return new Option[] {
			new Option("n", Type.INTS, "only operate on faces with the specified number of edges"),
			FaceVertexGen.FACE_OFFSET.option("s"),
			FaceVertexGen.MAX_MAGNITUDE_OFFSET.option("s"),
			FaceVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"),
			FaceVertexGen.FACE_MAGNITUDE_OFFSET.option("s"),
			FaceVertexGen.EQUILATERAL.option("s"),
			new Option("s", Type.VOID, "create new vertices at centers of original faces (strict mode)", FaceVertexGen.allOptionMutexes()),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}