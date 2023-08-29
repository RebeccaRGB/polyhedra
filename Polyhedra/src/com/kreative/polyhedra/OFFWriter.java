package com.kreative.polyhedra;

import java.io.*;
import java.util.*;

public class OFFWriter {
	private final StringBuffer sb;
	private final PrintWriter pw;
	
	public OFFWriter(StringBuffer sb) {
		this.sb = sb;
		this.pw = null;
	}
	
	public OFFWriter(PrintWriter pw) {
		this.sb = null;
		this.pw = pw;
	}
	
	public OFFWriter(OutputStream out) {
		this.sb = null;
		try { this.pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true); }
		catch (UnsupportedEncodingException e) { throw new IllegalStateException(e); }
	}
	
	private void println(Object... things) {
		if (sb != null) {
			for (int i = 0; i < things.length; i++) {
				if (i > 0) sb.append("\t");
				sb.append(things[i]);
			}
			sb.append("\n");
		}
		if (pw != null) {
			for (int i = 0; i < things.length; i++) {
				if (i > 0) pw.print("\t");
				pw.print(things[i]);
			}
			pw.println();
		}
	}
	
	public void writePolyhedron(Polyhedron p) {
		println("OFF");
		println(p.vertices.size(), p.faces.size(), p.edges.size());
		for (Polyhedron.Vertex v : p.vertices) {
			println(v.point.getX(), v.point.getY(), v.point.getZ());
		}
		for (Polyhedron.Face f : p.faces) {
			List<Integer> face = new ArrayList<Integer>();
			face.add(f.vertices.size());
			for (Polyhedron.Vertex v : f.vertices) face.add(v.index);
			face.add(f.color.getRed());
			face.add(f.color.getGreen());
			face.add(f.color.getBlue());
			println(face.toArray());
		}
	}
}