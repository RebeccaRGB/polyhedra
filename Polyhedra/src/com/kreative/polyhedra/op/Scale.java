package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Scale extends PolyhedronOp {
	private final double sx;
	private final double sy;
	private final double sz;
	private final boolean reverse;
	
	public Scale(double s) {
		this.sx = s;
		this.sy = s;
		this.sz = s;
		this.reverse = s < 0;
	}
	
	public Scale(double sx, double sy, double sz) {
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
		this.reverse = sx * sy * sz < 0;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		for (Polyhedron.Vertex vertex : seed.vertices) {
			double x = vertex.point.getX() * sx;
			double y = vertex.point.getY() * sy;
			double z = vertex.point.getZ() * sz;
			vertices.add(new Point3D(x, y, z));
		}
		for (Polyhedron.Face face : seed.faces) {
			List<Integer> indices = new ArrayList<Integer>(face.vertices.size());
			for (Polyhedron.Vertex v : face.vertices) indices.add(v.index);
			if (reverse) Collections.reverse(indices);
			faces.add(indices);
			faceColors.add(face.color);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Scale> {
		public String name() { return "Scale"; }
		
		public Scale parse(String[] args) {
			double sx = 1, sy = 1, sz = 1;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-s") && argi < args.length) {
					sx = sy = sz = parseDouble(args[argi++], (sx + sy + sz) / 3);
				} else if (arg.equalsIgnoreCase("-x") && argi < args.length) {
					sx = parseDouble(args[argi++], sx);
				} else if (arg.equalsIgnoreCase("-y") && argi < args.length) {
					sy = parseDouble(args[argi++], sy);
				} else if (arg.equalsIgnoreCase("-z") && argi < args.length) {
					sz = parseDouble(args[argi++], sz);
				} else {
					return null;
				}
			}
			return new Scale(sx, sy, sz);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("s", Type.REAL, "scale uniformly", "x","y","z"),
				new Option("x", Type.REAL, "scale x axis", "s"),
				new Option("y", Type.REAL, "scale y axis", "s"),
				new Option("z", Type.REAL, "scale z axis", "s"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}