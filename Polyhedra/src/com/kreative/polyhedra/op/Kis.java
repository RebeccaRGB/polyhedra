package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Kis extends PolyhedronOp {
	private final List<FacePredicate> predicates;
	private final FaceVertexGen fvgen;
	private final Object fvarg;
	
	public Kis(List<FacePredicate> predicates, FaceVertexGen fvgen, Object fvarg) {
		this.predicates = predicates;
		this.fvgen = fvgen;
		this.fvarg = fvarg;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		
		List<Point3D> seedVertices = seed.points();
		vertices.addAll(seedVertices);
		
		if (predicates != null) for (FacePredicate p : predicates) p.reset();
		for (Polyhedron.Face f : seed.faces) {
			boolean matches = true;
			if (predicates != null) {
				for (FacePredicate p : predicates) {
					if (!p.matches(f)) {
						matches = false;
						break;
					}
				}
			}
			if (matches) {
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
			List<FacePredicate> predicates = new ArrayList<FacePredicate>();
			FacePredicate.Builtin predtmp;
			FaceVertexGen fvgen = FaceVertexGen.EQUILATERAL;
			FaceVertexGen fvtmp;
			Object fvarg = 0;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-s")) {
					fvgen = FaceVertexGen.FACE_OFFSET;
					fvarg = 0;
				} else if ((fvtmp = FaceVertexGen.forFlagIgnoreCase(arg)) != null && (fvtmp.isVoidType() || argi < args.length)) {
					fvgen = fvtmp;
					fvarg = fvtmp.isVoidType() ? null : fvtmp.parseArgument(args[argi++]);
				} else if ((predtmp = FacePredicate.Builtin.forFlagIgnoreCase(arg)) != null && (predtmp.isVoidType() || argi < args.length)) {
					predicates.add(predtmp.parse(predtmp.isVoidType() ? null : args[argi++]));
				} else {
					return null;
				}
			}
			return new Kis(predicates, fvgen, fvarg);
		}
		
		public Option[] options() {
			List<Option> options = new ArrayList<Option>();
			for (FacePredicate.Builtin bi : FacePredicate.Builtin.values()) options.add(bi.option());
			options.add(FaceVertexGen.FACE_OFFSET.option("s"));
			options.add(FaceVertexGen.MAX_MAGNITUDE_OFFSET.option("s"));
			options.add(FaceVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"));
			options.add(FaceVertexGen.FACE_MAGNITUDE_OFFSET.option("s"));
			options.add(FaceVertexGen.EQUILATERAL.option("s"));
			options.add(new Option("s", Type.VOID, "create new vertices at centers of original faces (strict mode)", FaceVertexGen.allOptionMutexes()));
			return options.toArray(new Option[options.size()]);
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}