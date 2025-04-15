package com.kreative.polyhedra;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Path3DWriter {
	private final StringBuffer sb;
	private final PrintWriter pw;
	
	public Path3DWriter(StringBuffer sb) {
		this.sb = sb;
		this.pw = null;
	}
	
	public Path3DWriter(PrintWriter pw) {
		this.sb = null;
		this.pw = pw;
	}
	
	public Path3DWriter(OutputStream out) {
		this.sb = null;
		try { this.pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true); }
		catch (UnsupportedEncodingException e) { throw new IllegalStateException(e); }
	}
	
	private void println(Object... things) {
		if (sb != null) {
			for (int i = 0; i < things.length; i++) {
				if (i > 0) sb.append(" ");
				sb.append(things[i]);
			}
			sb.append("\n");
		}
		if (pw != null) {
			for (int i = 0; i < things.length; i++) {
				if (i > 0) pw.print(" ");
				pw.print(things[i]);
			}
			pw.println();
		}
	}
	
	public void writePolyhedron(Polyhedron p) {
		for (Polyhedron.Vertex v : p.vertices) {
			println("V", v.point.getX(), v.point.getY(), v.point.getZ());
		}
		for (Polyhedron.Face f : p.faces) {
			ArrayList<Object> things = new ArrayList<Object>();
			things.add("[#" + Integer.toHexString(f.color.getRGB() | 0xFF000000).substring(2).toUpperCase() + "]");
			for (int i = 0, n = f.vertices.size(); i < n; i++) {
				if (i == 0) things.add("V");
				if (i == 1) things.add("L");
				things.add("#" + f.vertices.get(i).index);
			}
			things.add("Z");
			println(things.toArray());
		}
	}
}