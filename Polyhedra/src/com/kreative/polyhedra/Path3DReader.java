package com.kreative.polyhedra;

import java.io.InputStream;
import java.util.Scanner;

public class Path3DReader {
	private final Scanner scanner;
	
	public Path3DReader(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public Path3DReader(InputStream in) {
		this.scanner = new Scanner(in, "UTF-8");
	}
	
	public Polyhedron readPolyhedron() {
		StringBuffer sb = new StringBuffer();
		while (scanner.hasNextLine()) {
			sb.append(scanner.nextLine());
			sb.append("\n");
		}
		Path3D p = new Path3D();
		p.parse(sb.toString());
		if (p.isEmpty()) return null;
		return p.createPolyhedron();
	}
}