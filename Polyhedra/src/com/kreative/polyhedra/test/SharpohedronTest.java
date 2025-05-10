package com.kreative.polyhedra.test;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.gen.Sharpohedron;
import com.kreative.polyhedra.op.Recolor;

public class SharpohedronTest {
	public static void main(String[] args) {
		int index = -1;
		for (Sharpohedron.FormSpecifier f : Sharpohedron.FormSpecifier.values()) {
			index++;
			System.out.println("\u001B[1;34mS" + (index+1) + ": " + f + " (" + f.archimedeanComponent + " + " + f.catalanComponent + ")\u001B[0m");
			Polyhedron p = new Sharpohedron(f, 1, Color.GRAY).gen();
			// Get edge lengths and angles
			int kites = 0, rhombs = 0, errors = 0;
			HashMap<FaceInfo,Integer> faces = new HashMap<FaceInfo,Integer>();
			for (Polyhedron.Face face : p.faces) {
				if (Recolor.Classifier.RHOMBUS.matches(face, 1e-12)) rhombs++;
				else if (Recolor.Classifier.KITE.matches(face, 1e-12)) kites++;
				else errors++;
				FaceInfo key = new FaceInfo(face);
				Integer value = faces.get(key);
				faces.put(key, ((value != null) ? (value + 1) : 1));
			}
			// Print vertex/edge/face count
			System.out.print("VEF/KRO:");
			System.out.print(" " + p.vertices.size());
			System.out.print(" " + p.edges.size());
			System.out.print(" " + p.faces.size());
			System.out.print(" / " + kites);
			System.out.print(" " + rhombs);
			System.out.print(" " + errors);
			System.out.println();
			// Print edge lengths and angles
			int faceType = 0;
			for (Map.Entry<FaceInfo,Integer> e : faces.entrySet()) {
				faceType++;
				System.out.println(e.getValue() + " Faces of Type " + faceType + ":");
				e.getKey().print();
			}
			System.out.println();
		}
	}
	
	private static class FaceInfo {
		private final TreeMap<Float,Integer> edges = new TreeMap<Float,Integer>();
		private final TreeMap<Float,Integer> angles = new TreeMap<Float,Integer>();
		public FaceInfo(Polyhedron.Face face) {
			for (Polyhedron.Edge edge : face.edges) {
				Float key = (float)edge.length();
				Integer value = edges.get(key);
				edges.put(key, ((value != null) ? (value + 1) : 1));
			}
			for (int i = 0, n = face.vertices.size(); i < n; i++) {
				Point3D vp = face.vertices.get(i).point;
				Point3D np = face.vertices.get((i + 1) % n).point;
				Point3D pp = face.vertices.get((i + n - 1) % n).point;
				Float key = (float)vp.angle(pp, np);
				Integer value = angles.get(key);
				angles.put(key, ((value != null) ? (value + 1) : 1));
			}
		}
		public final boolean equals(Object o) {
			return (
				(o instanceof FaceInfo)
				&& this.edges.equals(((FaceInfo)o).edges)
				&& this.angles.equals(((FaceInfo)o).angles)
			);
		}
		public final int hashCode() {
			return edges.hashCode() + angles.hashCode();
		}
		public final void print() {
			System.out.print("  Edges:");
			for (Map.Entry<Float,Integer> e : edges.entrySet()) {
				System.out.print(" " + e.getValue() + "×" + e.getKey());
			}
			System.out.println();
			System.out.print("  Angles:");
			for (Map.Entry<Float,Integer> e : angles.entrySet()) {
				System.out.print(" " + e.getValue() + "×" + e.getKey() + "°");
			}
			System.out.println();
		}
	}
}