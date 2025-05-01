package com.kreative.polyhedra;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class ObjWriter {
	private final StringBuffer sb;
	private final PrintWriter pw;
	
	public ObjWriter(StringBuffer sb) {
		this.sb = sb;
		this.pw = null;
	}
	
	public ObjWriter(PrintWriter pw) {
		this.sb = null;
		this.pw = pw;
	}
	
	public ObjWriter(OutputStream out) {
		this.sb = null;
		try { this.pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true); }
		catch (UnsupportedEncodingException e) { throw new IllegalStateException(e); }
	}
	
	private void println(String op, Object... args) {
		if (sb != null) {
			sb.append(op);
			for (Object arg : args) {
				sb.append(" ");
				sb.append(arg);
			}
			sb.append("\n");
		}
		if (pw != null) {
			pw.print(op);
			for (Object arg : args) {
				pw.print(" ");
				pw.print(arg);
			}
			pw.println();
		}
	}
	
	public void writePolyhedron(Polyhedron p) {
		for (Polyhedron.Vertex v : p.vertices) {
			println("v", v.point.getX(), v.point.getY(), v.point.getZ());
		}
		for (Polyhedron.Face f : p.faces) {
			int n = f.vertices.size();
			Object[] face = new Object[n];
			for (int i = 0; i < n; i++) face[i] = f.vertices.get(i).index + 1;
			println("f", face);
		}
	}
}