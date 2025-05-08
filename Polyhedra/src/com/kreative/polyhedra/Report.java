package com.kreative.polyhedra;

import java.util.ArrayList;

public class Report extends PolyhedronCon {
	public static void main(String[] args) {
		new Report().processArgs(args);
	}
	
	public void defaultAction() {
		printOptions();
	}
	
	public void consume(String source, Polyhedron p) {
		System.out.println(source);
		System.out.println("\tVertices: " + p.vertices.size());
		for (Polyhedron.Vertex v : p.vertices) {
			System.out.println("\t\t#" + v.index + "\t" + v.point + "\tmag=" + v.point.magnitude());
		}
		System.out.println("\tEdges: " + p.edges.size());
		for (Polyhedron.Edge e : p.edges) {
			Point3D m = e.midpoint();
			System.out.println("\t\t#" + e.vertex1.index + "-#" + e.vertex2.index + "\tlen=" + e.length());
			System.out.println("\t\t\tV0: #" + e.vertex1.index + "\t" + e.vertex1.point + "\tmag=" + e.vertex1.point.magnitude());
			System.out.println("\t\t\tV1: #" + e.vertex2.index + "\t" + e.vertex2.point + "\tmag=" + e.vertex2.point.magnitude());
			System.out.println("\t\t\tmidpoint\t" + m + "\tmag=" + m.magnitude());
		}
		System.out.println("\tFaces: " + p.faces.size());
		for (Polyhedron.Face f: p.faces) {
			ArrayList<Point3D> fv = new ArrayList<Point3D>(f.vertices.size());
			for (Polyhedron.Vertex v : f.vertices) fv.add(v.point);
			Point3D center = Point3D.average(fv);
			Point3D normal = center.normal(fv);
			System.out.println("\t\t#" + f.index + "\tcolor=" + f.color.getRed() + "," + f.color.getGreen() + "," + f.color.getRed());
			System.out.println("\t\t\tVertices: " + f.vertices.size());
			for (int i = 0; i < f.vertices.size(); i++) {
				Polyhedron.Vertex v = f.vertices.get(i);
				System.out.println(
					"\t\t\t\tV" + i + ": #" + v.index + "\t" + v.point +
					"\tmag=" + v.point.magnitude()
				);
			}
			System.out.println("\t\t\tEdges: " + f.edges.size());
			for (int i = 0; i < f.edges.size(); i++) {
				Polyhedron.Edge e = f.edges.get(i);
				System.out.println(
					"\t\t\t\tE" + i + ": #" + e.vertex1.index + "-#" + e.vertex2.index +
					"\tlen=" + e.length()
				);
			}
			System.out.println("\t\t\tAngles: " + f.vertices.size());
			for (int i = 0, n = f.vertices.size(); i < n; i++) {
				Polyhedron.Vertex v = f.vertices.get(i);
				Polyhedron.Vertex pv = f.vertices.get((i + n - 1) % n);
				Polyhedron.Vertex nv = f.vertices.get((i + 1) % n);
				System.out.println(
					"\t\t\t\tA" + i + ": #" + pv.index + "-#" + v.index + "-#" + nv.index +
					"\tdeg=" + v.point.angle(pv.point, nv.point) +
					"\trad=" + v.point.angleRad(pv.point, nv.point)
				);
			}
			System.out.println("\t\t\tcenter\t" + center + "\tmag=" + center.magnitude());
			System.out.println("\t\t\tnormal\t" + normal);
		}
		System.out.println("\tMetrics About Origin:");
		for (Metric metric : Metric.values()) {
			System.out.print("\t\t" + metric);
			for (MetricAggregator agg : MetricAggregator.values()) {
				System.out.print("\t" + agg + "=" + agg.aggregate(metric.iterator(p, Point3D.ZERO)));
			}
			System.out.println();
		}
		System.out.println("\tMetrics About Center:");
		Point3D center = p.center();
		for (Metric metric : Metric.values()) {
			System.out.print("\t\t" + metric);
			for (MetricAggregator agg : MetricAggregator.values()) {
				System.out.print("\t" + agg + "=" + agg.aggregate(metric.iterator(p, center)));
			}
			System.out.println();
		}
	}
	
	public void reportError(String message, Exception e) {
		System.err.println(message);
	}
}