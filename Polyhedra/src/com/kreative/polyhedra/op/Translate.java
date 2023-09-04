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
	
	public static Translate parse(String[] args) {
		double tx = 0, ty = 0, tz = 0;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-x") && argi < args.length) {
				tx = parseDouble(args[argi++], tx);
			} else if (arg.equalsIgnoreCase("-y") && argi < args.length) {
				ty = parseDouble(args[argi++], ty);
			} else if (arg.equalsIgnoreCase("-z") && argi < args.length) {
				tz = parseDouble(args[argi++], tz);
			} else {
				System.err.println("Options:");
				System.err.println("  -x <real>   translate x axis");
				System.err.println("  -y <real>   translate y axis");
				System.err.println("  -z <real>   translate z axis");
				return null;
			}
		}
		return new Translate(tx, ty, tz);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}