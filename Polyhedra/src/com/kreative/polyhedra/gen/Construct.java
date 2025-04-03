package com.kreative.polyhedra.gen;

import java.util.ArrayList;
import java.util.List;
import com.kreative.polyhedra.ConwayNotationParser;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;
import com.kreative.polyhedra.PolyhedronOp;
import com.kreative.polyhedra.op.Chain;

public class Construct extends PolyhedronGen {
	private final PolyhedronOp op;
	private final PolyhedronGen gen;
	
	public Construct(PolyhedronOp op, PolyhedronGen gen) {
		this.op = op;
		this.gen = gen;
	}
	
	public Polyhedron gen() {
		return (op != null) ? op.op(gen.gen()) : gen.gen();
	}
	
	public static Construct parse(String[] args) {
		if (args.length == 0) {
			System.err.println("This operation requires at least one parameter.");
			return null;
		}
		if (args.length == 1) {
			try {
				ConwayNotationParser p = new ConwayNotationParser();
				p.parse(args[0]);
				if (p.size() == 0) {
					throw new IllegalArgumentException("This operation requires a non-empty parameter.");
				}
				if (p.size() == 1) {
					PolyhedronGen gen = p.getGenerator(0);
					if (gen == null) return null;
					return new Construct(null, gen);
				}
				if (p.size() == 2) {
					PolyhedronOp op = p.getOperation(0);
					if (op == null) return null;
					PolyhedronGen gen = p.getGenerator(1);
					if (gen == null) return null;
					return new Construct(op, gen);
				}
				List<PolyhedronOp> ops = new ArrayList<PolyhedronOp>();
				for (int i = 0, n = p.size() - 1; i < n; i++) {
					PolyhedronOp op = p.getOperation(i);
					if (op != null) ops.add(op);
				}
				PolyhedronGen gen = p.getGenerator(p.size() - 1);
				if (gen == null) return null;
				return new Construct(new Chain(ops), gen);
			} catch (Exception e) {
				System.err.println(e);
				return null;
			}
		}
		if (args.length == 2) {
			PolyhedronOp op = parseOp(args[0]);
			if (op == null) return null;
			PolyhedronGen gen = parseGen(args[1]);
			if (gen == null) return null;
			return new Construct(op, gen);
		}
		List<PolyhedronOp> ops = new ArrayList<PolyhedronOp>();
		for (int i = 0; i < args.length - 1; i++) {
			PolyhedronOp op = parseOp(args[i]);
			if (op != null) ops.add(op);
		}
		PolyhedronGen gen = parseGen(args[args.length - 1]);
		if (gen == null) return null;
		return new Construct(new Chain(ops), gen);
	}
	
	public static Option[] options() {
		return new Option[] {
			new Option(Mult.REPEATED, Type.OP, "operation"),
			new Option(Mult.REQUIRED, Type.GEN, "generator"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}