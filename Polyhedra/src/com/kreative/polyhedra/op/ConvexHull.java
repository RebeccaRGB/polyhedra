package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;
import quickhull3d.Point3d;
import quickhull3d.QuickHull3D;

public class ConvexHull extends PolyhedronOp {
	private final Color color;
	
	public ConvexHull(Color color) {
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3d> points = new ArrayList<Point3d>();
		for (Polyhedron.Vertex v : seed.vertices) {
			points.add(new Point3d(v.point.getX(), v.point.getY(), v.point.getZ()));
		}
		
		QuickHull3D hull = new QuickHull3D();
		hull.build(points.toArray(new Point3d[points.size()]));
		
		List<Point3D> vertices = new ArrayList<Point3D>();
		for (Point3d v : hull.getVertices()) {
			vertices.add(new Point3D(v.x, v.y, v.z));
		}
		
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		for (int[] f : hull.getFaces()) {
			List<Integer> face = new ArrayList<Integer>();
			for (int i : f) face.add(i);
			faces.add(face);
			faceColors.add(color);
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<ConvexHull> {
		public String name() { return "ConvexHull"; }
		
		public ConvexHull parse(String[] args) {
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equals("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					return null;
				}
			}
			return new ConvexHull(color);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}