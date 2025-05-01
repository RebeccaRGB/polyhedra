package com.kreative.polyhedra;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ObjReader {
	private final Scanner obj;
	
	public ObjReader(Scanner obj) {
		this.obj = obj;
	}
	
	public ObjReader(InputStream obj) {
		this.obj = new Scanner(obj, "UTF-8");
	}
	
	public Polyhedron readPolyhedron(Color color) {
		List<Point3D> vertices = new ArrayList<Point3D>();
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		while (obj.hasNextLine()) {
			String line = obj.nextLine();
			int o = line.indexOf("#");
			if (o >= 0) line = line.substring(0, o);
			String[] fields = line.trim().split("\\s+");
			if ("v".equals(fields[0]) && fields.length >= 4) {
				double x = parseDouble(fields[1]);
				double y = parseDouble(fields[2]);
				double z = parseDouble(fields[3]);
				vertices.add(new Point3D(x, y, z));
			}
			if ("f".equals(fields[0])) {
				List<Integer> face = new ArrayList<Integer>();
				for (int i = 1; i < fields.length; i++) {
					String[] indices = fields[i].split("/");
					int index = parseInt(indices[0]);
					if (index > 0) face.add(index - 1);
					if (index < 0) face.add(vertices.size() + index);
				}
				faces.add(face);
				faceColors.add(color);
			}
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	private static double parseDouble(String s) {
		try { return Double.parseDouble(s); }
		catch (NumberFormatException e) { return 0; }
	}
	
	private static int parseInt(String s) {
		try { return Integer.parseInt(s); }
		catch (NumberFormatException e) { return 0; }
	}
}