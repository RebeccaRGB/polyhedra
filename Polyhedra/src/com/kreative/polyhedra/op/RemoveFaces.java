package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class RemoveFaces extends PolyhedronOp {
	private final List<FacePredicate> predicates;
	private final Color color;
	
	public RemoveFaces(List<FacePredicate> predicates, Color color) {
		this.predicates = predicates;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		if (predicates == null || predicates.isEmpty()) return seed;
		
		// Get indices of vertices to be removed.
		Set<Integer> indices = new HashSet<Integer>();
		FacePredicate.reset(predicates, seed);
		for (Polyhedron.Face f : seed.faces) {
			if (FacePredicate.matches(predicates, f)) {
				for (Polyhedron.Vertex v : f.vertices) {
					indices.add(v.index);
				}
			}
		}
		
		return RemoveVertices.removeVertices(seed, indices, color);
	}
	
	public static class Factory extends PolyhedronOp.Factory<RemoveFaces> {
		public String name() { return "RemoveFaces"; }
		
		public RemoveFaces parse(String[] args) {
			List<FacePredicate> predicates = new ArrayList<FacePredicate>();
			FacePredicate.Builder predtmp;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if ((predtmp = FacePredicate.Builder.forFlagIgnoreCase(arg)) != null && (predtmp.ignoresArgument() || argi < args.length)) {
					predicates.add(predtmp.buildFromArgument(predtmp.ignoresArgument() ? null : args[argi++]));
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					return null;
				}
			}
			return new RemoveFaces(predicates, color);
		}
		
		public Option[] options() {
			List<Option> options = new ArrayList<Option>();
			for (FacePredicate.Builder bi : FacePredicate.Builder.values()) options.add(bi.option());
			options.add(new Option("c", Type.COLOR, "color of replacement faces"));
			return options.toArray(new Option[options.size()]);
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}