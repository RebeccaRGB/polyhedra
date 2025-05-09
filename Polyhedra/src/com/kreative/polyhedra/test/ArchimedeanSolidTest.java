package com.kreative.polyhedra.test;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.kreative.polyhedra.Metric;
import com.kreative.polyhedra.MetricAggregator;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.gen.ArchimedeanSolid;
import com.kreative.polyhedra.gen.ArchimedeanSolid.FormSpecifier;
import com.kreative.polyhedra.gen.ArchimedeanSolid.SizeSpecifier;

public class ArchimedeanSolidTest {
	private static final int[] vertexCount = { 12, 12, 24, 24, 24, 48, 24, 24, 30, 60, 60, 60, 120, 60, 60 };
	private static final int[] edgeCount = { 18, 24, 36, 36, 48, 72, 60, 60, 60, 90, 90, 120, 180, 150, 150 };
	private static final int[] faceCount = { 8, 14, 14, 14, 26, 26, 38, 38, 32, 32, 32, 62, 62, 92, 92 };
	
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
			System.out.println("\u001B[1;34m" + f + "\u001B[0m");
			Polyhedron p = new ArchimedeanSolid(f, SizeSpecifier.EDGE_LENGTH, 1, Color.GRAY).gen();
			// Get/check/print vertex/edge/face count
			System.out.print("VEF:");
			System.out.print(((p.vertices.size() == vertexCount[index]) ? " \u001B[1;32m" : " \u001B[1;31m") + p.vertices.size() + "\u001B[0m");
			System.out.print(((p.edges.size() == edgeCount[index]) ? " \u001B[1;32m" : " \u001B[1;31m") + p.edges.size() + "\u001B[0m");
			System.out.print(((p.faces.size() == faceCount[index]) ? " \u001B[1;32m" : " \u001B[1;31m") + p.faces.size() + "\u001B[0m");
			System.out.println();
			// Get metrics
			double mrf1 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.midradiusFactor;
			double mrf2 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.midradiusFactor;
			double crf1 = MetricAggregator.MINIMUM.aggregate(Metric.VERTEX_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.circumradiusFactor;
			double crf2 = MetricAggregator.MAXIMUM.aggregate(Metric.VERTEX_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.circumradiusFactor;
			double elf1 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p, Point3D.ZERO));
			double elf2 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p, Point3D.ZERO));
			double[] mtx = {mrf1, mrf2, crf1, crf2, elf1, elf2};
			// Check/print metrics
			System.out.print("Metrics:");
			for (double m : mtx) System.out.print(((1 == (float)m) ? " \u001B[1;32m" : " \u001B[1;31m") + (float)m + "\u001B[0m");
			System.out.println();
			// Get edge angles
			TreeMap<Float,Float> angles = new TreeMap<Float,Float>();
			for (Polyhedron.Face face : p.faces) {
				for (int i = 0, n = face.vertices.size(); i < n; i++) {
					Point3D vp = face.vertices.get(i).point;
					Point3D np = face.vertices.get((i + 1) % n).point;
					Point3D pp = face.vertices.get((i + n - 1) % n).point;
					Float key = (float)vp.angle(pp, np);
					Float value = angles.get(key);
					angles.put(key, ((value != null) ? (value + 1) : 1));
				}
			}
			// Check/print edge angles
			System.out.print("Angles:");
			for (Map.Entry<Float,Float> e : angles.entrySet()) {
				Integer deg = validAngles.get(e.getKey());
				boolean ok = (deg != null) && ((e.getValue() % deg) == 0);
				System.out.print((ok ? " \u001B[1;32m" : " \u001B[1;31m") + e.getValue() + "×" + e.getKey() + "°\u001B[0m");
			}
			System.out.println();
			System.out.println();
		}
	}
}