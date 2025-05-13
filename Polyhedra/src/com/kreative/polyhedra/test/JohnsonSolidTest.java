package com.kreative.polyhedra.test;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import com.kreative.polyhedra.Metric;
import com.kreative.polyhedra.MetricAggregator;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.gen.JohnsonSolid;
import com.kreative.polyhedra.gen.JohnsonSolid.FormSpecifier;

public class JohnsonSolidTest {
	private static final int[] vertexCount = {
		5, 6, 9, 12, 15, 20, 7, 9, 11, 9, 11, 5, 7, 8, 10, 12, 10, 15, 20, 25, 30, 15, 20,
		25, 30, 8, 12, 16, 16, 20, 20, 25, 25, 30, 18, 18, 24, 30, 30, 35, 35, 40, 40, 18, 24, 30,
		35, 40, 7, 8, 9, 11, 12, 13, 14, 14, 15, 21, 22, 22, 23, 10, 9, 10, 15, 28, 32, 65, 70,
		70, 75, 60, 60, 60, 60, 55, 55, 55, 55, 50, 50, 50, 45, 8, 16, 10, 11, 12, 14, 16, 14, 18
	};
	
	private static final int[] edgeCount = {
		8, 10, 15, 20, 25, 35, 12, 16, 20, 20, 25, 9, 15, 15, 20, 25, 24, 27, 36, 45, 55, 33, 44,
		55, 65, 14, 24, 32, 32, 40, 40, 50, 50, 60, 36, 36, 48, 60, 60, 70, 70, 80, 80, 42, 56, 70,
		80, 90, 13, 17, 21, 19, 23, 22, 26, 26, 30, 35, 40, 40, 45, 20, 15, 18, 27, 48, 60, 105, 120,
		120, 135, 120, 120, 120, 120, 105, 105, 105, 105, 90, 90, 90, 75, 18, 40, 22, 26, 28, 33, 38, 26, 36
	};
	
	private static final int[] faceCount = {
		5, 6, 8, 10, 12, 17, 7, 9, 11, 13, 16, 6, 10, 9, 12, 15, 16, 14, 18, 22, 27, 20, 26,
		32, 37, 8, 14, 18, 18, 22, 22, 27, 27, 32, 20, 20, 26, 32, 32, 37, 37, 42, 42, 26, 34, 42,
		47, 52, 8, 11, 14, 10, 13, 11, 14, 14, 17, 16, 20, 20, 24, 12, 8, 10, 14, 22, 30, 42, 52,
		52, 62, 62, 62, 62, 62, 52, 52, 52, 52, 42, 42, 42, 32, 12, 26, 14, 17, 18, 21, 24, 14, 20
	};
	
	private static final Map<Float,Integer> validAngles = validAngles();
	private static Map<Float,Integer> validAngles() {
		Map<Float,Integer> m = new HashMap<Float,Integer>();
		m.put(60f, 3); m.put(90f, 4); m.put(108f, 5);
		m.put(120f, 6); m.put(135f, 8); m.put(144f, 10);
		return m;
	}
	
	public static void main(String[] args) {
		int index = -1;
		for (FormSpecifier f : FormSpecifier.values()) {
			index++;
			System.out.print("\u001B[1;34m" + (index+1) + "\u001B[0m");
			Polyhedron p1 = new JohnsonSolid(f, 1, Color.GRAY).gen();
			Polyhedron p2 = new JohnsonSolid(f, 2, Color.GRAY).gen();
			// Get/check/print vertex/edge/face count
			System.out.print("\t- VEF:");
			System.out.print(((p2.vertices.size() == vertexCount[index]) ? " \u001B[1;32m" : " \u001B[1;31m") + p2.vertices.size() + "\u001B[0m");
			System.out.print(((p2.edges.size() == edgeCount[index]) ? " \u001B[1;32m" : " \u001B[1;31m") + p2.edges.size() + "\u001B[0m");
			System.out.print(((p2.faces.size() == faceCount[index]) ? " \u001B[1;32m" : " \u001B[1;31m") + p2.faces.size() + "\u001B[0m");
			// Get/check/print edge lengths
			double elf1 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p1, Point3D.ZERO));
			double elf2 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p1, Point3D.ZERO));
			double elf3 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p2, Point3D.ZERO)) / 2;
			double elf4 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p2, Point3D.ZERO)) / 2;
			double[] mtx = {elf1, elf2, elf3, elf4};
			System.out.print("\t- Edges:");
			for (double m : mtx) System.out.print(((1 == (float)m) ? " \u001B[1;32m" : " \u001B[1;31m") + (float)m + "\u001B[0m");
			// Get/check/print edge angles
			Map<Float,Integer> angles = MetricAggregator.createHistogram(Metric.VERTEX_ANGLE.iterator(p2, Point3D.ZERO));
			System.out.print("\t- Angles:");
			for (Map.Entry<Float,Integer> e : angles.entrySet()) {
				Integer deg = validAngles.get(e.getKey());
				boolean ok = (deg != null) && ((e.getValue() % deg) == 0);
				float fc = e.getValue().floatValue() / deg.floatValue();
				System.out.print((ok ? " \u001B[1;32m" : " \u001B[1;31m") + fc + "×" + e.getKey() + "°\u001B[0m");
			}
			System.out.println();
		}
	}
}