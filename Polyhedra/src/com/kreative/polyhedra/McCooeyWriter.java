package com.kreative.polyhedra;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TreeSet;

public class McCooeyWriter {
	private final StringBuffer sb;
	private final PrintWriter pw;
	
	public McCooeyWriter(StringBuffer sb) {
		this.sb = sb;
		this.pw = null;
	}
	
	public McCooeyWriter(PrintWriter pw) {
		this.sb = null;
		this.pw = pw;
	}
	
	public McCooeyWriter(OutputStream out) {
		this.sb = null;
		try { this.pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true); }
		catch (UnsupportedEncodingException e) { throw new IllegalStateException(e); }
	}
	
	private void println(String... things) {
		if (sb != null) {
			for (int i = 0; i < things.length; i++) {
				sb.append(things[i]);
			}
			sb.append("\n");
		}
		if (pw != null) {
			for (int i = 0; i < things.length; i++) {
				pw.print(things[i]);
			}
			pw.println();
		}
	}
	
	private void leftAlign(String[][] table, int column) {
		int width = 0;
		for (int i = 0; i < table.length; i++) {
			if (table[i][column].length() > width) {
				width = table[i][column].length();
			}
		}
		for (int i = 0; i < table.length; i++) {
			while (table[i][column].length() < width) {
				table[i][column] = table[i][column] + " ";
			}
		}
	}
	
	private void rightAlign(String[][] table, int column) {
		int width = 0;
		for (int i = 0; i < table.length; i++) {
			if (table[i][column].length() > width) {
				width = table[i][column].length();
			}
		}
		for (int i = 0; i < table.length; i++) {
			while (table[i][column].length() < width) {
				table[i][column] = " " + table[i][column];
			}
		}
	}
	
	public void writePolyhedron(Polyhedron p) {
		// Gather non-integer coordinates.
		TreeSet<Double> coordSet = new TreeSet<Double>();
		double[] coordTmp = new double[3];
		for (Polyhedron.Vertex v : p.vertices) {
			coordTmp[0] = v.point.getX();
			coordTmp[1] = v.point.getY();
			coordTmp[2] = v.point.getZ();
			for (double c : coordTmp) {
				if (c != (int)c) {
					coordSet.add(Math.abs(c));
				}
			}
		}
		
		// Put non-integer coordinates into a table.
		ArrayList<Double> coordList = new ArrayList<Double>(coordSet);
		String[][] coordTable = new String[coordList.size()][];
		for (int i = 0, n = coordList.size(); i < n; i++) {
			String c = Double.toString(coordList.get(i));
			coordTable[i] = new String[]{ "C"+i, " = ", c };
		}
		
		// Print table of non-integer coordinates.
		leftAlign(coordTable, 0);
		for (String[] row : coordTable) println(row);
		if (coordTable.length > 0) println();
		
		// Put vertices into a table.
		String[][] vertexTable = new String[p.vertices.size()][];
		for (int i = 0, n = p.vertices.size(); i < n; i++) {
			Point3D v = p.vertices.get(i).point;
			int xi = coordList.indexOf(Math.abs(v.getX()));
			int yi = coordList.indexOf(Math.abs(v.getY()));
			int zi = coordList.indexOf(Math.abs(v.getZ()));
			String xs = (xi < 0) ? Double.toString(v.getX()) : (((v.getX() < 0) ? "-C" : "C") + xi);
			String ys = (yi < 0) ? Double.toString(v.getY()) : (((v.getY() < 0) ? "-C" : "C") + yi);
			String zs = (zi < 0) ? Double.toString(v.getZ()) : (((v.getZ() < 0) ? "-C" : "C") + zi);
			vertexTable[i] = new String[]{ "V"+i, " = (", xs, ", ", ys, ", ", zs, ")" };
		}
		
		// Print table of vertices.
		leftAlign(vertexTable, 0);
		rightAlign(vertexTable, 2);
		rightAlign(vertexTable, 4);
		rightAlign(vertexTable, 6);
		for (String[] row : vertexTable) println(row);
		if (vertexTable.length > 0) println();
		
		// Gather vertex indices.
		ArrayList<ArrayList<String>> faces = new ArrayList<ArrayList<String>>(p.faces.size());
		for (Polyhedron.Face f : p.faces) {
			ArrayList<String> s = new ArrayList<String>(f.vertices.size());
			for (Polyhedron.Vertex v : f.vertices) s.add(Integer.toString(v.index));
			faces.add(s);
		}
		
		// Find maximum string length.
		int width = 0;
		for (ArrayList<String> f : faces) {
			for (String s : f) {
				if (s.length() > width) {
					width = s.length();
				}
			}
		}
		
		// Print vertex indices.
		println("Faces:");
		for (ArrayList<String> f : faces) {
			String[] row = new String[f.size() * 2 + 1];
			int index = 0;
			row[index++] = "{ ";
			for (int i = 0, n = f.size(); i < n; i++) {
				if (i > 0) row[index++] = ", ";
				String s = f.get(i);
				while (s.length() < width) s = " " + s;
				row[index++] = s;
			}
			row[index++] = " }";
			println(row);
		}
	}
}