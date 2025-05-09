package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Translate extends PolyhedronOp {
	private final double tx;
	private final double ty;
	private final double tz;
	
	public Translate(double tx, double ty, double tz) {
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		for (Polyhedron.Vertex vertex : seed.vertices) {
			double x = vertex.point.getX() + tx;
			double y = vertex.point.getY() + ty;
			double z = vertex.point.getZ() + tz;
			vertices.add(new Point3D(x, y, z));
		}
		for (Polyhedron.Face face : seed.faces) {
			List<Integer> indices = new ArrayList<Integer>(face.vertices.size());
			for (Polyhedron.Vertex v : face.vertices) indices.add(v.index);
			faces.add(indices);
			faceColors.add(face.color);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Translate> {
		public String name() { return "Translate"; }
		
		public Translate parse(String[] args) {
			double tx = 0, ty = 0, tz = 0;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equals("-x") && argi < args.length) {
					tx = parseDouble(args[argi++], tx);
				} else if (arg.equals("-y") && argi < args.length) {
					ty = parseDouble(args[argi++], ty);
				} else if (arg.equals("-z") && argi < args.length) {
					tz = parseDouble(args[argi++], tz);
				} else {
					return null;
				}
			}
			return new Translate(tx, ty, tz);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("x", Type.REAL, "translate x axis"),
				new Option("y", Type.REAL, "translate y axis"),
				new Option("z", Type.REAL, "translate z axis"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}