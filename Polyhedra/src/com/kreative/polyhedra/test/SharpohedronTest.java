package com.kreative.polyhedra.test;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
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
			// Get dihedral angles
			TreeMap<Float,Integer> angles = new TreeMap<Float,Integer>();
			for (Polyhedron.Edge edge : p.edges) {
				Point3D m = edge.midpoint();
				List<Polyhedron.Face> faces = p.getFaces(edge);
				for (int i = 0; i < faces.size(); i++) {
					Point3D ci = faces.get(i).center();
					for (int j = i + 1; j < faces.size(); j++) {
						Point3D cj = faces.get(j).center();
						Float key = (float)m.angle(ci, cj);
						Integer value = angles.get(key);
						angles.put(key, ((value != null) ? (value + 1) : 1));
					}
				}
			}
			// Get edge lengths and angles
			int kites = 0, rhombs = 0, errors = 0;
			HashMap<FaceInfo,Integer> faces = new HashMap<FaceInfo,Integer>();
			for (Polyhedron.Face face : p.faces) {
				boolean rhomb = Recolor.Classifier.RHOMBUS.matches(face, 1e-12);
				boolean kite = Recolor.Classifier.KITE.matches(face, 1e-12);
				if (rhomb) rhombs++; else if (kite) kites++; else errors++;
				String type = rhomb ? "Rhombi" : kite ? "Kites" : "Errors";
				FaceInfo key = new FaceInfo(type, face);
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
			// Print dihedral angles
			System.out.print("Dihedral Angles:");
			for (Map.Entry<Float,Integer> e : angles.entrySet()) {
				System.out.print(" " + e.getValue() + "×" + e.getKey() + "°");
			}
			System.out.println();
			// Print edge lengths and angles
			for (Map.Entry<FaceInfo,Integer> e : faces.entrySet()) {
				System.out.println(e.getValue() + " " + e.getKey().type + ":");
				e.getKey().print();
			}
			System.out.println();
		}
	}
	
	private static class FaceInfo {
		private final String type;
		private final TreeMap<Float,Integer> edges = new TreeMap<Float,Integer>();
		private final TreeMap<Float,Integer> angles = new TreeMap<Float,Integer>();
		public FaceInfo(String type, Polyhedron.Face face) {
			this.type = type;
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
				&& this.type.equals(((FaceInfo)o).type)
				&& this.edges.equals(((FaceInfo)o).edges)
				&& this.angles.equals(((FaceInfo)o).angles)
			);
		}
		public final int hashCode() {
			return type.hashCode() + edges.hashCode() + angles.hashCode();
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