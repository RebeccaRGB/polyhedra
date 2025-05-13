package com.kreative.polyhedra.test;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private static final double TA = (1-Math.cbrt(2*(283+21*Math.sqrt(33)))-Math.cbrt(2*(283-21*Math.sqrt(33))));
	private static final double PHI = (1+Math.sqrt(5))/2;
	private static final double X = (4-Math.cbrt(44+12*PHI*(9+Math.sqrt(81*PHI-15)))-Math.cbrt(44+12*PHI*(9-Math.sqrt(81*PHI-15))))/12;
	private static final double XA = Math.cbrt((PHI+Math.sqrt(PHI-5/27.0))/2)+Math.cbrt((PHI-Math.sqrt(PHI-5/27.0))/2);
	
	private static final double[] dihedralAngle = {
		Math.toDegrees(Math.acos(-7/11.0)),
		Math.toDegrees(Math.acos(-1/2.0)),
		Math.toDegrees(Math.acos(-4/5.0)),
		Math.toDegrees(Math.acos(-(3+8*R2)/17)),
		Math.toDegrees(Math.acos(-(7+4*R2)/17)),
		Math.toDegrees(Math.acos(-(71+12*R2)/97)),
		Math.toDegrees(Math.acos(TA/21)),
		Math.toDegrees(Math.acos(TA/21)),
		Math.toDegrees(Math.acos(-(1+R5)/4)),
		Math.toDegrees(Math.acos(-(80+9*R5)/109)),
		Math.toDegrees(Math.acos(-3*(8+5*R5)/61)),
		Math.toDegrees(Math.acos(-(19+8*R5)/41)),
		Math.toDegrees(Math.acos(-(179+24*R5)/241)),
		Math.toDegrees(Math.acos(-(2*(XA+(2/XA))*(1+15*PHI)+(15+16*PHI))/209)),
		Math.toDegrees(Math.acos(-(2*(XA+(2/XA))*(1+15*PHI)+(15+16*PHI))/209))
	};
	
	private static final List<Map<Float,Integer>> validAngles = Arrays.asList(
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
	
	private static Map<Float,Integer> validAngles(Number... f) {
		Map<Float,Integer> m = new HashMap<Float,Integer>();
		for (int i = 1; i < f.length; i += 2) {
			double deg = Math.toDegrees(Math.acos(f[i-1].doubleValue()));
			m.put((float)deg, f[i].intValue());
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
			// Get/check/print metrics
			double irf1 = MetricAggregator.MINIMUM.aggregate(Metric.FACE_DISTANCE_TO_ORIGIN.iterator(p, Point3D.ZERO)) / f.inradiusFactor;
			double irf2 = MetricAggregator.MAXIMUM.aggregate(Metric.FACE_DISTANCE_TO_ORIGIN.iterator(p, Point3D.ZERO)) / f.inradiusFactor;
			double mrf1 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_DISTANCE_TO_ORIGIN.iterator(p, Point3D.ZERO)) / f.midradiusFactor;
			double mrf2 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_DISTANCE_TO_ORIGIN.iterator(p, Point3D.ZERO)) / f.midradiusFactor;
			double crf1 = MetricAggregator.MINIMUM.aggregate(Metric.VERTEX_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.smallCircumradiusFactor;
			double crf2 = MetricAggregator.MAXIMUM.aggregate(Metric.VERTEX_MAGNITUDE.iterator(p, Point3D.ZERO)) / f.largeCircumradiusFactor;
			double elf1 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p, Point3D.ZERO)) / f.shortEdgeLengthFactor;
			double elf2 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p, Point3D.ZERO)) / f.longEdgeLengthFactor;
			double daf1 = MetricAggregator.MINIMUM.aggregate(Metric.DIHEDRAL_ANGLE.iterator(p, Point3D.ZERO)) / dihedralAngle[index];
			double daf2 = MetricAggregator.MAXIMUM.aggregate(Metric.DIHEDRAL_ANGLE.iterator(p, Point3D.ZERO)) / dihedralAngle[index];
			double[] mtx = {irf1, irf2, mrf1, mrf2, crf1, crf2, elf1, elf2, daf1, daf2};
			System.out.print("Metrics:");
			for (double m : mtx) System.out.print(((1 == (float)m) ? " \u001B[1;32m" : " \u001B[1;31m") + (float)m + "\u001B[0m");
			System.out.println();
			// Get/check/print edge angles
			Map<Float,Integer> angles = MetricAggregator.createHistogram(Metric.VERTEX_ANGLE.iterator(p, Point3D.ZERO));
			System.out.print("Angles:");
			for (Map.Entry<Float,Integer> e : angles.entrySet()) {
				Integer deg = validAngles.get(index).get(e.getKey());
				boolean ok = (deg != null) && (e.getValue() == (deg * p.faces.size()));
				float avgdeg = e.getValue().floatValue() / p.faces.size();
				System.out.print((ok ? " \u001B[1;32m" : " \u001B[1;31m") + avgdeg + "×" + e.getKey() + "°\u001B[0m");
			}
			System.out.println();
			System.out.println();
		}
	}
}