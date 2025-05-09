package com.kreative.polyhedra.test;

import java.awt.Color;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.gen.Sharpohedron;
import com.kreative.polyhedra.op.Recolor;

public class SharpohedronTest {
	public static void main(String[] args) {
		int index = -1;
		for (Sharpohedron.FormSpecifier f : Sharpohedron.FormSpecifier.values()) {
			index++;
			System.out.println("\u001B[1;34m" + f + " (" + f.archimedeanComponent + " + " + f.catalanComponent + ")\u001B[0m");
			Polyhedron p = new Sharpohedron(f, 1, Color.GRAY).gen();
			
			System.out.print("VEFKR:");
			System.out.print(" " + p.vertices.size());
			System.out.print(" " + p.edges.size());
			System.out.print(" " + p.faces.size());
			int k = 0;
			int r = 0;
			for (Polyhedron.Face face : p.faces) {
				if (Recolor.Classifier.RHOMBUS.matches(face, 1e-12)) r++;
				else if (Recolor.Classifier.KITE.matches(face, 1e-12)) k++;
			}
			System.out.print(" " + k);
			System.out.print(" " + r);
			System.out.println();
		}
	}
}