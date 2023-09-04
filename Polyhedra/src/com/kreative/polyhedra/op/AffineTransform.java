package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.kreative.polyhedra.AffineTransform3D;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class AffineTransform extends PolyhedronOp {
	private final AffineTransform3D tx;
	private final boolean reverse;
	
	public AffineTransform(AffineTransform3D tx) {
		this.tx = tx;
		this.reverse = tx.getDeterminant() < 0;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		for (Polyhedron.Vertex vertex : seed.vertices) {
			vertices.add(tx.transform(vertex.point));
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
	
	public static AffineTransform parse(String[] args) {
		double m00 = (args.length > 0) ? parseDouble(args[0], 1) : 1;
		double m10 = (args.length > 1) ? parseDouble(args[1], 0) : 0;
		double m20 = (args.length > 2) ? parseDouble(args[2], 0) : 0;
		double m01 = (args.length > 3) ? parseDouble(args[3], 0) : 0;
		double m11 = (args.length > 4) ? parseDouble(args[4], 1) : 1;
		double m21 = (args.length > 5) ? parseDouble(args[5], 0) : 0;
		double m02 = (args.length > 6) ? parseDouble(args[6], 0) : 0;
		double m12 = (args.length > 7) ? parseDouble(args[7], 0) : 0;
		double m22 = (args.length > 8) ? parseDouble(args[8], 1) : 1;
		double m03 = (args.length > 9) ? parseDouble(args[9], 0) : 0;
		double m13 = (args.length > 10) ? parseDouble(args[10], 0) : 0;
		double m23 = (args.length > 11) ? parseDouble(args[11], 0) : 0;
		return new AffineTransform(new AffineTransform3D(
			m00, m10, m20,
			m01, m11, m21,
			m02, m12, m22,
			m03, m13, m23
		));
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}