package com.kreative.polyhedra.test;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
	private static final int[] faceCount = { 12, 12, 24, 24, 24, 48, 24, 24, 30, 60, 60, 60, 120, 60, 60 };
	private static final int[] edgeCount = { 18, 24, 36, 36, 48, 72, 60, 60, 60, 90, 90, 120, 180, 150, 150 };
	private static final int[] vertexCount = { 8, 14, 14, 14, 26, 26, 38, 38, 32, 32, 32, 62, 62, 92, 92 };
	
	private static final double R2 = Math.sqrt(2);
	private static final double R5 = Math.sqrt(5);
	private static final double T = (1+Math.cbrt(19-3*Math.sqrt(33))+Math.cbrt(19+3*Math.sqrt(33)))/3;
	private static final double PHI = (1+Math.sqrt(5))/2;
	private static final double X = (4-Math.cbrt(44+12*PHI*(9+Math.sqrt(81*PHI-15)))-Math.cbrt(44+12*PHI*(9-Math.sqrt(81*PHI-15))))/12;
	private static final List<Map<Float,Float>> validAngles = Arrays.asList(
		validAngles(-7/18.0, 1, 5/6.0, 2),
		validAngles(1/3.0, 2, -1/3.0, 2),
		validAngles(1/9.0, 1, 2/3.0, 2),
		validAngles((1-2*R2)/4, 1, (2+R2)/4, 2),
		validAngles((2-R2)/4, 3, -(2+R2)/8, 1),
		validAngles((2-R2)/12, 1, (6-R2)/8, 1, (1+6*R2)/12, 1),
		validAngles(2-T, 1, (1-T)/2, 4),
		validAngles(2-T, 1, (1-T)/2, 4),
		validAngles(R5/5, 2, -R5/5, 2),
		validAngles((9*R5-7)/36, 1, (9-R5)/12, 2),
		validAngles(-3*(1+R5)/20, 1, (15+R5)/20, 2),
		validAngles(-(5+2*R5)/20, 1, (9*R5-5)/40, 1, (5-2*R5)/10, 2),
		validAngles((5-2*R5)/30, 1, (15-2*R5)/20, 1, (9+5*R5)/24, 1),
		validAngles(1-2*(1-2*X*X)*(1-2*X*X), 1, X, 4),
		validAngles(1-2*(1-2*X*X)*(1-2*X*X), 1, X, 4)
	);
	
	private static Map<Float,Float> validAngles(Number... f) {
		Map<Float,Float> m = new HashMap<Float,Float>();
		for (int i = 1; i < f.length; i += 2) {
			double deg = Math.toDegrees(Math.acos(f[i-1].doubleValue()));
			m.put((float)deg, f[i].floatValue());
		}
		return m;
	}
	
	public static void main(String[] args) {
		int index = -1;
		for (FormSpecifier f : FormSpecifier.values()) {
			index++;
			System.out.println("\u001B[1;34m" + f + "\u001B[0m");
			Polyhedron p = new CatalanSolid(f, SizeSpecifier.DUAL_EDGE_LENGTH, 1, Color.GRAY).gen();
			// Get/check/print vertex/edge/face count
			System.out.print("VEF:");
			System.out.print(((p.vertices.size() == vertexCount[index]) ? " \u001B[1;32m" : " \u001B[1;31m") + p.vertices.size() + "\u001B[0m");
			System.out.print(((p.edges.size() == edgeCount[index]) ? " \u001B[1;32m" : " \u001B[1;31m") + p.edges.size() + "\u001B[0m");
			System.out.print(((p.faces.size() == faceCount[index]) ? " \u001B[1;32m" : " \u001B[1;31m") + p.faces.size() + "\u001B[0m");
			System.out.println();
			// Get metrics
			double irf1 = MetricAggregator.MINIMUM.aggregate(Metric.FACE_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.inradiusFactor;
			double irf2 = MetricAggregator.MAXIMUM.aggregate(Metric.FACE_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.inradiusFactor;
			double mrf1 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.midradiusFactor;
			double mrf2 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.midradiusFactor;
			double crf1 = MetricAggregator.MINIMUM.aggregate(Metric.VERTEX_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.smallCircumradiusFactor;
			double crf2 = MetricAggregator.MAXIMUM.aggregate(Metric.VERTEX_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.largeCircumradiusFactor;
			double elf1 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p, Point3D.ZERO)) / f.shortEdgeLengthFactor;
			double elf2 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p, Point3D.ZERO)) / f.longEdgeLengthFactor;
			double[] mtx = {irf1, irf2, mrf1, mrf2, crf1, crf2, elf1, elf2};
			// Check/print metrics
			System.out.print("Metrics:");
			for (int i = 0; i < 4; i++) System.out.print(" " + (float)mtx[i]);
			for (int i = 4; i < mtx.length; i++) System.out.print(((1 == (float)mtx[i]) ? " \u001B[1;32m" : " \u001B[1;31m") + (float)mtx[i] + "\u001B[0m");
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
				Float deg = validAngles.get(index).get(e.getKey());
				boolean ok = (deg != null) && (e.getValue() == (deg * p.faces.size()));
				System.out.print((ok ? " \u001B[1;32m" : " \u001B[1;31m") + (e.getValue() / p.faces.size()) + "×" + e.getKey() + "°\u001B[0m");
			}
			System.out.println();
			System.out.println();
		}
	}
}