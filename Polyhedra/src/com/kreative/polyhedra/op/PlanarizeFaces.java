package com.kreative.polyhedra.op;

import java.awt.Color;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;
import com.kreative.polyhedra.AffineTransform3D;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class PlanarizeFaces extends PolyhedronOp {
	private static Point3D getIntersectionPoint(
		Point3D normal1, Point3D center1,
		Point3D normal2, Point3D center2,
		Point3D normal3, Point3D center3
	) {
		AffineTransform3D matrix = new AffineTransform3D(
			normal1.getX(), normal2.getX(), normal3.getX(),
			normal1.getY(), normal2.getY(), normal3.getY(),
			normal1.getZ(), normal2.getZ(), normal3.getZ(),
			-normal1.dotProduct(center1),
			-normal2.dotProduct(center2),
			-normal3.dotProduct(center3)
		);
		try { return matrix.inverseTransform(Point3D.ZERO); }
		catch (NoninvertibleTransformException e) { return null; }
	}
	
	private static double planarizeFaces(Polyhedron p, List<Point3D> vertices) {
		// Find (average) center and normal of each face.
		List<Point3D> normals = new ArrayList<Point3D>();
		List<Point3D> centers = new ArrayList<Point3D>();
		for (Polyhedron.Face f : p.faces) {
			List<Point3D> faceVertices = new ArrayList<Point3D>();
			for (Polyhedron.Vertex v : f.vertices) faceVertices.add(vertices.get(v.index));
			Point3D center = Point3D.average(faceVertices);
			Point3D normal = center.normal(faceVertices);
			normals.add(normal);
			centers.add(center);
		}
		// Move each vertex to the (average) point where the planes of its faces intersect.
		// Track the maximum distance of each vertex from its original location.
		double maxDifference = 0;
		for (Polyhedron.Vertex v : p.vertices) {
			List<Point3D> intersectionPoints = new ArrayList<Point3D>();
			List<Polyhedron.Face> faces = p.getFaces(v);
			for (Polyhedron.Face f1 : faces) {
				for (Polyhedron.Face f2 : faces) {
					if (f2 == f1) continue;
					for (Polyhedron.Face f3 : faces) {
						if (f3 == f2 || f3 == f1) continue;
						Point3D intersectionPoint = getIntersectionPoint(
							normals.get(f1.index), centers.get(f1.index),
							normals.get(f2.index), centers.get(f2.index),
							normals.get(f3.index), centers.get(f3.index)
						);
						if (intersectionPoint == null) continue;
						intersectionPoints.add(intersectionPoint);
					}
				}
			}
			if (intersectionPoints.isEmpty()) continue;
			Point3D intersectionPoint = Point3D.average(intersectionPoints);
			double diff = intersectionPoint.distance(vertices.get(v.index));
			if (diff > maxDifference) maxDifference = diff;
			vertices.set(v.index, intersectionPoint);
		}
		return maxDifference;
	}
	
	private final int maxIterations;
	private final double maxDifference;
	
	public PlanarizeFaces(int maxIterations, double maxDifference) {
		this.maxIterations = (maxIterations > 0) ? maxIterations : Integer.MAX_VALUE;
		this.maxDifference = (maxDifference > 0) ? maxDifference : 0;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		
		for (Polyhedron.Vertex v : seed.vertices) vertices.add(v.point);
		for (Polyhedron.Face f : seed.faces) {
			List<Integer> face = new ArrayList<Integer>(f.vertices.size());
			for (Polyhedron.Vertex v : f.vertices) face.add(v.index);
			faces.add(face);
			faceColors.add(f.color);
		}
		
		int iterations = 0;
		double difference = 0;
		while (iterations < maxIterations) {
			iterations++;
			difference = planarizeFaces(seed, vertices);
			if (difference <= maxDifference) break;
		}
		
		System.err.println("PlanarizeFaces: n=" + iterations + ", delta=" + difference);
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<PlanarizeFaces> {
		public String name() { return "PlanarizeFaces"; }
		
		public PlanarizeFaces parse(String[] args) {
			int maxIterations = 100;
			double maxDifference = 1e-15;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equals("-n") && argi < args.length) {
					maxIterations = parseInt(args[argi++], maxIterations);
				} else if (arg.equals("-e") && argi < args.length) {
					maxDifference = parseDouble(args[argi++], maxDifference);
				} else {
					return null;
				}
			}
			return new PlanarizeFaces(maxIterations, maxDifference);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("n", Type.INT, "maximum number of iterations (default 100)"),
				new Option("e", Type.REAL, "maximum difference in vertex locations (default 10^-15)")
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}