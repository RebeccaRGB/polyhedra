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
	
	public Kis(List<FacePredicate> predicates, FaceVertexGen fvgen) {
		this.predicates = predicates;
		this.fvgen = fvgen;
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
				Point3D newVertex = fvgen.createVertex(seed, seedVertices, f, f.points());
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
			FacePredicate.Builder predtmp;
			FaceVertexGen fvgen = new FaceVertexGen.Equilateral();
			FaceVertexGen.Builder fvtmp;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if ((predtmp = FacePredicate.Builder.forFlagIgnoreCase(arg)) != null && (predtmp.ignoresArgument() || argi < args.length)) {
					predicates.add(predtmp.buildFromArgument(predtmp.ignoresArgument() ? null : args[argi++]));
				} else if (arg.equalsIgnoreCase("-s")) {
					fvgen = new FaceVertexGen.FaceOffset(0);
				} else if ((fvtmp = FaceVertexGen.Builder.forFlagIgnoreCase(arg)) != null && (fvtmp.ignoresArgument() || argi < args.length)) {
					fvgen = fvtmp.buildFromArgument(fvtmp.ignoresArgument() ? null : args[argi++]);
				} else {
					return null;
				}
			}
			return new Kis(predicates, fvgen);
		}
		
		public Option[] options() {
			List<Option> options = new ArrayList<Option>();
			for (FacePredicate.Builder bi : FacePredicate.Builder.values()) options.add(bi.option());
			options.add(FaceVertexGen.Builder.FACE_OFFSET.option("s"));
			options.add(FaceVertexGen.Builder.MAX_MAGNITUDE_OFFSET.option("s"));
			options.add(FaceVertexGen.Builder.AVERAGE_MAGNITUDE_OFFSET.option("s"));
			options.add(FaceVertexGen.Builder.FACE_MAGNITUDE_OFFSET.option("s"));
			options.add(FaceVertexGen.Builder.EQUILATERAL.option("s"));
			options.add(new Option("s", Type.VOID, "create new vertices at centers of original faces (strict mode)", FaceVertexGen.Builder.allOptionMutexes()));
			return options.toArray(new Option[options.size()]);
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}