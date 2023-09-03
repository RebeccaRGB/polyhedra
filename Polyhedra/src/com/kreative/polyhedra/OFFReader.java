package com.kreative.polyhedra;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OFFReader {
	private final Scanner off;
	
	public OFFReader(Scanner off) {
		this.off = off;
	}
	
	public OFFReader(InputStream off) {
		this.off = new Scanner(off, "UTF-8");
	}
	
	public Polyhedron readPolyhedron() {
		while (off.hasNextLine()) {
			String line = off.nextLine().trim();
			if (line.equals("OFF")) return readPolyhedron1();
		}
		return null;
	}
	
	private Polyhedron readPolyhedron1() {
		while (off.hasNextLine()) {
			String line = off.nextLine().trim();
			if (line.length() == 0 || line.startsWith("#")) continue;
			String[] tokens = line.split("\\s+");
			if (tokens.length != 3) return null;
			try {
				int numVertices = Integer.parseInt(tokens[0]);
				int numFaces = Integer.parseInt(tokens[1]);
				/* int numEdges = */ Integer.parseInt(tokens[2]);
				List<Point3D> vertices = new ArrayList<Point3D>();
				List<List<Integer>> faces = new ArrayList<List<Integer>>();
				List<Color> faceColors = new ArrayList<Color>();
				for (int i = 0; i < numVertices; i++) {
					Point3D vertex = readVertex();
					if (vertex == null) return null;
					vertices.add(vertex);
				}
				for (int i = 0; i < numFaces; i++) {
					List<Integer> face = readFace();
					if (face == null) return null;
					faces.add(face.subList(0, face.size() - 3));
					int r = face.get(face.size() - 3);
					int g = face.get(face.size() - 2);
					int b = face.get(face.size() - 1);
					faceColors.add(new Color(r, g, b));
				}
				return new Polyhedron(vertices, faces, faceColors);
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		return null;
	}
	
	private Point3D readVertex() {
		while (off.hasNextLine()) {
			String line = off.nextLine().trim();
			if (line.length() == 0 || line.startsWith("#")) continue;
			String[] tokens = line.split("\\s+");
			if (tokens.length != 3) return null;
			try {
				double x = Double.parseDouble(tokens[0]);
				double y = Double.parseDouble(tokens[1]);
				double z = Double.parseDouble(tokens[2]);
				return new Point3D(x, y, z);
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		return null;
	}
	
	private List<Integer> readFace() {
		while (off.hasNextLine()) {
			String line = off.nextLine().trim();
			if (line.length() == 0 || line.startsWith("#")) continue;
			String[] tokens = line.split("\\s+");
			if (tokens.length <= 1) return null;
			try {
				List<Integer> f = new ArrayList<Integer>();
				for (String t : tokens) f.add(Integer.parseInt(t));
				if (f.size() == f.get(0) + 4) {
					f.remove(0);
					return f;
				}
				if (f.size() == f.get(0) + 1) {
					f.remove(0);
					f.add(128);
					f.add(128);
					f.add(128);
					return f;
				}
				return null;
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		return null;
	}
}