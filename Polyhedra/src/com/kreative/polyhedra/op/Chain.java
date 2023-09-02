package com.kreative.polyhedra.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Chain extends PolyhedronOp {
	private final List<PolyhedronOp> ops;
	
	public Chain(PolyhedronOp... ops) {
		this(Arrays.asList(ops));
	}
	
	public Chain(List<PolyhedronOp> ops) {
		this.ops = new ArrayList<PolyhedronOp>(ops);
		Collections.reverse(this.ops);
	}
	
	public Polyhedron op(Polyhedron seed) {
		for (PolyhedronOp op : ops) seed = op.op(seed);
		return seed;
	}
	
	public static Chain parse(String[] args) {
		List<PolyhedronOp> ops = new ArrayList<PolyhedronOp>();
		for (String arg : args) {
			PolyhedronOp op = parseOp(arg);
			if (op != null) ops.add(op);
		}
		return new Chain(ops);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}