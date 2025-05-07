package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Augment extends PolyhedronOp {
	public static enum AugmentationSpec {
		ORTHO {
			public boolean createTriangle(Polyhedron seed, Polyhedron.Edge edge, Polyhedron.Face face) {
				for (Polyhedron.Face opp : seed.getOppositeFaces(edge, face)) {
					if ((opp.vertices.size() & 1) != 0) {
						return false;
					}
				}
				return true;
			}
		},
		GYRO {
			public boolean createTriangle(Polyhedron seed, Polyhedron.Edge edge, Polyhedron.Face face) {
				for (Polyhedron.Face opp : seed.getOppositeFaces(edge, face)) {
					if ((opp.vertices.size() & 1) == 0) {
						return false;
					}
				}
				return true;
			}
		};
		public abstract boolean createTriangle(Polyhedron seed, Polyhedron.Edge edge, Polyhedron.Face face);
	}
	
	private final List<FacePredicate> predicates;
	private final AugmentationSpec spec;
	private final double r;
	private final double h;
	
	public Augment(List<FacePredicate> predicates, AugmentationSpec spec, double r, double h) {
		this.predicates = predicates;
		this.spec = spec;
		this.r = r;
		this.h = h;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		
		List<Point3D> seedVertices = seed.points();
		vertices.addAll(seedVertices);
		
		FacePredicate.reset(predicates, seed);
		for (Polyhedron.Face f : seed.faces) {
			if (FacePredicate.matches(predicates, f)) {
				List<Point3D> points = f.points();
				Point3D center = Point3D.average(points);
				Point3D normal = center.normal(points);
				Point3D ch = center.add(normal.multiply(h));
				List<Point3D> midpoints = new ArrayList<Point3D>();
				for (Polyhedron.Edge e : f.edges) {
					if (!spec.createTriangle(seed, e, f)) {
						midpoints.add(e.midpoint());
					}
				}
				List<Integer> newVertices = new ArrayList<Integer>();
				if (midpoints.isEmpty()) {
					newVertices.add(vertices.size());
					vertices.add(ch);
				} else {
					for (int i = 0, n = midpoints.size(); i < n; i++) {
						newVertices.add(vertices.size());
						Point3D p1 = midpoints.get(i);
						Point3D p2 = midpoints.get((i + 1) % n);
						Point3D p3 = p1.midpoint(p2).add(normal.multiply(h));
						Point3D p4 = p3.subtract(ch).normalize(r).add(ch);
						vertices.add(p4);
					}
				}
				faces.add(newVertices);
				faceColors.add(f.color);
				int i = 0, n = newVertices.size();
				for (Polyhedron.Edge e : f.edges) {
					if (spec.createTriangle(seed, e, f)) {
						faces.add(Arrays.asList(
							e.vertex1.index, e.vertex2.index,
							newVertices.get((i + n - 1) % n)
						));
					} else {
						faces.add(Arrays.asList(
							e.vertex1.index, e.vertex2.index,
							newVertices.get(i % n),
							newVertices.get((i + n - 1) % n)
						));
						i++;
					}
				}
				continue;
			}
			List<Integer> face = new ArrayList<Integer>();
			for (Polyhedron.Vertex v : f.vertices) face.add(v.index);
			faces.add(face);
			faceColors.add(f.color);
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Augment> {
		public String name() { return "Augment"; }
		
		public Augment parse(String[] args) {
			List<FacePredicate> predicates = new ArrayList<FacePredicate>();
			FacePredicate.Builder predtmp;
			AugmentationSpec spec = AugmentationSpec.ORTHO;
			double r = 0.5;
			double h = 0.5;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if ((predtmp = FacePredicate.Builder.forFlagIgnoreCase(arg)) != null && (predtmp.ignoresArgument() || argi < args.length)) {
					predicates.add(predtmp.buildFromArgument(predtmp.ignoresArgument() ? null : args[argi++]));
				} else if (arg.equalsIgnoreCase("-o")) {
					spec = AugmentationSpec.ORTHO;
				} else if (arg.equalsIgnoreCase("-g")) {
					spec = AugmentationSpec.GYRO;
				} else if (arg.equalsIgnoreCase("-r") && argi < args.length) {
					r = parseDouble(args[argi++], r);
				} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
					h = parseDouble(args[argi++], h);
				} else {
					return null;
				}
			}
			return new Augment(predicates, spec, r, h);
		}
		
		public Option[] options() {
			List<Option> options = new ArrayList<Option>();
			for (FacePredicate.Builder bi : FacePredicate.Builder.values()) options.add(bi.option());
			options.add(new Option("o", Type.VOID, "augment with orthocupola"));
			options.add(new Option("g", Type.VOID, "augment with gyrocupola"));
			options.add(new Option("r", Type.REAL, "radius of augmented face"));
			options.add(new Option("h", Type.REAL, "height of augmented face"));
			return options.toArray(new Option[options.size()]);
		};
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}