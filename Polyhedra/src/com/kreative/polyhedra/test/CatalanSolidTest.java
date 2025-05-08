package com.kreative.polyhedra.test;

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;
import com.kreative.polyhedra.Metric;
import com.kreative.polyhedra.MetricAggregator;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.gen.CatalanSolid;
import com.kreative.polyhedra.gen.CatalanSolid.FormSpecifier;
import com.kreative.polyhedra.gen.CatalanSolid.SizeSpecifier;

public class CatalanSolidTest {
	public static void main(String[] args) {
		for (FormSpecifier f : FormSpecifier.values()) {
			System.out.println(f);
			Polyhedron p = new CatalanSolid(f, SizeSpecifier.DUAL_EDGE_LENGTH, 1, Color.GRAY).gen();
			double irf1 = MetricAggregator.MINIMUM.aggregate(Metric.FACE_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.inradiusFactor;
			double irf2 = MetricAggregator.MAXIMUM.aggregate(Metric.FACE_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.inradiusFactor;
			double mrf1 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.midradiusFactor;
			double mrf2 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.midradiusFactor;
			double crf1 = MetricAggregator.MINIMUM.aggregate(Metric.VERTEX_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.smallCircumradiusFactor;
			double crf2 = MetricAggregator.MAXIMUM.aggregate(Metric.VERTEX_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.largeCircumradiusFactor;
			double elf1 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p, Point3D.ZERO)) / f.shortEdgeLengthFactor;
			double elf2 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p, Point3D.ZERO)) / f.longEdgeLengthFactor;
			double[] mtx = {irf1, irf2, mrf1, mrf2, crf1, crf2, elf1, elf2};
			System.out.print("Metrics: ");
			for (double m : mtx) System.out.print(" " + (float)m);
			System.out.println();
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
			System.out.print("Angles:");
			for (Map.Entry<Float,Float> e : angles.entrySet()) {
				System.out.print(" " + (e.getValue() / p.faces.size()) + "×" + e.getKey() + "°");
			}
			System.out.println();
			System.out.println();
		}
	}
}