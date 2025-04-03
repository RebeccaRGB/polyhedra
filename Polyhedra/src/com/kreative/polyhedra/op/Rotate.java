package com.kreative.polyhedra.op;

import java.util.ArrayList;
import java.util.List;
import com.kreative.polyhedra.AffineTransform3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Rotate extends PolyhedronOp {
	public static enum Axis {
		X {
			public AffineTransform3D rotate(AffineTransform3D tx, double angle) {
				if ((angle % 90) == 0) return tx.quadrantRotateX((int)(angle / 90));
				return tx.rotateX(Math.toRadians(angle));
			}
		},
		Y {
			public AffineTransform3D rotate(AffineTransform3D tx, double angle) {
				if ((angle % 90) == 0) return tx.quadrantRotateY((int)(angle / 90));
				return tx.rotateY(Math.toRadians(angle));
			}
		},
		Z {
			public AffineTransform3D rotate(AffineTransform3D tx, double angle) {
				if ((angle % 90) == 0) return tx.quadrantRotateZ((int)(angle / 90));
				return tx.rotateZ(Math.toRadians(angle));
			}
		};
		public abstract AffineTransform3D rotate(AffineTransform3D tx, double angle);
	}
	
	private final AffineTransform3D tx;
	
	public Rotate(Object... args) {
		AffineTransform3D tx = AffineTransform3D.IDENTITY;
		Axis axis = null;
		for (Object arg : args) {
			if (arg instanceof Axis) {
				axis = (Axis)arg;
			} else if (arg instanceof Number) {
				if (axis == null) throw new IllegalArgumentException("No axis specified");
				tx = axis.rotate(tx, ((Number)arg).doubleValue());
			} else {
				throw new IllegalArgumentException("Not an Axis or Number: " + arg);
			}
		}
		this.tx = tx;
	}
	
	public Polyhedron op(Polyhedron seed) {
		return new Polyhedron(seed, tx);
	}
	
	public static Rotate parse(String[] args) {
		List<Object> rotateArgs = new ArrayList<Object>();
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-x") && argi < args.length) {
				rotateArgs.add(Axis.X);
				rotateArgs.add(parseDouble(args[argi++], 0));
			} else if (arg.equalsIgnoreCase("-y") && argi < args.length) {
				rotateArgs.add(Axis.Y);
				rotateArgs.add(parseDouble(args[argi++], 0));
			} else if (arg.equalsIgnoreCase("-z") && argi < args.length) {
				rotateArgs.add(Axis.Z);
				rotateArgs.add(parseDouble(args[argi++], 0));
			} else {
				printOptions(options());
				return null;
			}
		}
		return new Rotate(rotateArgs.toArray());
	}
	
	public static Option[] options() {
		return new Option[] {
			new Option("x", Type.REAL, "rotate x axis"),
			new Option("y", Type.REAL, "rotate y axis"),
			new Option("z", Type.REAL, "rotate z axis"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}